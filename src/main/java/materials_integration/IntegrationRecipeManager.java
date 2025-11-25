package materials_integration;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class IntegrationRecipeManager {
    private static final Map<String, List<ItemStack>> TAG_TO_OUTPUTS = new HashMap<>();
    private static final Map<Item, String> ITEM_TO_TAG = new HashMap<>();

    public static void initialize() {
        // 初始化标签到输出的映射
        initializeTagMapping(MaterialsIntegration.DIRT_DROP, MaterialsIntegration.DIRT_INTEGRATION.get());
        initializeTagMapping(MaterialsIntegration.LOG_DROP, MaterialsIntegration.LOG_INTEGRATION.get());
        initializeTagMapping(MaterialsIntegration.PLANKS_DROP, MaterialsIntegration.PLANKS_INTEGRATION.get());
        initializeTagMapping(MaterialsIntegration.STONE_DROP, MaterialsIntegration.STONE_INTEGRATION.get());
        initializeTagMapping(MaterialsIntegration.SAND_DROP, MaterialsIntegration.SAND_INTEGRATION.get());
        initializeTagMapping(MaterialsIntegration.ROCK_DROP, MaterialsIntegration.ROCK_INTEGRATION.get());

        // 构建物品到标签的反向映射
        buildReverseMapping();
    }

    private static void initializeTagMapping(String tagName, Item integrationItem) {
        TagKey<Item> tag = TagKey.create(Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath(MaterialsIntegration.MODID, tagName));

        List<ItemStack> outputs = new ArrayList<>();
        outputs.add(new ItemStack(integrationItem));

        // 可以在这里添加更多相关的输出物品
        TAG_TO_OUTPUTS.put(tagName, outputs);
    }

    private static void buildReverseMapping() {
        for (String tagName : TAG_TO_OUTPUTS.keySet()) {
            TagKey<Item> tag = TagKey.create(Registries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(MaterialsIntegration.MODID, tagName));

            ForgeRegistries.ITEMS.getValues().forEach(item -> {
                if (ForgeRegistries.ITEMS.getHolder(ForgeRegistries.ITEMS.getResourceKey(item).orElse(null))
                        .map(holder -> holder.is(tag))
                        .orElse(false)) {
                    ITEM_TO_TAG.put(item, tagName);
                }
            });
        }
    }

    public static boolean hasRecipeFor(ItemStack input) {
        return ITEM_TO_TAG.containsKey(input.getItem());
    }

    public static List<ItemStack> getRecipesFor(ItemStack input) {
        String tagName = ITEM_TO_TAG.get(input.getItem());
        if (tagName != null) {
            return new ArrayList<>(TAG_TO_OUTPUTS.get(tagName));
        }
        return Collections.emptyList();
    }
}