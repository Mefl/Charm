package svenhjol.charm.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.event.*;
import svenhjol.charm.module.*;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract Iterable<ItemStack> getArmorItems();

    @Shadow @Final private static TrackedData<Integer> POTION_SWIRLS_COLOR;

    @Redirect(
        method = "tryUseTotem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"
        )
    )
    private ItemStack hookTryUseTotem(LivingEntity livingEntity, Hand hand) {
        return UseTotemFromInventory.tryFromInventory(livingEntity, hand);
    }

    @Inject(
        method = "getArmorVisibility",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    private void hookArmorCover(CallbackInfoReturnable<Float> cir) {
        if (ModuleHandler.enabled("charm:armor_invisibility")) {
            LivingEntity entity = (LivingEntity) (Object) this;
            Iterable<ItemStack> armorItems = this.getArmorItems();

            int i = 0;
            int j = 0;

            for (ItemStack itemstack : armorItems) {
                if (!ArmorInvisibility.shouldArmorBeInvisible(entity, itemstack)) {
                    ++j;
                }
                ++i;
            }

            cir.setReturnValue(i > 0 ? (float)j / (float)i : 0.0F);
        }
    }

    /**
     * Checks trapdoor ladder is a variant ladder when player is climbing.
     * {@link VariantLadders#canEnterTrapdoor(World, BlockPos, BlockState)}
     */
    @Inject(
        method = "canEnterTrapdoor",
        at = @At("HEAD"),
        cancellable = true
    )
    private void hookCanEnterTrapdoor(BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (VariantLadders.canEnterTrapdoor(this.world, pos, state))
            cir.setReturnValue(true);
    }

    /**
     * Hooks after entity items have been dropped.
     * @param source
     * @param ci
     */
    @Inject(
        method = "drop",
        at = @At("TAIL")
    )
    private void hookDrop(DamageSource source, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity)(Object)this;
        int lootingLevel = EnchantmentHelper.getLooting(entity);

        EntityDropsCallback.AFTER.invoker().interact(entity, source, lootingLevel);
    }

    /**
     * Hooks before entity has dropped XP. Cancellable with actionresult != PASS.
     * @param ci
     */
    @Inject(
        method = "dropXp",
        at = @At("HEAD"),
        cancellable = true
    )
    private void hookDropXp(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity)(Object)this;
        ActionResult result = EntityDropsXpCallback.BEFORE.invoker().interact(entity);
        if (result != ActionResult.PASS)
            ci.cancel();
    }

    @Inject(
        method = "applyDamage",
        at = @At("HEAD"),
        cancellable = true
    )
    private void hookApplyDamage(DamageSource source, float amount, CallbackInfo ci) {
        ActionResult result = HurtEntityCallback.EVENT.invoker().interact((LivingEntity) (Object) this, source, amount);
        if (result == ActionResult.FAIL)
            ci.cancel();
    }

    @Inject(
        method = "onDeath",
        at = @At("HEAD")
    )
    private void hookOnDeath(DamageSource source, CallbackInfo ci) {
        EntityDeathCallback.EVENT.invoker().interact((LivingEntity)(Object)this, source);
    }

    @Inject(
        method = "jump",
        at = @At("TAIL")
    )
    private void hookJump(CallbackInfo ci) {
        EntityJumpCallback.EVENT.invoker().interact((LivingEntity)(Object)this);
    }

    @Inject(
        method = "method_30129",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
            shift = At.Shift.BEFORE
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void hookGetEquippedItems(CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir,
        Map<EquipmentSlot, ItemStack> map, EquipmentSlot[] var2, int var3, int var4,
        EquipmentSlot equipmentSlot, ItemStack itemStack3, ItemStack itemStack4
    ) {
        EntityEquipCallback.EVENT.invoker().interact((LivingEntity)(Object)this, equipmentSlot, itemStack3, itemStack4);
    }

    @Redirect(
        method = "tickStatusEffects",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"
        )
    )
    private void hookTickStatusEffects(World world, ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        boolean result = GentlePotionParticles.tryRenderParticles(world, x, y, z, velocityX, velocityY, velocityZ);
        if (!result)
            world.addParticle(parameters, x, y, z, velocityX, velocityY, velocityZ); // vanilla behavior
    }

    /**
     * After removing frozen ticks, check if environmental conditions are correct for adding frozen ticks.
     * This is handled by the Snowstorms module.
     */
    @Inject(
        method = "tickMovement",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;setFrozenTicks(I)V",
            shift = At.Shift.AFTER,
            ordinal = 1
        )
    )
    private void hookTickMovementSetFrozenTicks(CallbackInfo ci) {
        if (SnowStorms.shouldFreezeEntity((LivingEntity)(Object)this))
            this.setFrozenTicks(Math.min(this.getMinFreezeDamageTicks(), this.getFrozenTicks() + 3));
    }
}
