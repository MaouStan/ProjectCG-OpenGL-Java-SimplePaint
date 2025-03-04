package com.sample.paint.model;

import java.awt.Color;
import com.jogamp.opengl.GL2;
import com.sample.paint.util.DrawingAlgorithms;

public class Circle extends Shape {
    private float centerX;
    private float centerY;
    private float radius;

    public Circle(float x1, float y1, float x2, float y2, Color color, boolean filled, float thickness) {
        super(color, filled, thickness);

        // Calculate center and radius from two points
        this.centerX = (x1 + x2) / 2;
        this.centerY = (y1 + y2) / 2;

        // Use the smaller dimension to maintain circle shape
        float dx = Math.abs(x2 - x1);
        float dy = Math.abs(y2 - y1);
        this.radius = Math.min(dx, dy) / 2;
    }

    @Override
    public void draw(GL2 gl) {
        DrawingAlgorithms.midpointCircle(gl, centerX, centerY, radius, color, thickness, filled);
    }

    @Override
    public boolean isPointInside(float x, float y, float tolerance) {
        double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        return distance <= radius + tolerance;
    }
}
