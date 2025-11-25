package materials_integration;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;

public class SortingWorkbenchRecipeManager {

    public static boolean hasRecipeFor(ItemStack input) {
        if (input.isEmpty()) return false;

        Level level = getLevel();
        if (level == null) return false;

        return !getRecipesFor(input).isEmpty();
    }

    public static List<ItemStack> getRecipesFor(ItemStack input) {
        List<ItemStack> results = new ArrayList<>();
        if (input.isEmpty()) return results;

        Level level = getLevel();
        if (level == null) return results;

        level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.SORTING_WORKBENCH.get())
                .stream()
                .filter(recipe -> recipe.matches(new SimpleContainer(input), level))
                .flatMap(recipe -> recipe.getOutputs().stream())
                .forEach(results::add);

        return results;
    }

    private static Level getLevel() {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            return ServerLifecycleHooks.getCurrentServer().overworld();
        }
        return null;
    }
}