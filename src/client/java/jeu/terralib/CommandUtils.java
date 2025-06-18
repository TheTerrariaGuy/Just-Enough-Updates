package jeu.terralib;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;

import java.util.Deque;

public class CommandUtils {
    private CommandUtils() {} // no instantiation

    // Wrapper for queued messages
    public static class QueuedMessage {
        public final String stringMessage;
        public final Text textMessage;

        public QueuedMessage(String stringMessage) {
            this.stringMessage = stringMessage;
            this.textMessage = null;
        }
        public QueuedMessage(Text textMessage) {
            this.stringMessage = null;
            this.textMessage = textMessage;
        }
    }

    private static final Deque<QueuedMessage> commandQueue = new java.util.ArrayDeque<>();

    static {
        // runs at end of every client tick, manage queue
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!commandQueue.isEmpty()) {
                QueuedMessage msg = commandQueue.poll();
                if (msg != null) {
                    if (msg.stringMessage != null && !msg.stringMessage.isEmpty()) {
                        sendBypassQueue(msg.stringMessage);
                    } else if (msg.textMessage != null) {
                        sendTextBypassQueue(msg.textMessage);
                    }
                }
            }
        });
    }

    public static void send(String command) {
        commandQueue.add(new QueuedMessage(command));
    }

    public static void send(Text text) {
        commandQueue.add(new QueuedMessage(text));
    }

    public static void sendBypassQueue(String command) {
        if (MinecraftClient.getInstance().getNetworkHandler() != null && MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.networkHandler.sendChatMessage(command);
        }
    }

    public static void sendTextBypassQueue(Text text) {
        if (MinecraftClient.getInstance().inGameHud != null) {
            ChatHud chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();
            chatHud.addMessage(text);
        }
    }
}