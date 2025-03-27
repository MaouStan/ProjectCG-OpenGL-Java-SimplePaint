package com.sample.paint.model;

import java.awt.Color;
import com.jogamp.opengl.GL2;
import com.sample.paint.util.DrawingAlgorithms;

public class Ellipse extends Shape {
    private float centerX;
    private float centerY;
    private float radiusX;
    private float radiusY;

    public Ellipse(float x1, float y1, float x2, float y2, Color color, boolean filled, float thickness) {
        super(color, filled, thickness);
        this.centerX = (x1 + x2) / 2;
        this.centerY = (y1 + y2) / 2;
        this.radiusX = Math.abs(x2 - x1) / 2;
        this.radiusY = Math.abs(y2 - y1) / 2;
    }

    @Override
    public void draw(GL2 gl) {
        DrawingAlgorithms.midpointEllipse(gl, centerX, centerY, radiusX, radiusY, color, thickness, filled);
    }

    @Override
    public boolean isPointInside(float x, float y, float tolerance) {
        // Simple rectangular bounds check
        return Math.abs(x - centerX) <= radiusX + tolerance &&
               Math.abs(y - centerY) <= radiusY + tolerance;
    }

    // Getter methods for fill tool
    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public float getRadiusX() {
        return radiusX;
    }

    public float getRadiusY() {
        return radiusY;
    }
}
