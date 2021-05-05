package svenhjol.charm.module;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.AdvancementHandler;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.block.SugarBlock;
import svenhjol.charm.init.CharmAdvancements;

@Module(mod = Charm.MOD_ID, description = "A storage block for sugar. It obeys gravity and dissolves in water.")
public class BlockOfSugar extends CharmModule {
    public static SugarBlock SUGAR_BLOCK;

    public static final Identifier TRIGGER_DISSOLVED_SUGAR = new Identifier(Charm.MOD_ID, "dissolved_sugar");

    @Override
    public void register() {
        SUGAR_BLOCK = new SugarBlock(this);
    }

    public static void triggerAdvancementForNearbyPlayers(ServerWorld world, BlockPos pos) {
        AdvancementHandler.getPlayersInRange(world, pos).forEach(player -> {
            CharmAdvancements.ACTION_PERFORMED.trigger((ServerPlayerEntity)player, TRIGGER_DISSOLVED_SUGAR);
        });
    }
}
