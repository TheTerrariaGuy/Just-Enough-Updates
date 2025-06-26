package jeu.terralib;

import jeu.mixin.client.ChatHudAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.*;


public class TextUtils {
    public static MinecraftClient client;

    private TextUtils() {
    } // no constructing hehehehheh

    static {
        client = MinecraftClient.getInstance();
    }

    public static ArrayList<Text> splitByLines(Text text) {
        Stack<Text> linesStack = new Stack<>();
        Text[] siblings = text.getSiblings().toArray(new Text[0]);
        ArrayList<Text> lines = new ArrayList<>();
        for (int i = 0; i < siblings.length; i++) {
            if(siblings[i].getString().equals("\n")){
                MutableText line = Text.empty();
                while (!linesStack.isEmpty()) {
                    line.append(linesStack.pop());
                }
                lines.add(line);
            }else{
                linesStack.push(siblings[i]);
            }
        }
        MutableText line = Text.empty();
        while (!linesStack.isEmpty()) {
            line.append(linesStack.pop());
        }
        if(!line.getString().isEmpty()) lines.add(line);

        return lines;
    }

    public static Text strip(Text text) {
        Text[] siblings = text.getSiblings().toArray(new Text[0]);
        boolean strippable = false;
        if(siblings[0].getString().charAt(0) == ' '){
            strippable = true;
            siblings[0] = Text.literal(siblings[0].getString().substring(1)).setStyle(siblings[0].getStyle());
        }
        if(siblings[siblings.length-1].getString().charAt(siblings[siblings.length-1].getString().length()-1) == ' ' && siblings.length > 1){
            strippable = true;
            siblings[siblings.length-1] = Text.literal(siblings[siblings.length-1].getString().substring(0, siblings[siblings.length-1].getString().length()-1)).setStyle(siblings[siblings.length-1].getStyle());
        }
        if(strippable){
            MutableText line = Text.empty();
            for (int i = 0; i < siblings.length; i++) {
                line.append(siblings[i]);
            }
            return line;
        }
        return text;
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