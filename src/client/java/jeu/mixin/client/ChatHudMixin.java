package jeu.mixin.client;

import jeu.features.ChatCopy;
import jeu.terralib.Notif;
import jeu.terralib.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatHudMixin {
    // text copy wowowowow so useful
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
//        System.out.println(JustEnoughUpdatesClient.mixinEnabled.get("ChatHudMixin"));
        if(!ChatCopy.INSTANCE.enabled) return; // no checking location for this feature
        if (!(MinecraftClient.getInstance().currentScreen instanceof ChatScreen)) return;

//        System.out.println("mouseClicked: " + mouseX + ", " + mouseY + ", button: " + button);

        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT &&
                InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL)) {
            String msgStr = TextUtils.getTextAt(mouseX, mouseY);
            if(!msgStr.isEmpty()){
                MinecraftClient.getInstance().keyboard.setClipboard(msgStr);
                Notif.queueNotif("Copied Text", msgStr);
//                System.out.println("copied text:" + msgStr);
            }else{
                System.out.println("warn: copied text is empty, this is probably not what you wanted");
            }
        }
    }


}
