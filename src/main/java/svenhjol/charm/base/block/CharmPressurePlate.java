package svenhjol.charm.base.block;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.sound.BlockSoundGroup;
import svenhjol.charm.base.CharmModule;

public class CharmPressurePlate extends PressurePlateBlock implements ICharmBlock {
    private final CharmModule module;

    public CharmPressurePlate(CharmModule module, String name, PressurePlateBlock.ActivationRule activationRule, Settings settings) {
        super(activationRule, settings);

        this.register(module, name);
        this.module = module;
    }

    public CharmPressurePlate(CharmModule module, String name, Block block) {
        this(module, name, ActivationRule.EVERYTHING, Settings.of(Material.WOOD, block.getDefaultMapColor())
            .noCollision()
            .strength(0.5F)
            .sounds(BlockSoundGroup.WOOD));
    }

    @Override
    public boolean enabled() {
        return module.enabled;
    }
}
