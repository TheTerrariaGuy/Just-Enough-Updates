package jeu.screens;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

import java.io.*;
import java.util.*;

public class ModConfig {
    public static Map<String, Config> configs;
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "jeu_config.json");
    public static Config[] features = new Config[]{
            new Config("boolean", "Chat Copy", "Ctrl + Click to copy chat", true),
            new Config("boolean", "Party Commands", "!ptme and instance joining commands (!f0/1/2/3/4/5/6/7, !m1/2/3/4/5/6/7, !t1/2/3/4/5)", true),
            new Config("boolean", "Dungeon Party Finder Stats", "Displays total secrets and runs across all profiles for incoming party finder members", true),
            new Config("boolean", "Pet HUD", "Displays information for currently active pet (requires pet tab widget /tab for more information)", true),
            new Config("text", "Pet HUD X", "X position for the Pet HUD", "550"),
            new Config("text", "Pet HUD Y", "Y position for the Pet HUD", "480"),
            new Config("boolean", "Tree Progress", "Displays information for nearby trees", true),
            new Config("text", "Tree Progress X", "X position for the Tree Progress HUD", "320"),
            new Config("text", "Tree Progress Y", "Y position for the Tree Progress HUD", "280")
    };

    static {
        resetToDefault();
    }

    public static void resetToDefault(){
        configs = new HashMap<>();
        for (Config c : features) {
            configs.put(c.name.getString(), c);
        }
    }

    public static void save() {
        try (Writer writer = new FileWriter(CONFIG_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            List<SerializableConfig> serializableList = new ArrayList<>();
            for (Config c : configs.values()) {
                serializableList.add(new SerializableConfig(c));
            }
            gson.toJson(serializableList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        if (!CONFIG_FILE.exists()) return;
        try (Reader reader = new FileReader(CONFIG_FILE)) {
            Gson gson = new Gson();
            SerializableConfig[] loaded = gson.fromJson(reader, SerializableConfig[].class);
            if (loaded != null) {
                // Start with defaults
                resetToDefault();
                // Map loaded configs by name
                Map<String, SerializableConfig> loadedMap = new HashMap<>();
                for (SerializableConfig sc : loaded) {
                    loadedMap.put(sc.name, sc);
                }
                // Merge loaded values into defaults
                for (Config c : features) {
                    SerializableConfig sc = loadedMap.get(c.name.getString());
                    if (sc != null) {
                        if (c.type.equals("boolean")) c.on = sc.on;
                        if (c.type.equals("text")) c.value = sc.value;
                        // Add more type handling as needed
                    }
                }
                // Update configs map
                configs.clear();
                for (Config c : features) {
                    configs.put(c.name.getString(), c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resetToDefault();
            save();
        }
    }

    public static class Config {
        public String type, category;
        public Text name, description;
        public boolean on;
        public String value;
        public String[] values;

        public Config(String type, String name, String description) {
            this.type = type;
            this.name = Text.literal(name);
            this.description = Text.literal(description);
        }
        public Config(String type, String name, String description, boolean on) {
            this.type = type;
            this.name = Text.literal(name);
            this.description = Text.literal(description);
            this.on = on;
        }
        public Config(String type, String name, String description, String value) {
            this.type = type;
            this.name = Text.literal(name);
            this.description = Text.literal(description);
            this.value = value;
        }
        public Config() {}
    }

    // DTO for JSON serialization
    private static class SerializableConfig {
        public String type, category;
        public String name, description;
        public boolean on;
        public String value;
        public String[] values;

        public SerializableConfig() {}

        public SerializableConfig(Config c) {
            this.type = c.type;
            this.category = c.category;
            this.name = c.name != null ? c.name.getString() : null;
            this.description = c.description != null ? c.description.getString() : null;
            this.on = c.on;
            this.value = c.value;
            this.values = c.values;
        }

        public Config toConfig() {
            Config c = new Config();
            c.type = this.type;
            c.category = this.category;
            c.name = this.name != null ? Text.literal(this.name) : null;
            c.description = this.description != null ? Text.literal(this.description) : null;
            c.on = this.on;
            c.value = this.value;
            c.values = this.values;
            return c;
        }
    }
}