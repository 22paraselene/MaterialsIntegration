import json
import os

# --------------------------
# 全局配置
# --------------------------
CONFIG = {
    "output_root": "stonecutting",
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
            "mods": {
                "quark": [
                    "ancient_log",
                    "ancient_wood",
                    "azalea_log",
                    "azalea_wood",
                    "blossom_log",
                    "blossom_wood",
                    "stripped_ancient_log",
                    "stripped_ancient_wood",
                    "stripped_azalea_log",
                    "stripped_azalea_wood",
                    "stripped_blossom_log",
                    "stripped_blossom_wood",
                ],
                "spawn": [
                    "rotten_log",
                    "rotten_wood",
                    "stripped_rotten_log",
                    "stripped_rotten_wood",
                ],
            },
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
            "base_count": 1,
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
    },
}


# --------------------------
# 核心生成逻辑
# --------------------------
def generate_stonecutting_recipes():
    """生成所有切石机配方"""
    for category, config in CONFIG["categories"].items():
        output_dir = os.path.join(CONFIG["output_root"], category)
        os.makedirs(output_dir, exist_ok=True)

        # 处理主配方
        for namespace, items in config["entries"].items():
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
        if "slabs" in config["entries"]:
            for slab in config["entries"]["slabs"]:
                create_recipe_file(
                    output_dir=output_dir,
                    namespace="minecraft",
                    item=slab,
                    integration=config["integration"],
                    count=config.get("slab_count", 2),
                )

        # 处理MOD配方
        if "mods" in config:
            for mod_namespace, mod_items in config["mods"].items():
                for item in mod_items:
                    create_recipe_file(
                        output_dir=output_dir,
                        namespace=mod_namespace,
                        item=item,
                        integration=config["integration"],
                        count=config["base_count"],
                    )


def create_recipe_file(output_dir, namespace, item, integration, count):
    """创建单个配方文件"""
    recipe = {
        "type": "minecraft:stonecutting",
        "ingredient": {"item": f"materials_integration:{integration}"},
        "result": f"{namespace}:{item}",
        "count": count,
    }

    # 为非原版配方添加加载条件
    if namespace != "minecraft":
        mod_id = namespace  # 假设命名空间与模组ID一致
        recipe["fabric:load_conditions"] = [
            {"condition": "fabric:all_mods_loaded", "values": [mod_id]}
        ]
        recipe["conditions"] = [{"type": "forge:mod_loaded", "modid": mod_id}]

    filename = f"{namespace}_{item}.json"
    filepath = os.path.join(output_dir, filename)

    with open(filepath, "w") as f:
        json.dump(recipe, f, indent=4)


if __name__ == "__main__":
    generate_stonecutting_recipes()
    print("切石机配方生成完成！共处理 {} 个类别".format(len(CONFIG["categories"])))
