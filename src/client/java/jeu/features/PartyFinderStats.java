package jeu.features;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jeu.oopShits.Feature;
import jeu.terralib.APIUtils;
import jeu.terralib.ChatStuff;
import jeu.terralib.CommandUtils;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

public class PartyFinderStats extends Feature implements ChatStuff.ChatListener {
    public static PartyFinderStats INSTANCE = new PartyFinderStats(); public static PartyFinderStats getInstance(){return INSTANCE;}
    public void onTabUpdateImplemented(String channel, Text info) {}
    public void init() {
        ChatStuff.addListener(this);
    }

    @Override
    public boolean onMessage(Text input) {
        if(PartyFinderStats.INSTANCE.notVeryOn()) return false;
        String message = input.getString().replaceAll("§.", ""); // remove pesky color codes
//        System.out.println(message);
        if(message.startsWith("Party Finder >") && message.contains(" has joined the dungeon group!")) {
            String[] parts = message.split(" ");
            String username = parts[3].contains("[") ? parts[4] : parts[3]; // wowwowowow so fancy, hopefully nothing breaks since im observing packets
            APIUtils.getUUID(username)
                    .thenCompose(uuid ->  APIUtils.accessAPI("skyblock/profiles",
                            new APIUtils.PARAM[]{ new APIUtils.PARAM("uuid", uuid)}).thenApply(playerData -> Map.entry(uuid, playerData)))
                    .thenAccept(entry -> {
                        String uuid = entry.getKey();
                        JsonObject playerData = entry.getValue().getAsJsonObject();
                        String chatText = "[HOVER]";
                        /*
                        - Total secret count across all profiles
                        - Total runs across all profiles
                        */
                        String text = "§6§lPlayer Data for " + username + ": §r\n";
                        int totalSecrets = 0; // total secrets across all profiles
                        HashMap<Integer, Integer> normalRuns = new HashMap<>(); // normal floor runs
                        HashMap<Integer, Integer> masterRuns = new HashMap<>(); // master floor runs
                        for (JsonElement profile : playerData.getAsJsonArray("profiles")) {
                            JsonObject members = profile.getAsJsonObject().getAsJsonObject("members");
                            JsonObject memberObj = members != null && members.has(uuid) ? members.getAsJsonObject(uuid) : null;
                            if (memberObj == null) continue;
                            JsonObject dungeons = memberObj.has("dungeons") ? memberObj.getAsJsonObject("dungeons") : null;
                            if (dungeons == null) continue;

                            int secrets = dungeons.has("secrets") && !dungeons.get("secrets").isJsonNull() ? dungeons.get("secrets").getAsInt() : 0;
                            totalSecrets += secrets;

                            JsonObject dungeonTypes = dungeons.has("dungeon_types") ? dungeons.getAsJsonObject("dungeon_types") : null;
                            if (dungeonTypes != null) {
                                JsonObject catacombs = dungeonTypes.has("catacombs") ? dungeonTypes.getAsJsonObject("catacombs") : null;
                                if (catacombs != null && catacombs.has("tier_completions")) {
                                    JsonObject tiers = catacombs.getAsJsonObject("tier_completions");
                                    for (int i = 1; i <= 7; i++) {
                                        int prev = normalRuns.getOrDefault(i, 0);
                                        int add = tiers.has(String.valueOf(i)) && !tiers.get(String.valueOf(i)).isJsonNull() ? tiers.get(String.valueOf(i)).getAsInt() : 0;
                                        normalRuns.put(i, prev + add);
                                    }
                                }
                                JsonObject masterCatacombs = dungeonTypes.has("master_catacombs") ? dungeonTypes.getAsJsonObject("master_catacombs") : null;
                                if (masterCatacombs != null && masterCatacombs.has("tier_completions")) {
                                    JsonObject tiers = masterCatacombs.getAsJsonObject("tier_completions");
                                    for (int i = 1; i <= 7; i++) {
                                        int prev = masterRuns.getOrDefault(i, 0);
                                        int add = tiers.has(String.valueOf(i)) && !tiers.get(String.valueOf(i)).isJsonNull() ? tiers.get(String.valueOf(i)).getAsInt() : 0;
                                        masterRuns.put(i, prev + add);
                                    }
                                }
                            }
                        }
                        String[] colors = new String[]{"§4", "§c", "§6", "§e", "§2", "§a"}; // 6 colors :sob:
                        int[] stages = new int[]{0, 500, 2500, 10000, 25000, 50000}; // stages for colors
                        String color = "§f"; // white, if this shows up something went wrong
                        for (int i = 0; i < stages.length; i++) {
                            if(stages[i] > totalSecrets) {
                                color = colors[i];
                                break;
                            }
                            if(i == stages.length - 1) {
                                color = colors[i]; // max color
                            }
                        }
                        text += color + "Secrets: " + totalSecrets + "\n \n"; // 50k secrets is the max
                        text += "§6§lCompletions: §r" + "\n";
                        text += "§2Normal Floors";
                        for (int i = 1; i <= 7; i++) {
                            if (normalRuns.containsKey(i)) {
                                text += " | " + normalRuns.get(i);
                            }
                        }
                        text += " |\n§r§4Master Floors";
                        for (int i = 1; i <= 7; i++) {
                            if (masterRuns.containsKey(i)) {
                                text += " | " + masterRuns.get(i);
                            }
                        }
                        text += " |";

//                        System.out.println(chatText);
                        HoverEvent event = new HoverEvent.ShowText(Text.literal(text));
                        Text finaltext = Text.literal(chatText).setStyle(Text.literal(chatText).getStyle().withHoverEvent(event));
                        CommandUtils.send(finaltext);
                    });
        }
        return false;
    }
}
