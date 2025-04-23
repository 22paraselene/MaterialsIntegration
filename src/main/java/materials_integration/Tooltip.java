package materials_integration;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class Tooltip extends Item {
    private final String tooltipKey;

    public Tooltip(String tooltipKey) {
        super(new Item.Properties());
        this.tooltipKey = tooltipKey;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(itemStack, level, tooltip, flag);

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable(this.tooltipKey + ".line1"));
            tooltip.add(Component.translatable(this.tooltipKey + ".line2"));
            tooltip.add(Component.translatable(this.tooltipKey + ".line3"));
            tooltip.add(Component.translatable(this.tooltipKey + ".line4"));
        } else {
            tooltip.add(Component.translatable("tooltip.materials_integration.hold_shift"));
        }
    }
}