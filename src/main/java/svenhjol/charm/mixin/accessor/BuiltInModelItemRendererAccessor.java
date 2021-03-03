package svenhjol.charm.mixin.accessor;

import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BuiltinModelItemRenderer.class)
public interface BuiltInModelItemRendererAccessor {
    @Accessor
    ShieldEntityModel getModelShield();

    @Accessor
    TridentEntityModel getModelTrident();
}
