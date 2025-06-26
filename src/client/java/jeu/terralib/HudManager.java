package jeu.terralib;

import jeu.PetInfoHUD;
import jeu.TreeProgressHUD;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class HudManager {
    private static List<HudElement> elements = new ArrayList<>();
    private HudManager() {}


    public static void init() {
        System.out.println("wow such great init");
//        HudRenderCallback.EVENT.register((DrawContext context, RenderTickCounter tickDelta) -> {
//            if(MinecraftClient.getInstance().player == null) return; // Prevent rendering if player is null
//            for(HudElement element : elements) {
//
//                if(element.name.equals("Pet Info") && !PetInfoHUD.enabled){
//                    continue;
//                }
//                if(element.name.equals("Tree Progress") && !TreeProgressHUD.enabled){
//                    continue;
//                }
//                element.render(context);
//            }
//        });
    }

    public static List<HudElement> getElements() {
        return elements;
    }

    public static HudElement addHudElement(String name, Text content, int x, int y, int padding, int color) {
//        System.out.println("Adding HUD element: " + name + " at (" + x + ", " + y + ") with content: " + content);
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return null;
        int xOffset = HudElement.maxWidth(TextUtils.splitByLines(content))/2;
        HudElement.TextHudElement text = new HudElement.TextHudElement(content, x - xOffset, y, color);
        HudElement.BoxHudElement box = new HudElement.BoxHudElement(text, padding, 0x55000000);
        HudElement element = new HudElement(name, box, text);
        elements.add(element);
//        System.out.println("HUD element added, elements size: " + elements.size());
        return element;
    }

    public static void addHudElement(HudElement element) {
        elements.add(element);
    }

    public static boolean hasElement(String name){
        return elements.stream().anyMatch(e -> e.name.equals(name));
    }

    public static void removeHudElement(HudElement element){
        if(element == null) return;
        for (int i = elements.size() - 1; i >= 0; i--) {
            if(element.name.equals(elements.get(i).name)){
                elements.remove(i);
//                System.out.println("removing");
            }
        }
    }

    public static class HudElement {
        public final String name;
        private TextHudElement textElement;
        private BoxHudElement boxElement;


        public HudElement(String name, BoxHudElement box, TextHudElement text) {
            this.name = name;
            this.boxElement = box;
            this.textElement = text;
        }

        public void updateText(Text newText) {
            this.textElement = new TextHudElement(newText, textElement.x, textElement.y, textElement.color);
            this.boxElement = new BoxHudElement(this.textElement, (this.boxElement.x - this.textElement.x), this.boxElement.color);
        }

        public void updateBox(int x, int y, int width, int height, int color) {
            this.boxElement = new BoxHudElement(x, y, width, height, color);
        }

        public void render(DrawContext context) {
            if(boxElement != null) boxElement.render(context);
            if(textElement != null) textElement.render(context);
        }

        public static record TextHudElement(List<Text> text, int x, int y, int color) {
            public TextHudElement(Text text, int x, int y, int color) {
                this(TextUtils.splitByLines(text), x, y, color);
            }
            public void render(DrawContext context) {
                TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                for (int i = 0; i < text.size(); i++) {
                    Text line = text.get(i);
                    int lineY = y + i * textRenderer.fontHeight;
                    context.drawTextWithShadow(textRenderer, line, x, lineY, color);
                }
            }
        }

        public static record BoxHudElement(int x, int y, int width, int height, int color) {
            public BoxHudElement(TextHudElement text, int padding, int color) {
                this(text.x - padding, text.y - padding,
                        maxWidth(text.text) + 2 * padding,
                        maxHeight(text.text) + 2 * padding,
                        color);
            }
            public void render(DrawContext context) {
                context.fill(x, y, x + width, y + height, color);
            }
        }

        private static int maxWidth(List<Text> text) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            int maxWidth = 0;
            for (Text line : text) {
                int lineWidth = textRenderer.getWidth(line);
                if (lineWidth > maxWidth) {
                    maxWidth = lineWidth;
                }
            }
            return maxWidth;
        }
        private static int maxHeight(List<Text> text) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            int lineCount = text.size();
            return lineCount * textRenderer.fontHeight;
        }
    }
}
