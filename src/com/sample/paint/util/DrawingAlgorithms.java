package com.sample.paint.util;

import java.awt.Color;
import com.jogamp.opengl.GL2;
import com.sample.paint.model.Point;

public class DrawingAlgorithms {

    /**
     * Implements Bresenham's line drawing algorithm
     */
    public static void bresenhamLine(GL2 gl, float x1, float y1, float x2, float y2, Color color, float thickness) {
        int x0 = Math.round(x1 * 1000), y0 = Math.round(y1 * 1000);
        int xEnd = Math.round(x2 * 1000), yEnd = Math.round(y2 * 1000);
        int dx = Math.abs(xEnd - x0), dy = Math.abs(yEnd - y0);
        int sx = x0 < xEnd ? 1 : -1, sy = y0 < yEnd ? 1 : -1;
        int err = dx - dy;

        while (true) {
            GLRenderer.drawPoint(gl, x0 / 1000.0f, y0 / 1000.0f, color, thickness);
            if (x0 == xEnd && y0 == yEnd) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }

    /**
     * Draws a filled rectangle using scan line algorithm
     */
    public static void scanLineFillRect(GL2 gl, float x1, float y1, float x2, float y2, Color color, float thickness) {
        float minX = Math.min(x1, x2), maxX = Math.max(x1, x2);
        float minY = Math.min(y1, y2), maxY = Math.max(y1, y2);

        for (float y = minY; y <= maxY; y += 0.001f) {
            for (float x = minX; x <= maxX; x += 0.001f) {
                GLRenderer.drawPoint(gl, x, y, color, thickness);
            }
        }
    }

    /**
     * Implementation of Midpoint Circle Algorithm
     */
    public static void midpointCircle(GL2 gl, float xc, float yc, float radius, Color color, float thickness, boolean filled) {
        int r = Math.round(radius * 1000);
        int x = 0;
        int y = r;
        int d = 1 - r;

        while (x <= y) {
            if (filled) {
                drawScanLineFillCircle(gl, xc, yc, x, y, color, thickness);
            } else {
                plotCirclePoints(gl, xc, yc, x, y, color, thickness);
            }

            if (d < 0) {
                d += 2 * x + 3;
            } else {
                d += 2 * (x - y) + 5;
                y--;
            }
            x++;
        }
    }

    /**
     * Helper method for plotting circle points
     */
    private static void plotCirclePoints(GL2 gl, float xc, float yc, int x, int y, Color color, float thickness) {
        float xScaled = x / 1000.0f;
        float yScaled = y / 1000.0f;
        GLRenderer.drawPoint(gl, xc + xScaled, yc + yScaled, color, thickness);
        GLRenderer.drawPoint(gl, xc - xScaled, yc + yScaled, color, thickness);
        GLRenderer.drawPoint(gl, xc + xScaled, yc - yScaled, color, thickness);
        GLRenderer.drawPoint(gl, xc - xScaled, yc - yScaled, color, thickness);
        GLRenderer.drawPoint(gl, xc + yScaled, yc + xScaled, color, thickness);
        GLRenderer.drawPoint(gl, xc - yScaled, yc + xScaled, color, thickness);
        GLRenderer.drawPoint(gl, xc + yScaled, yc - xScaled, color, thickness);
        GLRenderer.drawPoint(gl, xc - yScaled, yc - xScaled, color, thickness);
    }

    /**
     * Helper method for filled circle
     */
    private static void drawScanLineFillCircle(GL2 gl, float xc, float yc, int x, int y, Color color, float thickness) {
        float xScaled = x / 1000.0f;
        float yScaled = y / 1000.0f;

        // Draw horizontal lines between symmetric points
        for (float i = -xScaled; i <= xScaled; i += 0.001f) {
            GLRenderer.drawPoint(gl, xc + i, yc + yScaled, color, thickness);
            GLRenderer.drawPoint(gl, xc + i, yc - yScaled, color, thickness);
        }
        for (float i = -yScaled; i <= yScaled; i += 0.001f) {
            GLRenderer.drawPoint(gl, xc + i, yc + xScaled, color, thickness);
            GLRenderer.drawPoint(gl, xc + i, yc - xScaled, color, thickness);
        }
    }

    /**
     * Implementation of Midpoint Ellipse Algorithm
     */
    public static void midpointEllipse(GL2 gl, float xc, float yc, float rx, float ry, Color color, float thickness, boolean filled) {
        int rxInt = Math.round(rx * 1000);
        int ryInt = Math.round(ry * 1000);
        int x = 0;
        int y = ryInt;
        long rx2 = (long) rxInt * rxInt;
        long ry2 = (long) ryInt * ryInt;

        // Region 1
        long p1 = ry2 - rx2 * ryInt + rx2 / 4;
        while (ry2 * x <= rx2 * y) {
            if (filled) {
                drawScanLineFillEllipse(gl, xc, yc, x, y, color, thickness);
            } else {
                plotEllipsePoints(gl, xc, yc, x, y, color, thickness);
            }

            if (p1 < 0) {
                x++;
                p1 += 2 * ry2 * x + ry2;
            } else {
                x++;
                y--;
                p1 += 2 * ry2 * x - 2 * rx2 * y + ry2;
            }
        }

        // Region 2
        long p2 = (long) (ry2 * (x + 0.5f) * (x + 0.5f) + rx2 * (y - 1) * (y - 1) - rx2 * ry2);
        while (y >= 0) {
            if (filled) {
                drawScanLineFillEllipse(gl, xc, yc, x, y, color, thickness);
            } else {
                plotEllipsePoints(gl, xc, yc, x, y, color, thickness);
            }

            if (p2 > 0) {
                y--;
                p2 += rx2 - 2 * rx2 * y;
            } else {
                x++;
                y--;
                p2 += 2 * ry2 * x - 2 * rx2 * y + rx2;
            }
        }
    }

    /**
     * Helper method for plotting ellipse points
     */
    private static void plotEllipsePoints(GL2 gl, float xc, float yc, int x, int y, Color color, float thickness) {
        float xScaled = x / 1000.0f;
        float yScaled = y / 1000.0f;
        GLRenderer.drawPoint(gl, xc + xScaled, yc + yScaled, color, thickness);
        GLRenderer.drawPoint(gl, xc - xScaled, yc + yScaled, color, thickness);
        GLRenderer.drawPoint(gl, xc + xScaled, yc - yScaled, color, thickness);
        GLRenderer.drawPoint(gl, xc - xScaled, yc - yScaled, color, thickness);
    }

    /**
     * Helper method for filled ellipse
     */
    private static void drawScanLineFillEllipse(GL2 gl, float xc, float yc, int x, int y, Color color, float thickness) {
        float xScaled = x / 1000.0f;
        float yScaled = y / 1000.0f;

        // Draw horizontal lines between symmetric points
        for (float i = -xScaled; i <= xScaled; i += 0.001f) {
            GLRenderer.drawPoint(gl, xc + i, yc + yScaled, color, thickness);
            GLRenderer.drawPoint(gl, xc + i, yc - yScaled, color, thickness);
        }
    }

    /**
     * Fills a triangle using scan line algorithm
     */
    public static void fillTriangle(GL2 gl, float x1, float y1, float x2, float y2, float x3, float y3, Color color, float thickness) {
        // Sort vertices by y-coordinate (y1 <= y2 <= y3)
        if (y1 > y2) {
            float tempX = x1; x1 = x2; x2 = tempX;
            float tempY = y1; y1 = y2; y2 = tempY;
        }
        if (y2 > y3) {
            float tempX = x2; x2 = x3; x3 = tempX;
            float tempY = y2; y2 = y3; y3 = tempY;
        }
        if (y1 > y2) {
            float tempX = x1; x1 = x2; x2 = tempX;
            float tempY = y1; y1 = y2; y2 = tempY;
        }

        // Calculate slopes of the three edges
        float dx1 = 0, dx2 = 0, dx3 = 0;
        if (y2 - y1 > 0) dx1 = (x2 - x1) / (y2 - y1);
        if (y3 - y1 > 0) dx2 = (x3 - x1) / (y3 - y1);
        if (y3 - y2 > 0) dx3 = (x3 - x2) / (y3 - y2);

        // Start and end points for scan lines
        float sx = x1, ex = x1;

        // First part of the triangle (between y1 and y2)
        for (float y = y1; y <= y2; y += 0.001f) {
            for (float x = Math.min(sx, ex); x <= Math.max(sx, ex); x += 0.001f) {
                GLRenderer.drawPoint(gl, x, y, color, thickness);
            }
            sx += dx1 * 0.001f;
            ex += dx2 * 0.001f;
        }

        // Second part of the triangle (between y2 and y3)
        sx = x2;
        for (float y = y2; y <= y3; y += 0.001f) {
            for (float x = Math.min(sx, ex); x <= Math.max(sx, ex); x += 0.001f) {
                GLRenderer.drawPoint(gl, x, y, color, thickness);
            }
            sx += dx3 * 0.001f;
            ex += dx2 * 0.001f;
        }
    }
}
