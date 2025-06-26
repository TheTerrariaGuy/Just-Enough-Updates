package jeu.mixin.client;

import jeu.PetInfoHUD;
import jeu.TreeProgressHUD;
import jeu.terralib.HudManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(InGameHud.class)
public class HudRenderMixin {

    @Inject(method = "renderChat", at = @At("TAIL"))
    private void injectChatRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        for (HudManager.HudElement element : HudManager.getElements()) {
            if (element.name.equals("Pet Info") && !PetInfoHUD.enabled) continue;
            if (element.name.equals("Tree Progress") && !TreeProgressHUD.enabled) continue;
            element.render(context);
        }
    }
}
