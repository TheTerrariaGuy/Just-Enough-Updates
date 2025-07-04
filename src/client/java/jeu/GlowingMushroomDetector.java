package jeu;

import jeu.terralib.WorldRenderUtils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class GlowingMushroomDetector {
    private static boolean enabled = false;

    public static void on() {
        enabled = true;
    }

    public static void off() {
        enabled = false;
    }

    public static void toggle() {
        enabled = !enabled;
    }

    public static void init() {
//        WorldRenderEvents.AFTER_TRANSLUCENT.register(GlowingMushroomDetector::run);
    }

    private static void run(WorldRenderContext context) {
        if (!enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;
        if (context == null) return;

        MatrixStack matrices = context.matrixStack();
        BlockPos pos = client.player.getBlockPos();
        Vec3d camera = client.gameRenderer.getCamera().getPos();

        // Highlight the block the player is standing on
        WorldRenderUtils.renderBlockHighlight(matrices, camera, pos.down(), 0.0f, 1.0f, 0.0f, 1f);
    }
}