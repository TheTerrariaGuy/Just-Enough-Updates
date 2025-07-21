package jeu.oopShits;

import jeu.screens.ModConfig;
import jeu.terralib.HudManager;
import jeu.terralib.TabList;
import net.minecraft.text.Text;

import java.util.HashSet;

public abstract class FeatureHud extends Feature implements TabList.TabListener {

    public HudManager.HudElement currentElement, defaultElement;

    public HashSet<String> activeZones;

    public ModConfig.Config xConf,yConf;

    @Override
    public void onTabUpdate(String channel, Text info){
        if(activeZones.contains(info.getString().split(":")[1].strip())){
            currentElement.setVisible(true);
        }else{
            currentElement.setVisible(false);
        }
    }

    public void on(){
        super.on();
        currentElement.setVisible(true);
        updateElement();
    }

    public void off(){
        super.off();
        currentElement.setVisible(false);
    }

    public abstract HudManager.HudElement getDefaultElement();

    public abstract void updateElement();

}
