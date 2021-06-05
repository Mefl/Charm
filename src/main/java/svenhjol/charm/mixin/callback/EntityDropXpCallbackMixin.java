package svenhjol.charm.mixin.callback;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charm.event.EntityDropXpCallback;

@Mixin(LivingEntity.class)
public class EntityDropXpCallbackMixin {

    /**
     * Fires the {@link EntityDropXpCallback} event before entity has dropped XP.
     *
     * Cancellable with ActionResult != PASS.
     */
    @Inject(
        method = "dropXp",
        at = @At("HEAD"),
        cancellable = true
    )
    private void hookDropXp(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity)(Object)this;
        ActionResult result = EntityDropXpCallback.BEFORE.invoker().interact(entity);
        if (result != ActionResult.PASS)
            ci.cancel();
    }
}