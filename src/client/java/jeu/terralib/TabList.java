package jeu.terralib;

import jeu.DevShits;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.text.Text;

import java.util.*;

public class TabList {
    public interface TabListener {
        void onTabUpdate(String key, Text data);
    }

    public static void init(){
//        System.out.println("init");
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            lobbyChange();
        });
        ClientPlayConnectionEvents.INIT.register((handler, client) -> {
            lobbyChange();
        });
    }


    public static class GeneralInfo implements TabListener { // for common info, eg area
//        public HashSet<String> COMBAT = new HashSet<>(){{
//
//        }};
        public static HashSet<String> ALL = new HashSet<>(){{
            add("Private Island");
            add("Garden");
            add("Hub");
            add("The Farming Islands");
            add("The Park");
            add("Spider's Den");
            add("The End");
            add("Crimson Isle");
            add("Gold Mine");
            add("Deep Caverns");
            add("Dwarven Mines");
            add("Crystal Hollows");
            add("Jerry's Workshop");
            add("Dungeon Hub");
            add("Rift Dimension"); // TODO: Check later
            add("Backwater Bayou");
            add("Galatea");
            add("Dungeon Hub");
        }};

        private static String area;
        public static String getArea(){
            if(area == null) return "none";
            return area;
        }

        public static void onLbbySwap(){
            area = "none";
        }

        @Override
        public void onTabUpdate(String key, Text data) {
            DevShits.debugSend("Lobby swap");
            if("Area".equals(key.strip())){
                area = data.getString().split(": ")[1].strip();
            }
            // TODO: find dungeon/kuudra/shaft identifier
        }
    }

    public static void lobbyChange(){
        fireEvent("Area", Text.empty().append(Text.literal("none"))); // sets to none
        fireEvent("LobbySwap", Text.empty().append(Text.literal(""))); // pings classes at lobbyswap
        GeneralInfo.area = "none"; // sets to none
    }

    // notif system
    private static final Map<String, List<TabListener>> listenersByChannel = new HashMap<>();

    public static void addListener(String channel, TabListener listener) {
        listenersByChannel.computeIfAbsent(channel, k -> new ArrayList<>()).add(listener);
//        System.out.println("current listener of " + channel + " length: " + listenersByChannel.get(channel).size());
    }

    public static void removeListener(String channel, TabListener listener) {
        List<TabListener> listeners = listenersByChannel.get(channel);
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                listenersByChannel.remove(channel);
            }
        }
    }

    public static void fireEvent(String channel, Text data) {
        List<TabListener> listeners = listenersByChannel.get(channel);
//        System.out.println("Firing event on channel: " + channel + ", with data: " + data.getString() + ", channel len: " + listeners.size());
        if (listeners != null) {
            var ls = listeners.iterator();
            try {
                while (ls.hasNext()) {
                    TabListener listener = ls.next();
//                    System.out.println("firing event " + channel + " for listener " + listener.getClass().getName());
                    listener.onTabUpdate(channel, data);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
