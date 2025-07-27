package jeu.screens;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jeu.features.*;
import jeu.oopShits.Feature;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

import java.io.*;
import java.util.*;
// TODO: refactor to chandle new feature system
public class ModConfig {
    public static Map<String, Config> configs;
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "jeu_config.json");


    public static HashMap<String, Class<? extends Feature>> featureClasses = new HashMap<>(){{
        put("Chat Copy", ChatCopy.class); // something something
        put("Party Commands", PartyCommands.class);
        put("Dungeon Party Finder Stats", PartyFinderStats.class);
        put("Pet HUD", PetInfoHUD.class);
        put("Tree Progress", TreeProgressHUD.class);
        put("Pest HUD", PestCooldownHUD.class);
        put("Glowing Mushroom Highlight", GlowingMushroomDetector.class);
        put("Sweep Display", SweepDisplay.class);
//        put("Suppress Warnings in Logs", SuppressWarningsInLogs.class);
    }};

    /*
        Categories:
        - General
        - Farming
        - Foraging
        - Mining
        - Fishing
        - Combat
        - Dungeons
        - Misc
     */

    // these are default values
    public static Config[] features = new Config[] {
        // toggles
        new Config(
            "toggle",
            "Chat Copy",
            "Ctrl + Click to copy chat",
            "General",
            true
        ),
        new Config(
            "toggle",
            "Party Commands",
            "!ptme and instance joining commands (!f0/1/2/3/4/5/6/7, !m1/2/3/4/5/6/7, !t1/2/3/4/5)",
            "General",
            true
        ),
        new Config(
            "toggle",
            "Dungeon Party Finder Stats",
            "Displays total secrets and runs across all profiles for incoming party finder members",
            "Dungeons",
            true
        ),
        new Config(
            "toggle",
            "Glowing Mushroom Highlight",
            "Highlights glowing mushrooms in the glowing mushroom cave.",
            "Fishing",
            true
        ),

        // hud toggles
        new Config(
            "hudToggle",
            "Pet HUD",
            "Displays information for currently active pet (requires pet tab widget /tab for more information)",
            "General",
            true
        ),
            new Config("Pet HUD X", 550),
            new Config("Pet HUD Y", 480),
        new Config(
            "hudToggle",
            "Tree Progress",
            "Displays information for nearby trees",
            "Foraging",
            true
        ),
            new Config("Tree Progress X", 320),
            new Config("Tree Progress Y", 280),
        new Config(
            "hudToggle",
            "Pest HUD",
            "Displays information for pest cooldown",
            "Farming",
            true
        ),
            new Config("Pest HUD X", 320),
            new Config("Pest HUD Y", 280),
        new Config(
                "hudToggle",
                "Sweep Display",
                "Displays sweep based on sweep message from swoop (removes the message).",
                "Foraging",
                true
        ),
            new Config("Sweep Display X", 320),
            new Config("Sweep Display Y", 280),
        // dev stuff
//        new Config("toggle", "Suppress Warnings in Logs", "Disables the spammy warns in logs", false)
    };


    /*
        Style reminder:
        - For coodinate numbers, use [feature name] + " X/Y"

        Config types:
        - toggle: normal toggleable feature
        - hudToggle: toggle + 2 numbers associated with it
        - number: not actual config, can also be used to store random shits

     */
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
                        if (c.type.equals("toggle")) c.on = sc.on;
                        if (c.type.equals("hudToggle")) c.on = sc.on;
                        if (c.type.equals("number")) c.intValue = sc.intValue;
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
        public int intValue;

        public Config(String type, String name, String description) {
            this.type = type;
            this.name = Text.literal(name);
            this.description = Text.literal(description);
            this.on = true;
        }
        public Config(String type, String name, String description, String category, boolean on) {
            this.type = type;
            this.name = Text.literal(name);
            this.description = Text.literal(description);
            this.on = on;
            this.category = category;
        }
//        public Config(String type, String name, String description, String value) {
//            this.type = type;
//            this.name = Text.literal(name);
//            this.description = Text.literal(description);
//            this.value = value;
//        }

        // this config will only store a number, idk if this is most efficient
        // TODO refactor into hashmap across all?
        public Config(String name, int value){
            this.type = "number";
            this.name = Text.literal(name);
            this.intValue = value;
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
        public int intValue;
        public SerializableConfig() {}

        public SerializableConfig(Config c) {
            this.type = c.type;
            this.category = c.category;
            this.name = c.name != null ? c.name.getString() : null;
            this.description = c.description != null ? c.description.getString() : null;
            this.on = c.on;
            this.value = c.value;
            this.values = c.values;
            this.intValue = c.intValue;
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
            c.intValue = this.intValue;
            return c;
        }
    }
}