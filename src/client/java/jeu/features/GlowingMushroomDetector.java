package jeu.features;

import jeu.mixin.client.LivingEntityAccessor;
import jeu.oopShits.Feature;
import jeu.terralib.HologramUtils;
import jeu.terralib.WorldRenderUtils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashSet;

public class GlowingMushroomDetector extends Feature {
    public static GlowingMushroomDetector INSTANCE = new GlowingMushroomDetector(); public static GlowingMushroomDetector getInstance(){return INSTANCE;}

    public void init() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(this::run);
    }

//    private static void runInstance(WorldRenderContext context){
//        INSTANCE.run(context);
//    }

    private void run(WorldRenderContext context) {
        if (!enabled) return;

        ArrayList<ArmorStandEntity> stands = HologramUtils.getAllHolograms();

        for (ArmorStandEntity s : stands) {
            // only let if the armor stand has more than 0 potion patricles associated
            if(s.getDataTracker().get(((LivingEntityAccessor) s).getPotionSwirls()).size() <= 0) continue;

            BlockPos block = s.getBlockPos(); // remember to change
            block.up();

            // shroom dimensions
            double minX = block.getX() + 0.3f;
            double minY = block.getY();
            double minZ = block.getZ() + 0.3f;
            double maxX = minX + 0.4f;
            double maxY = minY + 0.4f;
            double maxZ = minZ + 0.4f;

            WorldRenderUtils.renderOutline(context, minX, minY, minZ, maxX, maxY, maxZ, new WorldRenderUtils.Style(0f, 1f, 0f, 0.7f, 4), true);

        }
    }
}