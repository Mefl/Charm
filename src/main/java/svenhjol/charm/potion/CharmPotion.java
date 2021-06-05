package svenhjol.charm.potion;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import svenhjol.charm.module.CharmModule;
import svenhjol.charm.helper.RegistryHelper;

public abstract class CharmPotion extends Potion implements ICharmPotion {
    protected CharmModule module;

    public CharmPotion(CharmModule module, String name, StatusEffectInstance... effects) {
        super(effects);
        this.setModuleAndName(module, name);
    }

    public CharmPotion(CharmModule module, String name, String basedOn, StatusEffectInstance... effects) {
        super(basedOn, effects);
        this.setModuleAndName(module, name);
    }

    protected void setModuleAndName(CharmModule module, String name) {
        this.module = module;
        this.register(module, name);
    }

    protected void registerRecipe(Potion input, Item reagant) {
        RegistryHelper.brewingRecipe(input, reagant, this);
    }
}