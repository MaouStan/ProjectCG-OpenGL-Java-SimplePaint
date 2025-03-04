package utils;

import java.awt.Color;

public class ColorUtils {
    public static Color getRandomColor() {
        return new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
    }
}
