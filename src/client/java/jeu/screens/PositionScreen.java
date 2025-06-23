package jeu.screens;

import jeu.terralib.HudManager;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class PositionScreen extends Screen {
    HudManager.HudElement selected = null;


    public PositionScreen() {
        super(Text.literal("HUD Position"));
    }

    @Override
    protected void init() {

    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}

