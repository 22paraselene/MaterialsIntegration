package materials_integration;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class SortingWorkbenchRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Item input;
    private final TagKey<Item> outputTag;

    public SortingWorkbenchRecipe(ResourceLocation id, Item input, TagKey<Item> outputTag) {
        this.id = id;
        this.input = input;
        this.outputTag = outputTag;
    }

    @Override
    public boolean matches(Container container, Level level) {
        ItemStack inputStack = container.getItem(0);
        return !inputStack.isEmpty() && inputStack.getItem() == input;
    }

    @Override
    public ItemStack assemble(Container container, net.minecraft.core.RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(net.minecraft.core.RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SORTING_WORKBENCH.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.SORTING_WORKBENCH.get();
    }

    public Item getInput() {
        return input;
    }

    public TagKey<Item> getOutputTag() {
        return outputTag;
    }

    // 获取所有可能的输出物品
    public java.util.List<ItemStack> getOutputs() {
        java.util.List<ItemStack> outputs = new java.util.ArrayList<>();
        ForgeRegistries.ITEMS.tags().getTag(outputTag).forEach(item -> {
            outputs.add(new ItemStack(item));
        });
        return outputs;
    }

    // 序列化器
    public static class Serializer implements RecipeSerializer<SortingWorkbenchRecipe> {
        @Override
        public SortingWorkbenchRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            // 解析输入物品
            String inputItemStr = GsonHelper.getAsString(json, "input");
            ResourceLocation inputItemId = ResourceLocation.parse(inputItemStr);
            Item inputItem = ForgeRegistries.ITEMS.getValue(inputItemId);

            if (inputItem == null) {
                throw new IllegalStateException("Input item does not exist: " + inputItemStr);
            }

            // 解析输出标签
            String outputTagStr = GsonHelper.getAsString(json, "output_tag");
            ResourceLocation outputTagId = ResourceLocation.parse(outputTagStr);
            TagKey<Item> outputTag = TagKey.create(Registries.ITEM, outputTagId);

            return new SortingWorkbenchRecipe(recipeId, inputItem, outputTag);
        }

        @Override
        public SortingWorkbenchRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            // 从网络读取
            ResourceLocation inputItemId = buffer.readResourceLocation();
            ResourceLocation outputTagId = buffer.readResourceLocation();

            Item inputItem = ForgeRegistries.ITEMS.getValue(inputItemId);
            TagKey<Item> outputTag = TagKey.create(Registries.ITEM, outputTagId);

            return new SortingWorkbenchRecipe(recipeId, inputItem, outputTag);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SortingWorkbenchRecipe recipe) {
            // 写入网络
            buffer.writeResourceLocation(ForgeRegistries.ITEMS.getKey(recipe.getInput()));
            buffer.writeResourceLocation(recipe.getOutputTag().location());
        }
    }
}