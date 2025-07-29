package jeu.features;

import jeu.DevShits;
import jeu.oopShits.FeatureHud;
import jeu.screens.ModConfig;
import jeu.terralib.ChatStuff;
import jeu.terralib.HudManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;

import java.util.HashSet;

public class PickaxeCooldownHUD extends FeatureHud implements ChatStuff.ChatListener {
    public static PickaxeCooldownHUD INSTANCE = new PickaxeCooldownHUD(); public static PickaxeCooldownHUD getInstance(){return INSTANCE;}
    private static HashSet<String> pickaxeAbilities = new HashSet<>(){{
        add("Ability: Mining Speed Boost  RIGHT CLICK");
        add("Ability: Pickobulus  RIGHT CLICK");
        add("Ability: Maniac Miner  RIGHT CLICK");
    }};
    private static int cdTicks;


    public boolean onMessage(Text t){
        String s = t.getString();
        if(s.matches("^Your Pickaxe ability is on cooldown for .*s\\.")){
            int emp = Integer.parseInt(s.replace("Your Pickaxe ability is on cooldown for", "").replace(".", "").strip()) * 20;
            if(Math.abs(emp - cdTicks) > 20){
                cdTicks = emp;
            }
        }
        return false;
    }

    @Override
    public HudManager.HudElement getDefaultElement() {
        defaultElement = HudManager.makeHudElement(
                "Pickaxe Cooldown",
                Text.empty().append(Text.literal("Pickaxe Ability Cooldown: 999s")),
                ModConfig.configs.get("Pickaxe Cooldown X").intValue,
                ModConfig.configs.get("Pickaxe Cooldown Y").intValue,
                3,
                0xFFFFFF
        );
        return defaultElement;
    }

    @Override
    public void updateElement() {
        int cd = cdTicks/20;
        if(defaultElement != null) HudManager.removeHudElement(defaultElement);
        defaultElement = HudManager.addHudElement(
                "Pickaxe Cooldown",
                Text.empty()
                        .append(Text.literal(cd == 0 ? "READY" : cd + "s").setStyle(Style.EMPTY.withColor(Formatting.GREEN)))
                        .append(Text.literal("Pickaxe Ability Cooldown: ").setStyle(Style.EMPTY.withColor(Formatting.GOLD).withBold(true))),
                ModConfig.configs.get("Pickaxe Cooldown X").intValue,
                ModConfig.configs.get("Pickaxe Cooldown Y").intValue,
                3,
                0xFFFFFF,
                !notVeryOn()
        );
    }

    @Override
    public void init() {
        // Register in your mod initializer
        UseItemCallback.EVENT.register((player, world, hand) -> {
            // Handle right-click with item
            if(cdTicks > 0){
                return ActionResult.PASS;
            }
//            DevShits.debugSend("Right clicked with " + player.getStackInHand(hand).getCustomName().getString());
            ItemStack item = player.getStackInHand(hand);
            var loreList = item.getTooltip(Item.TooltipContext.DEFAULT, player, TooltipType.BASIC).iterator();
            boolean isPickaxe = false;
            int cd = -1;
            while (loreList.hasNext()){
                String line = loreList.next().getString();
//                DevShits.debugSend(line);
                if(isPickaxe && line.matches("^Cooldown: .*s")){
                    cd = Integer.parseInt(line.split(":")[1].replace("s", "").strip());
                    continue;
                }
                if(pickaxeAbilities.contains(line)){
                    isPickaxe = true;
                }
            }
            if(isPickaxe && cd != -1){
                cdTicks = cd * 20;
                updateElement();
            }
            return ActionResult.PASS;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(cdTicks > 0){
                cdTicks --;
                if(cdTicks % 20 == 0){
                    updateElement();
                }
            }
        });
    }

    @Override
    public void onTabUpdateImplemented(String channel, Text info) {}

}
