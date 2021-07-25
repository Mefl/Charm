package svenhjol.charm.mixin.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charm.event.TakeAnvilOutputEvent;

@Mixin(AnvilMenu.class)
public class TakeAnvilOutputEventMixin {
    /**
     * Fires the {@link TakeAnvilOutputEvent} event when an item is taken from the anvil.
     * This event is useful for anvil-related advancements.
     */
    @Inject(
        method = "onTake",
        at = @At("HEAD")
    )
    private void hookOnTakeOutput(Player player, ItemStack stack, CallbackInfo ci) {
        TakeAnvilOutputEvent.EVENT.invoker().interact((AnvilMenu)(Object)this, player, stack);
    }
}