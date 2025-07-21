package jeu.terralib;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import jeu.mixin.client.WorldRendererAccessor;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.OptionalDouble;

public class WorldRenderUtils {

    private static final Style style = new Style(1f, 0f, 0f, 0.5f, 4f);

    public static record Style(float red, float green, float blue, float alpha, float width){}

//    public static void renderFull(WorldRenderContext context, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha, float lineWidth, boolean culling){
////        renderFilled(context, minX, minY, minZ);
//    }
//
//    public static void renderFilled(WorldRenderContext context, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha, float lineWidth, boolean culling){
//
//    }
    // so much QOL wowow
    public static void renderOutline(WorldRenderContext context, BlockPos block, boolean culling) {
        renderOutline(context, block, style.red(), style.green(), style.blue(), style.alpha(), style.width(), culling);
    }

    public static void renderOutline(WorldRenderContext context, BlockPos block, float red, float green, float blue, float alpha, float lineWidth, boolean culling) {
        double minX = block.getX();
        double minY = block.getY();
        double minZ = block.getZ();
        double maxX = minX + 1;
        double maxY = minY + 1;
        double maxZ = minZ + 1;
        renderOutline(context, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha, lineWidth, culling);
    }

    public static void renderOutline(WorldRenderContext context, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, boolean culling) {
        renderOutline(context, minX, minY, minZ, maxX, maxY, maxZ, style.red(), style.green(), style.blue(), style.alpha(), style.width(), culling);
    }

    public static void renderOutline(WorldRenderContext context, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Style style, boolean culling) {
        renderOutline(context, minX, minY, minZ, maxX, maxY, maxZ, style.red(), style.green(), style.blue(), style.alpha(), style.width(), culling);
    }

    public static void renderOutline(WorldRenderContext context, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha, float lineWidth, boolean culling) {
        if (!((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer).getFrustum().isVisible(new Box(minX, minY, minZ, maxX, maxY, maxZ))) return;
        MatrixStack matrices = context.matrixStack();
        Vec3d camera = context.camera().getPos();
        matrices.push();
        matrices.translate(-camera.getX(), -camera.getY(), -camera.getZ());
        VertexConsumerProvider.Immediate consumers = (VertexConsumerProvider.Immediate) context.consumers();
        RenderLayer layer = RenderLayer.of(
                culling ? "cull_lines" : "lines",
                RenderLayer.DEFAULT_BUFFER_SIZE,
                false,
                false,
                RenderPipelines.LINES,
                RenderLayer.MultiPhaseParameters.builder()
                        .lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(lineWidth)))
                        .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                        .build(false)
        );
        VertexConsumer buffer = consumers.getBuffer(layer);
        VertexRendering.drawBox(matrices, buffer, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);

