package jeu.terralib;

import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabList {
    public interface TabListener {
        void onTabUpdate(String key, Text data);
    }

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
        System.out.println("Firing event on channel: " + channel + " with data: " + data);
        if (listeners != null) {
            for (TabListener listener : new ArrayList<>(listeners)) {
                listener.onTabUpdate(channel, data);
            }
        }
    }
}
