package materials_integration;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;

public class SortingWorkbenchMenu extends AbstractContainerMenu {
    private final Container inputContainer;
    private final ResultContainer outputContainer;

    private final List<ItemStack> availableOutputs = new ArrayList<>();
    private final Level level;
    private final Player player;
    private final Slot inSlot;
    private int scrollOffset = 0;
    private static final int OUTPUT_COLS = 6;
    private static final int VISIBLE_ROWS = 4;
    private int totalOutputRows = 0;

    private int selectedOutputIndex = -1;
    private boolean hasTakenOutput = false;

    public SortingWorkbenchMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory) {
        super(ModMenuTypes.SORTING_WORKBENCH.get(), containerId);
        this.level = playerInventory.player.level();
        this.player = playerInventory.player;

        this.inputContainer = new SimpleContainer(1) {
            @Override
            public void setChanged() {
                super.setChanged();
                SortingWorkbenchMenu.this.slotsChanged(this);
            }
        };

        this.outputContainer = new ResultContainer();

        inSlot = this.addSlot(new Slot(inputContainer, 0, 6, 71) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return SortingWorkbenchRecipeManager.hasRecipeFor(stack);
            }
        });

        // 输出槽位 - 禁用快速移动
        this.addSlot(new Slot(outputContainer, 0, 154, 71) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public boolean mayPickup(Player player) {
                // 禁用夸克MOD的快速移动
                return !player.isShiftKeyDown();
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                // 只处理非Shift点击
                if (player.isShiftKeyDown()) {
                    return; // Shift点击由quickMoveStack处理
                }

                hasTakenOutput = true;

                ItemStack inputStack = inSlot.getItem();
                int inputCount = inputStack.getCount();

                // 正常点击：取出最多一组（最大堆叠数）或者等于输入数量（如果少于最大堆叠数）
                handleNormalTake(player, stack, inputCount);

                updateAvailableOutputs();
                broadcastChanges();
            }

            private void handleNormalTake(Player player, ItemStack outputStack, int inputCount) {
                if (inputCount <= 0) return;

                // 计算实际能取出的数量（最多最大堆叠数，但不大于输入数量）
                int takeCount = Math.min(inputCount, outputStack.getMaxStackSize());

                // 创建要取出的物品
                ItemStack takenStack = outputStack.copy();
                takenStack.setCount(takeCount);

                // 消耗相应数量的输入物品
                inSlot.remove(takeCount);

                // 设置鼠标上的物品
                setCarried(takenStack);

                // 播放音效
                if (!level.isClientSide) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ITEM_PICKUP,
                            SoundSource.BLOCKS, 0.5F, 0.8F + level.random.nextFloat() * 0.4F);
                }
            }
        });

        // 玩家物品栏
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 99 + i * 18));
            }
        }

        // 玩家快捷栏
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 157));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            // 输出槽位（索引1）- 处理Shift+点击
            if (index == 1) {
                ItemStack inputStack = inSlot.getItem();
                int inputCount = inputStack.getCount();

                if (inputCount <= 0) {
                    return ItemStack.EMPTY;
                }

                // 创建完整数量的输出物品
                ItemStack fullOutput = slotStack.copy();
                fullOutput.setCount(inputCount);

                // 使用ItemHandlerHelper.giveItemToPlayer处理物品分发
                ItemHandlerHelper.giveItemToPlayer(player, fullOutput);

                // 消耗所有输入物品
                inSlot.remove(inputCount);

                // 播放音效
                if (!level.isClientSide) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ITEM_PICKUP,
                            SoundSource.BLOCKS, 0.5F, 0.8F + level.random.nextFloat() * 0.4F);
                }

                // 更新可用输出
                updateAvailableOutputs();
                broadcastChanges();

                return ItemStack.EMPTY;
            }
            // 输入槽位（索引0）
            else if (index == 0) {
                if (!this.moveItemStackTo(slotStack, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            }
            // 玩家物品栏 -> 输入槽位
            else if (this.slots.get(0).mayPlace(slotStack)) {
                if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            // 玩家物品栏内部移动
            else if (index >= 2 && index < 29) {
                if (!this.moveItemStackTo(slotStack, 29, 38, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (index >= 29 && index < 38) {
                if (!this.moveItemStackTo(slotStack, 2, 29, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            }

            slot.setChanged();
            if (slotStack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
            this.broadcastChanges();
        }

        return itemstack;
    }

    // 其他方法保持不变...
    @Override
    public boolean clickMenuButton(Player player, int id) {
        selectOutput(id);
        if (!level.isClientSide) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.UI_LOOM_SELECT_PATTERN,
                    SoundSource.BLOCKS, 0.7F, 1.0F);
        }
        return true;
    }

    @Override
    public void slotsChanged(Container container) {
        if (container == inputContainer) {
            updateAvailableOutputs();
        }
        super.slotsChanged(container);
    }

    private void updateAvailableOutputs() {
        ItemStack input = inputContainer.getItem(0);
        availableOutputs.clear();

        if (input.isEmpty()) {
            outputContainer.setItem(0, ItemStack.EMPTY);
            selectedOutputIndex = -1;
        } else {
            List<ItemStack> recipes = SortingWorkbenchRecipeManager.getRecipesFor(input);
            availableOutputs.addAll(recipes);

            if (selectedOutputIndex >= 0 && selectedOutputIndex < availableOutputs.size()) {
                ItemStack selectedOutput = availableOutputs.get(selectedOutputIndex).copy();
                selectedOutput.setCount(input.getCount());
                outputContainer.setItem(0, selectedOutput);
            } else if (!availableOutputs.isEmpty()) {
                selectedOutputIndex = 0;
                ItemStack selectedOutput = availableOutputs.get(0).copy();
                selectedOutput.setCount(input.getCount());
                outputContainer.setItem(0, selectedOutput);
            }
        }

        totalOutputRows = (int) Math.ceil((double) availableOutputs.size() / OUTPUT_COLS);
        scrollOffset = Math.min(scrollOffset, Math.max(0, totalOutputRows - VISIBLE_ROWS));
        broadcastFullState();
    }

    public void selectOutput(int index) {
        if (index < 0 || index >= availableOutputs.size()) {
            return;
        }

        ItemStack input = inputContainer.getItem(0);
        if (input.isEmpty()) {
            return;
        }

        selectedOutputIndex = index;
        ItemStack selectedOutput = availableOutputs.get(index).copy();
        selectedOutput.setCount(input.getCount());
        outputContainer.setItem(0, selectedOutput);

        broadcastFullState();
    }

    public ItemStack getAvailableOutput(int index) {
        return index < availableOutputs.size() ? availableOutputs.get(index) : ItemStack.EMPTY;
    }

    public int getAvailableOutputsSize() {
        return availableOutputs.size();
    }

    public void scrollTo(float scroll) {
        int maxScroll = Math.max(0, totalOutputRows - VISIBLE_ROWS);
        int newScroll = (int) (scroll * maxScroll + 0.5f);
        if (newScroll != scrollOffset) {
            scrollOffset = newScroll;
            broadcastFullState();
        }
    }

    public boolean canScroll() {
        return totalOutputRows > VISIBLE_ROWS;
    }

    public int getTotalOutputRows() {
        return totalOutputRows;
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public int getVisibleOutputCount() {
        int startIndex = scrollOffset * OUTPUT_COLS;
        int remaining = availableOutputs.size() - startIndex;
        return Math.min(remaining, VISIBLE_ROWS * OUTPUT_COLS);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        if (!level.isClientSide) {
            // 返还输入物品
            ItemStack inputStack = inputContainer.removeItemNoUpdate(0);
            if (!inputStack.isEmpty()) {
                player.getInventory().placeItemBackInInventory(inputStack);
            }

            // 清空输出容器，但不返还给玩家
            outputContainer.removeItemNoUpdate(0);
        }
    }
}