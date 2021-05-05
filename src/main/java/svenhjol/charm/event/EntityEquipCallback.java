package svenhjol.charm.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public interface EntityEquipCallback {
    Event<EntityEquipCallback> EVENT = EventFactory.createArrayBacked(EntityEquipCallback.class, (listeners) -> (livingEntity, slot, from, to) -> {
        for (EntityEquipCallback listener : listeners) {
            listener.interact(livingEntity, slot, from, to);
        }
    });

    void interact(LivingEntity entity, EquipmentSlot slot, @Nullable ItemStack from, @Nullable ItemStack to);
}
