package jeu.terralib;

import net.minecraft.client.MinecraftClient;

public class CommandUtils {
    private CommandUtils() {} // no instantiation
    public static void send(String command) {
        if (MinecraftClient.getInstance().getNetworkHandler() != null) {
            if(MinecraftClient.getInstance().player == null){
                System.out.println("wtsigma player is null ?!");
            }
            MinecraftClient.getInstance().player.networkHandler.sendChatMessage(command);
        }
    }
}
