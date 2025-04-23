import json
import os

output_dir = "blocks"
os.makedirs(output_dir, exist_ok=True)


# --------------------------
# 通用配置模板
# --------------------------
def create_general_recipe(main_item, drop_item, count=1):
    """通用物品掉落模板"""
    return {
        "type": "minecraft:block",
        "pools": [
            {
                "rolls": 1.0,
                "bonus_rolls": 0.0,
                "entries": [
                    {
                        "type": "minecraft:alternatives",
                        "children": [
                            silk_touch_entry(main_item),
                            survival_entry(main_item, drop_item, count),
                        ],
                    }
                ],
            }
        ],
    }


def create_log_recipe(main_item, drop_item, count=1):
    """专用原木落模板"""
    return {
        "type": "minecraft:block",
        "pools": [
            {
                "rolls": 1.0,
                "bonus_rolls": 0.0,
                "entries": [
                    {
                        "type": "minecraft:alternatives",
                        "children": [
                            silk_touch_entry(main_item),
                            survival_entry(main_item, drop_item, count),
                        ],
                    },
                ],
            },
        ],
    }


def create_slab_recipe(item, category):
    """专用台阶掉落模板"""
    return {
        "type": "minecraft:block",
        "pools": [
            {
                "rolls": 1.0,
                "bonus_rolls": 0.0,
                "entries": [
                    {
                        "type": "minecraft:alternatives",
                        "children": [
                            {
                                "type": "minecraft:item",
                                "conditions": [
                                    {
                                        "condition": "minecraft:block_state_property",
                                        "block": f"minecraft:{item}",
                                        "properties": {"type": "double"},
                                    },
                                    {
                                        "condition": "minecraft:match_tool",
                                        "predicate": {
                                            "enchantments": [
                                                {
                                                    "enchantment": "minecraft:silk_touch",
                                                    "levels": {"min": 1},
                                                }
                                            ]
                                        },
                                    },
                                ],
                                "name": f"minecraft:{item}",
                                "functions": [
                                    {"function": "minecraft:set_count", "count": 2},
                                    {"function": "minecraft:explosion_decay"},
                                ],
                            },
                            silk_touch_entry(item),
                            {
                                "type": "minecraft:item",
                                "conditions": [
                                    {
                                        "condition": "minecraft:block_state_property",
                                        "block": f"minecraft:{item}",
                                        "properties": {"type": "double"},
                                    },
                                    {"condition": "minecraft:survives_explosion"},
                                ],
                                "name": f"materials_integration:{category}_integration",
                            },
                            {
                                "type": "minecraft:item",
                                "conditions": [
                                    {"condition": "minecraft:survives_explosion"},
                                    {
                                        "condition": "minecraft:random_chance",
                                        "chance": 0.5,
                                    },
                                ],
                                "name": f"materials_integration:{category}_integration",
                            },
                        ],
                    }
                ],
            }
        ],
    }


def silk_touch_entry(item):
    """精准采集逻辑"""
    return {
        "type": "minecraft:item",
        "conditions": [
            {
                "condition": "minecraft:match_tool",
                "predicate": {
                    "enchantments": [
                        {"enchantment": "minecraft:silk_touch", "levels": {"min": 1}}
                    ]
                },
            }
        ],
        "name": f"minecraft:{item}",
    }


def survival_entry(item, drop_item, base_count, chance=None):
    """掉落逻辑"""
    entry = {
        "type": "minecraft:item",
        "conditions": [{"condition": "minecraft:survives_explosion"}],
        "name": f"materials_integration:{drop_item}",
    }

    if chance is not None:
        entry["conditions"].append(
            {"condition": "minecraft:random_chance", "chance": chance}
        )

    if base_count > 1:
        entry["functions"] = [{"function": "minecraft:set_count", "count": base_count}]

    return entry


