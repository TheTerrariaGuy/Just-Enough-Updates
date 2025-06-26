package jeu.mixin.client;

import jeu.terralib.TabList;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(ClientPlayNetworkHandler.class)
public class TabParserMixin {
    private static String currentTab;

    static{
        currentTab = "";
    }

    @Inject(method = "onPlayerList", at = @At("HEAD"))
    private void onPlayerList(PlayerListS2CPacket packet, CallbackInfo ci) {
        try{
            for (PlayerListS2CPacket.Entry entry : packet.getEntries()) {
                if (entry.displayName() != null) {
                    Text displayName = entry.displayName();
                    String text = displayName.getString().strip();
                    if(text.isEmpty()) continue;
                    if(text.matches("\\[Lvl \\d{1,3}\\].*")){
                        String[] petInfo = text.split("]");
                        TabList.fireEvent("Pet", displayName);
                        continue;
                    }
                    if(text.equals("MAX LEVEL") || text.matches("[\\d,.]+[km]?/[\\d,.]+[km]?\\s*.*\\(.*\\)")){
                        TabList.fireEvent("Pet XP", displayName); // give entire text, cant be bothered to parse it
                    }
                    String[] parts = text.split(":");
                    if(parts.length < 2 || parts[1].isEmpty()) continue; // username or header
                    TabList.fireEvent(parts[0].strip(), displayName);
                }
            }
        }
        catch (Exception e){
            // NO MORE LOGSSSS
        }
    }
}

/*
TAB CATEGORY NAMES:
- Pet
- Pet LV
- Pet XP
- Area
- Server
- Gems
- Fairy Souls
- Profile
- SB Level
- Bank
- Interest
 */


