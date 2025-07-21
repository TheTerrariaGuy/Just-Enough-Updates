package jeu;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import jeu.screens.SettingsGUI;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.awt.*;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ModCommands {

    private ModCommands(){}
    private static int delayTicks = -1;
    private static Runnable pendingAction = null;

    public static void init(){
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            registerCommand(dispatcher, "jeu", () -> MinecraftClient.getInstance().setScreen(new SettingsGUI(0)), 3);
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (delayTicks > 0) {
                delayTicks --;
            }
            if (delayTicks == 0 && pendingAction != null){
                pendingAction.run();
                pendingAction = null;
                delayTicks --;
            }
        });
    }

    private static void registerCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, String name, Runnable action, int delay) {
        dispatcher.register(literal(name).executes(context -> {
            delayTicks = delay;
            pendingAction = action;
            return Command.SINGLE_SUCCESS;
        }));
    }
}