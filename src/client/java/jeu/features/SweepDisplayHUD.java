package jeu.features;

import jeu.oopShits.FeatureHud;
import jeu.screens.ModConfig;
import jeu.terralib.ChatStuff;
import jeu.terralib.HudManager;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashSet;

public class SweepDisplayHUD extends FeatureHud implements ChatStuff.ChatListener {
    public static SweepDisplayHUD INSTANCE = new SweepDisplayHUD(); public static SweepDisplayHUD getInstance(){return INSTANCE;}
    private static double lastSweep = -1;
    private static String lastPenalty = "none";
    private static double lastLogs = -1;

    /*
        Example:
            [07:30:42] [Render thread/INFO]: [System] [CHAT] Sweep Details: 422.42∮ Sweep
            [07:30:42] [Render thread/INFO]: [System] [CHAT]   Fig Tree Toughness: 7 16.78 Logs
            [07:30:43] [Render thread/INFO]: [System] [CHAT] Sweep Details: 422.42∮ Sweep
            [07:30:43] [Render thread/INFO]: [System] [CHAT]   Fig Tree Toughness: 3.5 17.97 Logs
            [07:30:43] [Render thread/INFO]: [System] [CHAT]   Wrong Style: -50% Sweep 8.99 Logs Cut the trunk first!!
            [07:30:43] [Render thread/INFO]: [System] [CHAT] Sweep Details: 422.42∮ Sweep
            [07:30:43] [Render thread/INFO]: [System] [CHAT]   Fig Tree Toughness: 7 16.78 Logs

            ∮
     */
    @Override
    public boolean onMessage(Text s){
        if(notVeryOn()) return false;
        boolean changed = false;
        String msg = s.getString().strip();
        if(msg.matches("^Sweep Details: .*")){
            lastSweep = Double.parseDouble(msg.split(": ")[1].split("∮")[0].strip());
            changed = true;
        }
        if(msg.matches("(?i)^[a-z]{0,10} Tree Toughness: .*")){
            lastLogs = Double.parseDouble(msg.split(": ")[1].split("Logs")[0].split(" ")[1].strip());
            lastPenalty = "none";
             changed = true;
        }
        if(msg.matches("Wrong Style: .*")){
            String[] temp = msg.split(": ")[1].split("Logs")[0].strip().split("Sweep");
            lastPenalty = temp[0].strip();
            lastLogs = Double.parseDouble(temp[1].strip());
            changed = true;
        }
        if(msg.matches("Axe throw: .*")){
            String[] temp = msg.split(": ")[1].split("Logs")[0].strip().split("Sweep");
            lastPenalty = temp[0].strip();
            lastLogs = Double.parseDouble(temp[1].strip());
            changed = true;
        }
        if(changed){
            updateElement();
            return true;
        }
        return false;
    }

    @Override
    public HudManager.HudElement getDefaultElement() {
        defaultElement =  HudManager.makeHudElement(
                "Sweep Display",
                Text.empty().append(Text.literal("Sweep: 999∮ (99 Logs, -99% Penalty)")),
                ModConfig.configs.get("Sweep Display X").intValue,
                ModConfig.configs.get("Sweep Display Y").intValue,
                3,
                0xFFFFFF
        );
        return defaultElement;
    }

    @Override
    public void updateElement() {
        Text currentInfo = Text.empty()
                .append(Text.literal("none".equals(lastPenalty) ? ")" : ", " + lastPenalty + " Penalty)").setStyle(Style.EMPTY.withBold(false).withColor(Formatting.GRAY)))
                .append(Text.literal(" (" + lastLogs + " Logs").setStyle(Style.EMPTY.withBold(false).withColor(Formatting.GRAY)))
                .append(Text.literal(lastSweep + "∮").setStyle(Style.EMPTY.withBold(false).withColor(Formatting.DARK_GREEN)))
                .append(Text.literal("Sweep: ").setStyle(Style.EMPTY.withBold(true).withColor(Formatting.DARK_GREEN)));
        if(lastSweep == -1 || lastLogs == -1){
            currentInfo = Text.empty().append(Text.literal("No last sweep data! Please break a log with sweep messages enabled!").setStyle(Style.EMPTY.withBold(false).withColor(Formatting.GRAY)));
        }

        if(currentElement != null) HudManager.removeHudElement(currentElement);
        currentElement = HudManager.addHudElement(
                "Sweep Display",
                currentInfo,
                ModConfig.configs.get("Sweep Display X").intValue,
                ModConfig.configs.get("Sweep Display Y").intValue,
                3,
                0xFFFFFF,
                !notVeryOn()
        );


    }

    @Override
    public void init() {
        activeZones = new HashSet<>(){{
            add("Galatea");
        }};
        ChatStuff.addListener(this);
    }

    @Override
    public void onTabUpdateImplemented(String channel, Text info) {

    }
}
