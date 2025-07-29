package jeu.oopShits;

import jeu.DevShits;
import jeu.screens.ModConfig;
import jeu.terralib.HudManager;
import jeu.terralib.TabList;
import net.minecraft.text.Text;

import java.util.HashSet;

public abstract class FeatureHud extends Feature  {

    public HudManager.HudElement currentElement, defaultElement;

    public ModConfig.Config xConf,yConf;

    @Override
    public void onTabUpdate(String channel, Text info){
        if("Area".equals(channel) && currentElement != null) {
            if (activeZones.contains(info.getString().split(":")[1].strip())) {
//                DevShits.debugSend("enabled: " + this.getClass().getName());
                currentElement.setVisible(true);
                updateElement();
            } else {
//                DevShits.debugSend("disabled: " + this.getClass().getName());
                currentElement.setVisible(false);
            }
        }
        super.onTabUpdate(channel, info);
    }

    public void on(){
        super.on();
//        if(currentElement != null) currentElement.setVisible(true);
        if (activeZones.contains(TabList.GeneralInfo.getArea())) {
//            DevShits.debugSend("enabled: " + this.getClass().getName());
            currentElement.setVisible(true);
        } else {
            currentElement.setVisible(false);
        }
        updateElement();
    }

    public void off(){
        super.off();
        if(currentElement != null) currentElement.setVisible(false);
    }

    public abstract HudManager.HudElement getDefaultElement();

    // TODO: refactor to do area checks here, require super instead
    public abstract void updateElement();
}
