package jeu.features;

import jeu.oopShits.FeatureHud;
import jeu.screens.ModConfig;
import jeu.terralib.HudManager;
import jeu.terralib.TabList;
import jeu.terralib.TextUtils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.HashMap;

public class PetInfoHUD extends FeatureHud {
    private PetInfoHUD(){};
    private HashMap<String, Text> petInfo = new HashMap<>();
    public static PetInfoHUD INSTANCE = new PetInfoHUD(); public static PetInfoHUD getInstance(){return INSTANCE;}

    static {
        // set default hud values, since this class is a bit more complex
        INSTANCE.petInfo = new HashMap<>();
        INSTANCE.petInfo.put("Pet", Text.literal("Golden Dragon [Lv 200]"));
        INSTANCE.petInfo.put("Pet LV", Text.literal("0"));
        INSTANCE.petInfo.put("Pet XP", Text.literal("0/0 (100%)"));
    }

    public void init() {
        INSTANCE.activeZones = TabList.GeneralInfo.ALL; // all zones
        // add self as listener
        TabList.addListener("Pet", INSTANCE);
        TabList.addListener("Pet XP", INSTANCE);
    }

    // make sure not to run without screen
    public HudManager.HudElement getDefaultElement(){
        MutableText displayText = Text.empty().append(TextUtils.strip(petInfo.get("Pet"))).append(Text.literal("\n")).append(TextUtils.strip(petInfo.get("Pet XP")));
        defaultElement = HudManager.makeHudElement(
                "Pet HUD",
                displayText,
                ModConfig.configs.get("Pet HUD X").intValue,
                ModConfig.configs.get("Pet HUD Y").intValue,
                3,
                0xFFFFFF
        );
        return defaultElement;
    }

    public void updateElement() {
//        displayString = "[" + petInfo.get("Pet LV") + "] " + petInfo.get("Pet") + "\n" + petInfo.get("Pet XP");
        MutableText displayText = Text.empty().append(TextUtils.strip(petInfo.get("Pet"))).append(Text.literal("\n")).append(TextUtils.strip(petInfo.get("Pet XP")));
        System.out.println("Updating Pet Info HUD with: " + displayText.getString());
        // use \n as delimiter for new line
        if(currentElement != null) {
            // initialize the currentElement if it was null
            HudManager.removeHudElement(currentElement);
        }
//        System.out.println("Pet X:" + ModConfig.configs.get("Pet HUD X").value);
//        System.out.println("Pet Y:" + ModConfig.configs.get("Pet HUD Y").value);
        currentElement = HudManager.addHudElement(
                "Pet HUD",
                displayText,
                ModConfig.configs.get("Pet HUD X").intValue,
                ModConfig.configs.get("Pet HUD Y").intValue,
                3,
                0xFFFFFF,
                !notVeryOn()
        );
    }

    @Override
    public void onTabUpdateImplemented(String key, Text data) {
//        if(notVeryOn()) { // wow nested if, such bad programmer
//            if(currentElement != null && currentElement.visible()){
//                currentElement.setVisible(false);
//                return;
//            }
//        }
        if(petInfo.containsKey(key)) {
            petInfo.put(key, data);
            updateElement();
        } else {
            System.out.println("PetInfoHUD received unknown key: " + key + " with data: " + data);
        }
    }
}
