package jeu.oopShits;

import jeu.DevShits;
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

    public void initCall() {
        TabList.addListener("Area", this);
//        System.out.println(this.getClass().getName() + " is now listening to Area channel");
        init();
    }

    public abstract void init();

    public HashSet<String> activeZones = TabList.GeneralInfo.ALL; // default all

    public boolean notVeryOn() { return (!enabled) || (!inZone); } // great name dont you think

    @Override
    public void onTabUpdate(String channel, Text info){
//        System.out.println(this.getClass().getName() + " received " + info.getString() + " at " + channel);
        try {
            if ("Area".equals(channel.strip())) {
                if (!info.getString().contains(":")) {
                    System.out.println("error: no colon detected");
                } else if (activeZones.contains(info.getString().split(":")[1].strip())) {
                    inZone = true;
                } else {
                    inZone = false;
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        onTabUpdateImplemented(channel, info);
//        DevShits.debugSend(this.getClass().getName() + " is in zone: " + inZone);
    }
    public abstract void onTabUpdateImplemented(String channel, Text info);
}
