package svenhjol.charm.base.helper;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import svenhjol.charm.mixin.accessor.BlockEntityTypeAccessor;

import java.util.*;

public class RegistryHelper {
    public static void addBlocksToBlockEntity(BlockEntityType<?> type, Block... blocks) {
        Set<Block> typeBlocks = ((BlockEntityTypeAccessor) type).getBlocks();
        List<Block> mutable = new ArrayList<>(typeBlocks);

        for (Block block : blocks) {
            if (!mutable.contains(block))
                mutable.add(block);
        }

        ((BlockEntityTypeAccessor)type).setBlocks(new HashSet<>(mutable));
    }
}
