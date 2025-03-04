package com.sample.paint.drawing;

import com.jogamp.opengl.GL2;
import com.sample.paint.util.Point;
import java.awt.Color;
import java.util.List;

public class Rectangle extends Shape {
    private float startX, startY, endX, endY;

    public Rectangle(Color color, float startX, float startY, float endX, float endY) {
        super("Rectangle", color, null);
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    @Override
    public void draw(GL2 gl) {
        gl.glColor3f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glVertex2f(startX, startY);
        gl.glVertex2f(endX, startY);
        gl.glVertex2f(endX, endY);
        gl.glVertex2f(startX, endY);
        gl.glEnd();
    }

    @Override
    public boolean isPointInside(float x, float y, float size) {
        return (x >= startX - size && x <= endX + size && y >= startY - size && y <= endY + size);
    }
}
