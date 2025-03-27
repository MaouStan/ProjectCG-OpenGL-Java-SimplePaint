package com.sample.paint.model;

import java.awt.Color;
import com.jogamp.opengl.GL2;
import com.sample.paint.util.DrawingAlgorithms;

public class Rectangle extends Shape {
    private float x1, y1, x2, y2;

    public Rectangle(float x1, float y1, float x2, float y2, Color color, boolean filled, float thickness) {
        super(color, filled, thickness);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public void draw(GL2 gl) {
        float minX = Math.min(x1, x2);
        float maxX = Math.max(x1, x2);
        float minY = Math.min(y1, y2);
        float maxY = Math.max(y1, y2);

        if (filled) {
            DrawingAlgorithms.scanLineFillRect(gl, minX, minY, maxX, maxY, color, thickness);
        } else {
            // Draw outline
            DrawingAlgorithms.bresenhamLine(gl, minX, minY, maxX, minY, color, thickness); // Bottom
            DrawingAlgorithms.bresenhamLine(gl, maxX, minY, maxX, maxY, color, thickness); // Right
            DrawingAlgorithms.bresenhamLine(gl, maxX, maxY, minX, maxY, color, thickness); // Top
            DrawingAlgorithms.bresenhamLine(gl, minX, maxY, minX, minY, color, thickness); // Left
        }
    }

    @Override
    public boolean isPointInside(float x, float y, float tolerance) {
        return Math.min(x1, x2) - tolerance <= x && x <= Math.max(x1, x2) + tolerance &&
                Math.min(y1, y2) - tolerance <= y && y <= Math.max(y1, y2) + tolerance;
    }

    // Getter methods for fill tool
    public float getX1() {
        return x1;
    }

    public float getY1() {
        return y1;
    }

    public float getX2() {
        return x2;
    }

    public float getY2() {
        return y2;
    }
}
