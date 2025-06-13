package jeu;

import jeu.terralib.CommandUtils;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

import java.util.HashMap;

public class PartyCommands {
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
    public static void init() {
        isLeader = false;
        System.out.println("PartyCommands initialized, listening for party messages...");
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            String msg = message.getString();
            System.out.println("Received chat message: " + msg);
            PartyMessage processed = parsePartyMessage(msg);
            if(processed != null){
                System.out.println("Processed party message: " + processed.message + " | from " + processed.playerName);
                System.out.println("Is leader: " + isLeader);
                /*
                    ===== Party Commands =====
                    - !ptme - transfer party to me
                    - !l - set myself as party leader
                    - !nl - set party not leader
                    - !warp / !w - warp party, add -f flag to force warp
                 */

                // transfer to other player
                if(processed.message.equals("!ptme") && isLeader && !processed.playerName.equals(JustEnoughUpdatesClient.USERNAME)){
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
            if(message.toString().matches("^The party was transferred to ([A-Za-z0-9_]{1,16})\\.$\n")){
                // im such a good coder
                String username = message.getString().replace("The party was transferred to ", "").replace(".", "").replace(" ", "");
                username = cleanUsername(username); // paranoia
                isLeader = username.equals(JustEnoughUpdatesClient.USERNAME);
            }
            if(message.toString().matches("^([A-Za-z0-9_]{1,16}) has promoted ([A-Za-z0-9_]{1,16}) to Party Leader\\.$")){
                String username = message.toString().split("has promoted")[1].replace("to Party Leader", "").replace(" ", ""); // the person who got promoted
                username = cleanUsername(username);
                isLeader = username.equals(JustEnoughUpdatesClient.USERNAME);
            }
        });
    }

    private static String cleanUsername(String username) {
        // Remove any leading or trailing whitespace and special characters
        return username.trim().replaceAll("[^A-Za-z0-9_]", "");
    }

    private static PartyMessage parsePartyMessage(String msg) {
        // Example message: Party > [MVP] Imparellel: dps // literally took this out of my logs
        String[] parts = msg.split(" ");
        if (parts.length < 4 || !parts[0].equals("Party") || !parts[1].equals(">")) {
            return null;
        }
        String playerName = "null";
        String message = "";
        int rankOffset = !parts[2].startsWith("[") || !parts[2].endsWith("]") ? 0 : 1; // pesky ranks
        for(int i = 2 + rankOffset; i < parts.length; i++) {
            if (i == 2 + rankOffset) {
                playerName = parts[i];
                playerName = cleanUsername(playerName);
            } else {
                message += parts[i] + " "; // dont forget rest of msg
            }
        }
        return new PartyMessage(message.trim(), playerName);
    }

    public record PartyMessage(String message, String playerName) {}
}
/*


§9Party §8> §b[MVP] Imparellel§f: dps


Party > [MVP] Imparellel: dps


 */

