package jeu.terralib;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class WorldRenderUtils {

    /**
     * Renders a colored outline around a block
     * @param matrices The matrix stack for transformations
     * @param camera The camera position
     * @param pos The block position to highlight
     * @param red Red component (0.0-1.0)
     * @param green Green component (0.0-1.0)
     * @param blue Blue component (0.0-1.0)
     * @param alpha Alpha component (0.0-1.0)
     * @param lineWidth Width of the outline lines
     */

    public static void renderBlockOutline(MatrixStack matrices, Vec3d camera, BlockPos pos,
                                          float red, float green, float blue, float alpha, float lineWidth) {
        matrices.push();

        // Translate to block position relative to camera
        matrices.translate(pos.getX() - camera.x, pos.getY() - camera.y, pos.getZ() - camera.z);

        // Set up rendering state using direct OpenGL calls
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glLineWidth(lineWidth);
        GL11.glColor4f(red, green, blue, alpha);

        // Use immediate mode rendering for simplicity
        GL11.glBegin(GL11.GL_LINES);

        // Bottom face
        GL11.glVertex3f(0, 0, 0); GL11.glVertex3f(1, 0, 0);
        GL11.glVertex3f(1, 0, 0); GL11.glVertex3f(1, 0, 1);
        GL11.glVertex3f(1, 0, 1); GL11.glVertex3f(0, 0, 1);
        GL11.glVertex3f(0, 0, 1); GL11.glVertex3f(0, 0, 0);

        // Top face
        GL11.glVertex3f(0, 1, 0); GL11.glVertex3f(1, 1, 0);
        GL11.glVertex3f(1, 1, 0); GL11.glVertex3f(1, 1, 1);
        GL11.glVertex3f(1, 1, 1); GL11.glVertex3f(0, 1, 1);
        GL11.glVertex3f(0, 1, 1); GL11.glVertex3f(0, 1, 0);

        // Vertical edges
        GL11.glVertex3f(0, 0, 0); GL11.glVertex3f(0, 1, 0);
        GL11.glVertex3f(1, 0, 0); GL11.glVertex3f(1, 1, 0);
        GL11.glVertex3f(1, 0, 1); GL11.glVertex3f(1, 1, 1);
        GL11.glVertex3f(0, 0, 1); GL11.glVertex3f(0, 1, 1);

        GL11.glEnd();

        // Restore rendering state
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glLineWidth(1.0f);

        matrices.pop();
    }

    /**
     * Renders a filled block highlight with transparency
     * @param matrices The matrix stack for transformations
     * @param camera The camera position
     * @param pos The block position to highlight
     * @param red Red component (0.0-1.0)
     * @param green Green component (0.0-1.0)
     * @param blue Blue component (0.0-1.0)
     * @param alpha Alpha component (0.0-1.0)
     */
    public static void renderBlockFill(MatrixStack matrices, Vec3d camera, BlockPos pos,
                                       float red, float green, float blue, float alpha) {
        matrices.push();

        // Translate to block position relative to camera
        matrices.translate(pos.getX() - camera.x, pos.getY() - camera.y, pos.getZ() - camera.z);

        // Set up rendering state using direct OpenGL calls
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glColor4f(red, green, blue, alpha);

        // Use immediate mode rendering for simplicity
        GL11.glBegin(GL11.GL_QUADS);

        // Bottom face (y = 0)
        GL11.glVertex3f(0, 0, 0);
        GL11.glVertex3f(1, 0, 0);
        GL11.glVertex3f(1, 0, 1);
        GL11.glVertex3f(0, 0, 1);

        // Top face (y = 1)
        GL11.glVertex3f(0, 1, 1);
        GL11.glVertex3f(1, 1, 1);
        GL11.glVertex3f(1, 1, 0);
        GL11.glVertex3f(0, 1, 0);

        // North face (z = 0)
        GL11.glVertex3f(0, 0, 0);
        GL11.glVertex3f(0, 1, 0);
        GL11.glVertex3f(1, 1, 0);
        GL11.glVertex3f(1, 0, 0);

        // South face (z = 1)
        GL11.glVertex3f(1, 0, 1);
        GL11.glVertex3f(1, 1, 1);
        GL11.glVertex3f(0, 1, 1);
        GL11.glVertex3f(0, 0, 1);

        // West face (x = 0)
        GL11.glVertex3f(0, 0, 1);
        GL11.glVertex3f(0, 1, 1);
        GL11.glVertex3f(0, 1, 0);
        GL11.glVertex3f(0, 0, 0);

        // East face (x = 1)
        GL11.glVertex3f(1, 0, 0);
        GL11.glVertex3f(1, 1, 0);
        GL11.glVertex3f(1, 1, 1);
        GL11.glVertex3f(1, 0, 1);

        GL11.glEnd();

        // Restore rendering state
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);

        matrices.pop();
    }

    /**
     * Convenience method to render both outline and fill
     */
    public static void renderBlockHighlight(MatrixStack matrices, Vec3d camera, BlockPos pos,
                                            float red, float green, float blue, float alpha) {
        renderBlockFill(matrices, camera, pos, red, green, blue, alpha * 0.3f);
        renderBlockOutline(matrices, camera, pos, red, green, blue, alpha, 2.0f);
    }

    /**
     * Helper method to add a line to the buffer
     */
    private static void addLine(BufferBuilder buffer, Matrix4f matrix,
                                float x1, float y1, float z1, float x2, float y2, float z2,
                                float red, float green, float blue, float alpha) {
        buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha);
        buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha);
    }

    /**
     * Helper method to add a quad to the buffer
     */
    private static void addQuad(BufferBuilder buffer, Matrix4f matrix,
                                float x1, float y1, float z1, float x2, float y2, float z2,
                                float x3, float y3, float z3, float x4, float y4, float z4,
                                float red, float green, float blue, float alpha) {
        buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha);
        buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha);
        buffer.vertex(matrix, x3, y3, z3).color(red, green, blue, alpha);
        buffer.vertex(matrix, x4, y4, z4).color(red, green, blue, alpha);
    }

    /**
     * Example usage in a WorldRenderEvents.AFTER_TRANSLUCENT callback
     */
    public static void exampleUsage(MatrixStack matrices) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        Vec3d camera = client.gameRenderer.getCamera().getPos();
        BlockPos playerPos = client.player.getBlockPos();

        // Highlight the block the player is standing on in red
        renderBlockHighlight(matrices, camera, playerPos.down(), 1.0f, 0.0f, 0.0f, 0.8f);

        // Highlight blocks around the player in blue
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (x == 0 && z == 0) continue; // Skip center block
                BlockPos pos = playerPos.add(x, 0, z);
                renderBlockOutline(matrices, camera, pos, 0.0f, 0.0f, 1.0f, 0.5f, 1.0f);
            }
        }
    }
}