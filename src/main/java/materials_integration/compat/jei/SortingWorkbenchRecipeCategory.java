package materials_integration.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import materials_integration.MaterialsIntegration;


public class SortingWorkbenchRecipeCategory implements IRecipeCategory<SortingWorkbenchJEIRecipe> {

    public static final RecipeType<SortingWorkbenchJEIRecipe> RECIPE_TYPE =
            RecipeType.create(MaterialsIntegration.MODID, "sorting_workbench", SortingWorkbenchJEIRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public SortingWorkbenchRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(160, 250);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(MaterialsIntegration.SORTING_WORKBENCH_ITEM.get()));
        this.title = Component.translatable("jei.category." + MaterialsIntegration.MODID + ".sorting_workbench");
    }

    @Override
    public RecipeType<SortingWorkbenchJEIRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SortingWorkbenchJEIRecipe recipe, IFocusGroup focuses) {
        // 添加输入槽位
        builder.addSlot(mezz.jei.api.recipe.RecipeIngredientRole.INPUT, 5, 10)
                .addItemStack(recipe.getInputStack());

        // 添加输出槽位（所有可能的输出）
        var outputs = recipe.getOutputStacks();
        for (int i = 0; i < outputs.size(); i++) {
            int x = 30 + (i % 7) * 18;
            int y = 3 + (i / 7) * 18;
            builder.addSlot(mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT, x, y)
                    .addItemStack(outputs.get(i));
        }
    }
}