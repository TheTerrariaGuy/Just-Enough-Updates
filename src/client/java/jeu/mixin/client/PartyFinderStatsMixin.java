package jeu.mixin.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jeu.JustEnoughUpdatesClient;
import jeu.features.PartyFinderStats;
import jeu.terralib.APIUtils;
import jeu.terralib.ChatStuff;
import jeu.terralib.CommandUtils;
import jeu.terralib.TabList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(ClientPlayNetworkHandler.class)
public class PartyFinderStatsMixin {
    @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
    public void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        if (!MinecraftClient.getInstance().isOnThread()) {
            return; // Only proceed on the render thread
        }
        if(ChatStuff.fireEvent(packet.content())){
            ci.cancel();
        }
    }

    // notif stuff

}
/*

Basic Player Data Format:

"profiles": [
    "members": [
        {uuid}: {
            "dungeons": {
                "dungeon_types": {
                    "catacombs": {
                        "tier_completions": {
                            "1":
                            "2":
                            "3":
                            ...
                        },
                    },
                    "master_catacombs": {
                        "tier_completions": {
                            "1":
                            "2":
                            "3":
                            ...
                        },
                    },
                }
                secrets: 0,
                runs: 0
            }
        }
    ]
]


 */