# --------------------------
# 分类配置
# --------------------------
category_config = {
    "sand": {
        "base_items": ["gravel", "red_sand", "sand"],
        "special": {
            "sandstone": {
                "items": [
                    "sandstone",
                    "red_sandstone",
                    "cut_sandstone",
                    "cut_red_sandstone",
                    "chiseled_sandstone",
                    "sandstone_stairs",
                    "red_sandstone_stairs",
                    "sandstone_wall",
                    "red_sandstone_wall",
                    "smooth_sandstone",
                    "smooth_red_sandstone",
                ],
                "count": 4,
            }
        },
        "slabs": ["sandstone_slab", "red_sandstone_slab"],
    },
    "dirt": {
        "base_items": [
            "coarse_dirt",
            "dirt",
            "grass_block",
            "mud_brick_stairs",
            "mud_brick_wall",
            "mud_bricks",
            "mud",
            "mycelium",
            "packed_mud",
            "podzol",
            "rooted_dirt",
        ],
        "slabs": ["mud_brick_slab"],
    },
    "log": {
        "base_items": [
            "acacia_log",
            "acacia_wood",
            "birch_log",
            "birch_wood",
            "cherry_log",
            "cherry_wood",
            "dark_oak_log",
            "dark_oak_wood",
            "jungle_log",
            "jungle_wood",
            "mangrove_log",
            "mangrove_wood",
            "oak_log",
            "oak_wood",
            "spruce_log",
            "spruce_wood",
            "stripped_acacia_log",
            "stripped_acacia_wood",
            "stripped_birch_log",
            "stripped_birch_wood",
            "stripped_cherry_log",
            "stripped_cherry_wood",
            "stripped_dark_oak_log",
            "stripped_dark_oak_wood",
            "stripped_jungle_log",
            "stripped_jungle_wood",
            "stripped_mangrove_log",
            "stripped_mangrove_wood",
            "stripped_oak_log",
            "stripped_oak_wood",
            "stripped_spruce_log",
            "stripped_spruce_wood",
        ],
    },
    "planks": {
        "base_items": [
            "acacia_fence_gate",
            "acacia_fence",
            "acacia_planks",
            "acacia_stairs",
            "birch_fence_gate",
            "birch_fence",
            "birch_planks",
            "birch_stairs",
            "cherry_fence_gate",
            "cherry_fence",
            "cherry_planks",
            "cherry_stairs",
            "dark_oak_fence_gate",
            "dark_oak_fence",
            "dark_oak_planks",
            "dark_oak_stairs",
            "jungle_fence_gate",
            "jungle_fence",
            "jungle_planks",
            "jungle_stairs",
            "mangrove_fence_gate",
            "mangrove_fence",
            "mangrove_planks",
            "mangrove_stairs",
            "oak_fence_gate",
            "oak_fence",
            "oak_planks",
            "oak_stairs",
            "spruce_fence_gate",
            "spruce_fence",
            "spruce_planks",
            "spruce_stairs",
        ],
        "slabs": [
            "acacia_slab",
            "birch_slab",
            "cherry_slab",
            "dark_oak_slab",
            "jungle_slab",
            "mangrove_slab",
            "oak_slab",
            "spruce_slab",
        ],
    },
    "stone": {
        "base_items": [
            "andesite_stairs",
            "andesite_wall",
            "andesite",
            "calcite",
            "chiseled_deepslate",
            "cobbled_deepslate_stairs",
            "cobbled_deepslate_wall",
            "cobbled_deepslate",
            "cobblestone_stairs",
            "cobblestone_wall",
            "cobblestone",
            "deepslate_brick_stairs",
            "deepslate_brick_wall",
            "deepslate_bricks",
            "deepslate_tile_stairs",
            "deepslate_tile_wall",
            "deepslate_tiles",
            "deepslate",
            "diorite_stairs",
            "diorite_wall",
            "diorite",
            "dripstone_block",
            "granite_stairs",
            "granite_wall",
            "granite",
            "mossy_cobblestone_stairs",
            "mossy_cobblestone_wall",
            "mossy_cobblestone",
            "mossy_stone_brick_stairs",
            "mossy_stone_brick_wall",
            "mossy_stone_bricks",
            "polished_andesite_stairs",
            "polished_andesite",
            "polished_deepslate_stairs",
            "polished_deepslate_wall",
            "polished_deepslate",
            "polished_diorite_stairs",
            "polished_diorite",
            "polished_granite_stairs",
            "polished_granite",
            "smooth_stone",
            "stone_brick_stairs",
            "stone_brick_wall",
            "stone_bricks",
            "stone_stairs",
            "stone",
            "tuff",
        ],
        "slabs": [
            "andesite_slab",
            "cobbled_deepslate_slab",
            "cobblestone_slab",
            "deepslate_brick_slab",
            "deepslate_tile_slab",
            "diorite_slab",
            "granite_slab",
            "mossy_cobblestone_slab",
            "polished_andesite_slab",
            "polished_deepslate_slab",
            "polished_diorite_slab",
            "polished_granite_slab",
            "stone_slab",
            "stone_brick_slab",
        ],
    },
    "rock": {
        "base_items": [
            "chiseled_stone_bricks",
            "cracked_stone_bricks",
            "deepslate",
            "mossy_stone_brick_stairs",
            "mossy_stone_brick_wall",
            "mossy_stone_bricks",
            "smooth_stone",
            "stone_brick_stairs",
            "stone_brick_wall",
            "stone_bricks",
            "stone_stairs",
            "stone",
        ],
        "slabs": [
            "mossy_stone_brick_slab",
            "smooth_stone_slab",
            "stone_brick_slab",
            "stone_slab",
        ],
    },
}


# --------------------------
# 生成逻辑
# --------------------------
def generate_loot_tables():
    for category, config in category_config.items():
        # 处理基础方块
        for item in config.get("base_items", []):
            recipe = create_general_recipe(item, f"{category}_integration")
            write_file(item, recipe)
        # 处理特殊方块
        if "special" in config:
            for spec in config["special"].values():
                for item in spec["items"]:
                    recipe = create_general_recipe(
                        item, f"{category}_integration", spec["count"]
                    )
                    write_file(item, recipe)

        # 处理台阶类
        for slab in config.get("slabs", []):
            recipe = create_slab_recipe(slab, category)
            write_file(slab, recipe)


def write_file(filename, data):
    """写入JSON文件"""
    path = os.path.join(output_dir, f"{filename}.json")
    with open(path, "w") as f:
        json.dump(data, f, indent=4)


if __name__ == "__main__":
    generate_loot_tables()
    print("战利品表生成完成！")
