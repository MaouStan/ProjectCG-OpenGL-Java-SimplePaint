package com.sample.paint.util;

import java.awt.Color;
import com.jogamp.opengl.GL2;

public class GLRenderer {

    /**
     * Draws a point at the specified coordinates with given color and thickness
     */
    public static void drawPoint(GL2 gl, float x, float y, Color color, float thickness) {
        gl.glColor3f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
        gl.glPointSize(thickness);
        gl.glBegin(GL2.GL_POINTS);
        gl.glVertex2f(x, y);
        gl.glEnd();
        gl.glPointSize(1.0f); // Reset point size
    }

    /**
     * Draws a thicker point for erasing (essentially a filled circle)
     */
    public static void drawThickPoint(GL2 gl, float x, float y, Color color, float thickness) {
        gl.glColor3f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);

        // Use the point size to create a larger point
        gl.glPointSize(thickness * 2);

        gl.glBegin(GL2.GL_POINTS);
        gl.glVertex2f(x, y);
        gl.glEnd();

        // Draw additional points in a small circle pattern for better coverage
        float r = thickness / 200.0f; // Small radius based on thickness
        for (float angle = 0; angle < 360; angle += 30) {
            float radian = (float) Math.toRadians(angle);
            float offsetX = (float) (r * Math.cos(radian));
            float offsetY = (float) (r * Math.sin(radian));

            gl.glBegin(GL2.GL_POINTS);
            gl.glVertex2f(x + offsetX, y + offsetY);
            gl.glEnd();
        }

        // Reset point size to default
        gl.glPointSize(1.0f);
    }

    /**
     * Sets up the OpenGL viewport and projection matrix for proper aspect ratio
     */
    public static void setupViewport(GL2 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        float aspectRatio = (float) width / height;
        gl.glOrtho(-aspectRatio, aspectRatio, -1.0, 1.0, -1.0, 1.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    /**
     * Clears the screen with white background
     */
    public static void clearScreen(GL2 gl) {
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
    }
}
