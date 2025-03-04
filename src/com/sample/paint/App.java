package com.sample.paint;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class App extends JFrame implements GLEventListener {
    private GLCanvas canvas;
    private float startX, startY, endX, endY;
    private boolean drawing = false;
    private String currentShape = "Line";
    private List<Shape> shapes = new ArrayList<>();
    private List<Point> brushPoints = new ArrayList<>();
    private float eraserSize = 0.05f;
    private Color currentColor = Color.RED;
    private JButton selectedButton;

    public App() {
        setTitle("OpenGL Paint Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set up OpenGL profile and capabilities
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        canvas = new GLCanvas(capabilities);
        canvas.addGLEventListener(this);

        // Add mouse listener for drawing
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Convert mouse coordinates to OpenGL coordinates
                startX = (float) e.getX() / canvas.getWidth() * 2 - 1;
                startY = 1 - (float) e.getY() / canvas.getHeight() * 2;
                drawing = true;
                if (currentShape.equals("Brush")) {
                    brushPoints.clear();
                    brushPoints.add(new Point(startX, startY));
                }
                if (currentShape.equals("Eraser")) {
                    eraseShape(startX, startY);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Convert mouse coordinates to OpenGL coordinates
                endX = (float) e.getX() / canvas.getWidth() * 2 - 1;
                endY = 1 - (float) e.getY() / canvas.getHeight() * 2;
                drawing = false;
                if (currentShape.equals("Brush")) {
                    shapes.add(new Shape(currentShape, new ArrayList<>(brushPoints)));
                } else {
                    shapes.add(new Shape(currentShape, startX, startY, endX, endY));
                }
                if (currentShape.equals("Eraser")) {
                    eraseShape(endX, endY);
                }
                canvas.display(); // Refresh the canvas to draw the shape
            }
        });

        canvas.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Convert mouse coordinates to OpenGL coordinates
                endX = (float) e.getX() / canvas.getWidth() * 2 - 1;
                endY = 1 - (float) e.getY() / canvas.getHeight() * 2;
                if (currentShape.equals("Brush")) {
                    brushPoints.add(new Point(endX, endY));
                }
                if (currentShape.equals("Eraser")) {
                    eraseShape(endX, endY);
                }
                canvas.display(); // Refresh the canvas to show ghost drawing
            }
        });

        // Add toolbar for shape selection
        JToolBar toolBar = new JToolBar();
        JButton lineButton = createShapeButton("Line");
        toolBar.add(lineButton);
        JButton rectButton = createShapeButton("Rectangle");
        toolBar.add(rectButton);
        JButton brushButton = createShapeButton("Brush");
        toolBar.add(brushButton);
        JButton eraserButton = createShapeButton("Eraser");
        toolBar.add(eraserButton);
        JButton colorButton = new JButton("Pick Color");
        colorButton.setFocusable(false);
        colorButton.setForeground(currentColor);
        colorButton.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(this, "Pick a Color", currentColor);
            if (selectedColor != null) {
                currentColor = selectedColor;
                colorButton.setForeground(selectedColor);
            }
        });
        toolBar.add(colorButton);
        // Add more shape buttons as needed
        add(toolBar, BorderLayout.NORTH);

        add(canvas, BorderLayout.CENTER);
        setVisible(true);
    }

    private JButton createShapeButton(String shape) {
        JButton button = new JButton(shape);
        button.setBorder(UIManager.getBorder("Button.border"));
        button.setPreferredSize(new Dimension(40, 40));
        button.setSize(getPreferredSize());
        button.setFocusable(false);
        button.addActionListener(e -> {
            currentShape = shape;
            // if on active set border red
            if (selectedButton != null) {
                // selectedButton.setBorder(BorderFactory.createEmptyBorder());
                // set to default border
                selectedButton.setForeground(Color.BLACK);
            }
            selectedButton = button;
            // selectedButton text to red
            selectedButton.setForeground(Color.RED);
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        });
        return button;
    }

    public static void main(String[] args) {
        // Run the application on the event dispatch thread
        SwingUtilities.invokeLater(() -> new App());
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // Set background color to white
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // Cleanup code (if needed)
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT); // Clear the screen

        // Draw all shapes
        for (Shape shape : shapes) {
            shape.draw(gl);
        }

        // Draw ghost shape if currently drawing
        if (drawing) {
            Shape ghostShape;
            if (currentShape.equals("Brush")) {
                ghostShape = new Shape(currentShape, new ArrayList<>(brushPoints));
            } else {
                ghostShape = new Shape(currentShape, startX, startY, endX, endY);
            }
            ghostShape.draw(gl);
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, width, height); // Adjust the viewport to the new window size
    }

    private void eraseShape(float x, float y) {
        shapes.removeIf(shape -> shape.isPointInside(x, y, eraserSize));
        canvas.display(); // Refresh the canvas to remove the shape
    }

    private class Shape {
        String type;
        float startX, startY, endX, endY;
        List<Point> points;
        Color color;

        Shape(String type, float startX, float startY, float endX, float endY) {
            this.type = type;
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.color = currentColor;
        }

        Shape(String type, List<Point> points) {
            this.type = type;
            this.points = points;
            this.color = currentColor;
        }

        void draw(GL2 gl) {
            gl.glColor3f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f); // Set drawing color
            switch (type) {
                case "Line":
                    gl.glBegin(GL2.GL_LINES);
                    gl.glVertex2f(startX, startY);
                    gl.glVertex2f(endX, endY);
                    gl.glEnd();
                    break;
                case "Rectangle":
                    gl.glBegin(GL2.GL_LINE_LOOP);
                    gl.glVertex2f(startX, startY);
                    gl.glVertex2f(endX, startY);
                    gl.glVertex2f(endX, endY);
                    gl.glVertex2f(startX, endY);
                    gl.glEnd();
                    break;
                case "Brush":
                    gl.glBegin(GL2.GL_LINE_STRIP);
                    for (Point point : points) {
                        gl.glVertex2f(point.x, point.y);
                    }
                    gl.glEnd();
                    break;
                // Add more shapes as needed
            }
        }

        boolean isPointInside(float x, float y, float size) {
            switch (type) {
                case "Line":
                    return isPointNearLine(x, y, startX, startY, endX, endY, size);
                case "Rectangle":
                    return isPointNearRectangle(x, y, startX, startY, endX, endY, size);
                case "Brush":
                    return points.stream().anyMatch(point -> isPointNearPoint(x, y, point.x, point.y, size));
                default:
                    return false;
            }
        }

        private boolean isPointNearLine(float px, float py, float x1, float y1, float x2, float y2, float size) {
            float dist = (float) (Math.abs((y2 - y1) * px - (x2 - x1) * py + x2 * y1 - y2 * x1) /
                    Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2)));
            return dist <= size;
        }

        private boolean isPointNearRectangle(float px, float py, float x1, float y1, float x2, float y2, float size) {
            return (px >= x1 - size && px <= x2 + size && py >= y1 - size && py <= y2 + size);
        }

        private boolean isPointNearPoint(float px, float py, float x, float y, float size) {
            return Math.sqrt(Math.pow(px - x, 2) + Math.pow(py - y, 2)) <= size;
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
