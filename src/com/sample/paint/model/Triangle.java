package com.sample.paint.model;

import java.awt.Color;
import com.jogamp.opengl.GL2;
import com.sample.paint.util.DrawingAlgorithms;

public class Triangle extends Shape {
    private float x1, y1; // First point (fixed at startX, startY)
    private float x2, y2; // Second point (fixed at endX, endY)
    private float x3, y3; // Third point (calculated)

    public Triangle(float x1, float y1, float x2, float y2, Color color, boolean filled, float thickness) {
        super(color, filled, thickness);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;

        // Calculate the third point to make an isosceles triangle
        // The third point is placed so that the line from (x1,y1) to (x3,y3) is perpendicular to
        // the line from (x1,y1) to (x2,y2), and the distance from (x1,y1) to (x3,y3) is the same
        // as the distance from (x1,y1) to (x2,y2)

        float dx = x2 - x1;
        float dy = y2 - y1;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // Rotate by 90 degrees to get the perpendicular direction
        float perpX = -dy;
        float perpY = dx;

        // Normalize and scale to get the third point
        float perpLength = (float) Math.sqrt(perpX * perpX + perpY * perpY);
        perpX = perpX / perpLength * distance;
        perpY = perpY / perpLength * distance;

        // Set the third point
        this.x3 = x1 + perpX;
        this.y3 = y1 + perpY;
    }

    @Override
    public void draw(GL2 gl) {
        if (filled) {
            DrawingAlgorithms.fillTriangle(gl, x1, y1, x2, y2, x3, y3, color, thickness);
        } else {
            // Draw outline using three lines
            DrawingAlgorithms.bresenhamLine(gl, x1, y1, x2, y2, color, thickness);
            DrawingAlgorithms.bresenhamLine(gl, x2, y2, x3, y3, color, thickness);
            DrawingAlgorithms.bresenhamLine(gl, x3, y3, x1, y1, color, thickness);
        }
    }

    @Override
    public boolean isPointInside(float px, float py, float tolerance) {
        // Check if the point is inside the triangle or within tolerance of any edge
        if (isPointInTriangle(px, py)) {
            return true;
        }

        // Check if point is near any edge
        float d1 = distancePointToLine(px, py, x1, y1, x2, y2);
        float d2 = distancePointToLine(px, py, x2, y2, x3, y3);
        float d3 = distancePointToLine(px, py, x3, y3, x1, y1);

        return Math.min(Math.min(d1, d2), d3) <= tolerance;
    }

    // Helper method to calculate if a point is inside the triangle
    private boolean isPointInTriangle(float px, float py) {
        // Barycentric coordinate method
        float denominator = ((y2 - y3) * (x1 - x3) + (x3 - x2) * (y1 - y3));

        // Handle degenerate triangle
        if (Math.abs(denominator) < 0.0001f) {
            return false;
        }

        float a = ((y2 - y3) * (px - x3) + (x3 - x2) * (py - y3)) / denominator;
        float b = ((y3 - y1) * (px - x3) + (x1 - x3) * (py - y3)) / denominator;
        float c = 1 - a - b;

        return a >= 0 && a <= 1 && b >= 0 && b <= 1 && c >= 0 && c <= 1;
    }

    // Helper method to calculate distance from point to line segment
    private float distancePointToLine(float px, float py, float x1, float y1, float x2, float y2) {
        float A = px - x1;
        float B = py - y1;
        float C = x2 - x1;
        float D = y2 - y1;

        float dot = A * C + B * D;
        float len_sq = C * C + D * D;
        float param = dot / len_sq;

        float xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        float dx = px - xx;
        float dy = py - yy;

        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
