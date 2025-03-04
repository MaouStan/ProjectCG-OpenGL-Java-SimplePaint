package com.sample.paint.model;

import java.awt.Color;
import com.jogamp.opengl.GL2;

public abstract class Shape {
    protected Color color;
    protected float thickness;
    protected boolean filled;

    public Shape(Color color, boolean filled, float thickness) {
        this.color = color;
        this.filled = filled;
        this.thickness = thickness;
    }

    /**
     * Draw this shape using OpenGL
     */
    public abstract void draw(GL2 gl);

    /**
     * Check if the shape contains a point within the given tolerance
     */
    public abstract boolean isPointInside(float x, float y, float tolerance);

    // Getters and setters
    public Color getColor() {
        return color;
    }

    public float getThickness() {
        return thickness;
    }

    public boolean isFilled() {
        return filled;
    }
}
