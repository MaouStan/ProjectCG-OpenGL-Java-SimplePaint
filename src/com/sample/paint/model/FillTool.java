package com.sample.paint.model;

import com.jogamp.opengl.GL2;
import java.awt.Color;

/**
 * A tool that represents filling a shape with a color
 * This is not a shape itself but used to temporarily visualize the fill operation
 */
public class FillTool extends Shape {
    private float x, y;  // Coordinates where to apply the fill

    public FillTool(float x, float y, Color color) {
        super(color, true, 1.0f);  // Always filled with thickness 1
        this.x = x;
        this.y = y;
    }

    @Override
    public void draw(GL2 gl) {
        // Draw a paint bucket icon at the position
        float size = 0.015f;  // Slightly larger for better visibility

        // Save current color
        float[] currentColor = new float[4];
        gl.glGetFloatv(GL2.GL_CURRENT_COLOR, currentColor, 0);

        // Set the fill tool color
        gl.glColor3f(
            color.getRed() / 255.0f,
            color.getGreen() / 255.0f,
            color.getBlue() / 255.0f
        );

        // Draw a paint bucket icon (simplified)
        gl.glLineWidth(2.0f);

        // Draw outer bucket shape
        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glVertex2f(x - size, y - size);
        gl.glVertex2f(x + size, y - size);
        gl.glVertex2f(x + size, y + size * 0.7f);
        gl.glVertex2f(x - size, y + size * 0.7f);
        gl.glEnd();

        // Draw handle
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex2f(x, y + size * 0.7f);
        gl.glVertex2f(x + size * 0.5f, y + size * 1.4f);
        gl.glEnd();

        // Draw drip
        gl.glBegin(GL2.GL_LINE_STRIP);
        gl.glVertex2f(x - size * 0.5f, y - size * 1.5f);
        gl.glVertex2f(x - size * 0.3f, y - size);
        gl.glEnd();

        // Reset line width
        gl.glLineWidth(1.0f);

        // Restore original color
        gl.glColor3f(currentColor[0], currentColor[1], currentColor[2]);
    }

    @Override
    public boolean isPointInside(float x, float y, float tolerance) {
        // Determine if a point is inside this tool (not really needed for fill tool)
        float dx = this.x - x;
        float dy = this.y - y;
        return Math.sqrt(dx * dx + dy * dy) <= tolerance;
    }

    // Getters for x and y coordinates
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
