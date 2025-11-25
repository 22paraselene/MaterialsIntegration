package materials_integration;

import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MaterialsIntegration.MODID);

    public static final RegistryObject<RecipeType<SortingWorkbenchRecipe>> SORTING_WORKBENCH =
            RECIPE_TYPES.register("sorting_workbench", () -> new RecipeType<SortingWorkbenchRecipe>() {
                @Override
                public String toString() {
                    return "materials_integration:sorting_workbench";
                }
            });
}