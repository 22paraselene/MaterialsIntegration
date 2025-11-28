package materials_integration;

import net.minecraft.server.level.ServerPlayer;
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
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class SortingWorkbenchMenu extends AbstractContainerMenu {
    private final Container inputContainer;
    private final ResultContainer outputContainer;
    private final Level level;
    private final Player player;
    private final Slot inSlot;

    // 客户端和服务端同步的数据
    private final List<ItemStack> availableOutputs = new ArrayList<>();
    private int scrollOffset = 0;
    private static final int OUTPUT_COLS = 6;
    private static final int VISIBLE_ROWS = 4;
    private int totalOutputRows = 0;
    private int selectedOutputIndex = -1;

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

        // 输出槽位
        this.addSlot(new Slot(outputContainer, 0, 154, 71) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public boolean mayPickup(Player player) {
                return !player.isShiftKeyDown();
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                if (player.isShiftKeyDown()) {
                    return;
                }

                ItemStack inputStack = inSlot.getItem();
                int inputCount = inputStack.getCount();

                if (inputCount <= 0) return;

                // 只在服务端处理物品消耗
                if (!level.isClientSide) {
                    handleNormalTake(player, stack, inputCount);
                    updateAvailableOutputs();
                    broadcastChanges();

                    // 同步数据到客户端
                    syncDataToClient();
                }
            }

            private void handleNormalTake(Player player, ItemStack outputStack, int inputCount) {
                int takeCount = Math.min(inputCount, outputStack.getMaxStackSize());
                ItemStack takenStack = outputStack.copy();
                takenStack.setCount(takeCount);

                inSlot.remove(takeCount);
                setCarried(takenStack);

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ITEM_PICKUP,
                        SoundSource.BLOCKS, 0.5F, 0.8F + level.random.nextFloat() * 0.4F);
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

            if (index == 1) {
                ItemStack inputStack = inSlot.getItem();
                int inputCount = inputStack.getCount();

                if (inputCount <= 0) {
                    return ItemStack.EMPTY;
                }

                // 只在服务端处理物品转移
                if (!level.isClientSide) {
                    ItemStack fullOutput = slotStack.copy();
                    fullOutput.setCount(inputCount);

                    ItemHandlerHelper.giveItemToPlayer(player, fullOutput);
                    inSlot.remove(inputCount);

                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ITEM_PICKUP,
                            SoundSource.BLOCKS, 0.5F, 0.8F + level.random.nextFloat() * 0.4F);

                    updateAvailableOutputs();
                    broadcastChanges();
                    syncDataToClient();
                }

                return ItemStack.EMPTY;
            } else if (index == 0) {
                if (!this.moveItemStackTo(slotStack, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.slots.get(0).mayPlace(slotStack)) {
                if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 2 && index < 29) {
                if (!this.moveItemStackTo(slotStack, 29, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 29 && index < 38) {
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

    @Override
    public boolean clickMenuButton(Player player, int id) {
        // 只在服务端处理选择逻辑
        if (!level.isClientSide) {
            selectOutput(id);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.UI_LOOM_SELECT_PATTERN,
                    SoundSource.BLOCKS, 0.7F, 1.0F);
            syncDataToClient();
            return true;
        }
        return false;
    }

    @Override
    public void slotsChanged(Container container) {
        if (container == inputContainer) {
            // 只在服务端更新配方
            if (!level.isClientSide) {
                updateAvailableOutputs();
                syncDataToClient();
            }
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
    }

    // 同步数据到客户端的方法
    private void syncDataToClient() {
        if (!level.isClientSide) {
            Networking.sendToClient(new SortingWorkbenchSyncPacket(
                    containerId,
                    availableOutputs,
                    selectedOutputIndex,
                    scrollOffset,
                    totalOutputRows
            ), (ServerPlayer) player);
        }
    }

    // 从网络包更新客户端数据的方法
    public void updateFromPacket(List<ItemStack> outputs, int selectedIndex, int scroll, int totalRows) {
        this.availableOutputs.clear();
        this.availableOutputs.addAll(outputs);
        this.selectedOutputIndex = selectedIndex;
        this.scrollOffset = scroll;
        this.totalOutputRows = totalRows;

        // 更新输出槽位
        ItemStack input = inputContainer.getItem(0);
        if (!input.isEmpty() && selectedIndex >= 0 && selectedIndex < availableOutputs.size()) {
            ItemStack selectedOutput = availableOutputs.get(selectedIndex).copy();
            selectedOutput.setCount(input.getCount());
            outputContainer.setItem(0, selectedOutput);
        } else {
            outputContainer.setItem(0, ItemStack.EMPTY);
        }
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
    }

    // 客户端使用的获取方法
    public ItemStack getAvailableOutput(int index) {
        return index < availableOutputs.size() ? availableOutputs.get(index) : ItemStack.EMPTY;
    }

    public int getAvailableOutputsSize() {
        return availableOutputs.size();
    }

    public void scrollTo(float scroll) {
        if (!level.isClientSide) {
            int maxScroll = Math.max(0, totalOutputRows - VISIBLE_ROWS);
            int newScroll = (int) (scroll * maxScroll + 0.5f);
            if (newScroll != scrollOffset) {
                scrollOffset = newScroll;
                syncDataToClient();
            }
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

    public int getSelectedOutputIndex() {
        return selectedOutputIndex;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        if (!level.isClientSide) {
            ItemStack inputStack = inputContainer.removeItemNoUpdate(0);
            if (!inputStack.isEmpty()) {
                player.getInventory().placeItemBackInInventory(inputStack);
            }
            outputContainer.removeItemNoUpdate(0);
        }
    }
}