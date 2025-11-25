package materials_integration.compat.jei;

import materials_integration.SortingWorkbenchRecipe;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class SortingWorkbenchJEIRecipe {
    private final SortingWorkbenchRecipe recipe;

    public SortingWorkbenchJEIRecipe(SortingWorkbenchRecipe recipe) {
        this.recipe = recipe;
    }

    public ItemStack getInputStack() {
        return new ItemStack(recipe.getInput());
    }

    public List<ItemStack> getOutputStacks() {
        return recipe.getOutputs();
    }

    public SortingWorkbenchRecipe getOriginalRecipe() {
        return recipe;
    }
}