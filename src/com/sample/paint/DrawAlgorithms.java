package com.sample.paint;

import com.jogamp.opengl.GL2;

import java.awt.Point;
import java.util.List;

public class DrawAlgorithms {

    public static void drawLine(GL2 gl, float x1, float y1, float x2, float y2) {
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex2f(x1, y1);
        gl.glVertex2f(x2, y2);
        gl.glEnd();
    }

    public static void drawRectangle(GL2 gl, float x1, float y1, float x2, float y2) {
        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glVertex2f(x1, y1);
        gl.glVertex2f(x2, y1);
        gl.glVertex2f(x2, y2);
        gl.glVertex2f(x1, y2);
        gl.glEnd();
    }

    public static void drawCircle(GL2 gl, float cx, float cy, float radius) {
        gl.glBegin(GL2.GL_LINE_LOOP);
        for (int i = 0; i <= 300; i++) {
            double angle = 2 * Math.PI * i / 300;
            float x = (float) (cx + Math.cos(angle) * radius);
            float y = (float) (cy + Math.sin(angle) * radius);
            gl.glVertex2f(x, y);
        }
        gl.glEnd();
    }

    public static void drawEllipse(GL2 gl, float cx, float cy, float rx, float ry) {
        gl.glBegin(GL2.GL_LINE_LOOP);
        for (int i = 0; i <= 300; i++) {
            double angle = 2 * Math.PI * i / 300;
            float x = (float) (cx + Math.cos(angle) * rx);
            float y = (float) (cy + Math.sin(angle) * ry);
            gl.glVertex2f(x, y);
        }
        gl.glEnd();
    }

    public static void fillShape(GL2 gl, List<Point> points) {
        gl.glBegin(GL2.GL_POLYGON);
        for (Point point : points) {
            gl.glVertex2f(point.x, point.y);
        }
        gl.glEnd();
    }
}
