package com.sample.paint.drawing;

import com.jogamp.opengl.GL2;
import com.sample.paint.util.Point;
import java.awt.Color;
import java.util.List;

public class Line extends Shape {
    private float startX, startY, endX, endY;

    public Line(Color color, float startX, float startY, float endX, float endY) {
        super("Line", color, null);
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    @Override
    public void draw(GL2 gl) {
        gl.glColor3f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex2f(startX, startY);
        gl.glVertex2f(endX, endY);
        gl.glEnd();
    }

    @Override
    public boolean isPointInside(float x, float y, float size) {
        float dist = (float) (Math.abs((endY - startY) * x - (endX - startX) * y + endX * startY - endY * startX) /
                Math.sqrt(Math.pow(endY - startY, 2) + Math.pow(endX - startX, 2)));
        return dist <= size;
    }
}
