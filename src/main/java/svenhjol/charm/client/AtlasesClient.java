package svenhjol.charm.client;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CartographyTableScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.helper.PlayerHelper;
import svenhjol.charm.event.RenderHeldItemCallback;
import svenhjol.charm.gui.AtlasScreen;
import svenhjol.charm.module.Atlases;
import svenhjol.charm.render.AtlasRenderer;
import svenhjol.charm.screenhandler.AtlasInventory;

import java.util.List;

public class AtlasesClient extends CharmClientModule {
    private AtlasRenderer renderer;

    public AtlasesClient(CharmModule module) {
        super(module);
    }

    @Override
    public void register() {
        ScreenRegistry.register(Atlases.CONTAINER, AtlasScreen::new);
    }

    @Override
    public void init() {
        RenderHeldItemCallback.EVENT.register(this::handleRenderItem);
        ItemTooltipCallback.EVENT.register(this::handleItemTooltip);
        ClientPlayNetworking.registerGlobalReceiver(Atlases.MSG_CLIENT_UPDATE_ATLAS_INVENTORY, this::handleClientUpdateAtlas);
    }

    public void handleClientUpdateAtlas(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf data, PacketSender sender) {
        int atlasSlot = data.readInt();
        client.execute(() -> updateInventory(atlasSlot));
    }

    public ActionResult handleRenderItem(float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack itemStack, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (itemStack.getItem() == Atlases.ATLAS_ITEM) {
            if (renderer == null) {
                renderer = new AtlasRenderer();
            }
            renderer.renderAtlas(matrices, vertexConsumers, light, hand, equipProgress, swingProgress, itemStack);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    public void handleItemTooltip(ItemStack stack, TooltipContext context, List<Text> lines) {
        if (stack == null || stack.isEmpty() || stack.getItem() != Atlases.ATLAS_ITEM)
            return;

        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null)
            return;

        AtlasInventory inventory = Atlases.getInventory(player.world, stack);

        ItemStack map = inventory.getLastActiveMapItem();
        if (map == null)
            return;

        lines.add(new LiteralText("Scale " + inventory.getScale()).formatted(Formatting.GRAY));

        MutableText name = map.hasCustomName() ? map.getName().copy()
            : map.getName().copy().append(new LiteralText(" #" + FilledMapItem.getMapId(map)));
        lines.add(name.formatted(Formatting.GRAY, Formatting.ITALIC));
    }

    public static void updateInventory(int atlasSlot) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null) return;
        ItemStack atlas = PlayerHelper.getInventory(player).getStack(atlasSlot);
        Atlases.getInventory(mc.world, atlas).reload(atlas);
    }

    public static boolean shouldDrawAtlasCopy(CartographyTableScreen screen) {
        return ModuleHandler.enabled(Atlases.class) && screen.getScreenHandler().getSlot(0).getStack().getItem() == Atlases.ATLAS_ITEM
            && screen.getScreenHandler().getSlot(1).getStack().getItem() == Items.MAP;
    }
}

