package materials_integration;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, MaterialsIntegration.MODID);

    public static final RegistryObject<MenuType<SortingWorkbenchMenu>> SORTING_WORKBENCH =
            MENUS.register("sorting_workbench",
                    () -> IForgeMenuType.create((windowId, inv, data) ->
                            new SortingWorkbenchMenu(windowId, inv)
                    ));
}