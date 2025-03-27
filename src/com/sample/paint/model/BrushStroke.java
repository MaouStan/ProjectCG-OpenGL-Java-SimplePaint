package com.sample.paint.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import com.jogamp.opengl.GL2;
import com.sample.paint.util.DrawingAlgorithms;

public class BrushStroke extends Shape {
    private List<Point> points;

    public BrushStroke(List<Point> points, Color color, float thickness) {
        super(color, false, thickness); // Brush strokes are never filled
        this.points = new ArrayList<>(points);
    }

    @Override
    public void draw(GL2 gl) {
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            DrawingAlgorithms.bresenhamLine(gl, p1.x, p1.y, p2.x, p2.y, color, thickness);
        }
    }

    @Override
    public boolean isPointInside(float x, float y, float tolerance) {
        for (Point p : points) {
            if (p.distanceTo(x, y) <= tolerance) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the list of points defining this brush stroke
     * Used for eraser color updates
     */
    public List<Point> getPoints() {
        return new ArrayList<>(points);
    }
}
