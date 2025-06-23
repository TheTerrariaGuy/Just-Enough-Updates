package jeu;

import jeu.screens.SettingsGUI;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ModCommands {

    private ModCommands(){}
    private static int delayTicks = -1;

    public static void init(){
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("jeu").executes(context -> {
                delayTicks = 3;
                return 1;
            }));
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (delayTicks > 0) {
                delayTicks --;
            }
            if (delayTicks == 0){
                MinecraftClient.getInstance().setScreen(new SettingsGUI());
                delayTicks --;
            }
        });

    }
}
