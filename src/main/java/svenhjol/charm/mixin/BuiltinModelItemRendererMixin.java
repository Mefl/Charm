package svenhjol.charm.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charm.event.BlockItemRenderCallback;
import svenhjol.charm.event.ModelItemRenderCallback;
import svenhjol.charm.handler.ColoredGlintHandler;

@Mixin(BuiltinModelItemRenderer.class)
public class BuiltinModelItemRendererMixin {
    @Shadow @Final private BlockEntityRenderDispatcher blockEntityRenderDispatcher;

    /**
     * Allows modules to define their own blockItem entity or item model renderers.
     */
    @Inject(
        method = "render",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    private void hookRender(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int light, int overlay, CallbackInfo ci) {
        ColoredGlintHandler.targetStack = stack; // take reference to item to be rendered

        Item item = stack.getItem();
        if (item instanceof BlockItem) {
            BlockEntity blockEntity = BlockItemRenderCallback.EVENT.invoker().interact(((BlockItem) item).getBlock());

            if (blockEntity != null) {
                this.blockEntityRenderDispatcher.renderEntity(blockEntity, matrices, vertexConsumerProvider, light, overlay);
                ci.cancel();
            }
        } else {
            boolean result = ModelItemRenderCallback.EVENT.invoker().interact((BuiltinModelItemRenderer)(Object)this, matrices, stack, vertexConsumerProvider, light, overlay);

            if (result) {
                ci.cancel();
            }
        }
    }
}
