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
