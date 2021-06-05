package svenhjol.charm.module.more_village_biomes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import svenhjol.charm.Charm;
import svenhjol.charm.event.AddEntityCallback;
import svenhjol.charm.module.CharmModule;
import svenhjol.charm.helper.BiomeHelper;
import svenhjol.charm.annotation.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Module(mod = Charm.MOD_ID, description = "Villages can spawn in swamps and jungles.",
    requiresMixins = {"AddEntityCallback"})
public class MoreVillageBiomes extends CharmModule {
    @Override
    public void init() {
        List<RegistryKey<Biome>> plainsBiomeKeys = new ArrayList<>(Arrays.asList(
            BiomeKeys.JUNGLE, BiomeKeys.BAMBOO_JUNGLE, BiomeKeys.SWAMP
        ));

        List<RegistryKey<Biome>> taigaBiomeKeys = new ArrayList<>(Arrays.asList(
            BiomeKeys.SNOWY_TAIGA
        ));

        List<RegistryKey<Biome>> snowyBiomeKeys = new ArrayList<>(Arrays.asList(
            BiomeKeys.ICE_SPIKES
        ));

        for (RegistryKey<Biome> biomeKey : plainsBiomeKeys) {
            BiomeHelper.addStructureToBiome(ConfiguredStructureFeatures.VILLAGE_PLAINS, biomeKey);
        }

        for (RegistryKey<Biome> biomeKey : taigaBiomeKeys) {
            BiomeHelper.addStructureToBiome(ConfiguredStructureFeatures.VILLAGE_TAIGA, biomeKey);
        }

        for (RegistryKey<Biome> biomeKey : snowyBiomeKeys) {
            BiomeHelper.addStructureToBiome(ConfiguredStructureFeatures.VILLAGE_SNOWY, biomeKey);
        }

        AddEntityCallback.EVENT.register(this::changeVillagerSkin);
    }

    private ActionResult changeVillagerSkin(Entity entity) {
        if (!entity.world.isClient
            && entity instanceof VillagerEntity
            && entity.age == 0
        ) {
            VillagerEntity villager = (VillagerEntity) entity;
            VillagerData data = villager.getVillagerData();
            ServerWorld world = (ServerWorld)entity.world;

            if (data.getType() == VillagerType.PLAINS) {
                Biome biome = BiomeHelper.getBiome(world, villager.getBlockPos());
                Biome.Category category = biome.getCategory();

                if (category.equals(Biome.Category.JUNGLE) || category.equals(Biome.Category.SWAMP))
                    villager.setVillagerData(data.withType(VillagerType.forBiome(BiomeHelper.getBiomeKeyAtPosition(world, villager.getBlockPos()))));
            }
        }

        return ActionResult.PASS;
    }
}