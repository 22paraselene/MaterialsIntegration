package materials_integration;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid = MaterialsIntegration.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MaterialsIntegrationClient {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onClientSetup(FMLClientSetupEvent event) {
        // 等待所有内容加载完成后再设置渲染类型
        event.enqueueWork(() -> {
            // 设置 Sorting Workbench 的渲染类型为 cutout（支持透明度）
            ItemBlockRenderTypes.setRenderLayer(MaterialsIntegration.SORTING_WORKBENCH.get(), RenderType.cutout());
        });
    }
}