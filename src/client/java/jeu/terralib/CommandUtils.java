package jeu.terralib;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

import java.util.Deque;

public class CommandUtils {
    private CommandUtils() {} // no instantiation
    private static final Deque<String> commandQueue = new java.util.ArrayDeque<>();
    static {
        // runs at end of every client tick, manage queue
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(!commandQueue.isEmpty()) {
                String command = commandQueue.poll();                                                                                 
                if (command != null && !command.isEmpty()) {
                    sendBypassQueue(command);
                }
            }
        });
    }
    public static void send(String command) {
        commandQueue.add(command);
    }
    public static void sendBypassQueue(String command) {
        if (MinecraftClient.getInstance().getNetworkHandler() != null) {
            if(MinecraftClient.getInstance().player == null){
                System.out.println("wtsigma player is null ?!");
            }
            MinecraftClient.getInstance().player.networkHandler.sendChatMessage(command);
        }
    }
}
