package jeu.mixin.client;

import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;
import net.minecraft.client.gui.hud.ChatHudLine;

@Mixin(ChatHud.class)
public interface ChatHudAccessor {
    @Invoker("getMessageIndex")
    int callGetMessageIndex(double chatLineX, double chatLineY);
    @Invoker("getMessageLineIndex")
    int callGetMessageLineIndex(double chatLineX, double chatLineY);
    @Invoker("toChatLineX")
    double callToChatLineX(double chatLineX);
    @Invoker("toChatLineY")
    double callToChatLineY(double chatLineY);
    @Accessor("visibleMessages")
    List<ChatHudLine.Visible> getVisibleMessages();
}