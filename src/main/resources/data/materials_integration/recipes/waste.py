import json
import os

# --------------------------
# 全局配置
# --------------------------
CONFIG = {
    "output_root": "crafting",
    "categories": {
        "waste": {
            "mods": {
                "quark": [
                    "ancient_leaves",
                    "blue_blossom_leaves",
                    "lavender_blossom_leaves",
                    "orange_blossom_leaves",
                    "red_blossom_leaves",
                    "yellow_blossom_leaves",
                ],
            },
            "entries": {
                "minecraft": [
                    "acacia_leaves",
                    "azalea_leaves",
                    "birch_leaves",
                    "cherry_leaves",
                    "dark_oak_leaves",
                    "dead_bush",
                    "fern",
                    "flowering_azalea_leaves",
                    "glow_lichen",
                    "grass",
                    "jungle_leaves",
                    "large_fern",
                    "mangrove_leaves",
                    "oak_leaves",
                    "seagrass",
                    "spruce_leaves",
                    "tall_grass",
                    "tall_seagrass",
                    "vine",
                ]
            },
        },
    },
}


# --------------------------
# 核心生成逻辑
# --------------------------
def crafting_shapeless_recipes():
    """生成所有农林废弃物配方"""
    for category, config in CONFIG["categories"].items():
        output_dir = os.path.join(CONFIG["output_root"], category)
        os.makedirs(output_dir, exist_ok=True)

        # 处理主配方
        for namespace, items in config["entries"].items():

            for item in items:
                create_recipe_file(
                    output_dir=output_dir,
                    namespace=namespace,
                    item=item,
                )

        # 处理MOD配方
        if "mods" in config:
            for mod_namespace, mod_items in config["mods"].items():
                for item in mod_items:
                    create_recipe_file(
                        output_dir=output_dir,
                        namespace=mod_namespace,
                        item=item,
                    )


def create_recipe_file(
    output_dir,
    namespace,
    item,
):
    """创建单个配方文件"""

    recipe = {
        "type": "minecraft:crafting_shapeless",
        "ingredients": [
            {
                "item": f"{namespace}:{item}",
            }
        ],
        "result": {"item": "materials_integration:waste"},
    }

    # 为非原版配方添加加载条件
    if namespace != "minecraft":
        mod_id = namespace
        recipe["fabric:load_conditions"] = [
            {"condition": "fabric:all_mods_loaded", "values": [mod_id]}
        ]
        recipe["conditions"] = [{"type": "forge:mod_loaded", "modid": mod_id}]

    filename = f"{namespace}_{item}.json"
    filepath = os.path.join(output_dir, filename)

    with open(filepath, "w") as f:
        json.dump(recipe, f, indent=4)


if __name__ == "__main__":
    crafting_shapeless_recipes()
    print("农林废弃物配方生成完成！共处理 {} 个类别".format(len(CONFIG["categories"])))
