package svenhjol.charm.base.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SignItem;
import svenhjol.charm.base.CharmModule;

public class CharmSignItem extends SignItem implements ICharmItem {
    private final CharmModule module;

    public CharmSignItem(CharmModule module, String name, Block standingBlock, Block wallBlock, Settings settings) {
        super(settings, standingBlock, wallBlock);

        this.register(module, name);
        this.module = module;
    }

    public CharmSignItem(CharmModule module, String name, Block standingBlock, Block wallBlock) {
        this(module, name, standingBlock, wallBlock, new Item.Settings()
            .maxCount(16)
            .group(ItemGroup.DECORATIONS));
    }

    @Override
    public boolean enabled() {
        return module.enabled;
    }
}
