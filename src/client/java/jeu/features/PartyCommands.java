package jeu.features;

import jeu.JustEnoughUpdatesClient;
import jeu.oopShits.Feature;
import jeu.terralib.CommandUtils;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

import java.util.HashMap;

public class PartyCommands extends Feature {
    public static PartyCommands INSTANCE = new PartyCommands(); public static PartyCommands getInstance(){return INSTANCE;}
    public static boolean isLeader;
    private static final HashMap<Integer, String> FLOORIDS = new HashMap<>() {{
        put(0, "ENTRANCE");
        put(1, "FLOOR_ONE");
        put(2, "FLOOR_TWO");
        put(3, "FLOOR_THREE");
        put(4, "FLOOR_FOUR");
        put(5, "FLOOR_FIVE");
        put(6, "FLOOR_SIX");
        put(7, "FLOOR_SEVEN");
    }};
    private static final HashMap<Integer, String> KUUDRAIDS = new HashMap<>() {{
        put(1, "NORMAL");
        put(2, "HOT");
        put(3, "BURNING");
        put(4, "FIERY");
        put(5, "INFERNAL");
    }};

    public void init() {
        isLeader = true;
//        System.out.println("PartyCommands initialized, listening for party messages...");
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
//            if(!INSTANCE.enabled) return;
            if(notVeryOn()) return;
            String msg = message.getString();
//            System.out.println("Received chat message: " + msg);
            PartyMessage processed = parsePartyMessage(msg);
            msg = msg.replace("§.", "");
            if(processed != null){
                System.out.println("Processed party message: " + processed.message + " | from " + processed.playerName);
//                System.out.println("Is leader: " + isLeader);
                /*
                    ===== Party Commands =====
                    - !ptme - transfer party to me
                    - !l - set myself as party leader
                    - !nl - set party not leader
                    - !warp / !w - warp party, add -f flag to force warp
                 */

                // transfer to other player
                if((processed.message.equals("!ptme") || processed.message.equals("!pt")) && isLeader && !processed.playerName.equals(JustEnoughUpdatesClient.USERNAME)){
                    CommandUtils.send("/party transfer " + processed.playerName);
                    isLeader = false;
                }

                // set status as leader
                if(processed.message.equals("!l")){
                    if(!processed.playerName.equals(JustEnoughUpdatesClient.USERNAME)){
                        isLeader = false;
                    }else{
                        isLeader = true;
                    }
                    CommandUtils.send("/pc [Just Enough Updates] " + JustEnoughUpdatesClient.USERNAME + " is now considered the party leader.");
                }

                // set status as not leader
                if(processed.message.equals("!nl")){
                    // dont change leader status if not the player
                    if(processed.playerName.equals(JustEnoughUpdatesClient.USERNAME)) {
                        isLeader = false;
                        CommandUtils.send("/pc [Just Enough Updates] " + JustEnoughUpdatesClient.USERNAME + " is now considered not the party leader.");
                    }
                }

                // warp party
                if ((processed.message.equals("!warp") || processed.message.equals("!w")) && isLeader) {
                    CommandUtils.send("/party warp");
                }

                // force warp party, add flag -f, for convenience, if u know mod messed up
                if (processed.message.equals("!warp -f") || processed.message.equals("!w -f")) {
                    CommandUtils.send("/party warp");
                }

                // commands with params
                String[] parts = processed.message.split(" ");
                if(parts[0].startsWith("!f")){
                    // forceable command
                    if(parts.length > 1 && parts[1].equals("-f") || isLeader){
                        int floor;
                        try {floor = Integer.parseInt(parts[0].substring(2,3));}
                        catch (NumberFormatException e) {
                            CommandUtils.send("/pc [Just Enough Updates] Bad format: give number after !f");
                            return; // invalid floor
                        }
                        if(floor < 0 || floor > 7){
                            CommandUtils.send("/pc [Just Enough Updates] smh bad floor number, must be between 0 and 7");
                            return; // invalid floor
                        }
                        if(floor > 0) CommandUtils.send("/pc [Just Enough Updates] Entering floor " + "" + floor);
                        else CommandUtils.send("/pc [Just Enough Updates] Entering entrance (why are you doing entrance)");
                        CommandUtils.send("/joindungeon CATACOMBS_" + FLOORIDS.get(floor));

                    }
                }

                if(parts[0].startsWith("!m")){
                    // forceable command
                    if(parts.length > 1 && parts[1].equals("-f") || isLeader){
                        int floor;
                        try {floor = Integer.parseInt(parts[0].substring(2,3));}
                        catch (NumberFormatException e) {
                            CommandUtils.send("/pc [Just Enough Updates] Bad format: give number after !m");
                            return; // invalid floor
                        }
                        if(floor < 1 || floor > 7){
                            if(floor == 0) CommandUtils.send("/pc [Just Enough Updates] me too i also want a m0");
                            else CommandUtils.send("/pc [Just Enough Updates] smh bad master floor number, must be between 1 and 7");
                            return; // invalid floor
                        }
                        CommandUtils.send("/pc [Just Enough Updates] Entering mastermode floor " + "" + floor);
                        CommandUtils.send("/joindungeon MASTER_CATACOMBS_" + FLOORIDS.get(floor));
                    }
                }

                if(parts[0].startsWith("!t")){
                    // forceable command
                    if(parts.length > 1 && parts[1].equals("-f") || isLeader){
                        int tier;
                        try {tier = Integer.parseInt(parts[0].substring(2,3));}
                        catch (NumberFormatException e) {
                            CommandUtils.send("/pc [Just Enough Updates] Bad format: give number after !t");
                            return; // invalid floor
                        }
                        if(tier < 1 || tier > 5){
                            CommandUtils.send("/pc [Just Enough Updates] smh bad tier number, must be between 1 and 5");
                            return; // invalid floor
                        }
                        CommandUtils.send("/pc [Just Enough Updates] Entering kuudra tier " + "" + tier);
                        CommandUtils.send("/joinkuudra KUUDRA_" + KUUDRAIDS.get(tier));
                    }
                }


            }
            if(msg.matches("^The party was transferred to.+")){
                System.out.println("Party transfer message detected, setting isLeader");
                String username = null;
                if(msg.contains(" by ")){
                    username = message.getString().replace("The party was transferred to ", "").split("by")[0].strip();
                }else if(msg.contains(" because ")){
                    username = message.getString().replace("The party was transferred to ", "").split("because")[0].strip();
                }
                if(username == null){
                    System.out.println("error: bad party message: " + msg);
                }else{
                    username = cleanUsername(username);
                    isLeader = username.equals(JustEnoughUpdatesClient.USERNAME);
                }
            }
            if(msg.matches("^(.{1,50}) has promoted (.{1,50}) to Party Leader.*")){
                System.out.println("Party transfer message detected, setting isLeader");
                String username = msg.split("has promoted")[1].split("to Party Leader")[0].strip(); // the person who got promoted
                username = cleanUsername(username);
                isLeader = username.equals(JustEnoughUpdatesClient.USERNAME);
            }
            if(msg.matches("^You have joined (.{1,50})'s party!$")){
                System.out.println("Joined party message detected, setting isLeader to false");
                isLeader = false;
            }
            if(msg.matches("^You have left the party.$")){
                System.out.println("Left party message detected, setting isLeader to true");
                isLeader = true;
            }
            if(msg.matches("^The party was disbanded because all invites expired and the party was empty.$")){
                System.out.println("Disband message detected, setting isLeader to true");
                isLeader = true;
            }
            if(msg.matches("^([^:]{1,50}) has disbanded the party!")){
                System.out.println("Disband message detected, setting isLeader to true");
                isLeader = true;
            }
            if(msg.matches("^You are not this party's leader!$")){
                System.out.println("Not party leader message detected, setting isLeader to false");
                isLeader = false;
            }

        });
    }

    private String cleanUsername(String username) {
        // Remove any leading or trailing whitespace and special characters
        String[] parts = username.split(" ");
        if(parts.length > 1) {
            // If there are multiple parts, join them with a single space
            username = parts[parts.length-1]; // in case of ranks
        }
        return username.trim().replaceAll("[^A-Za-z0-9_]", "");
    }

    private PartyMessage parsePartyMessage(String msg) {
        // Only process if message starts with the party color code prefix
        // Remove all color and formatting codes
        msg = msg.replaceAll("§.", "");
        if (!msg.startsWith("Party >")) return null;

        int colonIndex = msg.indexOf(':');
        if (colonIndex == -1) return null;

        String beforeColon = msg.substring(0, colonIndex).trim();
        String afterColon = msg.substring(colonIndex + 1).trim();

        String[] parts = beforeColon.split(" ");
        if (parts.length < 3 || !parts[0].equals("Party") || !parts[1].equals(">")) {
            return null;
        }

        // Find player name (after rank if present)
        String playerName = parts[2];
        if (playerName.startsWith("[")) {
            if (parts.length < 4) return null;
            playerName = parts[3];
        }
        playerName = cleanUsername(playerName);

        return new PartyMessage(afterColon, playerName);
    }
    public record PartyMessage(String message, String playerName) {}
}
/*


§9Party §8> §b[MVP] Imparellel§f: dps


 */

