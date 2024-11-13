package org.sam.heartsmod;

import com.google.gson.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Heartsmod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

    private static final Path CONFIG_PATH = Paths.get("config", "playerlives.json");

    // Stores player lives as a map of UUIDs to remaining lives
    public static Map<UUID, Integer> PLAYER_LIVES = new HashMap<>();

    public static void loadConfig() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                    UUID uuid = UUID.fromString(entry.getKey());
                    int lives = entry.getValue().getAsInt();
                    PLAYER_LIVES.put(uuid, lives);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveConfig() {
        JsonObject json = new JsonObject();
        PLAYER_LIVES.forEach((uuid, lives) -> json.addProperty(uuid.toString(), lives));

        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(json, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        loadConfig();
    }
}
