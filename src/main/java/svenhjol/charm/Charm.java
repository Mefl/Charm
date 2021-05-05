package svenhjol.charm;

import net.fabricmc.api.ModInitializer;
import svenhjol.charm.base.*;
import svenhjol.charm.base.handler.LogHandler;
import svenhjol.charm.init.*;
import svenhjol.charm.module.*;

import java.util.Arrays;

public class Charm implements ModInitializer {
    public static final String MOD_ID = "charm";
    public static LogHandler LOG = new LogHandler("Charm");

    private static boolean hasRunFirst = false;

    public static void runFirst() {
        if (hasRunFirst)
            return;

        new CharmLoader(MOD_ID, Arrays.asList(
            Acquisition.class,
            AerialAffinity.class,
            AnvilImprovements.class,
            ArmorInvisibility.class,
            Astrolabes.class,
            Atlases.class,
            AutomaticRecipeUnlock.class,
            AutoRestock.class,
            BatBuckets.class,
            BeaconsHealMobs.class,
            Beekeepers.class,
            BiomeDungeons.class,
            BlockOfEnderPearls.class,
            BlockOfGunpowder.class,
            BlockOfSugar.class,
            Bookcases.class,
            Bumblezone.class,
            CampfiresNoDamage.class,
            Casks.class,
            CaveSpidersDropCobwebs.class,
            VariantChains.class,
            ChickensDropFeathers.class,
            ClearItemFrames.class,
            ColoredBundles.class,
            ColoredGlints.class,
            CookingPots.class,
            CopperRails.class,
            ExtraNuggets.class,
            CoralSeaLanterns.class,
            CoralSquids.class,
            Core.class,
            DecreaseRepairCost.class,
            DirtToPath.class,
            EbonyWood.class,
            EditableSigns.class,
            EnderBundles.class,
            EndermitePowder.class,
            EntitySpawners.class,
            ExtraBoats.class,
            ExtractEnchantments.class,
            ExtraRecipes.class,
            FeatherFallingCrops.class,
            GentlePotionParticles.class,
            Glowballs.class,
            VariantBars.class,
            HoeHarvesting.class,
            HuskImprovements.class,
            InventoryTidying.class,
            Kilns.class,
            VariantLanterns.class,
            Lumberjacks.class,
            MapTooltips.class,
            MineshaftImprovements.class,
            Mooblooms.class,
            MorePortalFrames.class,
            MoreVillageBiomes.class,
            MusicImprovements.class,
            ParrotsStayOnShoulder.class,
            PathToDirt.class,
            PlayerPressurePlates.class,
            PlayerState.class,
            PotionOfHogsbane.class,
            PotionOfSpelunking.class,
            PortableCrafting.class,
            Quadrants.class,
            RaidHorns.class,
            RedstoneLanterns.class,
            RedstoneSand.class,
            RemoveNitwits.class,
            RemovePotionGlint.class,
            RemoveSpyglassScope.class,
            ShulkerBoxTooltips.class,
            SnowStorms.class,
            StackableEnchantedBooks.class,
            StackablePotions.class,
            StackableStews.class,
            StrayImprovements.class,
            TamedAnimalsNoDamage.class,
            UseTotemFromInventory.class,
            VariantBarrels.class,
            VariantBookshelves.class,
            VariantChests.class,
            VariantLadders.class,
            VariantMobTextures.class,
            VillagersFollowEmeraldBlocks.class,
            WanderingTraderImprovements.class,
            WitchesDropLuck.class,
            Woodcutters.class
        ));

        CharmLoot.init();
        CharmParticles.init();
        CharmStructures.init();
        CharmSounds.init();
        CharmTags.init();
        CharmAdvancements.init();

        hasRunFirst = true;
    }

    @Override
    public void onInitialize() {
        runFirst();
    }
}
