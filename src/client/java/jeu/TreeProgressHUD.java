package jeu;

import jeu.screens.ModConfig;
import jeu.terralib.GeneralHelper;
import jeu.terralib.HologramUtils;
import jeu.terralib.HudManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

public class TreeProgressHUD {
    private static int tickCount;
    public static boolean enabled;
    public static HudManager.HudElement currentHud;

    public static void init(){
        tickCount = 0;
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            tickCount ++;
            if(tickCount > 4){ // 0.25 second
                tickCount = 0;
                refreshHud();
            }
        });
    }
    public static void on(){
        enabled = true;
    }
    public static void off(){
        enabled = false;
    }
    public static void refreshHud(){
        if (MinecraftClient.getInstance().player == null) return;
        ArrayList<ArmorStandEntity> headers = (ArrayList<ArmorStandEntity>) HologramUtils.getNearbyHolograms(15, 15, 50);
        Queue<HeaderWithDistance> sorted = new PriorityQueue<>();
        for(ArmorStandEntity header : headers){
            String t = header.getCustomName() == null ? header.getName().getString() : header.getCustomName().getString();
            if(t.matches("^.*TREE \\d+%$")){
                sorted.add(new HeaderWithDistance(header));
//                System.out.println("found tree");
            }
        }
        MutableText renderText = Text.empty();
        while(!sorted.isEmpty()){
            HeaderWithDistance d = sorted.poll();
            if(d.distance > 50){
                break;
            }
            if(renderText.getString().split("\n").length < 5){
                String t = d.header.getCustomName() == null ? d.header.getName().getString() : d.header.getCustomName().getString();
                renderText.append(d.header.getCustomName()).append(Text.literal("\n"));
            }
        }
        if(currentHud != null){
            HudManager.removeHudElement(currentHud);
        }
        if(!renderText.getString().isEmpty()) currentHud = HudManager.addHudElement("Tree Progress", renderText, GeneralHelper.safeParseInt(ModConfig.configs.get("Tree Progress X").value), GeneralHelper.safeParseInt(ModConfig.configs.get("Tree Progress Y").value), 3, 0xFFFFFF);
    }
    public static class HeaderWithDistance implements Comparable<HeaderWithDistance>{
        public ArmorStandEntity header;
        public float distance;
        public HeaderWithDistance(ArmorStandEntity header) {
            if (MinecraftClient.getInstance().player != null) {
                this.distance = MinecraftClient.getInstance().player.distanceTo(header);
            } else {
                this.distance = Float.MAX_VALUE; // Or another default value
            }
            this.header = header;
        }

        @Override
        public int compareTo(@NotNull TreeProgressHUD.HeaderWithDistance o) {
            if(this.distance > o.distance){
                return 1;
            } else if (this.distance == o.distance) {
                return 0;
            }else{
                return -1;
            }
        }
    }
}
