package com.sample.paint.drawing;

import com.jogamp.opengl.GL2;
import com.sample.paint.util.Point;
import java.awt.Color;
import java.util.List;

public class Ellipse extends Shape {
    private float centerX, centerY, radiusX, radiusY;

    public Ellipse(Color color, float centerX, float centerY, float radiusX, float radiusY) {
        super("Ellipse", color, null);
        this.centerX = centerX;
        this.centerY = centerY;
        this.radiusX = radiusX;
        this.radiusY = radiusY;
    }

    @Override
    public void draw(GL2 gl) {
        gl.glColor3f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
        gl.glBegin(GL2.GL_LINE_LOOP);
        for (int i = 0; i <= 360; i++) {
            double angle = Math.toRadians(i);
            float x = (float) (centerX + radiusX * Math.cos(angle));
            float y = (float) (centerY + radiusY * Math.sin(angle));
            gl.glVertex2f(x, y);
        }
        gl.glEnd();
    }

    @Override
    public boolean isPointInside(float x, float y, float size) {
        float dx = (x - centerX) / radiusX;
        float dy = (y - centerY) / radiusY;
        return (dx * dx + dy * dy) <= 1 + size;
    }
}
