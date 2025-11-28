package materials_integration;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SortingWorkbenchSyncPacket {
    private final int containerId;
    private final List<ItemStack> availableOutputs;
    private final int selectedOutputIndex;
    private final int scrollOffset;
    private final int totalOutputRows;

    public SortingWorkbenchSyncPacket(int containerId, List<ItemStack> availableOutputs,
                                      int selectedOutputIndex, int scrollOffset, int totalOutputRows) {
        this.containerId = containerId;
        this.availableOutputs = availableOutputs;
        this.selectedOutputIndex = selectedOutputIndex;
        this.scrollOffset = scrollOffset;
        this.totalOutputRows = totalOutputRows;
    }

    public static void encode(SortingWorkbenchSyncPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.containerId);
        buffer.writeInt(packet.availableOutputs.size());
        for (ItemStack stack : packet.availableOutputs) {
            buffer.writeItem(stack);
        }
        buffer.writeInt(packet.selectedOutputIndex);
        buffer.writeInt(packet.scrollOffset);
        buffer.writeInt(packet.totalOutputRows);
    }

    public static SortingWorkbenchSyncPacket decode(FriendlyByteBuf buffer) {
        int containerId = buffer.readInt();
        int outputCount = buffer.readInt();
        List<ItemStack> outputs = new ArrayList<>();
        for (int i = 0; i < outputCount; i++) {
            outputs.add(buffer.readItem());
        }
        int selectedIndex = buffer.readInt();
        int scrollOffset = buffer.readInt();
        int totalRows = buffer.readInt();

        return new SortingWorkbenchSyncPacket(containerId, outputs, selectedIndex, scrollOffset, totalRows);
    }

    public static void handle(SortingWorkbenchSyncPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            // 在客户端处理
            if (context.get().getDirection().getReceptionSide().isClient()) {
                net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
                if (minecraft.player != null && minecraft.player.containerMenu instanceof SortingWorkbenchMenu menu
                        && menu.containerId == packet.containerId) {
                    menu.updateFromPacket(packet.availableOutputs, packet.selectedOutputIndex,
                            packet.scrollOffset, packet.totalOutputRows);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}