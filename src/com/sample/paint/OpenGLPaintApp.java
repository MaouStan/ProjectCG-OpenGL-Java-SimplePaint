package com.sample.paint;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.sample.paint.model.*;
import com.sample.paint.model.Point;
import com.sample.paint.model.Rectangle;
import com.sample.paint.model.Shape;
import com.sample.paint.ui.ShapesToolbar;
import com.sample.paint.util.GLRenderer;
import com.sample.paint.util.DrawingAlgorithms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class OpenGLPaintApp extends JFrame implements GLEventListener, ActionListener {
    private GLCanvas canvas;
    private float startX, startY, endX, endY;
    private boolean drawing = false;
    private String currentShape = "Line";
    private List<com.sample.paint.model.Shape> shapes = new ArrayList<>();
    private List<com.sample.paint.model.Point> brushPoints = new ArrayList<>();
    private float eraserSize = 0.05f;
    private Color currentColor = Color.RED;
    private boolean isFilled = false;
    private float thickness = 1.0f;
    private ShapesToolbar toolbar;
    private Color backgroundColor = Color.WHITE;
    private List<com.sample.paint.model.Point> eraserPoints = new ArrayList<>();
    private String eraserMode = "point"; // Default to point eraser

    public OpenGLPaintApp() {
        setTitle("OpenGL Algorithm-Based Paint Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize OpenGL canvas
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        canvas = new GLCanvas(capabilities);
        canvas.addGLEventListener(this);

        // Set up UI components
        toolbar = new ShapesToolbar(this);
        toolbar.addThicknessListener(e -> {
            thickness = toolbar.getThickness();
            if (currentShape.equals("Eraser")) {
                eraserSize = 0.01f * thickness * 2;
            }
        });

        setupMouseListeners();

        // Add components to frame
        add(toolbar, BorderLayout.WEST); // Changed from NORTH to WEST
        add(canvas, BorderLayout.CENTER);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "Pick Color":
                Color selectedColor = JColorChooser.showDialog(this, "Pick a Color", currentColor);
                if (selectedColor != null) {
                    currentColor = selectedColor;
                    toolbar.updateColorDisplay(currentColor);
                }
                break;
            case "Fill":
                isFilled = toolbar.isFilled();
                break;
            case "Eraser":
                currentShape = command;
                // Show eraser options when Eraser is selected
                toolbar.setEraserOptionsVisible(true);
                break;
            case "PointEraser":
                eraserMode = "point";
                break;
            case "ShapeEraser":
                eraserMode = "shape";
                break;
            case "ClearCanvas":
                clearCanvas();
                break;
            default:
                currentShape = command;
                if (!command.equals("Eraser")) {
                    // Hide eraser options when any other tool is selected
                    toolbar.setEraserOptionsVisible(false);
                }
                break;
        }
    }

    /**
     * Clear the entire canvas
     */
    private void clearCanvas() {
        // Show confirmation dialog before clearing
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear the entire canvas?",
                "Clear Canvas",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            shapes.clear(); // Remove all shapes
            canvas.display(); // Refresh display
        }
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // Nothing to dispose
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        GLRenderer.clearScreen(gl);

        // Draw all shapes
        for (com.sample.paint.model.Shape shape : shapes) {
            shape.draw(gl);
        }

        // Draw the shape being currently drawn (ghost shape)
        if (drawing) {
            if (currentShape.equals("Eraser")) {
                // Draw eraser trail with background color
                drawEraserTrail(gl);
            } else if (!currentShape.equals("Eraser")) {
                Shape ghostShape = createShape();
                if (ghostShape != null) {
                    ghostShape.draw(gl);
                }
            }
        }
    }

    // Create appropriate shape based on current settings
    private Shape createShape() {
        switch (currentShape) {
            case "Line":
                return new Line(startX, startY, endX, endY, currentColor, thickness);
            case "Rectangle":
                return new Rectangle(startX, startY, endX, endY, currentColor, isFilled,
                        thickness);
            case "Circle":
                return new Circle(startX, startY, endX, endY, currentColor, isFilled, thickness);
            case "Ellipse":
                return new Ellipse(startX, startY, endX, endY, currentColor, isFilled, thickness);
            case "Triangle":
                return new Triangle(startX, startY, endX, endY, currentColor, isFilled, thickness);
            case "Brush":
                return new BrushStroke(new ArrayList<>(brushPoints), currentColor, thickness);
            default:
                return null;
        }
    }

    // New method to draw eraser trail
    private void drawEraserTrail(GL2 gl) {
        float thickerSize = thickness / 2; // Make eraser slightly thicker than brush

        if (eraserMode.equals("point")) {
            // For point eraser, only draw individual points (no connecting lines)
            for (Point p : eraserPoints) {
                GLRenderer.drawThickPoint(gl, p.x, p.y, backgroundColor, thickerSize);
            }
        } else {
            // For shape eraser, draw connecting lines (for visual feedback)
            for (int i = 0; i < eraserPoints.size() - 1; i++) {
                Point p1 = eraserPoints.get(i);
                Point p2 = eraserPoints.get(i + 1);
                GLRenderer.drawThickPoint(gl, p1.x, p1.y, backgroundColor, thickerSize);
                DrawingAlgorithms.bresenhamLine(gl, p1.x, p1.y, p2.x, p2.y, backgroundColor, thickerSize);
            }

            // Draw the current point
            if (!eraserPoints.isEmpty()) {
                Point p = eraserPoints.get(eraserPoints.size() - 1);
                GLRenderer.drawThickPoint(gl, p.x, p.y, backgroundColor, thickerSize);
            }
        }
    }

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
                } else if (currentShape.equals("Eraser")) {
                    // Start a new eraser trail - clear the previous points
                    eraserPoints.clear();
                    eraserPoints.add(new Point(startX, startY));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                float aspectRatio = (float) canvas.getWidth() / canvas.getHeight();
                endX = ((float) e.getX() / canvas.getWidth() * 2 - 1) * aspectRatio;
                endY = 1 - (float) e.getY() / canvas.getHeight() * 2;
                drawing = false;

                if (currentShape.equals("Brush")) {
                    shapes.add(new BrushStroke(new ArrayList<>(brushPoints), currentColor, thickness));
                } else if (currentShape.equals("Eraser")) {
                    if (eraserMode.equals("point") && !eraserPoints.isEmpty()) {
                        // Add all eraser points as a permanent brush stroke with background color
                        shapes.add(new BrushStroke(new ArrayList<>(eraserPoints), backgroundColor, thickness * 2));
                    }

                    // Clear for next drawing operation
                    eraserPoints.clear();
                } else {
                    Shape shape = createShape();
                    if (shape != null) {
                        shapes.add(shape);
                    }
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
                    // Handle erasing based on mode
                    if (eraserMode.equals("point")) {
                        // Add to eraser trail for point eraser (draw with background color)
                        eraserPoints.add(new Point(endX, endY));

                        // Immediately add a permanent eraser point (for better performance)
                        if (eraserPoints.size() % 3 == 0) { // Add every few points to avoid too many small shapes
                            // Add a single point as a permanent shape with background color
                            shapes.add(new BrushStroke(List.of(new Point(endX, endY)), backgroundColor, thickness * 2));
                        }
                    } else {
                        // Shape eraser - remove entire shapes
                        eraseShapes(endX, endY);
                    }
                }
                canvas.display();
            }
        });
    }

    /**
     * Erase entire shapes that the cursor touches
     */
    private void eraseShapes(float x, float y) {
        shapes.removeIf(shape -> shape.isPointInside(x, y, eraserSize));
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        GLRenderer.setupViewport(gl, width, height);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OpenGLPaintApp::new);
    }
}
