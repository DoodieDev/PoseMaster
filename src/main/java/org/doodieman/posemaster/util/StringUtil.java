package org.doodieman.posemaster.util;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.security.SecureRandom;

public class StringUtil {

    //Random string generation
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();
    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }

    //Format location as a fancy text
    public static String formatLocation(Location location, int decimals, boolean includeCommas) {
        String formatPattern = "%." + decimals + "f";
        String formattedX = String.format(formatPattern, location.getX());
        String formattedY = String.format(formatPattern, location.getY());
        String formattedZ = String.format(formatPattern, location.getZ());
        if (includeCommas) {
            return formattedX + ", " + formattedY + ", " + formattedZ;
        } else {
            return formattedX + " " + formattedY + " " + formattedZ;
        }
    }

    //Convert a string to a Bukkit vector
    public static Vector stringToVector(String input) {
        try {
            String[] split = input.split(" ");
            double x = Double.parseDouble(split[0]);
            double y = Double.parseDouble(split[1]);
            double z = Double.parseDouble(split[2]);
            return new Vector(x, y, z);
        } catch (Exception exception) {
            return null;
        }
    }

}
