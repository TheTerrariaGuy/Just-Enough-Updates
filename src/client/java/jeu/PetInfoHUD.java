package jeu;

import jeu.screens.ModConfig;
import jeu.terralib.HudManager;
import jeu.terralib.TabList;
import jeu.terralib.TextUtils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class PetInfoHUD implements TabList.TabListener{
    private PetInfoHUD(){};
    public static final PetInfoHUD INSTANCE = new PetInfoHUD();
    public static HudManager.HudElement petInfoElement;
    public static boolean enabled;
    private static HashMap<String, Text> petInfo = new HashMap<>();
    private String displayString;

    public static void dinit() {
//        TabList.removeListener("Pet", INSTANCE);
////        TabList.removeListener("Pet LV", INSTANCE);
//        TabList.removeListener("Pet XP", INSTANCE);
//        HudManager.removeHudElement(petInfoElement);
        enabled = false;
    }
    public static void on(){
        enabled = true;
    }
    public static void init() {
        TabList.addListener("Pet", INSTANCE);
//        TabList.addListener("Pet LV", INSTANCE);
        TabList.addListener("Pet XP", INSTANCE);
        petInfo = new HashMap<>();
        petInfo.put("Pet", Text.literal("None (turn on pet info in tab list settings)"));
        petInfo.put("Pet LV", Text.literal("0"));
        petInfo.put("Pet XP", Text.literal("0/0 (0%)"));
    }

    private void updatePetInfoDisplay() {
//        displayString = "[" + petInfo.get("Pet LV") + "] " + petInfo.get("Pet") + "\n" + petInfo.get("Pet XP");

        MutableText displayText = Text.empty().append(TextUtils.strip(petInfo.get("Pet"))).append(Text.literal("\n")).append(TextUtils.strip(petInfo.get("Pet XP")));
        System.out.println("Updating Pet Info HUD with: " + displayText.getString());
        // use \n as delimiter for new line
        if(petInfoElement == null && !HudManager.hasElement("Pet Info")) {
            // initialize the petInfoElement if it was null
            petInfoElement = HudManager.addHudElement("Pet Info", displayText, safeParseInt(ModConfig.configs.get("Pet HUD X").value), safeParseInt(ModConfig.configs.get("Pet HUD Y").value), 3, 0xFFFFFF);
        }else{

            petInfoElement.updateText(displayText);
        }
    }
    public static int safeParseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("bad number");
            return 10;
        }
    }
    @Override
    public void onTabUpdate(String key, Text data) {
        if(petInfo.containsKey(key)) {
            petInfo.put(key, data);
            updatePetInfoDisplay();
        } else {
            System.out.println("PetInfoHUD received unknown key: " + key + " with data: " + data);
        }
    }

    public class PetInfo implements Comparable{
        private String name, petItem, petCandy, petSkin;
        private int level;
        private double xp;
        boolean max;

        @Override
        public int compareTo(@NotNull Object o) {
            return -1;
        }
    }
}
