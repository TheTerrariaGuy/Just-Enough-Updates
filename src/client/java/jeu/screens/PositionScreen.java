package jeu.screens;

import jeu.oopShits.Feature;
import jeu.oopShits.FeatureHud;
import jeu.terralib.HudManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class PositionScreen extends Screen {
    public String selected;
    public int scroll;
    public ButtonWidget exitButton, resetButton, fullResetButton;
    public int currX, currY, initX, initY, lastMouseX, lastMouseY;
    public boolean mouseDown = false;
    private boolean added = false;

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(!isMouseOverButton(mouseX, mouseY)){
            mouseDown = true;
            lastMouseX = (int) mouseX;
            lastMouseY = (int) mouseY;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        mouseDown = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if(!HudManager.hasElement(selected)){
            try {
                if(!HudManager.hasElement(selected)){
                    Class<? extends Feature> c = ModConfig.featureClasses.get(selected);
//                    System.out.println(c.getName());
                    FeatureHud instance = (FeatureHud) c.getMethod("getInstance").invoke(null);
//                    System.out.println(instance);
                    HudManager.HudElement ele = instance.getDefaultElement();
//                    System.out.println(ele);
//                HudManager.HudElement ele = (HudManager.HudElement) c.getMethod("getDefaultElement").invoke(instance);
//                System.out.println("Created hud element: " + ele.name);
                    HudManager.addHudElement(ele);
                    added = true;
                }
            }
            catch (Exception e){
                e.printStackTrace();
                // if the error doesnt affect runtime, i dont need to fix it :smiling_imp:
            }
        }
        this.resetButton.visible = currX != initX || currY != initY;
        super.render(context, mouseX, mouseY, deltaTicks);
        exitButton.render(context, mouseX, mouseY, deltaTicks);
        fullResetButton.render(context, mouseX, mouseY, deltaTicks);
        resetButton.render(context, mouseX, mouseY, deltaTicks);

        if(mouseDown) {
            float dX = mouseX - lastMouseX; // handles dragging hopefully
            float dY = mouseY - lastMouseY;
            currX += dX;
            currY += dY;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }
//        System.out.println(HudManager.getElement(selected));
        HudManager.moveElement(selected, currX, currY);
        HudManager.getElement(selected).render(context); // renders in this context
    }

    // why am i not doing this in init? idk
    public PositionScreen(String selected, int lastScroll) {
        super(Text.literal("HUD Position"));
        this.selected = selected;
        scroll = lastScroll;
        currX = ModConfig.configs.get(selected + " X").intValue;
        currY = ModConfig.configs.get(selected + " Y").intValue;
        initX = currX;
        initY = currY;
        exitButton = ButtonWidget.builder(
                Text.literal("<"),
                btn -> {
                    close();
                }
        ).position(5, 5).size(20, 20).build();

        fullResetButton = ButtonWidget.builder(
                Text.literal("🔄"),
                btn -> {
                    currX = this.width/2;
                    currY = this.height/2;
                    HudManager.moveElement(selected, currX, currY);
                }
        ).position(30, 5).size(20, 20).build();

        resetButton = ButtonWidget.builder(
                Text.literal("✖"),
                btn -> {
                    currX = initX;
                    currY = initY;
                    HudManager.moveElement(selected, currX, currY);
                }
        ).position(55, 5).size(20, 20).build();
    }

    @Override
    protected void init() {
        addDrawableChild(exitButton);
        addDrawableChild(fullResetButton);
        addDrawableChild(resetButton);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true; // or false to disable closing with Escape
    }

    @Override
    public void close() {
        ModConfig.configs.get(selected + " X").intValue = currX;
        ModConfig.configs.get(selected + " Y").intValue = currY;
        ModConfig.save();
        if(added){
            HudManager.removeHudElement(HudManager.getElement(selected)); // cooked
//            System.out.println("removing hud element");
        }
        if (this.client != null) {
            this.client.setScreen(new SettingsGUI(scroll));
        }
    }

    private boolean isMouseOverButton(double mouseX, double mouseY) {
        return exitButton.isMouseOver(mouseX, mouseY)
                || resetButton.isMouseOver(mouseX, mouseY)
                || fullResetButton.isMouseOver(mouseX, mouseY);
    }
}

