import json
import os

# --------------------------
# 全局配置
# --------------------------
CONFIG = {
    "output_root": "integration",
    "categories": {
        "dirt": {
            "integration": "dirt_integration",
            "base_count": 1,
            "slab_count": 2,
            "entries": {
                "minecraft": [
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
        },
        "log": {
            "integration": "log_integration",
            "base_count": 1,
            "entries": {
                "minecraft": [
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
                ]
            },
        },
        "planks": {
            "integration": "planks_integration",
            "base_count": 4,
            "slab_count": 2,
            "entries": {
                "minecraft": [
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
        },
        "sand": {
            "integration": "sand_integration",
            "base_count": 1,
            "entries": {"minecraft": ["gravel", "red_sand", "sand"]},
        },
        "stone": {
            "integration": "stone_integration",
            "base_count": 1,
            "slab_count": 2,
            "entries": {
                "minecraft": [
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
                    "polished_andesite_stairs",
                    "polished_andesite",
                    "polished_deepslate_stairs",
                    "polished_deepslate_wall",
                    "polished_deepslate",
                    "polished_diorite_stairs",
                    "polished_diorite",
                    "polished_granite_stairs",
                    "polished_granite",
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
                ],
            },
        },
        "wooden_components": {
            "integration": "planks_integration",
            "recipes": {
                "door": {
                    "inputs": [{"item": "planks_integration", "count": 6}],
                    "result_count": 1,
                },
                "trapdoor": {
                    "inputs": [{"item": "planks_integration", "count": 6}],
                    "result_count": 2,
                },
                "fence": {
                    "inputs": [
                        {"item": "planks_integration", "count": 4},
                        {"item": "stick", "count": 2},
                    ],
                    "result_count": 3,
                },
                "fence_gate": {
                    "inputs": [
                        {"item": "planks_integration", "count": 2},
                        {"item": "stick", "count": 4},
                    ],
                    "result_count": 1,
                },
                "sign": {
                    "inputs": [
                        {"item": "planks_integration", "count": 6},
                        {"item": "stick", "count": 1},
                    ],
                    "result_count": 1,
                },
                "button": {
                    "inputs": [{"item": "planks_integration", "count": 1}],
                    "result_count": 1,
                },
                "pressure_plate": {
                    "inputs": [{"item": "planks_integration", "count": 2}],
                    "result_count": 1,
                },
            },
            "variants": [
                "acacia",
                "birch",
                "cherry",
                "dark_oak",
                "jungle",
                "mangrove",
                "oak",
                "spruce",
            ],
        },
        "hanging_signs": {
            "integration": "log_integration",
            "recipes": {
                "hanging_sign": {
                    "inputs": [
                        {"item": "log_integration", "count": 6},
                        {"item": "minecraft:chain", "count": 2},
                    ],
                    "result_count": 6,
                }
            },
            "variants": [
                "acacia",
                "birch",
                "cherry",
                "dark_oak",
                "jungle",
                "mangrove",
                "oak",
                "spruce",
            ],
        },
    },
}


# --------------------------
# 核心生成逻辑
# --------------------------
def generate_stonecutting_recipes():
    """生成建筑师建材配方"""
    for category, config in CONFIG["categories"].items():
        output_dir = os.path.join(CONFIG["output_root"], category)
        os.makedirs(output_dir, exist_ok=True)

        # 处理主配方
        for namespace, items in config.get("entries", {}).items():
            if namespace == "slabs":
                continue  # 单独处理台阶

            for item in items:
                create_recipe_file(
                    output_dir=output_dir,
                    namespace=namespace,
                    item=item,
                    integration=config["integration"],
                    count=config["base_count"],
                )

        # 处理台阶配方
        if "slabs" in config.get("entries", {}):
            for slab in config["entries"]["slabs"]:
                create_recipe_file(
                    output_dir=output_dir,
                    namespace="minecraft",
                    item=slab,
                    integration=config["integration"],
                    count=config.get("slab_count", 2),
                )


def create_recipe_file(output_dir, namespace, item, integration, count):
    """创建单个配方文件"""
    recipe = {
        "type": "recipe",
        "crafter": "builder_crafting",
        "intermediate": "minecraft:air",
        "inputs": [{"item": f"materials_integration:{integration}", "count": count}],
        "result": f"{namespace}:{item}",
        "count": 1,
    }

    filename = f"{item}.json"
    filepath = os.path.join(output_dir, filename)

    with open(filepath, "w") as f:
        json.dump(recipe, f, indent=4)


def generate_wooden_recipes():
    """生成木制品配方"""
    wooden_config = CONFIG["categories"]["wooden_components"]
    output_dir = os.path.join(CONFIG["output_root"], "wooden_components")
    os.makedirs(output_dir, exist_ok=True)

    recipe_types = list(wooden_config["recipes"].keys())
    variants = wooden_config["variants"]

    for variant in variants:
        for recipe_type in recipe_types:
            recipe = wooden_config["recipes"][recipe_type]
            inputs = recipe["inputs"]
            result_count = recipe["result_count"]

            # 处理输入项
            input_list = []
            for input_item in inputs:
                item_name = input_item["item"]
                item_count = input_item["count"]

                if item_name == "planks_integration":
                    item_namespace = "materials_integration"
                if item_name == "stick":
                    item_namespace = "minecraft"
                    item_name = "stick"
                else:
                    item_namespace = "materials_integration"

                input_list.append(
                    {"item": f"{item_namespace}:{item_name}", "count": item_count}
                )

            # 构建配方
            recipe_data = {
                "type": "recipe",
                "crafter": "builder_crafting",
                "intermediate": "minecraft:air",
                "inputs": input_list,
                "result": f"minecraft:{variant}_{recipe_type}",
                "count": result_count,
            }

            # 保存配方文件
            filename = f"{variant}_{recipe_type}.json"
            filepath = os.path.join(output_dir, filename)
            with open(filepath, "w") as f:
                json.dump(recipe_data, f, indent=4)


def generate_hanging_signs():
    """生成悬挂告示牌配方"""
    sign_config = CONFIG["categories"]["hanging_signs"]
    output_dir = os.path.join(CONFIG["output_root"], "hanging_signs")
    os.makedirs(output_dir, exist_ok=True)

    for variant in sign_config["variants"]:
        inputs = sign_config["recipes"]["hanging_sign"]["inputs"]
        result_count = sign_config["recipes"]["hanging_sign"]["result_count"]

        # 处理输入项
        input_list = []
        for input_item in inputs:
            item_name = input_item["item"]
            item_count = input_item["count"]

            if item_name == "log_integration":
                item_name = f"{variant}_log"
                item_namespace = "materials_integration"
            elif item_name == "minecraft:chain":
                item_namespace = "minecraft"
                item_name = "chain"
            else:
                item_namespace = "materials_integration"

            input_list.append(
                {"item": f"{item_namespace}:{item_name}", "count": item_count}
            )

        # 构建配方
        recipe_data = {
            "type": "recipe",
            "crafter": "builder_crafting",
            "intermediate": "minecraft:air",
            "inputs": input_list,
            "result": f"minecraft:{variant}_hanging_sign",
            "count": result_count,
        }

        # 保存配方文件
        filename = f"{variant}_hanging_sign.json"
        filepath = os.path.join(output_dir, filename)
        with open(filepath, "w") as f:
            json.dump(recipe_data, f, indent=4)


if __name__ == "__main__":
    generate_stonecutting_recipes()
    generate_wooden_recipes()
    generate_hanging_signs()
    print(f"配方生成完成！共处理 {len(CONFIG['categories'])} 个类别")
