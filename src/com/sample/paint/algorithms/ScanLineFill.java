package com.sample.paint.algorithms;

import com.sample.paint.util.Point;
import java.util.List;

public class ScanLineFill {
    public static void fillPolygon(List<Point> polygon, int[][] framebuffer, int color) {
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Point point : polygon) {
            if (point.y < minY) minY = (int)point.y;
            if (point.y > maxY) maxY = (int)point.y;
        }

        for (int y = minY; y <= maxY; y++) {
            int[] nodes = new int[polygon.size()];
            int nodeCount = 0;
            int j = polygon.size() - 1;

            for (int i = 0; i < polygon.size(); i++) {
                if (polygon.get(i).y < y && polygon.get(j).y >= y || polygon.get(j).y < y && polygon.get(i).y >= y) {
                    nodes[nodeCount++] = (int) (polygon.get(i).x + (float)(y - polygon.get(i).y) / (polygon.get(j).y - polygon.get(i).y) * (polygon.get(j).x - polygon.get(i).x));
                }
                j = i;
            }

            for (int i = 0; i < nodeCount - 1; i++) {
                for (int k = i + 1; k < nodeCount; k++) {
                    if (nodes[i] > nodes[k]) {
                        int temp = nodes[i];
                        nodes[i] = nodes[k];
                        nodes[k] = temp;
                    }
                }
            }

            for (int i = 0; i < nodeCount; i += 2) {
                if (nodes[i] >= framebuffer[0].length) break;
                if (nodes[i + 1] > 0) {
                    if (nodes[i] < 0) nodes[i] = 0;
                    if (nodes[i + 1] >= framebuffer[0].length) nodes[i + 1] = framebuffer[0].length - 1;
                    for (int x = nodes[i]; x <= nodes[i + 1]; x++) {
                        framebuffer[y][x] = color;
                    }
                }
            }
        }
    }
}
