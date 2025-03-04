package com.sample.paint.algorithms;

import com.sample.paint.util.Point;
import java.util.ArrayList;
import java.util.List;

public class MidpointCircle {
    public static List<Point> computeCircle(int centerX, int centerY, int radius) {
        List<Point> points = new ArrayList<>();
        int x = radius;
        int y = 0;
        int p = 1 - radius;

        while (x >= y) {
            points.add(new Point(centerX + x, centerY + y));
            points.add(new Point(centerX - x, centerY + y));
            points.add(new Point(centerX + x, centerY - y));
            points.add(new Point(centerX - x, centerY - y));
            points.add(new Point(centerX + y, centerY + x));
            points.add(new Point(centerX - y, centerY + x));
            points.add(new Point(centerX + y, centerY - x));
            points.add(new Point(centerX - y, centerY - x));
            y++;
            if (p <= 0) {
                p += 2 * y + 1;
            } else {
                x--;
                p += 2 * (y - x) + 1;
            }
        }
        return points;
    }
}
