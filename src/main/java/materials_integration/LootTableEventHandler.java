package materials_integration;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = MaterialsIntegration.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LootTableEventHandler {

    private static boolean tagsLoaded = false;

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        // 确保 tag 已加载
        if (!tagsLoaded) {
            TagFileReader.loadAllTags();
            tagsLoaded = true;
        }

        ResourceLocation tableName = event.getName();

        // 只处理方块破坏的战利品表
        if (!tableName.getPath().startsWith("blocks/")) {
            return;
        }

        // 获取方块ID
        String blockPath = tableName.getPath().substring(7);
        ResourceLocation blockId = new ResourceLocation(tableName.getNamespace(), blockPath);
        Block block = ForgeRegistries.BLOCKS.getValue(blockId);

        if (block == null) {
            return;
        }

        // 检查方块是否在我们的tag中
        String dropType = getDropTypeForBlock(block);
        if (dropType != null) {
            replaceLootTable(event, block, dropType);
        }
    }

    // 获取方块的掉落类型
    private static String getDropTypeForBlock(Block block) {
        // 检查各种tag类型，按优先级顺序
        String[] tagTypes = {
                MaterialsIntegration.LOG_DROP + "_" + MaterialsIntegration.DROP_FOUR,
                MaterialsIntegration.LOG_DROP + "_" + MaterialsIntegration.DROP_TWO,
                MaterialsIntegration.LOG_DROP + "_" + MaterialsIntegration.DROP_CHANCE,
                MaterialsIntegration.LOG_DROP,

                MaterialsIntegration.PLANKS_DROP + "_" + MaterialsIntegration.DROP_FOUR,
                MaterialsIntegration.PLANKS_DROP + "_" + MaterialsIntegration.DROP_TWO,
                MaterialsIntegration.PLANKS_DROP + "_" + MaterialsIntegration.DROP_CHANCE,
                MaterialsIntegration.PLANKS_DROP,

                MaterialsIntegration.STONE_DROP + "_" + MaterialsIntegration.DROP_FOUR,
                MaterialsIntegration.STONE_DROP + "_" + MaterialsIntegration.DROP_TWO,
                MaterialsIntegration.STONE_DROP + "_" + MaterialsIntegration.DROP_CHANCE,
                MaterialsIntegration.STONE_DROP,

                MaterialsIntegration.ROCK_DROP + "_" + MaterialsIntegration.DROP_FOUR,
                MaterialsIntegration.ROCK_DROP + "_" + MaterialsIntegration.DROP_TWO,
                MaterialsIntegration.ROCK_DROP + "_" + MaterialsIntegration.DROP_CHANCE,
                MaterialsIntegration.ROCK_DROP,

                MaterialsIntegration.DIRT_DROP + "_" + MaterialsIntegration.DROP_FOUR,
                MaterialsIntegration.DIRT_DROP + "_" + MaterialsIntegration.DROP_TWO,
                MaterialsIntegration.DIRT_DROP + "_" + MaterialsIntegration.DROP_CHANCE,
                MaterialsIntegration.DIRT_DROP,

                MaterialsIntegration.SAND_DROP + "_" + MaterialsIntegration.DROP_FOUR,
                MaterialsIntegration.SAND_DROP + "_" + MaterialsIntegration.DROP_TWO,
                MaterialsIntegration.SAND_DROP + "_" + MaterialsIntegration.DROP_CHANCE,
                MaterialsIntegration.SAND_DROP
        };

        for (String tagName : tagTypes) {
            if (TagFileReader.isBlockInTag(block, tagName)) {
                // 解析掉落类型
                String[] parts = tagName.split("_");
                String resourceType = parts[0];
                String dropVariant = parts.length > 2 ? parts[2] : "normal";

                return resourceType + "_" + dropVariant;
            }
        }

        return null;
    }

    // 完全替换战利品表（不保留原版掉落）
    private static void replaceLootTable(LootTableLoadEvent event, Block block, String dropType) {
        // 解析掉落类型
        String[] parts = dropType.split("_");
        String resourceType = parts[0];
        String dropVariant = parts.length > 1 ? parts[1] : "normal";

        // 获取对应的集成物品
        ResourceLocation integrationItem = getIntegrationItem(resourceType);
        if (integrationItem == null) {
            return;
        }

        // 构建新的掉落池
        LootPool.Builder poolBuilder = LootPool.lootPool()
                .name("materials_integration_pool")
                .when(ExplosionCondition.survivesExplosion());

        // 根据掉落变体设置掉落数量
        switch (dropVariant) {
            case "four":
                poolBuilder.add(LootItem.lootTableItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(integrationItem)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(4))));
                break;
            case "two":
                poolBuilder.add(LootItem.lootTableItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(integrationItem)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2))));
                break;
            case "chance":
                poolBuilder.add(LootItem.lootTableItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(integrationItem)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                        .setWeight(1));
                poolBuilder.add(LootItem.lootTableItem(ForgeRegistries.ITEMS.getValue(integrationItem))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(0)))
                        .setWeight(1));
                break;
            case "normal":
            default:
                poolBuilder.add(LootItem.lootTableItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(integrationItem)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))));
                break;
        }

        // 创建新的战利品表，完全替换原版掉落
        LootTable.Builder newTable = LootTable.lootTable().withPool(poolBuilder);

        // 替换原战利品表
        event.setTable(newTable.build());
    }

    // 获取对应资源类型的集成物品
    private static ResourceLocation getIntegrationItem(String resourceType) {
        return switch (resourceType) {
            case "log" -> ForgeRegistries.ITEMS.getKey(MaterialsIntegration.LOG_INTEGRATION.get());
            case "planks" -> ForgeRegistries.ITEMS.getKey(MaterialsIntegration.PLANKS_INTEGRATION.get());
            case "stone" -> ForgeRegistries.ITEMS.getKey(MaterialsIntegration.STONE_INTEGRATION.get());
            case "rock" -> ForgeRegistries.ITEMS.getKey(MaterialsIntegration.ROCK_INTEGRATION.get());
            case "dirt" -> ForgeRegistries.ITEMS.getKey(MaterialsIntegration.DIRT_INTEGRATION.get());
            case "sand" -> ForgeRegistries.ITEMS.getKey(MaterialsIntegration.SAND_INTEGRATION.get());
            default -> null;
        };
    }
}