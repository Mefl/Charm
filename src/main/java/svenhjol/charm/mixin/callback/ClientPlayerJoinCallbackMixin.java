package svenhjol.charm.mixin.callback;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charm.event.ClientPlayerJoinCallback;

@Mixin(MinecraftClient.class)
public class ClientPlayerJoinCallbackMixin {

    /**
     * Fires the {@link ClientPlayerJoinCallback} event.
     *
     * This event is used by the Charm loader to initialize decorations
     * and colored glint handling. It can be used by any Charm client
     * module to perform its own init when a client player enters the world.
     */
    @Inject(
        method = "joinWorld",
        at = @At("RETURN")
    )
    private void hookJoinWorld(ClientWorld world, CallbackInfo ci) {
        ClientPlayerJoinCallback.EVENT.invoker().interact((MinecraftClient)(Object)this);
    }
}
