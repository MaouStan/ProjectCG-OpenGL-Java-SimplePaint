package com.sample.paint;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class OpenGLPaintApp extends JFrame implements GLEventListener {
    private GLCanvas canvas;
    private float startX, startY, endX, endY;
    private boolean drawing = false;
    private String currentShape = "Line";
    private List<Shape> shapes = new ArrayList<>();
    private List<Point> brushPoints = new ArrayList<>();
    private float eraserSize = 0.05f;
    private Color currentColor = Color.RED;
    private boolean isFilled = false;
    private JButton selectedButton;

    public OpenGLPaintApp() {
        setTitle("OpenGL Algorithm-Based Paint Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        canvas = new GLCanvas(capabilities);
        canvas.addGLEventListener(this);

        setupMouseListeners();
        setupGUI();

        add(canvas, BorderLayout.CENTER);
        setVisible(true);
    }

    private void setupGUI() {
        JToolBar toolBar = new JToolBar();
        String[] shapes = { "Line", "Rectangle", "Circle", "Ellipse", "Brush", "Eraser" };
        for (String shape : shapes) {
            toolBar.add(createShapeButton(shape));
        }

        JButton colorButton = new JButton("Color");
        colorButton.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(this, "Pick a Color", currentColor);
            if (selectedColor != null)
                currentColor = selectedColor;
        });
        toolBar.add(colorButton);

        JCheckBox fillCheckBox = new JCheckBox("Fill");
        fillCheckBox.addActionListener(e -> isFilled = fillCheckBox.isSelected());
        toolBar.add(fillCheckBox);

        add(toolBar, BorderLayout.NORTH);
    }

    private JButton createShapeButton(String shape) {
        JButton button = new JButton(shape);
        button.setFocusable(false);
        button.addActionListener(e -> {
            currentShape = shape;
            if (selectedButton != null)
                selectedButton.setForeground(Color.BLACK);
            selectedButton = button;
            selectedButton.setForeground(Color.RED);
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        });
        return button;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // White background
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

        for (Shape shape : shapes) {
            shape.draw(gl);
        }

        if (drawing && !currentShape.equals("Eraser")) {
            Shape ghostShape = currentShape.equals("Brush")
                    ? new Shape(currentShape, new ArrayList<>(brushPoints), currentColor, isFilled)
                    : new Shape(currentShape, startX, startY, endX, endY, currentColor, isFilled);
            ghostShape.draw(gl);
        }
    }

    // In OpenGLPaintApp class, update mouse listeners to account for aspect ratio
    private void setupMouseListeners() {
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                float aspectRatio = (float) canvas.getWidth() / canvas.getHeight();
                startX = ((float) e.getX() / canvas.getWidth() * 2 - 1) * aspectRatio;
                startY = 1 - (float) e.getY() / canvas.getHeight() * 2;
                drawing = true;
                if (currentShape.equals("Brush")) {
                    brushPoints.clear();
                    brushPoints.add(new Point(startX, startY));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                float aspectRatio = (float) canvas.getWidth() / canvas.getHeight();
                endX = ((float) e.getX() / canvas.getWidth() * 2 - 1) * aspectRatio;
                endY = 1 - (float) e.getY() / canvas.getHeight() * 2;
                drawing = false;
                if (currentShape.equals("Brush")) {
                    shapes.add(new Shape(currentShape, new ArrayList<>(brushPoints), currentColor, isFilled));
                } else if (currentShape.equals("Eraser")) {
                    eraseShape(endX, endY);
                } else {
                    shapes.add(new Shape(currentShape, startX, startY, endX, endY, currentColor, isFilled));
                }
                canvas.display();
            }
        });

        canvas.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                float aspectRatio = (float) canvas.getWidth() / canvas.getHeight();
                endX = ((float) e.getX() / canvas.getWidth() * 2 - 1) * aspectRatio;
                endY = 1 - (float) e.getY() / canvas.getHeight() * 2;
                if (currentShape.equals("Brush")) {
                    brushPoints.add(new Point(endX, endY));
                } else if (currentShape.equals("Eraser")) {
                    eraseShape(endX, endY);
                }
                canvas.display();
            }
        });
    }

    // Update reshape method to maintain proper projection
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        float aspectRatio = (float) width / height;
        gl.glOrtho(-aspectRatio, aspectRatio, -1.0, 1.0, -1.0, 1.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    private void eraseShape(float x, float y) {
        shapes.removeIf(shape -> shape.isPointInside(x, y, eraserSize));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OpenGLPaintApp::new);
    }

    private void drawPoint(GL2 gl, float x, float y, Color color) {
        gl.glColor3f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
        gl.glBegin(GL2.GL_POINTS);
        gl.glVertex2f(x, y);
        gl.glEnd();
    }

    private class Shape {
        String type;
        float startX, startY, endX, endY;
        List<Point> points;
        Color color;
        boolean filled;

        Shape(String type, float startX, float startY, float endX, float endY, Color color, boolean filled) {
            this.type = type;
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.color = color;
            this.filled = filled;
        }

        Shape(String type, List<Point> points, Color color, boolean filled) {
            this.type = type;
            this.points = points;
            this.color = color;
            this.filled = filled;
        }

        void draw(GL2 gl) {
            switch (type) {
                case "Line":
                    // Lines are always unfilled, ignore 'filled' flag
                    drawLine(gl, startX, startY, endX, endY);
                    break;
                case "Rectangle":
                    // Apply fill option for rectangles
                    drawRectangle(gl, startX, startY, endX, endY, filled);
                    break;
                case "Circle":
                    // Apply fill option for circles
                    drawMidpointCircle(gl, startX, startY, endX, endY, filled);
                    break;
                case "Ellipse":
                    // Apply fill option for ellipses
                    drawMidpointEllipse(gl, startX, startY, endX, endY, filled);
                    break;
                case "Brush":
                    // Brush is always unfilled, ignore 'filled' flag
                    drawBrush(gl);
                    break;
            }
        }

        // Existing drawing methods remain unchanged
        private void drawLine(GL2 gl, float x1, float y1, float x2, float y2) {
            bresenhamLine(gl, x1, y1, x2, y2);
        }

        private void drawRectangle(GL2 gl, float x1, float y1, float x2, float y2, boolean filled) {
            float minX = Math.min(x1, x2), maxX = Math.max(x1, x2);
            float minY = Math.min(y1, y2), maxY = Math.max(y1, y2);

            if (filled) {
                drawScanLineFill(gl, minX, minY, maxX, maxY);
            } else {
                bresenhamLine(gl, minX, minY, maxX, minY); // Top
                bresenhamLine(gl, maxX, minY, maxX, maxY); // Right
                bresenhamLine(gl, maxX, maxY, minX, maxY); // Bottom
                bresenhamLine(gl, minX, maxY, minX, minY); // Left
            }
        }

        // Add to Shape class
        // Updated drawMidpointCircle in Shape class
        private void drawMidpointCircle(GL2 gl, float x1, float y1, float x2, float y2, boolean filled) {
            float xc = (x1 + x2) / 2; // Center x
            float yc = (y1 + y2) / 2; // Center y

            // Calculate radius using the shorter dimension to ensure circle shape
            float dx = Math.abs(x2 - x1);
            float dy = Math.abs(y2 - y1);
            float radius = Math.min(dx, dy) / 2; // Use minimum to avoid stretching

            int r = Math.round(radius * 1000);
            int x = 0;
            int y = r;
            int d = 1 - r;

            while (x <= y) {
                if (filled) {
                    drawScanLineFillCircle(gl, xc, yc, x, y);
                } else {
                    plotCirclePoints(gl, xc, yc, x, y);
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

        // Helper method for plotting circle points
        private void plotCirclePoints(GL2 gl, float xc, float yc, int x, int y) {
            float xScaled = x / 1000.0f;
            float yScaled = y / 1000.0f;
            drawPoint(gl, xc + xScaled, yc + yScaled, color);
            drawPoint(gl, xc - xScaled, yc + yScaled, color);
            drawPoint(gl, xc + xScaled, yc - yScaled, color);
            drawPoint(gl, xc - xScaled, yc - yScaled, color);
            drawPoint(gl, xc + yScaled, yc + xScaled, color);
            drawPoint(gl, xc - yScaled, yc + xScaled, color);
            drawPoint(gl, xc + yScaled, yc - xScaled, color);
            drawPoint(gl, xc - yScaled, yc - xScaled, color);
        }

        // Helper method for filled circle
        private void drawScanLineFillCircle(GL2 gl, float xc, float yc, int x, int y) {
            float xScaled = x / 1000.0f;
            float yScaled = y / 1000.0f;
            // Draw horizontal lines between symmetric points
            for (float i = -xScaled; i <= xScaled; i += 0.001f) {
                drawPoint(gl, xc + i, yc + yScaled, color);
                drawPoint(gl, xc + i, yc - yScaled, color);
            }
            for (float i = -yScaled; i <= yScaled; i += 0.001f) {
                drawPoint(gl, xc + i, yc + xScaled, color);
                drawPoint(gl, xc + i, yc - xScaled, color);
            }
        }

        // Update isPointInside method to handle Circle
        boolean isPointInside(float x, float y, float size) {
            switch (type) {
                case "Line":
                    return isPointNearLine(x, y, startX, startY, endX, endY, size);
                case "Rectangle":
                case "Ellipse":
                    return Math.min(startX, endX) - size <= x && x <= Math.max(startX, endX) + size &&
                            Math.min(startY, endY) - size <= y && y <= Math.max(startY, endY) + size;
                case "Circle":
                    float xc = (startX + endX) / 2;
                    float yc = (startY + endY) / 2;
                    float radius = (float) Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2)) / 2;
                    return Math.sqrt(Math.pow(x - xc, 2) + Math.pow(y - yc, 2)) <= radius + size;
                case "Brush":
                    return points.stream().anyMatch(p -> Math.hypot(p.x - x, p.y - y) <= size);
                default:
                    return false;
            }
        }

        private void drawMidpointEllipse(GL2 gl, float x1, float y1, float x2, float y2, boolean filled) {
            // Calculate center and radii
            float xc = (x1 + x2) / 2; // Center x
            float yc = (y1 + y2) / 2; // Center y
            float rx = Math.abs(x2 - x1) / 2; // Semi-major axis (horizontal radius)
            float ry = Math.abs(y2 - y1) / 2; // Semi-minor axis (vertical radius)

            // Use integer arithmetic scaled by 1000 for precision, then convert back
            int rxInt = Math.round(rx * 1000);
            int ryInt = Math.round(ry * 1000);
            int x = 0;
            int y = ryInt;
            long rx2 = (long) rxInt * rxInt;
            long ry2 = (long) ryInt * ryInt;

            // Region 1: Slope > -1 (dy/dx > -1)
            long p1 = ry2 - rx2 * ryInt + rx2 / 4; // Initial decision parameter for region 1
            while (ry2 * x <= rx2 * y) { // Continue until slope = -1
                if (filled) {
                    drawScanLineFillEllipse(gl, xc, yc, x, y);
                } else {
                    plotEllipsePoints(gl, xc, yc, x, y);
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

            // Region 2: Slope < -1
            long p2 = (long) (ry2 * (x + 0.5f) * (x + 0.5f) + rx2 * (y - 1) * (y - 1) - rx2 * ry2); // Initial decision
                                                                                                    // parameter
            // for region 2
            while (y >= 0) {
                if (filled) {
                    drawScanLineFillEllipse(gl, xc, yc, x, y);
                } else {
                    plotEllipsePoints(gl, xc, yc, x, y);
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

        private void plotEllipsePoints(GL2 gl, float xc, float yc, int x, int y) {
            float xScaled = x / 1000.0f;
            float yScaled = y / 1000.0f;
            drawPoint(gl, xc + xScaled, yc + yScaled, color);
            drawPoint(gl, xc - xScaled, yc + yScaled, color);
            drawPoint(gl, xc + xScaled, yc - yScaled, color);
            drawPoint(gl, xc - xScaled, yc - yScaled, color);
        }

        private void drawScanLineFillEllipse(GL2 gl, float xc, float yc, int x, int y) {
            float xScaled = x / 1000.0f;
            float yScaled = y / 1000.0f;
            // Draw horizontal lines between symmetric points
            for (float i = -xScaled; i <= xScaled; i += 0.001f) {
                drawPoint(gl, xc + i, yc + yScaled, color);
                drawPoint(gl, xc + i, yc - yScaled, color);
            }
        }

        private void drawBrush(GL2 gl) {
            for (int i = 0; i < points.size() - 1; i++) {
                Point p1 = points.get(i);
                Point p2 = points.get(i + 1);
                bresenhamLine(gl, p1.x, p1.y, p2.x, p2.y);
            }
        }

        private void bresenhamLine(GL2 gl, float x1, float y1, float x2, float y2) {
            int x0 = Math.round(x1 * 1000), y0 = Math.round(y1 * 1000);
            int xEnd = Math.round(x2 * 1000), yEnd = Math.round(y2 * 1000);
            int dx = Math.abs(xEnd - x0), dy = Math.abs(yEnd - y0);
            int sx = x0 < xEnd ? 1 : -1, sy = y0 < yEnd ? 1 : -1;
            int err = dx - dy;

            while (true) {
                drawPoint(gl, x0 / 1000.0f, y0 / 1000.0f, color);
                if (x0 == xEnd && y0 == yEnd)
                    break;
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

        private void ddaLine(GL2 gl, float x1, float y1, float x2, float y2) {
            float dx = x2 - x1, dy = y2 - y1;
            int steps = (int) Math.max(Math.abs(dx), Math.abs(dy)) * 1000;
            float xInc = dx / steps, yInc = dy / steps;
            float x = x1, y = y1;

            for (int i = 0; i <= steps; i++) {
                drawPoint(gl, x, y, color);
                x += xInc;
                y += yInc;
            }
        }

        private void drawScanLineFill(GL2 gl, float x1, float y1, float x2, float y2) {
            float minX = Math.min(x1, x2), maxX = Math.max(x1, x2);
            float minY = Math.min(y1, y2), maxY = Math.max(y1, y2);
            for (float y = minY; y <= maxY; y += 0.001f) {
                for (float x = minX; x <= maxX; x += 0.001f) {
                    drawPoint(gl, x, y, color);
                }
            }
        }

        private boolean isPointNearLine(float px, float py, float x1, float y1, float x2, float y2, float size) {
            float dist = (float) (Math.abs((y2 - y1) * px - (x2 - x1) * py + x2 * y1 - y2 * x1) /
                    Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2)));
            return dist <= size;
        }
    }

    private class Point {
        float x, y;

        Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
