package com.sample.paint.drawing;

import com.jogamp.opengl.GL2;
import com.sample.paint.util.Point;
import java.awt.Color;
import java.util.List;

public class Brush extends Shape {

    public Brush(Color color, List<Point> points) {
        super("Brush", color, points);
    }

    @Override
    public void draw(GL2 gl) {
        gl.glColor3f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
        gl.glBegin(GL2.GL_LINE_STRIP);
        for (Point point : points) {
            gl.glVertex2f(point.x, point.y);
        }
        gl.glEnd();
    }

    @Override
    public boolean isPointInside(float x, float y, float size) {
        return points.stream().anyMatch(point -> Math.sqrt(Math.pow(x - point.x, 2) + Math.pow(y - point.y, 2)) <= size);
    }
}
