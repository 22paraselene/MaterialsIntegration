package materials_integration;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

@Mod(MaterialsIntegration.MODID)
public class MaterialsIntegration {
    public static final String MODID = "materials_integration";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<Item> DIRT_INTEGRATION = ITEMS.register("dirt_integration",
            () -> new Tooltip("tooltip.materials_integration.dirt"));
    public static final RegistryObject<Item> LOG_INTEGRATION = ITEMS.register("log_integration",
            () -> new Tooltip("tooltip.materials_integration.log"));
    public static final RegistryObject<Item> PLANKS_INTEGRATION = ITEMS.register("planks_integration",
            () -> new Tooltip("tooltip.materials_integration.planks"));
    public static final RegistryObject<Item> ROCK_INTEGRATION = ITEMS.register("rock_integration",
            () -> new Tooltip("tooltip.materials_integration.rock"));
    public static final RegistryObject<Item> SAND_INTEGRATION = ITEMS.register("sand_integration",
            () -> new Tooltip("tooltip.materials_integration.sand"));
    public static final RegistryObject<Item> STONE_INTEGRATION = ITEMS.register("stone_integration",
            () -> new Tooltip("tooltip.materials_integration.stone"));
    public static final RegistryObject<Item> FUEL_INTEGRATION = ITEMS.register("fuel_integration",
            () -> new Tooltip("tooltip.materials_integration.fuel") {
                @Override
                public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType) {return 1600;}
            });
    public static final RegistryObject<Item> WASTE = ITEMS.register("waste",
            () -> new Tooltip("tooltip.materials_integration.waste") {
                @Override
                public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType) {return 120;}
            });
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ComposterBlock.COMPOSTABLES.put(WASTE.get(), 0.3F);
        });
    }
    public static final RegistryObject<CreativeModeTab> RESOURCE_INTEGRATION = CREATIVE_MODE_TABS
            .register("resource_integration", () -> CreativeModeTab.builder()
                    .title(Component.translatable(
                            "itemGroup.materials_integration.resource_integration"))
                    .icon(() -> LOG_INTEGRATION.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(LOG_INTEGRATION.get());
                        output.accept(DIRT_INTEGRATION.get());
                        output.accept(PLANKS_INTEGRATION.get());
                        output.accept(ROCK_INTEGRATION.get());
                        output.accept(SAND_INTEGRATION.get());
                        output.accept(STONE_INTEGRATION.get());
                        output.accept(FUEL_INTEGRATION.get());
                        output.accept(WASTE.get());
                    }).build());

    public MaterialsIntegration(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        modEventBus.register(new PlaceBlock());
        modEventBus.addListener(this::commonSetup);
    }
}