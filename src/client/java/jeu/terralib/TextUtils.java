package jeu.terralib;

import jeu.mixin.client.ChatHudAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;


public final class TextUtils {
    public static MinecraftClient client;

    private TextUtils() {
    } // no constructing hehehehheh

    static {
        client = MinecraftClient.getInstance();
    }

    public record ColoredSegment(String text, TextColor color) {
    }

    public static List<ColoredSegment> flatten(Text text) {
        List<ColoredSegment> result = new ArrayList<>();
        Deque<Text> stack = new LinkedList<>();
        stack.push(text);

        while (!stack.isEmpty()) {
            Text current = stack.pop();
            TextColor color = current.getStyle().getColor();
            result.add(new ColoredSegment(current.getString(), color));

            List<Text> children = current.getSiblings();
            for (int i = children.size() - 1; i >= 0; i--) {
                stack.push(children.get(i));
            }
        }

        return result;
    }

    public static boolean matches(List<ColoredSegment> segments, List<ColoredSegment> pattern) {
        if (segments.size() < pattern.size()) return false;
        for (int i = 0; i < pattern.size(); i++) {
            var seg = segments.get(i);
            var pat = pattern.get(i);

            if (pat.color() != null && seg.color() != pat.color()) return false;
            if (!seg.text().equals(pat.text())) return false;
        }
        return true;
    }

    // uses screen coordinates
    // TODO: refactor to output a Text object instead of String for more flexibility
    public static String getTextAt(double mouseX, double mouseY) {

//        // what the sigma
        ChatHud chatHud = client.inGameHud.getChatHud();
        ChatHudAccessor hudAccessor = (ChatHudAccessor) chatHud;

        int index = hudAccessor.callGetMessageLineIndex(hudAccessor.callToChatLineX(mouseX), hudAccessor.callToChatLineY(mouseY));
        if (index < 0 || index >= ((ChatHudAccessor) chatHud).getVisibleMessages().size()) {
            return "";
        }
        String output = "";
        List<ChatHudLine.Visible> visibleMessages = collectMessageLines(hudAccessor.getVisibleMessages(), index);
        for (ChatHudLine.Visible line : visibleMessages) {
            output = cutLastIfSpace(extractString(line.content())) + "" + output;
        }
        return output;
    }
    private static String cutLastIfSpace(String str) {
        if (str.isEmpty()) return str;
        if (str.charAt(str.length() - 1) == ' ') {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }
    public static String extractString(OrderedText orderedText) {
        StringBuilder sb = new StringBuilder();
        orderedText.accept((charIndex, style, codePoint) -> {
            sb.appendCodePoint(codePoint);
            return true;
        });
        return sb.toString(); // remove the trailing newline character
    }

    public static List<ChatHudLine.Visible> collectMessageLines(
            List<ChatHudLine.Visible> visible, int startIndex
    ) {
        List<ChatHudLine.Visible> result = new ArrayList<>();
        if (startIndex < 0 || startIndex >= visible.size()) return result;

        int i = startIndex;
        while (i < visible.size() - 1 && !visible.get(i + 1).endOfEntry()) {
            i++;
        }
        int j = i;
        while (j >= 0) {
            ChatHudLine.Visible curr = visible.get(j);
            result.add(0, curr); // insert at front to maintain order
            if (curr.endOfEntry()) break;
            j--;
        }
        return result;
    }
}