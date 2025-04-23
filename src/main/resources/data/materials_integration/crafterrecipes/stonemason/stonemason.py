import json
import os

# --------------------------
# 全局配置
# --------------------------
CONFIG = {
    "output_root": "integration",
    "categories": {
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
        "rock": {
            "integration": "rock_integration",
            "base_count": 1,
            "slab_count": 2,
            "entries": {
                "minecraft": [
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
        },
    },
}


# --------------------------
# 核心生成逻辑
# --------------------------
def generate_stonecutting_recipes():
    """生成石匠建材配方"""
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
        "crafter": "stonemason_crafting",
        "intermediate": "minecraft:air",
        "inputs": [{"item": f"materials_integration:{integration}", "count": count}],
        "result": f"{namespace}:{item}",
        "count": 1,
    }

    filename = f"{item}.json"
    filepath = os.path.join(output_dir, filename)

    with open(filepath, "w") as f:
        json.dump(recipe, f, indent=4)


if __name__ == "__main__":
    generate_stonecutting_recipes()
    print(f"配方生成完成！共处理 {len(CONFIG['categories'])} 个类别")
