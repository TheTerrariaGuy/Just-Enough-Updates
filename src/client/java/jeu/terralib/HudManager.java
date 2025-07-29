package jeu.terralib;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HudManager {
    private static Map<String, HudElement> elements = new HashMap<>();
    private HudManager() {}

    // TODO: use iterators to loop render
    public static void init() {
        ClientPlayConnectionEvents.DISCONNECT.register((client, handler) -> {
            elements = new HashMap<>();
        });
//        System.out.println("wow such great init");
        // maybe use later

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
        return new ArrayList<>(elements.values());
    }

    public static HudElement getElement(String name){
        return elements.get(name);
    }

    public static void moveElement(String name, int x, int y){
//        System.out.println(name);

        HudElement e = elements.get(name);
        e.updateTextPosition(x - e.boxElement.width/2 + e.padding, y);
        e.updateBox(x - e.boxElement.width/2 + e.padding, y, e.boxElement.width, e.boxElement.height, e.boxElement.color);
    }

    public static HudElement addHudElement(String name, Text content, int x, int y, int padding, int color, boolean v) {
//        System.out.println("Adding HUD element: " + name + " at (" + x + ", " + y + ") with content: " + content);
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return null;
        int xOffset = HudElement.maxWidth(TextUtils.splitByLines(content))/2;
        HudElement.TextHudElement text = new HudElement.TextHudElement(content, x - xOffset, y, color);
        HudElement.BoxHudElement box = new HudElement.BoxHudElement(text, padding, 0x55000000);
        HudElement element = new HudElement(name, box, text, padding);
        element.visible = v;
        elements.put(name, element);
//        System.out.println("HUD element added, elements size: " + elements.size());
        return element;
    }

    public static HudElement makeHudElement(String name, Text content, int x, int y, int padding, int color){
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return null;
        int xOffset = HudElement.maxWidth(TextUtils.splitByLines(content))/2;
        HudElement.TextHudElement text = new HudElement.TextHudElement(content, x - xOffset, y, color);
        HudElement.BoxHudElement box = new HudElement.BoxHudElement(text, padding, 0x55000000);
        HudElement element = new HudElement(name, box, text, padding);
        element.visible = true;
        return element;
    }

    public static void addHudElement(HudElement element) {
        if (element != null) {
            elements.put(element.name, element);
        }
    }

    public static boolean hasElement(String name){
        return elements.containsKey(name);
    }

    public static void removeHudElement(HudElement element){
        if(element == null) return;
        elements.remove(element.name);
    }
    // TODO: refactor to use visible boolean
    public static class HudElement {
        public final String name;
        private TextHudElement textElement;
        private BoxHudElement boxElement;
        private boolean visible;
        private int padding;

        public boolean visible() {return visible;}

        public void setVisible(boolean b) {visible = b;}


        public HudElement(String name, BoxHudElement box, TextHudElement text, int padding) {
            this.name = name;
            this.boxElement = box;
            this.textElement = text;
            this.padding = padding;
        }

        public void updateText(Text newText) {
            this.textElement = new TextHudElement(newText, textElement.x, textElement.y, textElement.color);
            this.boxElement = new BoxHudElement(this.textElement, (this.boxElement.x - this.textElement.x), this.boxElement.color);
        }

        public void updateBox(int x, int y, int width, int height, int color) {
            this.boxElement = new BoxHudElement(x - padding, y - padding, width, height, color);
        }

        public void updateTextPosition(int x, int y){
            this.textElement = new TextHudElement(this.textElement.text(), x, y, this.textElement.color());
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
