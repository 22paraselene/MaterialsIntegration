import json
import os

output_dir = "stonecutting"
os.makedirs(output_dir, exist_ok=True)

# --------------------------
# 分类配置
# --------------------------
category_config = {
    "materials_integration:sand_integration": {
        "block": ["minecraft:gravel", "minecraft:red_sand", "minecraft:sand"],
        "slabs": [
            "minecraft:cut_sandstone_slab",
            "minecraft:cut_red_sandstone_slab",
            "minecraft:red_sandstone_slab",
            "minecraft:sandstone_slab",
            "minecraft:smooth_red_sandstone_slab",
            "minecraft:smooth_sandstone_slab",
        ],
    },
    "materials_integration:dirt_integration": {
        "block": [
            "minecraft:coarse_dirt",
            "minecraft:dirt",
            "minecraft:grass_block",
            "minecraft:mud_brick_stairs",
            "minecraft:mud_brick_wall",
            "minecraft:mud_bricks",
            "minecraft:mud",
            "minecraft:mycelium",
            "minecraft:packed_mud",
            "minecraft:podzol",
            "minecraft:rooted_dirt",
        ],
        "slabs": ["minecraft:mud_brick_slab"],
    },
    "materials_integration:log_integration": {
        "block": [
            "minecraft:acacia_log",
            "minecraft:acacia_wood",
            "minecraft:birch_log",
            "minecraft:birch_wood",
            "minecraft:cherry_log",
            "minecraft:cherry_wood",
            "minecraft:dark_oak_log",
            "minecraft:dark_oak_wood",
            "minecraft:jungle_log",
            "minecraft:jungle_wood",
            "minecraft:mangrove_log",
            "minecraft:mangrove_wood",
            "minecraft:oak_log",
            "minecraft:oak_wood",
            "minecraft:spruce_log",
            "minecraft:spruce_wood",
            "minecraft:stripped_acacia_log",
            "minecraft:stripped_acacia_wood",
            "minecraft:stripped_birch_log",
            "minecraft:stripped_birch_wood",
            "minecraft:stripped_cherry_log",
            "minecraft:stripped_cherry_wood",
            "minecraft:stripped_dark_oak_log",
            "minecraft:stripped_dark_oak_wood",
            "minecraft:stripped_jungle_log",
            "minecraft:stripped_jungle_wood",
            "minecraft:stripped_mangrove_log",
            "minecraft:stripped_mangrove_wood",
            "minecraft:stripped_oak_log",
            "minecraft:stripped_oak_wood",
            "minecraft:stripped_spruce_log",
            "minecraft:stripped_spruce_wood",
            "quark:ancient_log",
            "quark:ancient_wood",
            "quark:azalea_log",
            "quark:azalea_wood",
            "quark:blossom_log",
            "quark:blossom_wood",
            "quark:hollow_acacia_log",
            "quark:hollow_ancient_log",
            "quark:hollow_azalea_log",
            "quark:hollow_birch_log",
            "quark:hollow_blossom_log",
            "quark:hollow_cherry_log",
            "quark:hollow_dark_oak_log",
            "quark:hollow_jungle_log",
            "quark:hollow_mangrove_log",
            "quark:hollow_oak_log",
            "quark:hollow_spruce_log",
            "quark:stripped_ancient_log",
            "quark:stripped_ancient_wood",
            "quark:stripped_azalea_log",
            "quark:stripped_azalea_wood",
            "quark:stripped_blossom_log",
            "quark:stripped_blossom_wood",
            "quark:stripped_ancient_log",
            "quark:stripped_ancient_wood",
            "quark:stripped_azalea_log",
            "quark:stripped_azalea_wood",
            "quark:stripped_blossom_log",
            "quark:stripped_blossom_wood",
        ],
    },
    "materials_integration:planks_integration": {
        "block": [
            "minecraft:acacia_fence_gate",
            "minecraft:acacia_fence",
            "minecraft:acacia_planks",
            "minecraft:acacia_stairs",
            "minecraft:birch_fence_gate",
            "minecraft:birch_fence",
            "minecraft:birch_planks",
            "minecraft:birch_stairs",
            "minecraft:cherry_fence_gate",
            "minecraft:cherry_fence",
            "minecraft:cherry_planks",
            "minecraft:cherry_stairs",
            "minecraft:dark_oak_fence_gate",
            "minecraft:dark_oak_fence",
            "minecraft:dark_oak_planks",
            "minecraft:dark_oak_stairs",
            "minecraft:jungle_fence_gate",
            "minecraft:jungle_fence",
            "minecraft:jungle_planks",
            "minecraft:jungle_stairs",
            "minecraft:mangrove_fence_gate",
            "minecraft:mangrove_fence",
            "minecraft:mangrove_planks",
            "minecraft:mangrove_stairs",
            "minecraft:oak_fence_gate",
            "minecraft:oak_fence",
            "minecraft:oak_planks",
            "minecraft:oak_stairs",
            "minecraft:spruce_fence_gate",
            "minecraft:spruce_fence",
            "minecraft:spruce_planks",
            "minecraft:spruce_stairs",
            "quark:vertical_oak_planks",
            "quark:vertical_spruce_planks",
            "quark:vertical_birch_planks",
            "quark:vertical_jungle_planks",
            "quark:vertical_acacia_planks",
            "quark:vertical_dark_oak_planks",
            "quark:vertical_mangrove_planks",
            "quark:vertical_cherry_planks",
            "quark:ancient_planks",
            "quark:vertical_ancient_planks",
            "quark:azalea_planks",
            "quark:vertical_azalea_planks",
            "quark:blossom_planks",
            "quark:vertical_blossom_planks",
            "quark:ancient_planks_stairs",
            "quark:azalea_planks_stairs",
            "quark:blossom_planks_stairs",
            "quark:ancient_fence",
            "quark:ancient_fence_gate",
            "quark:azalea_fence",
            "quark:azalea_fence_gate",
            "quark:blossom_fence",
            "quark:blossom_fence_gate",
        ],
        "slabs": [
            "minecraft:acacia_slab",
            "minecraft:birch_slab",
            "minecraft:cherry_slab",
            "minecraft:dark_oak_slab",
            "minecraft:jungle_slab",
            "minecraft:mangrove_slab",
            "minecraft:oak_slab",
            "minecraft:spruce_slab",
            "quark:acacia_vertical_slab",
            "quark:birch_vertical_slab",
            "quark:cherry_vertical_slab",
            "quark:dark_oak_vertical_slab",
            "quark:jungle_vertical_slab",
            "quark:mangrove_vertical_slab",
            "quark:oak_vertical_slab",
            "quark:spruce_vertical_slab",
            "quark:ancient_planks_vertical_slab",
            "quark:azalea_planks_vertical_slab",
            "quark:blossom_planks_vertical_slab",
            "quark:ancient_planks_slab",
            "quark:azalea_planks_slab",
            "quark:blossom_planks_slab",
        ],
    },
    "materials_integration:stone_integration": {
        "block": [
            "minecraft:andesite_stairs",
            "minecraft:andesite_wall",
            "minecraft:andesite",
            "minecraft:calcite",
            "minecraft:chiseled_deepslate",
            "minecraft:cobbled_deepslate_stairs",
            "minecraft:cobbled_deepslate_wall",
            "minecraft:cobbled_deepslate",
            "minecraft:cobblestone_stairs",
            "minecraft:cobblestone_wall",
            "minecraft:cobblestone",
            "minecraft:deepslate_brick_stairs",
            "minecraft:deepslate_brick_wall",
            "minecraft:deepslate_bricks",
            "minecraft:deepslate_tile_stairs",
            "minecraft:deepslate_tile_wall",
            "minecraft:deepslate_tiles",
            "minecraft:diorite_stairs",
            "minecraft:diorite_wall",
            "minecraft:diorite",
            "minecraft:dripstone_block",
            "minecraft:granite_stairs",
            "minecraft:granite_wall",
            "minecraft:granite",
            "minecraft:mossy_cobblestone_stairs",
            "minecraft:mossy_cobblestone_wall",
            "minecraft:mossy_cobblestone",
            "minecraft:polished_andesite_stairs",
            "minecraft:polished_andesite",
            "minecraft:polished_deepslate_stairs",
            "minecraft:polished_deepslate_wall",
            "minecraft:polished_deepslate",
            "minecraft:polished_diorite_stairs",
            "minecraft:polished_diorite",
            "minecraft:polished_granite_stairs",
            "minecraft:polished_granite",
            "minecraft:stone_stairs",
            "minecraft:tuff",
            "quark:limestone",
            "quark:jasper",
            "quark:permafrost",
        ],
        "slabs": [
            "minecraft:andesite_slab",
            "minecraft:cobbled_deepslate_slab",
            "minecraft:cobblestone_slab",
            "minecraft:deepslate_brick_slab",
            "minecraft:deepslate_tile_slab",
            "minecraft:diorite_slab",
            "minecraft:granite_slab",
            "minecraft:mossy_cobblestone_slab",
            "minecraft:polished_andesite_slab",
            "minecraft:polished_deepslate_slab",
            "minecraft:polished_diorite_slab",
            "minecraft:polished_granite_slab",
            "minecraft:stone_slab",
            "minecraft:stone_brick_slab",
        ],
    },
    "materials_integration:rock_integration": {
        "block": [
            "minecraft:chiseled_stone_bricks",
            "minecraft:cracked_stone_bricks",
            "minecraft:mossy_stone_brick_stairs",
            "minecraft:mossy_stone_brick_wall",
            "minecraft:mossy_stone_bricks",
            "minecraft:smooth_stone",
            "minecraft:stone_brick_stairs",
            "minecraft:stone_brick_wall",
            "minecraft:stone_bricks",
            "minecraft:stone",
            "minecraft:deepslate",
        ],
        "slabs": [
            "minecraft:mossy_stone_brick_slab",
            "minecraft:smooth_stone_slab",
            "minecraft:stone_brick_slab",
        ],
    },
}


