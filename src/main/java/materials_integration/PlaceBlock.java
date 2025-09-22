package materials_integration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
        return state.is(BlockTags.DOORS) ||
                state.is(BlockTags.TRAPDOORS) ||
                state.is(BlockTags.BUTTONS) ||
                state.is(BlockTags.BEDS) ||
                state.is(BlockTags.SHULKER_BOXES) ||
                state.hasBlockEntity();
    }

    private static final Map<String, Block> DEFAULT_BLOCKS = new HashMap<>();
    static {
        DEFAULT_BLOCKS.put("dirt_integration",
                ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("minecraft:dirt")));
        DEFAULT_BLOCKS.put("log_integration",
                ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("minecraft:oak_log")));
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

        // 只处理主手（右手）触发的事件
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        ItemStack heldStack = player.getMainHandItem();
        if (!isIntegrationItem(heldStack)) {
            return;
        }

        // 客户端只处理手部动画（右手）
        if (event.getLevel().isClientSide()) {
            player.swing(InteractionHand.MAIN_HAND); // 明确指定主手
            return;
        }

        BlockPos pos = event.getPos();
        Level level = event.getLevel();
        BlockState targetState = level.getBlockState(pos);

        if (isInteractionBlock(targetState)) {
            return;
        }

        ResourceLocation heldItemId = ForgeRegistries.ITEMS.getKey(heldStack.getItem());
        String itemPath = heldItemId != null ? heldItemId.getPath() : null;

        // 尝试匹配标签
        BlockState stateToPlace = null;
        if (itemPath != null) {
            ResourceLocation tagLocation = ResourceLocation.fromNamespaceAndPath(MaterialsIntegration.MODID,
                    "stonecutter_outputs/" + itemPath);
            TagKey<Block> outputTag = BlockTags.create(tagLocation);

            if (targetState.is(outputTag)) {
                stateToPlace = targetState;
            }
            // 使用默认方块作为备选
            else if (DEFAULT_BLOCKS.containsKey(itemPath)) {
                Block defaultBlock = DEFAULT_BLOCKS.get(itemPath);
                if (defaultBlock != null) {
                    stateToPlace = defaultBlock.defaultBlockState();
                }
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