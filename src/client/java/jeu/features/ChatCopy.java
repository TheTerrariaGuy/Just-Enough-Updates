package jeu.features;

import jeu.oopShits.Feature;
import net.minecraft.text.Text;

public class ChatCopy extends Feature {
    public static ChatCopy INSTANCE = new ChatCopy(); public static ChatCopy getInstance(){return INSTANCE;}
    public void init() {}
    public void onTabUpdateImplemented(String channel, Text info) {}
}
