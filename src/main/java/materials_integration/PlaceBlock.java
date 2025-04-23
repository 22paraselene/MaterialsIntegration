package materials_integration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = MaterialsIntegration.MODID)
public class PlaceBlock {

    // 默认方块映射表
    private static final Map<String, Block> DEFAULT_BLOCKS = new HashMap<>();
    static {
        DEFAULT_BLOCKS.put("dirt_integration",
                ForgeRegistries.BLOCKS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "dirt")));
        DEFAULT_BLOCKS.put("log_integration",
                ForgeRegistries.BLOCKS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "oak_log")));
        DEFAULT_BLOCKS.put("planks_integration",
                ForgeRegistries.BLOCKS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "oak_planks")));
        DEFAULT_BLOCKS.put("rock_integration",
                ForgeRegistries.BLOCKS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "stone_bricks")));
        DEFAULT_BLOCKS.put("stone_integration",
                ForgeRegistries.BLOCKS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "cobblestone")));
        DEFAULT_BLOCKS.put("sand_integration",
                ForgeRegistries.BLOCKS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "sand")));
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getSide().isClient())
            return;

        Player player = event.getEntity();
        ItemStack heldStack = player.getMainHandItem();
        BlockPos pos = event.getPos();
        Level level = event.getLevel();
        BlockState targetState = level.getBlockState(pos);

        if (!isIntegrationItem(heldStack))
            return;

        ResourceLocation heldItemId = ForgeRegistries.ITEMS.getKey(heldStack.getItem());
        String itemPath = heldItemId.getPath();
        ResourceLocation tagLocation = ResourceLocation.fromNamespaceAndPath(MaterialsIntegration.MODID,
                "stonecutter_outputs/" + itemPath);
        TagKey<Block> outputTag = BlockTags.create(tagLocation);

        BlockState stateToPlace = null;

        // 优先检查标签匹配
        if (targetState.is(outputTag)) {
            stateToPlace = targetState;
        }
        // 其次检查默认方块
        else if (DEFAULT_BLOCKS.containsKey(itemPath)) {
            Block defaultBlock = DEFAULT_BLOCKS.get(itemPath);
            if (defaultBlock != null) {
                stateToPlace = defaultBlock.defaultBlockState();
            }
        }

        if (stateToPlace == null)
            return;

        // 材料版本替换逻辑
        Block targetBlock = stateToPlace.getBlock();
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(targetBlock);
        if (blockId != null) {
            String path = blockId.getPath();
            String material = path;

            // 处理木板类建筑部件
            if (itemPath.equals("planks_integration")) {
                material = path.replaceAll("_(fence|fence_gate|stairs|slab)$", "_planks");
            }
            // 处理砖类建筑部件
            else if (path.matches(".*_brick_(stairs|slab|wall)$")) {
                material = path.replaceAll("_brick_(stairs|slab|wall)$", "_bricks");
            }
            // 处理普通建筑部件
            else {
                String base = path.replaceAll("_(stairs|slab|wall)$", "");
                material = base;
            }

            // 应用材料替换
            if (!path.equals(material)) {
                Block baseBlock = ForgeRegistries.BLOCKS
                        .getValue(ResourceLocation.fromNamespaceAndPath(blockId.getNamespace(), material));
                if (baseBlock != null) {
                    stateToPlace = baseBlock.defaultBlockState();
                }
            }
        }

        // 原木朝向处理（基于点击面）
        if (itemPath.equals("log_integration") && stateToPlace.hasProperty(RotatedPillarBlock.AXIS)) {
            Direction clickedFace = event.getFace();
            Axis axis = switch (clickedFace) {
                case UP, DOWN -> Axis.Y;
                case NORTH, SOUTH -> Axis.Z;
                case EAST, WEST -> Axis.X;
            };
            stateToPlace = stateToPlace.setValue(RotatedPillarBlock.AXIS, axis);
        }

        // 音效选择
        SoundEvent sound = switch (itemPath) {
            case "dirt_integration" -> SoundEvents.GRASS_PLACE;
            case "log_integration", "planks_integration" -> SoundEvents.WOOD_PLACE;
            case "sand_integration" -> SoundEvents.SAND_PLACE;
            case "stone_integration", "rock_integration" -> SoundEvents.STONE_PLACE;
            default -> SoundEvents.STONE_PLACE;
        };

        BlockPos placePos = pos.relative(event.getFace());
        if (level.isEmptyBlock(placePos)) {
            level.setBlock(placePos, stateToPlace, 3);
            level.playSound(null, placePos, sound, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);

            // 触发手部动画
            player.swing(event.getHand(), true);

            // 消耗物品
            if (!player.isCreative()) {
                heldStack.shrink(1);
            }
            event.setCanceled(true);
        }
    }

    private static boolean isIntegrationItem(ItemStack stack) {
        if (stack.isEmpty())
            return false;
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return itemId != null && itemId.getNamespace().equals(MaterialsIntegration.MODID);
    }
}