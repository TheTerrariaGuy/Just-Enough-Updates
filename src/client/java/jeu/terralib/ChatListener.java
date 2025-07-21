package jeu.terralib;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

public class ChatListener {

    public static void init(){
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if(message.getString().matches("^Sending to server.*")){
                TabList.GeneralInfo.onLbbySwap(); // lobby swap auto sets the area to "none"
            }
        });
    }
}
