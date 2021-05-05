package svenhjol.charm.base.helper;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.impl.biome.modification.BuiltInRegistryKeys;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import svenhjol.charm.Charm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@SuppressWarnings({"UnstableApiUsage", "unused", "deprecation"})
public class BiomeHelper {
    public static Map<Biome.Category, List<RegistryKey<Biome>>> BIOME_CATEGORY_MAP = new HashMap<>();

    public static Biome getBiome(ServerWorld world, BlockPos pos) {
        BiomeAccess biomeAccess = world.getBiomeAccess();
        return biomeAccess.getBiome(pos);
    }

    public static Biome getBiomeFromBiomeKey(RegistryKey<Biome> biomeKey) {
        return BuiltinRegistries.BIOME.get(biomeKey);
    }

    public static Optional<RegistryKey<Biome>> getBiomeKeyAtPosition(ServerWorld world, BlockPos pos) {
        return world.getBiomeKey(pos);
    }

    public static BlockPos locateBiome(RegistryKey<Biome> biomeKey, ServerWorld world, BlockPos pos) {
        Biome biome = world.getRegistryManager().get(Registry.BIOME_KEY).get(biomeKey);
        return locateBiome(biome, world, pos);
    }

    public static BlockPos locateBiome(Biome biome, ServerWorld world, BlockPos pos) {
        return world.locateBiome(biome, pos, 6400, 8);
    }

    public static void addFeatureToBiomeCategories(ConfiguredFeature<?, ?> feature, Biome.Category biomeCategory, GenerationStep.Feature generationStep) {
        List<RegistryKey<Biome>> biomeKeys = BIOME_CATEGORY_MAP.get(biomeCategory);
        biomeKeys.forEach(biomeKey -> BiomeHelper.addFeatureToBiome(feature, biomeKey, generationStep));
    }

    public static void addFeatureToBiome(ConfiguredFeature<?, ?> feature, RegistryKey<Biome> biomeKey, GenerationStep.Feature generationStep) {
        RegistryKey<ConfiguredFeature<?, ?>> featureKey;
        Predicate<BiomeSelectionContext> biomeSelector;

        try {
            biomeSelector = BiomeSelectors.includeByKey(biomeKey);
            featureKey = BuiltInRegistryKeys.get(feature);
        } catch (Exception e) {
            Charm.LOG.error("Failed to add feature to biome.");
            return;
        }

        BiomeModifications.addFeature(biomeSelector, generationStep, featureKey);
    }

    public static void addStructureToBiomeCategories(ConfiguredStructureFeature<?, ?> structureFeature, Biome.Category biomeCategory) {
        List<RegistryKey<Biome>> biomeKeys = BIOME_CATEGORY_MAP.get(biomeCategory);
        biomeKeys.forEach(biomeKey -> BiomeHelper.addStructureToBiome(structureFeature, biomeKey));
    }

    public static void addStructureToBiome(ConfiguredStructureFeature<?, ?> structureFeature, RegistryKey<Biome> biomeKey) {
        RegistryKey<ConfiguredStructureFeature<?, ?>> structureKey;
        Predicate<BiomeSelectionContext> biomeSelector;

        try {
            biomeSelector = BiomeSelectors.includeByKey(biomeKey);
            structureKey = BuiltInRegistryKeys.get(structureFeature);
        } catch (Exception e) {
            Charm.LOG.error("Failed to add structure to biome. This may cause crashes when trying to locate the structure.");
            return;
        }

        BiomeModifications.addStructure(biomeSelector, structureKey);
    }

    public static void addSpawnEntry(RegistryKey<Biome> biomeKey, SpawnGroup group, EntityType<?> entity, int weight, int minGroupSize, int maxGroupSize) {
        try {
            Predicate<BiomeSelectionContext> biomeSelector = BiomeSelectors.includeByKey(biomeKey);
            BiomeModifications.addSpawn(biomeSelector, group, entity, weight, minGroupSize, maxGroupSize);
        } catch (Exception e) {
            Charm.LOG.error("Failed to add entity to biome spawn. This may cause crashes when trying to spawn the entity.");
        }
    }
}
