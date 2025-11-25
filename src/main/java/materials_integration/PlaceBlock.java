package materials_integration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = MaterialsIntegration.MODID)
public class PlaceBlock {

    private static boolean isInteractionBlock(BlockState state) {
        Block block = state.getBlock();

        // 检查是否是已知的有GUI的方块
        return state.is(BlockTags.DOORS) ||
                state.is(BlockTags.TRAPDOORS) ||
                state.is(BlockTags.BUTTONS) ||
                state.is(BlockTags.BEDS) ||
                state.is(BlockTags.SHULKER_BOXES) ||
                state.hasBlockEntity() ||
                // 检查是否是容器或工作台类方块
                block instanceof MenuProvider ||
                // 检查特定的原版工作台类方块
                isWorkbenchBlock(block) ||
                // 检查是否是MOD的工作台类方块（通过标签或注册名）
                isModWorkbenchBlock(block);
    }

    // 检查是否是原版工作台类方块
    private static boolean isWorkbenchBlock(Block block) {
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);
        if (blockId == null) return false;

        String path = blockId.getPath();
        return path.contains("crafting_table") ||
                path.contains("furnace") ||
                path.contains("blast_furnace") ||
                path.contains("smoker") ||
                path.contains("stonecutter") ||
                path.contains("loom") ||
                path.contains("cartography_table") ||
                path.contains("smithing_table") ||
                path.contains("grindstone") ||
                path.contains("anvil") ||
                path.contains("enchanting_table") ||
                path.contains("brewing_stand");
    }

    // 检查是否是MOD的工作台类方块
    private static boolean isModWorkbenchBlock(Block block) {
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);
        if (blockId == null) return false;

        // 检查常见的MOD工作台命名模式
        String path = blockId.getPath();
        return path.contains("workbench") ||
                path.contains("crafting") ||
                path.contains("table") &&
                        (path.contains("craft") || path.contains("work")) ||
                // 检查方块是否在工作台相关的标签中
                isBlockInWorkbenchTag(block);
    }

    // 检查方块是否在工作台相关的标签中
    private static boolean isBlockInWorkbenchTag(Block block) {
        // 这里可以添加更多工作台相关的标签
        // 注意：需要检查你的MOD或常用MOD是否提供了这样的标签
        return false; // 暂时返回false，你可以根据需要扩展
    }

    // 物品ID到默认方块的映射
    private static final Map<String, Block> DEFAULT_BLOCKS = new HashMap<>();
    static {
        DEFAULT_BLOCKS.put("log_integration",
                ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("minecraft:oak_log")));
        DEFAULT_BLOCKS.put("dirt_integration",
                ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("minecraft:dirt")));
        DEFAULT_BLOCKS.put("planks_integration",
                ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("minecraft:oak_planks")));
        DEFAULT_BLOCKS.put("rock_integration",
                ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("minecraft:stone_bricks")));
        DEFAULT_BLOCKS.put("stone_integration",
                ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("minecraft:cobblestone")));
        DEFAULT_BLOCKS.put("sand_integration",
                ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("minecraft:sand")));
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();

        // 只处理主手（右手）触发的事件
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        ItemStack heldStack = player.getMainHandItem();
        if (!isIntegrationItem(heldStack)) {
            return;
        }

        // 客户端只处理手部动画（右手）
        if (level.isClientSide()) {
            player.swing(InteractionHand.MAIN_HAND);
            return;
        }

        BlockPos pos = event.getPos();
        BlockState targetState = level.getBlockState(pos);

        // 增加调试信息
        if (level.isClientSide) {
            System.out.println("右键方块: " + ForgeRegistries.BLOCKS.getKey(targetState.getBlock()));
            System.out.println("是交互方块: " + isInteractionBlock(targetState));
        }

        if (isInteractionBlock(targetState)) {
            return; // 如果是工作台等有GUI的方块，不触发放置逻辑
        }

        ResourceLocation heldItemId = ForgeRegistries.ITEMS.getKey(heldStack.getItem());
        String itemPath = heldItemId != null ? heldItemId.getPath() : null;

        if (itemPath == null) {
            return;
        }

        // 获取目标方块的物品形式
        Item targetItem = getBlockItem(targetState.getBlock());
        if (targetItem == null) {
            return;
        }

        // 尝试匹配标签 - 使用物品标签而不是方块标签
        BlockState stateToPlace = null;

        // 使用物品ID构建标签路径，例如：materials_integration:log_integration
        ResourceLocation tagLocation = ResourceLocation.fromNamespaceAndPath(
                MaterialsIntegration.MODID,
                itemPath  // 直接使用物品ID，例如 "log_integration"
        );
        TagKey<Item> outputTag = ItemTags.create(tagLocation);

        // 如果目标方块的物品形式在标签中，则放置相同的方块
        if (isItemInTag(targetItem, outputTag)) {
            stateToPlace = targetState;
        }
        // 使用默认方块作为备选
        else if (DEFAULT_BLOCKS.containsKey(itemPath)) {
            Block defaultBlock = DEFAULT_BLOCKS.get(itemPath);
            if (defaultBlock != null) {
                stateToPlace = defaultBlock.defaultBlockState();
            }
        }

        if (stateToPlace == null) {
            return;
        }

        Direction clickedFace = event.getFace();
        if (clickedFace == null) {
            return;
        }

        BlockPos placePos = pos.relative(clickedFace);

        // 确保目标位置可以放置方块
        if (!level.getBlockState(placePos).canBeReplaced()) {
            return;
        }

        // 创建BlockItem进行放置
        BlockItem blockItem = createBlockItem(stateToPlace);
        if (blockItem == null) {
            return;
        }

        // 创建临时ItemStack
        ItemStack tempStack = new ItemStack(blockItem);

        // 创建放置上下文
        Vec3 clickLocation = event.getHitVec().getLocation();
        BlockHitResult blockHit = new BlockHitResult(
                clickLocation,
                clickedFace,
                placePos,
                false
        );

        UseOnContext useContext = new UseOnContext(
                level,
                player,
                event.getHand(),
                tempStack,
                blockHit
        );

        BlockPlaceContext placeContext = new BlockPlaceContext(useContext);
        InteractionResult result = blockItem.place(placeContext);

        if (result.consumesAction()) {
            // 播放我们放置的方块的音效
            SoundEvent sound = stateToPlace.getSoundType().getPlaceSound();
            level.playSound(null, placePos, sound, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);

            // 消耗右手物品
            if (!player.isCreative()) {
                heldStack.shrink(1);
            }

            // 明确指定使用主手（右手）播放动画
            player.swing(InteractionHand.MAIN_HAND);
        }
    }

    // 检查物品是否在标签中
    private static boolean isItemInTag(Item item, TagKey<Item> tag) {
        return ForgeRegistries.ITEMS.tags().getTag(tag).contains(item);
    }

    // 获取方块的物品形式
    private static Item getBlockItem(Block block) {
        return ForgeRegistries.ITEMS.getValue(ForgeRegistries.BLOCKS.getKey(block));
    }

    // 创建对应方块的BlockItem
    private static BlockItem createBlockItem(BlockState state) {
        Block block = state.getBlock();
        Item item = ForgeRegistries.ITEMS.getValue(ForgeRegistries.BLOCKS.getKey(block));
        return (item instanceof BlockItem) ? (BlockItem) item : null;
    }

    private static boolean isIntegrationItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return itemId != null && itemId.getNamespace().equals(MaterialsIntegration.MODID);
    }
}