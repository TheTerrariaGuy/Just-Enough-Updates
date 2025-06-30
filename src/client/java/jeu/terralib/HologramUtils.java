package jeu.terralib;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.Box;
import net.minecraft.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class HologramUtils {
//    public static List<ArmorStandEntity> hologramStands;
    private static int lastUpdate;
    private static ArrayList<ArmorStandEntity> cache;

    static {
//        hologramStands = new ArrayList<>();
        lastUpdate = 0;
    }

    public static void init(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(lastUpdate < 20) lastUpdate ++;
        });
    }

    public static ArrayList<ArmorStandEntity> getNearbyHolograms(double radius, double upper, double lower){
        if(cache != null && lastUpdate < 20){
            return cache;
        }
        lastUpdate = 0;
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.player == null) return new ArrayList<ArmorStandEntity>();
        double px = client.player.getX();
        double py = client.player.getY();
        double pz = client.player.getZ();

        double x1 = px - radius;
        double y1 = py - lower;
        double z1 = pz - radius;
        double x2 = px + radius;
        double y2 = py + upper;
        double z2 = pz + radius;

        Box area = new Box(x1, y1, z1, x2, y2, z2);
        List<ArmorStandEntity> nearbyEntities = client.world.getEntitiesByType(
                EntityType.ARMOR_STAND, // or null for all types
                area,
                entity -> true // optional filter, or more specific like e -> e instanceof LivingEntity
        );
        cache = (ArrayList<ArmorStandEntity>) nearbyEntities;
        return (ArrayList<ArmorStandEntity>) nearbyEntities;
    }

    public static ArrayList<ArmorStandEntity> getAllEntities(){
        return cache;
    }

}
