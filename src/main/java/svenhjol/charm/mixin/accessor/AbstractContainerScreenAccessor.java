package svenhjol.charm.mixin.accessor;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import svenhjol.charm.annotation.CharmMixin;

@Mixin(AbstractContainerScreen.class)
@CharmMixin(required = true)
public interface AbstractContainerScreenAccessor {
    @Accessor("snapbackStartX")
    int getX();

    @Accessor("snapbackStartY")
    int getY();
}