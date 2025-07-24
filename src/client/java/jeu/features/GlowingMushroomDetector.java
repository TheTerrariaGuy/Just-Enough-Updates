package jeu.features;

import jeu.oopShits.Feature;
import jeu.terralib.TabList;
import jeu.terralib.WorldRenderUtils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;

public class GlowingMushroomDetector extends Feature {
    public static GlowingMushroomDetector INSTANCE = new GlowingMushroomDetector(); public static GlowingMushroomDetector getInstance(){return INSTANCE;}
    private static HashSet<BlockPos> shrooms = new HashSet<BlockPos>();

    public void init() {
        activeZones = new HashSet<>(){{
            add("The Farming Islands");
        }};
        WorldRenderEvents.AFTER_TRANSLUCENT.register(this::run);
    }

//    private static void runInstance(WorldRenderContext context){
//        INSTANCE.run(context);
//    }

    public void onParicleAdd(ParticleEffect parameters, double x, double y, double z){
        if(notVeryOn()) return;
        if(parameters.getType() != ParticleTypes.ENTITY_EFFECT) return;
//        DevShits.INSTANCE.addDebugPoint(new DevShits.Pos(x, y, z));
        BlockPos pos = BlockPos.ofFloored(x, y, z);
        ClientWorld world = MinecraftClient.getInstance().world;
//        System.out.println("Particle: " + String.format("%.2f", x) + " " + String.format("%.2f", y) + " " + String.format("%.2f", z)
//                + ", Block type: " + world.getBlockState(pos)
//                + ", Particle type: " + Registries.PARTICLE_TYPE.getId(parameters.getType()));

        if(world.getBlockState(pos).isOf(Blocks.BROWN_MUSHROOM) || world.getBlockState(pos).isOf(Blocks.RED_MUSHROOM)) {
            System.out.println("Found Glowing Mushroom At: " + pos.toShortString());
            shrooms.add(pos);
            return;
        }
//        if(world.getBlockState(pos.down()).isOf(Blocks.BROWN_MUSHROOM) || world.getBlockState(pos.down()).isOf(Blocks.RED_MUSHROOM)) {
//            System.out.println("Found Glowing Mushroom At: " + pos.down().toShortString());
//            shrooms.add(pos.down());
//            return;
//        }
//        if(world.getBlockState(pos.up()).isOf(Blocks.BROWN_MUSHROOM) || world.getBlockState(pos.up()).isOf(Blocks.RED_MUSHROOM)) {
//            System.out.println("Found Glowing Mushroom At: " + pos.up().toShortString());
//            shrooms.add(pos.up());
//            return;
//        }
//

    }

    public void onBlockUpdate(BlockPos pos){
        if(notVeryOn()) return;
        if(!enabled) return;
        if(shrooms.contains(pos)){
            System.out.println("removing shroom at: " + pos.toShortString());
            shrooms.remove(pos);
        }
//        DevShits.debugSend("block update at: " + pos.getX() + ", " + pos.getY() + ", " +pos.getZ());
    }

    private void run(WorldRenderContext context) {
        if(notVeryOn()) {
            if(shrooms.size() != 0){ // clears shroom stash
                shrooms.removeAll(shrooms);
            }
            return;
        }

        for (BlockPos s : shrooms) {
            // shroom dimensions
            double minX = s.getX() + 0.3f;
            double minY = s.getY();
            double minZ = s.getZ() + 0.3f;
            double maxX = minX + 0.4f;
            double maxY = minY + 0.4f;
            double maxZ = minZ + 0.4f;

            WorldRenderUtils.renderOutline(context, minX, minY, minZ, maxX, maxY, maxZ, new WorldRenderUtils.Style(0f, 1f, 0f, 0.7f, 4), true);

        }
    }
}