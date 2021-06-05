package svenhjol.charm.module.extra_boats;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.util.Identifier;
import svenhjol.charm.Charm;
import svenhjol.charm.module.CharmClientModule;
import svenhjol.charm.module.CharmModule;
import svenhjol.charm.helper.EntityHelper;

public class ExtraBoatsClient extends CharmClientModule {
    public ExtraBoatsClient(CharmModule module) {
        super(module);
    }

    @Override
    public void register() {
        // nether-wood boats
        for (String s : new String[]{"crimson", "warped"}) {
            EntityHelper.registerEntityModelLayer(new Identifier(Charm.MOD_ID, "boat/" + s), BoatEntityModel.getTexturedModelData().createModel());
        }

        // register charm boats renderer
        EntityRendererRegistry.INSTANCE.register(ExtraBoats.CHARM_BOAT, CharmBoatEntityRenderer::new);
    }
}