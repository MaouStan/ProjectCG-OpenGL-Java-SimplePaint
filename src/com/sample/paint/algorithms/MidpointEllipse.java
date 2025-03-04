package com.sample.paint.algorithms;

import com.sample.paint.util.Point;
import java.util.ArrayList;
import java.util.List;

public class MidpointEllipse {
    public static List<Point> computeEllipse(int centerX, int centerY, int radiusX, int radiusY) {
        List<Point> points = new ArrayList<>();
        int x = 0;
        int y = radiusY;
        int p1 = (int) (Math.pow(radiusY, 2) - Math.pow(radiusX, 2) * radiusY + 0.25 * Math.pow(radiusX, 2));
        int dx = 2 * radiusY * radiusY * x;
        int dy = 2 * radiusX * radiusX * y;

        while (dx < dy) {
            points.add(new Point(centerX + x, centerY + y));
            points.add(new Point(centerX - x, centerY + y));
            points.add(new Point(centerX + x, centerY - y));
            points.add(new Point(centerX - x, centerY - y));
            if (p1 < 0) {
                x++;
                dx += 2 * radiusY * radiusY;
                p1 += dx + radiusY * radiusY;
            } else {
                x++;
                y--;
                dx += 2 * radiusY * radiusY;
                dy -= 2 * radiusX * radiusX;
                p1 += dx - dy + radiusY * radiusY;
            }
        }

        int p2 = (int) (Math.pow(radiusY, 2) * (x + 0.5) * (x + 0.5) + Math.pow(radiusX, 2) * (y - 1) * (y - 1) - Math.pow(radiusX, 2) * Math.pow(radiusY, 2));
        while (y >= 0) {
            points.add(new Point(centerX + x, centerY + y));
            points.add(new Point(centerX - x, centerY + y));
            points.add(new Point(centerX + x, centerY - y));
            points.add(new Point(centerX - x, centerY - y));
            if (p2 > 0) {
                y--;
                dy -= 2 * radiusX * radiusX;
                p2 += radiusX * radiusX - dy;
            } else {
                y--;
                x++;
                dx += 2 * radiusY * radiusY;
                dy -= 2 * radiusX * radiusX;
                p2 += dx - dy + radiusX * radiusX;
            }
        }
        return points;
    }
}
