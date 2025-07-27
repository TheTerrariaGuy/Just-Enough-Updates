package jeu.terralib;

import net.minecraft.text.Text;

import java.util.*;

public class ChatStuff {
    public interface ChatListener {
        boolean onMessage(Text message);
    }

    private static final Set<ChatListener> listeners = new HashSet<>();

    public static void addListener(ChatListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(ChatListener listener) {
        if(listener != null) listeners.remove(listener);
        else System.out.println("listeners is null");
    }

    public static boolean fireEvent(Text data) {
        if (listeners == null) return false;
        var it = listeners.iterator();
        boolean c = false;
        try {
            while (it.hasNext()) {
                ChatListener listener = it.next();
                boolean cancel = listener.onMessage(data);
                if(cancel) c = true;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return c;
    }
}
