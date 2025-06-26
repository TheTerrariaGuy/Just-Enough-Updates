package jeu.terralib;

public class GeneralHelper {
    private GeneralHelper(){}
    public static int safeParseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("bad number");
            return 10;
        }
    }
}
