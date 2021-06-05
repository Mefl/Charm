package svenhjol.charm.mixin.callback;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charm.event.SetupGuiCallback;

import java.util.List;

@Mixin(Screen.class)
public abstract class SetupGuiCallbackMixin {
    @Shadow
    @Final
    private List<Selectable> selectables;

    /**
     * Fires the {@link SetupGuiCallback} event.
     *
     * Simulates Forge's InitGuiEvent.Post.
     *
     * We can access existing GUI buttons and add new ones as required
     * using the SetupGuiCallback invoked in this hook.
     */
    @Inject(
        method = "init(Lnet/minecraft/client/MinecraftClient;II)V",
        at = @At("RETURN")
    )
    private void hookConstructor(MinecraftClient client, int width, int height, CallbackInfo ci) {
        SetupGuiCallback.EVENT.invoker().interact(client, width, height, this.selectables);
    }
}