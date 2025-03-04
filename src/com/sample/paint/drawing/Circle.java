package com.sample.paint.drawing;

import com.jogamp.opengl.GL2;
import com.sample.paint.util.Point;
import java.awt.Color;
import java.util.List;

public class Circle extends Shape {
    private float centerX, centerY, radius;

    public Circle(Color color, float centerX, float centerY, float radius) {
        super("Circle", color, null);
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }

    @Override
    public void draw(GL2 gl) {
        gl.glColor3f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
        gl.glBegin(GL2.GL_LINE_LOOP);
        for (int i = 0; i <= 360; i++) {
            double angle = Math.toRadians(i);
            float x = (float) (centerX + radius * Math.cos(angle));
            float y = (float) (centerY + radius * Math.sin(angle));
            gl.glVertex2f(x, y);
        }
        gl.glEnd();
    }

    @Override
    public boolean isPointInside(float x, float y, float size) {
        return Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2)) <= radius + size;
    }
}
