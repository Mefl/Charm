package svenhjol.charm.module;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import svenhjol.charm.Charm;
import svenhjol.charm.client.PortableCraftingClient;
import svenhjol.charm.screenhandler.PortableCraftingScreenHandler;
import svenhjol.meson.MesonModule;
import svenhjol.meson.iface.Module;

@Module(description = "Allows crafting from inventory if the player has a crafting table in their inventory.")
public class PortableCrafting extends MesonModule {
    private static final Text LABEL = new TranslatableText("container.charm.portable_crafting_table");
    public static final Identifier MSG_SERVER_OPEN_CRAFTING = new Identifier(Charm.MOD_ID, "server_open_crafting");
    public static PortableCraftingClient client;

    @Override
    public void init() {
        // listen for network requests to open the portable ender chest
        ServerSidePacketRegistry.INSTANCE.register(MSG_SERVER_OPEN_CRAFTING, (context, data) -> {
            context.getTaskQueue().execute(() -> {
                ServerPlayerEntity player = (ServerPlayerEntity)context.getPlayer();
                if (player == null || !player.inventory.contains(new ItemStack(Blocks.CRAFTING_TABLE)))
                    return;

                PortableCrafting.openContainer(player);
            });
        });
    }

    @Override
    public void initClient() {
        client = new PortableCraftingClient(this);
    }

    public static void openContainer(ServerPlayerEntity player) {
        player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, inv, p) -> new PortableCraftingScreenHandler(i, inv, ScreenHandlerContext.create(p.world, p.getBlockPos())), LABEL));
    }
}
