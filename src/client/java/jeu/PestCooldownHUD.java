package jeu;

import jeu.screens.ModConfig;
import jeu.terralib.GeneralHelper;
import jeu.terralib.HudManager;
import jeu.terralib.TabList;
import jeu.terralib.TextUtils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.awt.geom.Area;
import java.util.HashMap;

public class PestCooldownHUD implements TabList.TabListener {
    private PestCooldownHUD(){};
    public static final PestCooldownHUD INSTANCE = new PestCooldownHUD();
    public static HudManager.HudElement pestElement;
    public static boolean enabled;
    private static HashMap<String, Text> pestInfo = new HashMap<>();
    private String displayString;

    public static void off() {
        enabled = false;
    }
    public static void on(){
        enabled = true;
        if(pestElement != null) INSTANCE.updatePestDisplay();
    }
    public static void init() {
        TabList.addListener("Area", INSTANCE);
        TabList.addListener("Cooldown", INSTANCE);
        pestInfo = new HashMap<>();
        pestInfo.put("Cooldown", Text.literal("None"));
        pestInfo.put("Area", Text.literal("None"));
    }

    public void updatePestDisplay() {
        System.out.println("triggered,"  + ": " + pestInfo.get("Area").getString());
        if(!pestInfo.get("Area").getString().strip().equals("Area: Garden")){
            if(HudManager.hasElement("Pest Cooldown")) HudManager.removeHudElement(pestElement);
            return;
        }
        System.out.println("triggered123");
        MutableText displayText = Text.empty().append(TextUtils.strip(pestInfo.get("Cooldown")));
        System.out.println("Updating Pest Cooldown HUD with: " + displayText.getString());
        if(pestElement != null) {
            HudManager.removeHudElement(pestElement);
        }
        pestElement = HudManager.addHudElement("Pest Cooldown", displayText, GeneralHelper.safeParseInt(ModConfig.configs.get("Pest HUD X").value), GeneralHelper.safeParseInt(ModConfig.configs.get("Pest HUD Y").value), 3, 0xFFFFFF);
    }

    @Override
    public void onTabUpdate(String key, Text data) {
        System.out.println(key);
        if(pestInfo.containsKey(key)) {
            pestInfo.put(key, data);
            updatePestDisplay();
        } else {
            System.out.println("PestCooldownHUD received unknown key: " + key + " with data: " + data);
        }
    }
}
