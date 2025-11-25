package materials_integration;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class TagFileReader {

    private static final Gson GSON = new Gson();
    private static final Map<String, Set<Block>> TAG_CACHE = new HashMap<>();

    public static void loadAllTags() {
        // 定义所有需要加载的 tag
        String[] tagNames = {
                "dirt_drop", "dirt_drop_two", "dirt_drop_four", "dirt_drop_chance",
                "log_drop", "log_drop_two", "log_drop_four", "log_drop_chance",
                "planks_drop", "planks_drop_two", "planks_drop_four", "planks_drop_chance",
                "stone_drop", "stone_drop_two", "stone_drop_four", "stone_drop_chance",
                "rock_drop", "rock_drop_two", "rock_drop_four", "rock_drop_chance",
                "sand_drop", "sand_drop_two", "sand_drop_four", "sand_drop_chance"
        };

        for (String tagName : tagNames) {
            loadTag(tagName);
        }
    }

    private static void loadTag(String tagName) {
        String path = String.format("/data/%s/tags/blocks/%s.json", MaterialsIntegration.MODID, tagName);

        try (InputStream inputStream = TagFileReader.class.getResourceAsStream(path)) {
            if (inputStream == null) {
                return;
            }

            JsonObject json = GSON.fromJson(new InputStreamReader(inputStream), JsonObject.class);
            JsonArray values = json.getAsJsonArray("values");

            Set<Block> blocks = new HashSet<>();

            for (JsonElement element : values) {
                if (element.isJsonPrimitive()) {
                    // 简单字符串格式: "minecraft:dirt"
                    String blockId = element.getAsString();
                    addBlockFromId(blocks, blockId);
                } else if (element.isJsonObject()) {
                    // 对象格式: {"id": "quark:hollow_oak_log", "required": false}
                    JsonObject obj = element.getAsJsonObject();
                    if (obj.has("id")) {
                        String blockId = obj.get("id").getAsString();
                        // 忽略 required 字段，只要方块存在就添加
                        addBlockFromId(blocks, blockId);
                    }
                }
            }

            TAG_CACHE.put(tagName, blocks);

        } catch (Exception e) {
            // 静默处理错误
        }
    }

    private static void addBlockFromId(Set<Block> blocks, String blockId) {
        try {
            ResourceLocation location = ResourceLocation.parse(blockId);
            Block block = ForgeRegistries.BLOCKS.getValue(location);

            if (block != null) {
                blocks.add(block);
            }
        } catch (Exception e) {
            // 忽略解析错误
        }
    }

    public static boolean isBlockInTag(Block block, String tagName) {
        Set<Block> blocks = TAG_CACHE.get(tagName);
        return blocks != null && blocks.contains(block);
    }
}