package jeu.terralib;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;

import java.util.Deque;

public class CommandUtils {
    private CommandUtils() {} // no instantiation

    private static final Deque<QueuedCommand> commandQueue = new java.util.ArrayDeque<>();

    static {
        // runs at end of every client tick, manage queue
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!commandQueue.isEmpty()) {
                QueuedCommand msg = commandQueue.poll();
                if (msg != null) {
                    if(msg.count <= 0){
                        if (msg.command != null && !msg.command.getString().isEmpty()) {
                            sendTextBypassQueue(msg.command);
                        }
                    }else{
                        msg.count--;
                        commandQueue.addFirst(msg);
                    }
                }
            }
        });
    }

    public static void send(String command) {
        commandQueue.add(new QueuedCommand(Text.literal(command)));
    }

    public static void send(Text text) {
        commandQueue.add(new QueuedCommand(text));
    }

    public static void sendTextBypassQueue(Text text) {
//        if (MinecraftClient.getInstance().inGameHud != null) {
//            ChatHud chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();
//            chatHud.addMessage(text);
//        }
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.networkHandler.sendChatMessage(text.getString());
        }
    }

    public static class QueuedCommand {
        Text command;
        int count;
        public QueuedCommand(Text command) {
            this.command = command;
            this.count = 5;
        }
    }
}