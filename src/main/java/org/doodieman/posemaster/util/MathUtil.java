package org.doodieman.posemaster.util;

import java.text.DecimalFormat;

public class MathUtil {

    public static double degreesToRadians(double degrees) {
        return (degrees % 360) * (Math.PI / 180);
    }

    public static double radiansToDegrees(double radians) {
        return (radians / Math.PI) * 180;
    }

    public static double radiansToDegreesRounded(double radians) {
        return roundTwoDecimals(radiansToDegrees(radians));
    }

    public static double roundTwoDecimals(double input) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Double.parseDouble(decimalFormat.format(input));
    }

}
