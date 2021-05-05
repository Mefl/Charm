package svenhjol.charm.mixin;

import net.minecraft.client.gui.screen.ingame.CartographyTableScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import svenhjol.charm.client.AtlasesClient;

@Mixin(CartographyTableScreen.class)
public class CartographyTableScreenMixin {
    @ModifyArg(
        method = "drawBackground",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/ingame/CartographyTableScreen;drawMap(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/Integer;Lnet/minecraft/item/map/MapState;ZZZZ)V"
        ),
        index = 3
    )
    private boolean hookDrawBackground(boolean value) {
        if (AtlasesClient.shouldDrawAtlasCopy((CartographyTableScreen) (Object) this)) {
            return true;
        }
        return value;
    }
}
