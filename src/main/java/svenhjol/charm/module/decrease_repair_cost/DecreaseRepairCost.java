package svenhjol.charm.module.decrease_repair_cost;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.util.TriConsumer;
import svenhjol.charm.Charm;
import svenhjol.charm.handler.ModuleHandler;
import svenhjol.charm.module.CharmModule;
import svenhjol.charm.event.TakeAnvilOutputCallback;
import svenhjol.charm.event.UpdateAnvilCallback;
import svenhjol.charm.annotation.Config;
import svenhjol.charm.annotation.Module;
import svenhjol.charm.init.CharmAdvancements;
import svenhjol.charm.mixin.accessor.ForgingScreenHandlerAccessor;

@Module(mod = Charm.MOD_ID, description = "Combine a tool or armor with an amethyst shard on an anvil to reduce its repair cost.",
    requiresMixins = {"UpdateAnvilCallback", "TakeAnvilOutputCallback"})
public class DecreaseRepairCost extends CharmModule {
    public static final Identifier TRIGGER_DECREASED_COST = new Identifier(Charm.MOD_ID, "decreased_cost");

    @Config(name = "XP cost", description = "Number of levels required to reduce repair cost on the anvil.")
    public static int xpCost = 0;

    @Config(name = "Repair cost decrease", description = "The tool repair cost will be decreased by this amount.")
    public static int decreaseAmount = 5;

    @Override
    public void init() {
        // if anvil improvements are not enabled, then set the xpCost to 1.
        if (!ModuleHandler.enabled("charm:anvil_improvements") && xpCost < 1)
            xpCost = 1;

        // register the anvil recipe for this operation
        UpdateAnvilCallback.EVENT.register(this::tryReduceRepairCost);

        // listen for when player takes item from anvil
        TakeAnvilOutputCallback.EVENT.register(this::handleTakeOutput);
    }

    private ActionResult tryReduceRepairCost(AnvilScreenHandler handler, PlayerEntity player, ItemStack left, ItemStack right, Inventory output, String name, int baseCost, TriConsumer<ItemStack, Integer, Integer> apply) {
        ItemStack out; // this will be the tool/armor with reduced repair cost

        if (left.isEmpty() || right.isEmpty())
            return ActionResult.PASS; // if both the input and middle items are empty, do nothing

        if (right.getItem() != Items.AMETHYST_SHARD)
            return ActionResult.PASS; // if the middle item is not an amethyst shard, do nothing

        if (left.getRepairCost() == 0)
            return ActionResult.PASS; // if the input item does not need repairing, do nothing

        // get the repair cost from the input item
        int cost = left.getRepairCost();

        // copy the input item to the output item and reduce the repair cost by the amount in the config
        out = left.copy();
        out.setRepairCost(Math.max(0, cost - decreaseAmount));

        // apply the stuff to the anvil
        apply.accept(out, xpCost, 1); // item to output, the xp cost of this operation, and the amount of shards used.

        return ActionResult.SUCCESS;
    }

    private void handleTakeOutput(AnvilScreenHandler handler, PlayerEntity player, ItemStack stack) {
        if (!player.world.isClient) {
            Inventory input = ((ForgingScreenHandlerAccessor) handler).getInput();
            if (!input.isEmpty()
                && !input.getStack(0).isEmpty()
                && input.getStack(0).getRepairCost() > stack.getRepairCost()
            ) {
                triggerDecreasedCost((ServerPlayerEntity) player);
            }
        }
    }

    public static void triggerDecreasedCost(ServerPlayerEntity player) {
        CharmAdvancements.ACTION_PERFORMED.trigger(player, TRIGGER_DECREASED_COST);
    }
}