package materials_integration.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import materials_integration.MaterialsIntegration;

@JeiPlugin
public class SortingWorkbenchJEIPlugin implements IModPlugin {

    private static final ResourceLocation UID = new ResourceLocation(MaterialsIntegration.MODID, "sorting_workbench");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new SortingWorkbenchRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // 获取当前世界的配方管理器
        assert Minecraft.getInstance().level != null;
        var recipeManager = Minecraft.getInstance().level.getRecipeManager();

        // 获取所有排序工作台配方
        var recipes = recipeManager.getAllRecipesFor(materials_integration.ModRecipeTypes.SORTING_WORKBENCH.get());

        // 转换为 JEI 可用的格式
        var jeiRecipes = recipes.stream()
                .map(SortingWorkbenchJEIRecipe::new)
                .toList();

        registration.addRecipes(SortingWorkbenchRecipeCategory.RECIPE_TYPE, jeiRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // 注册排序工作台本身作为配方催化剂
        registration.addRecipeCatalyst(new ItemStack(MaterialsIntegration.SORTING_WORKBENCH_ITEM.get()),
                SortingWorkbenchRecipeCategory.RECIPE_TYPE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        // 如果你有 GUI 需要处理，可以在这里注册
        // registration.addRecipeClickArea(SortingWorkbenchScreen.class, x, y, width, height, RECIPE_TYPE);
    }
}