package jeu.features;

import jeu.oopShits.Feature;
import net.minecraft.text.Text;

public class PartyFinderStats extends Feature {
    public static PartyFinderStats INSTANCE = new PartyFinderStats(); public static PartyFinderStats getInstance(){return INSTANCE;}
    public void onTabUpdateImplemented(String channel, Text info) {}
    public void init() {}
}
