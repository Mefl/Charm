package svenhjol.charm.module;

import com.mojang.brigadier.StringReader;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.init.CharmAdvancements;

import java.util.ArrayList;
import java.util.List;

@Module(mod = Charm.MOD_ID, description = "Right-click with a hoe to quickly harvest and replant a fully-grown crop.")
public class HoeHarvesting extends CharmModule {
    public static final Identifier TRIGGER_REPLANTED_CROPS = new Identifier(Charm.MOD_ID, "replanted_crops");

    private static final List<BlockState> harvestable = new ArrayList<>();

    @Override
    public void init() {
        addHarvestable("minecraft:beetroots[age=3]");
        addHarvestable("minecraft:carrots[age=7]");
        addHarvestable("minecraft:nether_wart[age=3]");
        addHarvestable("minecraft:potatoes[age=7]");
        addHarvestable("minecraft:wheat[age=7]");

        UseBlockCallback.EVENT.register(this::tryHarvest);
    }

    public ActionResult tryHarvest(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        // event is broken in fabric? hand is always mainhand
        ItemStack mainhand = player.getMainHandStack();
        ItemStack offhand = player.getOffHandStack();
        ItemStack held;

        if (mainhand.getItem() instanceof HoeItem) {
            held = mainhand;
        } else if (offhand.getItem() instanceof HoeItem) {
            held = offhand;
        } else {
            held = null;
        }

        if (held != null) {
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if (!harvestable.contains(state))
                return ActionResult.PASS;

            Item blockItem = block.asItem();
            BlockState newState = block.getDefaultState();

            if (!world.isClient) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity)player;
                ServerWorld serverWorld = (ServerWorld)serverPlayer.world;

                List<ItemStack> drops = Block.getDroppedStacks(state, serverWorld, pos, null, player, ItemStack.EMPTY);
                for (ItemStack drop : drops) {
                    if (drop.getItem() == blockItem)
                        drop.decrement(1);

                    if (!drop.isEmpty())
                        Block.dropStack(world, pos, drop);
                }

                world.syncGlobalEvent(2001, pos, Block.getRawIdFromState(newState));
                world.setBlockState(pos, newState);
                world.playSound(null, pos, SoundEvents.BLOCK_CROP_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);

                triggerReplantedCrops(serverPlayer);

                // damage the hoe a bit
                held.damage(1, player, p -> p.swingHand(hand));

                return ActionResult.CONSUME;
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    public static void addHarvestable(String blockState) {
        BlockState state;

        try {
            BlockArgumentParser parser = new BlockArgumentParser(new StringReader(blockState), false).parse(false);
            state = parser.getBlockState();
        } catch (Exception e) {
            state = null;
        }

        if (state == null)
            state = Blocks.AIR.getDefaultState();

        harvestable.add(state);
    }

    public static void triggerReplantedCrops(ServerPlayerEntity player) {
        CharmAdvancements.ACTION_PERFORMED.trigger(player, TRIGGER_REPLANTED_CROPS);
    }
}
