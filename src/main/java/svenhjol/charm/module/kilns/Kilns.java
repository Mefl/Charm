package svenhjol.charm.module.kilns;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import svenhjol.charm.Charm;
import svenhjol.charm.module.CharmModule;
import svenhjol.charm.helper.RegistryHelper;
import svenhjol.charm.helper.DecorationHelper;
import svenhjol.charm.annotation.Module;
import svenhjol.charm.init.CharmAdvancements;

@Module(mod = Charm.MOD_ID, client = KilnsClient.class, description = "A functional block that speeds up cooking of clay, glass, bricks and terracotta.")
public class Kilns extends CharmModule {
    public static final Identifier RECIPE_ID = new Identifier(Charm.MOD_ID, "firing");
    public static final Identifier BLOCK_ID = new Identifier(Charm.MOD_ID, "kiln");
    public static final Identifier TRIGGER_FIRED_ITEM = new Identifier(Charm.MOD_ID, "fired_item");

    public static KilnBlock KILN;
    public static BlockEntityType<KilnBlockEntity> BLOCK_ENTITY;
    public static RecipeType<FiringRecipe> RECIPE_TYPE;
    public static CookingRecipeSerializer<FiringRecipe> RECIPE_SERIALIZER;
    public static ScreenHandlerType<KilnScreenHandler> SCREEN_HANDLER;

    @Override
    public void register() {
        KILN = new KilnBlock(this);
        RECIPE_TYPE = RegistryHelper.recipeType(RECIPE_ID.toString());
        RECIPE_SERIALIZER = RegistryHelper.recipeSerializer(RECIPE_ID.toString(), new CookingRecipeSerializer<>(FiringRecipe::new, 100));
        BLOCK_ENTITY = RegistryHelper.blockEntity(BLOCK_ID, KilnBlockEntity::new, KILN);
        SCREEN_HANDLER = RegistryHelper.screenHandler(BLOCK_ID, KilnScreenHandler::new);
    }

    @Override
    public void init() {
        DecorationHelper.DECORATION_BLOCKS.add(KILN);
        DecorationHelper.STATE_CALLBACK.put(KILN, facing -> KILN.getDefaultState().with(KilnBlock.FACING, facing));
    }

    public static void triggerFiredItem(ServerPlayerEntity player) {
        CharmAdvancements.ACTION_PERFORMED.trigger(player, TRIGGER_FIRED_ITEM);
    }
}