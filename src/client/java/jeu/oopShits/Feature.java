package jeu.oopShits;

import jeu.screens.ModConfig;
import jeu.terralib.TabList;
import net.minecraft.text.Text;

import java.util.HashSet;

/*

 */


public abstract class Feature implements TabList.TabListener {

    public boolean enabled = false;

    public boolean inZone = false;

    public String name;

    public ModConfig.Config config;

    public void on() {
        enabled = true;
    }

    public void off() {
        enabled = false;
    }

    public void toggle() {
        enabled = !enabled;
    }

    public void init() {
        System.out.println("no init override: " + this.getClass().getName());
        TabList.addListener("Area", this);
    }

    public HashSet<String> activeZones = TabList.GeneralInfo.ALL; // default all

    public boolean notVeryOn() { return (!enabled) || (!inZone); } // great name dont you think

    @Override
    public void onTabUpdate(String channel, Text info){
        if(activeZones.contains(info.getString().split(":")[1].strip())){
            inZone = true;
        }else{
            inZone = false;
        }
    }
}
