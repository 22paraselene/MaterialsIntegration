package materials_integration;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class SortingWorkbenchScreen extends AbstractContainerScreen<SortingWorkbenchMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MaterialsIntegration.MODID, "textures/gui/sorting_workbench.png");

    private static final int SCROLLBAR_X = 138;
    private static final int SCROLLBAR_Y = 15;
    private static final int SCROLLBAR_WIDTH = 12;
    private static final int SCROLLBAR_HEIGHT = 72;
    private static final int SCROLLBAR_SLIDER_HEIGHT = 15;

    // 输出区域的边界
    private static final int OUTPUT_AREA_X = 26;
    private static final int OUTPUT_AREA_Y = 15;
    private static final int OUTPUT_AREA_WIDTH = 108;
    private static final int OUTPUT_AREA_HEIGHT = 72;

    private float scrollOff;
    private boolean scrolling;
    private ItemStack hoveredOutput = ItemStack.EMPTY; // 跟踪悬停的输出物品

    public SortingWorkbenchScreen(SortingWorkbenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 181;
        this.inventoryLabelY = 88;
    }

    @Override
    protected void init() {
        super.init();
        this.scrollOff = 0.0f;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // 绘制输出槽位背景
        drawOutputSlotBackgrounds(guiGraphics);

        // 绘制滚动条
        if (menu.canScroll()) {
            // 绘制滚动条滑块
            int scrollY = (int)((float)(SCROLLBAR_HEIGHT - SCROLLBAR_SLIDER_HEIGHT) * this.scrollOff);
            guiGraphics.blit(TEXTURE,
                    leftPos + SCROLLBAR_X,
                    topPos + SCROLLBAR_Y + scrollY,
                    176, 0,
                    SCROLLBAR_WIDTH, SCROLLBAR_SLIDER_HEIGHT);
        }
    }

    private void drawOutputSlotBackgrounds(GuiGraphics guiGraphics) {
        int visibleOutputCount = menu.getVisibleOutputCount();
        int scrollOffset = menu.getScrollOffset();

        for (int i = 0; i < visibleOutputCount; i++) {
            int row = i / 6;
            int col = i % 6;
            int x = leftPos + OUTPUT_AREA_X + col * 18;
            int y = topPos + OUTPUT_AREA_Y + row * 18;

            // 绘制槽位背景（使用纹理中的槽位背景区域）
            guiGraphics.blit(TEXTURE,
                    x, y,
                    176, 18, 18, 18);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // 渲染输出物品
        renderOutputItems(guiGraphics, mouseX, mouseY);

        // 渲染Tooltip（包括悬停物品的Tooltip）
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderOutputItems(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int visibleOutputCount = menu.getVisibleOutputCount();
        int scrollOffset = menu.getScrollOffset();
        int startIndex = scrollOffset * 6;

        hoveredOutput = ItemStack.EMPTY; // 重置悬停物品

        for (int i = 0; i < visibleOutputCount; i++) {
            int absoluteIndex = startIndex + i;
            if (absoluteIndex >= menu.getAvailableOutputsSize()) {
                continue;
            }

            int row = i / 6;
            int col = i % 6;
            int x = leftPos + OUTPUT_AREA_X + col * 18 + 1;
            int y = topPos + OUTPUT_AREA_Y + row * 18 + 1;

            ItemStack output = menu.getAvailableOutput(absoluteIndex);
            if (!output.isEmpty()) {
                guiGraphics.renderItem(output, x, y);
                guiGraphics.renderItemDecorations(this.font, output, x, y);

                // 检查鼠标是否悬停在此物品上
                if (isMouseOverOutputSlot(mouseX, mouseY, col, row)) {
                    hoveredOutput = output;
                }
            }
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        // 如果鼠标悬停在输出物品上，显示该物品的Tooltip
        if (!hoveredOutput.isEmpty()) {
            List<Component> tooltip = getTooltipFromItem(hoveredOutput);
            guiGraphics.renderTooltip(this.font, tooltip, hoveredOutput.getTooltipImage(), hoveredOutput, mouseX, mouseY);
        }
    }

    // 获取物品的Tooltip
    private List<Component> getTooltipFromItem(ItemStack stack) {
        return stack.getTooltipLines(this.minecraft.player, TooltipFlag.Default.NORMAL);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.scrolling = false;

        if (menu.canScroll() && isMouseOverScrollbar(mouseX, mouseY)) {
            this.scrolling = true;
            return true;
        }

        // 检查是否点击了输出物品选择区域
        if (isMouseInOutputArea(mouseX, mouseY)) {
            handleOutputClick(mouseX, mouseY);
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void handleOutputClick(double mouseX, double mouseY) {
        int visibleOutputCount = menu.getVisibleOutputCount();
        int scrollOffset = menu.getScrollOffset();
        int startIndex = scrollOffset * 6;

        for (int i = 0; i < visibleOutputCount; i++) {
            int row = i / 6;
            int col = i % 6;

            if (isMouseOverOutputSlot(mouseX, mouseY, col, row)) {
                int absoluteIndex = startIndex + i;
                if (absoluteIndex < menu.getAvailableOutputsSize()) {
                    menu.selectOutput(absoluteIndex);
                    this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, absoluteIndex);
                }
                return;
            }
        }
    }

    private boolean isMouseOverOutputSlot(double mouseX, double mouseY, int col, int row) {
        int x = leftPos + OUTPUT_AREA_X + col * 18;
        int y = topPos + OUTPUT_AREA_Y + row * 18;
        return mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
        if (menu.canScroll() && isMouseInOutputArea(mouseX, mouseY)) {
            int maxScroll = Math.max(0, menu.getTotalOutputRows() - 4);
            if (maxScroll > 0) {
                float newScrollOff = this.scrollOff - (float)scrollDelta / (float)maxScroll;
                this.scrollOff = Math.max(0.0F, Math.min(1.0F, newScrollOff));
                menu.scrollTo(this.scrollOff);
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollDelta);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.scrolling && menu.canScroll()) {
            int scrollAreaTop = topPos + SCROLLBAR_Y;
            int scrollAreaBottom = scrollAreaTop + SCROLLBAR_HEIGHT - SCROLLBAR_SLIDER_HEIGHT;

            float relativeY = ((float)mouseY - (float)scrollAreaTop) / ((float)(scrollAreaBottom - scrollAreaTop));
            this.scrollOff = Math.max(0.0F, Math.min(1.0F, relativeY));
            menu.scrollTo(this.scrollOff);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.scrolling) {
            this.scrolling = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean isMouseInOutputArea(double mouseX, double mouseY) {
        return mouseX >= leftPos + OUTPUT_AREA_X &&
                mouseX <= leftPos + OUTPUT_AREA_X + OUTPUT_AREA_WIDTH &&
                mouseY >= topPos + OUTPUT_AREA_Y &&
                mouseY <= topPos + OUTPUT_AREA_Y + OUTPUT_AREA_HEIGHT;
    }

    private boolean isMouseOverScrollbar(double mouseX, double mouseY) {
        return mouseX >= leftPos + SCROLLBAR_X &&
                mouseX <= leftPos + SCROLLBAR_X + SCROLLBAR_WIDTH &&
                mouseY >= topPos + SCROLLBAR_Y &&
                mouseY <= topPos + SCROLLBAR_Y + SCROLLBAR_HEIGHT;
    }
}