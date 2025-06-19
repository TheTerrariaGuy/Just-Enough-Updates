package jeu.mixin.client;

import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class PetHUDMixin {

    @Inject(method = "onPlayerList", at = @At("HEAD"))
    private void onPlayerList(PlayerListS2CPacket packet, CallbackInfo ci) {
        packet.getEntries().forEach(entry -> {
//            System.out.println(entry.toString());
        });
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