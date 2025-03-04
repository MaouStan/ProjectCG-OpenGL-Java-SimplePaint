package com.sample.paint.algorithms;

import com.sample.paint.util.Point;
import java.util.ArrayList;
import java.util.List;

public class DdaLine {
    public static List<Point> computeLine(int x1, int y1, int x2, int y2) {
        List<Point> points = new ArrayList<>();
        int dx = x2 - x1;
        int dy = y2 - y1;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        float xIncrement = dx / (float) steps;
        float yIncrement = dy / (float) steps;
        float x = x1;
        float y = y1;

        for (int i = 0; i <= steps; i++) {
            points.add(new Point(Math.round(x), Math.round(y)));
            x += xIncrement;
            y += yIncrement;
        }
        return points;
    }
}
