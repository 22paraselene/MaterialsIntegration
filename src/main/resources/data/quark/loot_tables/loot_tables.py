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
                                        "block": f"quark:{item}",
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
                                "name": f"quark:{item}",
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
                                        "block": f"quark:{item}",
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
        "name": f"quark:{item}",
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
    "log": {
        "base_items": [
            "ancient_wood",
            "azalea_wood",
            "blossom_wood",
            "stripped_ancient_wood",
            "stripped_azalea_wood",
            "stripped_blossom_wood",
            "ancient_log",
            "azalea_log",
            "blossom_log",
            "stripped_ancient_log",
            "stripped_azalea_log",
            "stripped_blossom_log",
        ],
    },
    "stone": {
        "base_items": [
            "limestone",
            "jasper",
            "permafrost",
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
