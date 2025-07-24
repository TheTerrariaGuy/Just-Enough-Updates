package jeu.screens;

import jeu.JustEnoughUpdatesClient;
import jeu.ModCommands;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.ArrayList;

import static jeu.screens.ModConfig.features;

public class SettingsGUI extends Screen {
    ArrayList<FeatureCardWidget> widgets;
    private int scrollY = 0;
    private int maxScroll = 0;
    private final int padding = 10;
    private ButtonWidget exitButton;


    public SettingsGUI(int scroll) {
        super(Text.literal("Settings"));
        scrollY = scroll;
    }
    @Override
    public void removed() {
        super.removed();
        ModConfig.save();
        JustEnoughUpdatesClient.refreshFeatures();;
    }
    @Override
    public void init() {
        widgets = new ArrayList<>();
        this.clearChildren();
        int totalHeight = 20;
        if (features != null) {
            for (int i = 0; i < features.length; i++) {
                if(features[i].type.equals("number")) continue; // nunber = single number setting, dont need a card for it
                FeatureCardWidget widget = new FeatureCardWidget(features[i], padding);
                widgets.add(widget);
                totalHeight += widget.height;
                if (widget.toggleButton != null) {
                    this.addDrawableChild(widget.toggleButton);
                }
                if (widget.inputField != null) {
                    this.addDrawableChild(widget.inputField);
                }
                if (widget.positionButton != null) {
                    this.addDrawableChild(widget.positionButton);
                }
            }
        }
        totalHeight += padding;
        maxScroll = Math.max(0, totalHeight - this.height + padding);
        scrollY = 0;


        // Exit button (fixed, not affected by scroll)
        exitButton = ButtonWidget.builder(
                Text.literal("X"),
                btn -> {
                    if (this.client != null) this.client.setScreen(null);
                }
        ).position(5, 5).size(20, 20).build();
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        return; // do not render background
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int y = 20 - scrollY;
        for (FeatureCardWidget widget : widgets) {
            boolean visible = y + widget.height > 0 && y < this.height;
            if (widget.toggleButton != null) widget.toggleButton.visible = visible;
            if (widget.positionButton != null) widget.positionButton.visible = visible;

//            if (widget.inputField != null) { // no need for now
//                widget.inputField.visible = visible;
//                if(!isDifferent(widget)){
//                    widget.toggleButton.visible = false;
//                }
//            }

            if (visible) {
                widget.updateWidgetPositions(padding, y);
                widget.renderCard(context, padding, y);
            }
            y += widget.height;
        }
        super.render(context, mouseX, mouseY, deltaTicks);
        exitButton.render(context, mouseX, mouseY, deltaTicks);
    }

    public boolean isDifferent(FeatureCardWidget widget){
        try{
            return !widget.inputField.getText().equals(ModConfig.configs.get(widget.name.getString()).value);
        }
        catch (Exception e){
            return true;
        }

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (exitButton.mouseClicked(mouseX, mouseY, button)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }
    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollY -= (int) (verticalAmount * 20);
        scrollY = Math.max(0, Math.min(scrollY, maxScroll));
        return true;
    }

    public class FeatureCardWidget {
        public int width, height, padding;
        private String type;
        private final Text name;
        private final java.util.List<OrderedText> wrappedLines;
        private final ButtonWidget toggleButton;
        private final ButtonWidget positionButton;
        private final TextFieldWidget inputField;

        public FeatureCardWidget(ModConfig.Config config, int padding) {
            int screenWidth = SettingsGUI.this.width;
            this.name = config.name;
            this.type = config.type;
            this.padding = padding;
            width = screenWidth - 2 * padding;

            wrappedLines = SettingsGUI.this.textRenderer.wrapLines(
                    config.description, width / 2 - padding
            );

            height = wrappedLines.size() * SettingsGUI.this.textRenderer.fontHeight + 2 * padding + 20;
//            if (height < 50 + 2 * padding && "hudToggle".equals(type)) height = 60 + 2 * padding; // no need for this right now
            if (height < 20 + 2 * padding) height = 20 + 2 * padding;

            if ("toggle".equals(type)) {
                this.toggleButton = ButtonWidget.builder(
                        Text.literal(config.on ? "ON" : "OFF"),
                        btn -> {
                            config.on = !config.on;
                            btn.setMessage(Text.literal(config.on ? "ON" : "OFF"));
                            ModConfig.save();
                        }
                ).position(0, 0).size(25, 25).build();
                this.inputField = null;
                this.positionButton = null;
            } else if ("hudToggle".equals(type)) {
//                this.inputField = new TextFieldWidget(
//                        SettingsGUI.this.textRenderer,
//                        0, 0, width/2, 20,
//                        Text.literal("Enter value")
//                );
//                this.inputField.setText(config.value != null ? config.value : "");
//                this.toggleButton = ButtonWidget.builder(
//                        Text.literal("Update"),
//                        btn -> {
//                            config.value = inputField.getText();
//                            ModConfig.save();
//                        }
//                ).position(0, 0).size(60, 20).build();
                this.toggleButton = ButtonWidget.builder(
                        Text.literal(config.on ? "ON" : "OFF"),
                        btn -> {
                            config.on = !config.on;
                            btn.setMessage(Text.literal(config.on ? "ON" : "OFF"));
                            ModConfig.save();
                        }
                ).position(0, 0).size(25, 25).build();

                this.positionButton = ButtonWidget.builder(
                        Text.literal("✎"),
                        btn -> {
                            if (client != null) client.setScreen(new PositionScreen(this.name.getString(), scrollY));
                        }
                ).position(0, 0).size(25, 25).build();

                this.inputField = null;
            } else {
                this.toggleButton = null;
                this.inputField = null;
                this.positionButton = null;
            }
        }

        public void updateWidgetPositions(int x, int y) {
            if ("toggle".equals(type) && toggleButton != null) {
                toggleButton.setX(x + padding + width - 50);
                toggleButton.setY(y + padding);
            }
            if ("hudToggle".equals(type) && positionButton != null && toggleButton != null) {
//                inputField.setX(x + (width * 3) / 8 + 20);
//                inputField.setY(y + padding);
//                toggleButton.setX(x + padding + width - 85);
//                toggleButton.setY(y + padding + 25);
                toggleButton.setX(x + padding + width - 50);
                toggleButton.setY(y + padding);
                positionButton.setX(x + padding + width - 80);
                positionButton.setY(y + padding);
            }
        }

        public void renderCard(DrawContext context, int x, int y) {
            context.fill(x, y, x + width, y + height, 0x99222222);
            context.drawText(SettingsGUI.this.textRenderer, name, x + padding, y + padding, 0xFFFFFF, true);

            int yOffset = 0;
            for (OrderedText line : wrappedLines) {
                context.drawText(SettingsGUI.this.textRenderer, line, x + padding, y + 20 + yOffset, 0xAAAAAA, true);
                yOffset += SettingsGUI.this.textRenderer.fontHeight;
            }
        }
    }
}