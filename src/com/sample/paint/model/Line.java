package com.sample.paint.model;

import java.awt.Color;
import com.jogamp.opengl.GL2;
import com.sample.paint.util.DrawingAlgorithms;

public class Line extends Shape {
    private float startX, startY;
    private float endX, endY;

    public Line(float startX, float startY, float endX, float endY, Color color, float thickness) {
        super(color, false, thickness); // Lines are never filled
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    @Override
    public void draw(GL2 gl) {
        DrawingAlgorithms.bresenhamLine(gl, startX, startY, endX, endY, color, thickness);
    }

    @Override
    public boolean isPointInside(float x, float y, float tolerance) {
        // Calculate distance from point to line
        float dist = (float) (Math.abs((endY - startY) * x - (endX - startX) * y + endX * startY - endY * startX) /
                Math.sqrt(Math.pow(endY - startY, 2) + Math.pow(endX - startX, 2)));
        return dist <= tolerance;
    }
}
