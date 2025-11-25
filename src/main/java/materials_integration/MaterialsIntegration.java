package materials_integration;


import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
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

    // 为每种资源定义掉落标签前缀
    public static final String DIRT_DROP = "dirt_drop";
    public static final String LOG_DROP = "log_drop";
    public static final String PLANKS_DROP = "planks_drop";
    public static final String STONE_DROP = "stone_drop";
    public static final String SAND_DROP = "sand_drop";
    public static final String ROCK_DROP = "rock_drop";

    public static final String DROP_TWO = "two";
    public static final String DROP_FOUR = "four";
    public static final String DROP_CHANCE = "chance";

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    // 注册物品
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

    // 注册工作台方块
    public static final RegistryObject<Block> SORTING_WORKBENCH = BLOCKS.register("sorting_workbench",
            () -> new SortingWorkbenchBlock(Block.Properties.of()
                    .strength(2.5f)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));
    // 注册工作台的方块物品
    public static final RegistryObject<Item> SORTING_WORKBENCH_ITEM = ITEMS.register("sorting_workbench",
            () -> new BlockItem(SORTING_WORKBENCH.get(), new Item.Properties()));

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
                        output.accept(SORTING_WORKBENCH_ITEM.get()); // 添加工作台
                    }).build());

    public MaterialsIntegration(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        System.out.println("MaterialsIntegration MOD initialization started");

        // 注册所有组件
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ModMenuTypes.MENUS.register(modEventBus);

        // 注册配方类型和序列化器
        ModRecipeTypes.RECIPE_TYPES.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);

        // 注册事件处理器
        modEventBus.register(new PlaceBlock());

        // 添加通用设置
        modEventBus.addListener(this::commonSetup);

        System.out.println("MaterialsIntegration MOD initialization completed");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ComposterBlock.COMPOSTABLES.put(WASTE.get(), 0.3F);
            IntegrationRecipeManager.initialize(); // 初始化原有的配方系统
        });
    }
}