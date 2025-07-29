package jeu.features;

import jeu.oopShits.FeatureHud;
import jeu.screens.ModConfig;
import jeu.terralib.HudManager;
import jeu.terralib.TabList;
import jeu.terralib.TextUtils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.HashSet;

public class PestCooldownHUD extends FeatureHud {
    private PestCooldownHUD() {}
    private HashMap<String, Text> pestInfo = new HashMap<>();
    public static PestCooldownHUD INSTANCE = new PestCooldownHUD(); public static PestCooldownHUD getInstance(){return INSTANCE;}

    public HudManager.HudElement getDefaultElement() {
        defaultElement = HudManager.makeHudElement(
                "Pest HUD",
                Text.empty().append(Text.literal("Pest Cooldown: 999s")), // TODO: double check format
                ModConfig.configs.get("Pest HUD X").intValue,
                ModConfig.configs.get("Pest HUD Y").intValue,
                3,
                0xFFFFFF
        );
//        System.out.println("Pest Cooldown: created gui");
        return defaultElement;
    }

    public void init() {
        INSTANCE.activeZones = new HashSet<>(){{
            add("Garden");
        }};
//        TabList.addListener("Area", INSTANCE);
        TabList.addListener("Cooldown", INSTANCE);
        pestInfo = new HashMap<>();
        pestInfo.put("Cooldown", Text.literal("None"));
        pestInfo.put("Area", Text.literal("None"));
    }

    public void updateElement() {
        if(notVeryOn()){
            if(HudManager.hasElement("Pest HUD")) HudManager.removeHudElement(INSTANCE.currentElement);
            return;
        }
        MutableText displayText = Text.empty().append(TextUtils.strip(pestInfo.get("Cooldown")));
        if(INSTANCE.currentElement != null) {
            HudManager.removeHudElement(INSTANCE.currentElement);
        }
        INSTANCE.currentElement = HudManager.addHudElement(
                "Pest HUD",
                displayText,
                ModConfig.configs.get("Pest HUD X").intValue,
                ModConfig.configs.get("Pest HUD Y").intValue,
                3,
                0xFFFFFF,
                !notVeryOn()
        );
    }

    public void onTabUpdateImplemented(String key, Text data) {
//        System.out.println(key);
        if(pestInfo.containsKey(key)) {
            pestInfo.put(key, data);
            updateElement();
        } else {
            System.out.println("PestCooldownHUD received unknown key: " + key + " with data: " + data);
        }
    }
}
