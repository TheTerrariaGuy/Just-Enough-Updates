package jeu.terralib;

import net.minecraft.text.Text;

import java.awt.geom.Area;
import java.util.*;

public class TabList {
    public interface TabListener {
        void onTabUpdate(String key, Text data);
    }
    public static class GeneralInfo implements TabListener { // for common info, eg area
//        public HashSet<String> COMBAT = new HashSet<>(){{
//
//        }};
        public static HashSet<String> ALL = new HashSet<>(){{
            add("Private Island");
            add("Garden");
            add("Hub");
            add("The Barn");
            add("Mushroom Desert");
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
            if("Area".equals(key.strip())){
                area = data.getString().split(": ")[1].strip();
            }
            // TODO: find dungeon/kuudra/shaft identifier
        }
    }



    // notif system

    private static final Map<String, List<TabListener>> listenersByChannel = new HashMap<>();

    public static void addListener(String channel, TabListener listener) {
        listenersByChannel.computeIfAbsent(channel, k -> new ArrayList<>()).add(listener);
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
//        System.out.println("Firing event on channel: " + channel + " with data: " + data);
        if (listeners != null) {
            for (TabListener listener : new ArrayList<>(listeners)) {
                listener.onTabUpdate(channel, data);
            }
        }
    }
}
