import json
import os

output_dir = "blocks"
os.makedirs(output_dir, exist_ok=True)


# --------------------------
# 分类配置
# --------------------------
category_config = {
    "materials_integration:log_integration": {
        "block": [
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
        "door": [
            "quark:ancient_door",
            "quark:azalea_door",
            "quark:blossom_door",
        ],
        "trapdoor": [
            "quark:ancient_trapdoor",
            "quark:azalea_trapdoor",
            "quark:blossom_trapdoor",
        ],
    },
    "materials_integration:stone_integration": {
        "block": [
            "quark:limestone",
            "quark:jasper",
            "quark:permafrost",
        ],
    },
}


# --------------------------
# 通用配置模板
# --------------------------
def create_block_loot(block, item):
    """方块掉落表"""
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
                                        "condition": "minecraft:match_tool",
                                        "predicate": {
                                            "enchantments": [
                                                {
                                                    "enchantment": "minecraft:silk_touch",
                                                    "levels": {"min": 1},
                                                }
                                            ]
                                        },
                                    }
                                ],
                                "name": block,
                            },
                            {
                                "type": "minecraft:item",
                                "conditions": [
                                    {"condition": "minecraft:survives_explosion"}
                                ],
                                "name": item,
                            },
                        ],
                    }
                ],
            }
        ],
    }


def create_block_4_loot(block, item):
    """四倍方块掉落表"""
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
                                        "condition": "minecraft:match_tool",
                                        "predicate": {
                                            "enchantments": [
                                                {
                                                    "enchantment": "minecraft:silk_touch",
                                                    "levels": {"min": 1},
                                                }
                                            ]
                                        },
                                    }
                                ],
                                "name": block,
                            },
                            {
                                "type": "minecraft:item",
                                "conditions": [
                                    {"condition": "minecraft:survives_explosion"}
                                ],
                                "name": item,
                                "functions": [
                                    {"function": "minecraft:set_count", "count": 4}
                                ],
                            },
                        ],
                    }
                ],
            }
        ],
    }


def create_slab_loot(block, item):
    """台阶掉落表"""
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
                                        "block": block,
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
                                "name": block,
                                "functions": [
                                    {"function": "minecraft:set_count", "count": 2},
                                    {"function": "minecraft:explosion_decay"},
                                ],
                            },
                            {
                                "type": "minecraft:item",
                                "conditions": [
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
                                    }
                                ],
                                "name": block,
                            },
                            {
                                "type": "minecraft:item",
                                "conditions": [
                                    {
                                        "condition": "minecraft:block_state_property",
                                        "block": block,
                                        "properties": {"type": "double"},
                                    },
                                    {"condition": "minecraft:survives_explosion"},
                                ],
                                "name": item,
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
                                "name": item,
                            },
                        ],
                    }
                ],
            }
        ],
    }


def create_door_loot(block, item):
    """门掉落表"""
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
                                        "condition": "minecraft:match_tool",
                                        "predicate": {
                                            "enchantments": [
                                                {
                                                    "enchantment": "minecraft:silk_touch",
                                                    "levels": {"min": 1},
                                                }
                                            ]
                                        },
                                    }
                                ],
                                "name": block,
                            },
                            {
                                "type": "minecraft:item",
                                "conditions": [
                                    {"condition": "minecraft:survives_explosion"}
                                ],
                                "name": item,
                                "functions": [
                                    {"function": "minecraft:set_count", "count": 2}
                                ],
                            },
                        ],
                    }
                ],
            }
        ],
    }


def create_trapdoor_loot(block, item):
    """活板门掉落表"""
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
                                        "condition": "minecraft:match_tool",
                                        "predicate": {
                                            "enchantments": [
                                                {
                                                    "enchantment": "minecraft:silk_touch",
                                                    "levels": {"min": 1},
                                                }
                                            ]
                                        },
                                    }
                                ],
                                "name": block,
                            },
                            {
                                "type": "minecraft:item",
                                "conditions": [
                                    {"condition": "minecraft:survives_explosion"}
                                ],
                                "name": item,
                                "functions": [
                                    {"function": "minecraft:set_count", "count": 3}
                                ],
                            },
                        ],
                    }
                ],
            }
        ],
    }


# --------------------------
# 生成逻辑
# --------------------------
def generate_loot_tables():
    for category, config in category_config.items():
        # 处理方块
        for item in config.get("block", []):
            loot = create_block_loot(item, category)
            write_file(item, loot)
        # 处理台阶
        for item in config.get("slab", []):
            loot = create_slab_loot(item, category)
            write_file(item, loot)
        # 处理四倍掉落
        for item in config.get("block_4", []):
            loot = create_block_4_loot(item, category)
            write_file(item, loot)
        # 处理门
        for item in config.get("door", []):
            loot = create_door_loot(item, category)
            write_file(item, loot)
        # 处理活板门
        for item in config.get("trapdoor", []):
            loot = create_trapdoor_loot(item, category)
            write_file(item, loot)


def write_file(item, loot):
    """写入JSON文件"""
    filename = item.split(":")[-1]
    path = os.path.join(output_dir, f"{filename}.json")
    with open(path, "w") as f:
        json.dump(loot, f, indent=4)


if __name__ == "__main__":
    generate_loot_tables()
    print("战利品表生成完成！")
