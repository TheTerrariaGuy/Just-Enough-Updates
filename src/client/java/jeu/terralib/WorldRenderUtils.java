//package jeu.terralib;
//
//import com.mojang.blaze3d.vertex.VertexFormat;
//import com.mojang.blaze3d.systems.RenderSystem;
//import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
//import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
//
//import net.minecraft.client.render.*;
//import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Vec3d;
//
//public class WorldRenderUtils {
//    public static void init(){
//        WorldRenderEvents.AFTER_ENTITIES.register((context) -> {
//            render(context);
//        });
//    }
//
//    public static void render(WorldRenderContext context) {
//        MatrixStack matrixStack = context.matrixStack();
//        Camera camera = context.camera();
//        Vec3d cameraPos = camera.getPos();
//        // make BufferedBuilder
//        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);
//        RenderSystem.setShader(GameRenderer::getPositionColorShader);
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.disableTexture();
//
//        // Example: draw a red box at (100, 64, 100)
//        BlockPos pos = new BlockPos(100, 64, 100);
//        Box box = new Box(pos).offset(-cameraPos.x, -cameraPos.y, -cameraPos.z);
//
//        drawBox(buffer, box, 1f, 0f, 0f, 1f);
//
//        BufferRenderer.drawWithShader(buffer.end());
//        RenderSystem.enableTexture();
//        RenderSystem.disableBlend();
//    }
//
//    private static void drawBox(VertexConsumer buffer, Box box, float r, float g, float b, float a) {
//        // Draw all 12 edges of the box
//        // You can also use `WorldRenderer.drawBox` if you prefer simpler code
//        WorldRenderer.drawBox(buffer, box, r, g, b, a);
//    }
//
//
//    public static record WorldHighlight(int x, int y, int z, boolean cull){
//
//    }
//}