# --------------------------
# 生成逻辑
# --------------------------
def create_block_recipes(result, ingredient):
    """方块合成表"""
    namespace = result.split(":")[0]
    recipe = {
        "type": "minecraft:stonecutting",
        "ingredient": {"item": ingredient},
        "result": result,
        "count": 1,
    }
    if namespace != "minecraft":
        recipe.update(
            {
                "fabric:load_conditions": [
                    {"condition": "fabric:all_mods_loaded", "values": [namespace]}
                ],
                "conditions": [{"type": "forge:mod_loaded", "modid": namespace}],
            }
        )
    return recipe


def create_slab_recipes(result, ingredient):
    """台阶合成表"""
    namespace = result.split(":")[0]
    recipe = {
        "type": "minecraft:stonecutting",
        "ingredient": {"item": ingredient},
        "result": result,
        "count": 2,
    }
    if namespace != "minecraft":
        recipe.update(
            {
                "fabric:load_conditions": [
                    {"condition": "fabric:all_mods_loaded", "values": [namespace]}
                ],
                "conditions": [{"type": "forge:mod_loaded", "modid": namespace}],
            }
        )
    return recipe


def generate_recipes_tables():
    for category, config in category_config.items():
        # 处理方块
        for item in config.get("block", []):
            recipes = create_block_recipes(item, category)
            write_file(item, recipes)
        # 处理台阶
        for item in config.get("slab", []):
            recipes = create_slab_recipes(item, category)
            write_file(item, recipes)


def write_file(item, recipes):
    """写入JSON文件"""
    filename = item.split(":")[-1]
    path = os.path.join(output_dir, f"{filename}.json")
    with open(path, "w") as f:
        json.dump(recipes, f, indent=4)


if __name__ == "__main__":
    generate_recipes_tables()
    print("切石机配方生成完成！")