        consumers.draw(layer);
        matrices.pop();
    }



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

        // Set up rendering state
        RenderSystem.setShaderColor(red, green, blue, alpha);
        RenderSystem.lineWidth(lineWidth);

        // Get tessellator and buffer builder
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        // Bottom face
        addLine(buffer, matrix, 0, 0, 0, 1, 0, 0, red, green, blue, alpha);
        addLine(buffer, matrix, 1, 0, 0, 1, 0, 1, red, green, blue, alpha);
        addLine(buffer, matrix, 1, 0, 1, 0, 0, 1, red, green, blue, alpha);
        addLine(buffer, matrix, 0, 0, 1, 0, 0, 0, red, green, blue, alpha);

        // Top face
        addLine(buffer, matrix, 0, 1, 0, 1, 1, 0, red, green, blue, alpha);
        addLine(buffer, matrix, 1, 1, 0, 1, 1, 1, red, green, blue, alpha);
        addLine(buffer, matrix, 1, 1, 1, 0, 1, 1, red, green, blue, alpha);
        addLine(buffer, matrix, 0, 1, 1, 0, 1, 0, red, green, blue, alpha);

        // Vertical edges
        addLine(buffer, matrix, 0, 0, 0, 0, 1, 0, red, green, blue, alpha);
        addLine(buffer, matrix, 1, 0, 0, 1, 1, 0, red, green, blue, alpha);
        addLine(buffer, matrix, 1, 0, 1, 1, 1, 1, red, green, blue, alpha);
        addLine(buffer, matrix, 0, 0, 1, 0, 1, 1, red, green, blue, alpha);

        BuiltBuffer built = buffer.end();
        RenderLayer.getLines().draw(built);

        RenderSystem.lineWidth(1.0f);
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

        // Set up rendering state
        RenderSystem.setShaderColor(red, green, blue, alpha);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();

        // Bottom face (y = 0)
        BufferBuilder bottomBuffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bottomBuffer.vertex(matrix, 0, 0, 0).color(red, green, blue, alpha);
        bottomBuffer.vertex(matrix, 1, 0, 0).color(red, green, blue, alpha);
        bottomBuffer.vertex(matrix, 1, 0, 1).color(red, green, blue, alpha);
        bottomBuffer.vertex(matrix, 0, 0, 1).color(red, green, blue, alpha);

        BuiltBuffer bottomBuilt = bottomBuffer.end();
        RenderLayer.getTranslucent().draw(bottomBuilt);

        // Top face (y = 1)
        BufferBuilder topBuffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        topBuffer.vertex(matrix, 0, 1, 1).color(red, green, blue, alpha);
        topBuffer.vertex(matrix, 1, 1, 1).color(red, green, blue, alpha);
        topBuffer.vertex(matrix, 1, 1, 0).color(red, green, blue, alpha);
        topBuffer.vertex(matrix, 0, 1, 0).color(red, green, blue, alpha);
        BuiltBuffer topBuilt = topBuffer.end();
        RenderLayer.getTranslucent().draw(topBuilt);

        // North face (z = 0)
        BufferBuilder northBuffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        northBuffer.vertex(matrix, 0, 0, 0).color(red, green, blue, alpha);
        northBuffer.vertex(matrix, 0, 1, 0).color(red, green, blue, alpha);
        northBuffer.vertex(matrix, 1, 1, 0).color(red, green, blue, alpha);
        northBuffer.vertex(matrix, 1, 0, 0).color(red, green, blue, alpha);
        BuiltBuffer northBuilt = northBuffer.end();
        RenderLayer.getTranslucent().draw(northBuilt);

        // South face (z = 1)
        BufferBuilder southBuffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        southBuffer.vertex(matrix, 1, 0, 1).color(red, green, blue, alpha);
        southBuffer.vertex(matrix, 1, 1, 1).color(red, green, blue, alpha);
        southBuffer.vertex(matrix, 0, 1, 1).color(red, green, blue, alpha);
        southBuffer.vertex(matrix, 0, 0, 1).color(red, green, blue, alpha);
        BuiltBuffer southBuilt = southBuffer.end();
        RenderLayer.getTranslucent().draw(southBuilt);

        // West face (x = 0)
        BufferBuilder westBuffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        westBuffer.vertex(matrix, 0, 0, 1).color(red, green, blue, alpha);
        westBuffer.vertex(matrix, 0, 1, 1).color(red, green, blue, alpha);
        westBuffer.vertex(matrix, 0, 1, 0).color(red, green, blue, alpha);
        westBuffer.vertex(matrix, 0, 0, 0).color(red, green, blue, alpha);
        BuiltBuffer westBuilt = westBuffer.end();
        RenderLayer.getTranslucent().draw(westBuilt);

        // East face (x = 1)
        BufferBuilder eastBuffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        eastBuffer.vertex(matrix, 1, 0, 0).color(red, green, blue, alpha);
        eastBuffer.vertex(matrix, 1, 1, 0).color(red, green, blue, alpha);
        eastBuffer.vertex(matrix, 1, 1, 1).color(red, green, blue, alpha);
        eastBuffer.vertex(matrix, 1, 0, 1).color(red, green, blue, alpha);
        BuiltBuffer eastBuilt = eastBuffer.end();
        RenderLayer.getTranslucent().draw(eastBuilt);

        matrices.pop();
    }

    /**
     * Convenience method to render both outline and fill
     */
    public static void renderBlockHighlight(MatrixStack matrices, Vec3d camera, BlockPos pos,
                                            float red, float green, float blue, float alpha) {
        renderBlockFill(matrices, camera, pos, red, green, blue, alpha);
//        renderBlockOutline(matrices, camera, pos, red, green, blue, alpha, 2.0f);
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
