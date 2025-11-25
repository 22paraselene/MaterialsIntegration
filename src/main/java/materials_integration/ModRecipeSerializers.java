package materials_integration;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MaterialsIntegration.MODID);

    public static final RegistryObject<RecipeSerializer<SortingWorkbenchRecipe>> SORTING_WORKBENCH =
            RECIPE_SERIALIZERS.register("sorting_workbench", () -> new SortingWorkbenchRecipe.Serializer());
}