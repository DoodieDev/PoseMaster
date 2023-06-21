package doodieman.posemaster.utils;

import org.bukkit.ChatColor;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class StringUtil {

    private final static NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY);

    static {
        numberFormat.setMaximumFractionDigits(5);
    }

    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String formatNum(double input) {
        return numberFormat.format(input);
    }

    public static String formatNum(BigDecimal input) {
        return numberFormat.format(input);
    }

    public static double roundDecimals(double input, int decimals) {
        double scale = Math.pow(10, decimals);
        return Math.round(input * scale) / scale;
    }

    public static double roundTwoDecimals(double input) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Double.parseDouble(decimalFormat.format(input));
    }

}
