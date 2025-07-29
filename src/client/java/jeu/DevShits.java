package jeu;

import com.mojang.brigadier.Command;
import jeu.features.GlowingMushroomDetector;
import jeu.features.PetInfoHUD;
import jeu.screens.SettingsGUI;
import jeu.terralib.CommandUtils;
import jeu.terralib.HologramUtils;
import jeu.terralib.TabList;
import jeu.terralib.WorldRenderUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;

import static net.minecraft.server.command.CommandManager.literal;

public class DevShits implements TabList.TabListener {
    public static DevShits INSTANCE = new DevShits();
    private static int delayTicks = -1;
    private static Runnable pendingAction = null;
    private static boolean isSigma = false;
    private static boolean logParticles; // TODO: remove later

    public static void init(){
        isSigma = true;
        System.out.println("wowowowow such pro");
        TabList.addListener("Area", INSTANCE);

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            registerCommand(dispatcher, "hud", () -> {
                debugSend("pets??: " + PetInfoHUD.INSTANCE.currentElement.visible());
            }, 3);
            registerCommand(dispatcher, "particles", () -> {
                logParticles = !logParticles;
            }, 3);
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

            var iterator = INSTANCE.debugDots.entrySet().iterator();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                int rem = entry.getValue();
                if (rem <= 1) {
                    iterator.remove();
                } else {
                    entry.setValue(rem - 1);
                }
            }
        });

        INSTANCE.startRender();
    }

    private void startRender(){
        WorldRenderEvents.AFTER_TRANSLUCENT.register(this::run);
    }

    private static void registerCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, String name, Runnable action, int delay) {
        dispatcher.register(ClientCommandManager.literal(name).executes(context -> {
            delayTicks = delay;
            pendingAction = action;
            return Command.SINGLE_SUCCESS;
        }));
    }

    public static void debugSend(String msg){
        if(!isSigma) return; // only sigmas allowed
        CommandUtils.sendDirectToChat(msg);
    }


    public HashMap<Pos, Integer> debugDots = new HashMap<>();

    public void run(WorldRenderContext context){
        var iterator = debugDots.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            Pos p = entry.getKey();
            double minX = p.x - 0.1f;
            double minY = p.y - 0.1f;
            double minZ = p.z - 0.1f;
            double maxX = minX + 0.2f;
            double maxY = minY + 0.2f;
            double maxZ = minZ + 0.2f;
            WorldRenderUtils.renderOutline(context, minX, minY, minZ, maxX, maxY, maxZ, new WorldRenderUtils.Style(0f, 0f, 1f, 0.7f, 2), false);
        }
    }

    public void addDebugPoint(Pos p, int ticks){
        if(!logParticles) return;
        debugDots.put(p, ticks);
    }

    public void addDebugPoint(Pos p){
        addDebugPoint(p, 100); // 60 frames of time default
    }

    @Override
    public void onTabUpdate(String key, Text data) {
        if(!"Area".equals(key)) return;
//        debugSend("Area updated: " + data.getString().split(":")[1].strip() + ".");
    }

    public static record Pos(double x, double y, double z) {}
}
