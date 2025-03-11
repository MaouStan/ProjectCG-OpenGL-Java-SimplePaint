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
     * Draws a border around a point to show the brush/eraser size
     * @param gl OpenGL context
     * @param x X coordinate
     * @param y Y coordinate
     * @param borderColor Color of the border
     * @param size Size of the brush/eraser
     * @param zoomFactor Current zoom level
     */
    public static void drawPointBorder(GL2 gl, float x, float y, Color borderColor, float size, float zoomFactor) {
        // Save current color
        float[] currentColor = new float[4];
        gl.glGetFloatv(GL2.GL_CURRENT_COLOR, currentColor, 0);

        // Set border color
        gl.glColor3f(borderColor.getRed() / 255.0f,
                     borderColor.getGreen() / 255.0f,
                     borderColor.getBlue() / 255.0f);

        // Draw circle outline
        gl.glLineWidth(1.0f);
        gl.glBegin(GL2.GL_LINE_LOOP);

        // Adjust radius based on zoom factor - at higher zoom the border should be smaller
        float radius = size / (2.0f * zoomFactor);
        int segments = 20; // Number of segments for the circle

        for (int i = 0; i < segments; i++) {
            float theta = (float) (2.0f * Math.PI * i / segments);
            float dx = radius * (float) Math.cos(theta);
            float dy = radius * (float) Math.sin(theta);
            gl.glVertex2f(x + dx / 1000.0f, y + dy / 1000.0f);
        }

        gl.glEnd();
        gl.glLineWidth(1.0f);

        // Restore original color
        gl.glColor3f(currentColor[0], currentColor[1], currentColor[2]);
    }

    /**
     * Legacy version of drawPointBorder for backward compatibility
     */
    public static void drawPointBorder(GL2 gl, float x, float y, Color borderColor, float size) {
        drawPointBorder(gl, x, y, borderColor, size, 1.0f);
    }

    /**
     * Sets up the OpenGL viewport and projection matrix with zoom and pan
     */
    public static void setupViewport(GL2 gl, int width, int height, float zoomFactor, float panX, float panY) {
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        float aspectRatio = (float) width / height;

        // Apply zoom and pan to the projection matrix
        gl.glOrtho(
            -aspectRatio / zoomFactor + panX,  // left
            aspectRatio / zoomFactor + panX,   // right
            -1.0 / zoomFactor + panY,          // bottom
            1.0 / zoomFactor + panY,           // top
            -1.0,                              // near
            1.0                                // far
        );

        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    /**
     * Original viewport setup without zoom (for backward compatibility)
     */
    public static void setupViewport(GL2 gl, int width, int height) {
        setupViewport(gl, width, height, 1.0f, 0.0f, 0.0f);
    }

    /**
     * Clears the screen with specified background color
     */
    public static void clearScreen(GL2 gl, Color backgroundColor) {
        gl.glClearColor(
            backgroundColor.getRed() / 255.0f,
            backgroundColor.getGreen() / 255.0f,
            backgroundColor.getBlue() / 255.0f,
            1.0f
        );
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
    }

    /**
     * Clears the screen with white background (for backward compatibility)
     */
    public static void clearScreen(GL2 gl) {
        clearScreen(gl, Color.WHITE);
    }
}
