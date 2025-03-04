package com.sample.paint;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.sample.paint.model.*;
import com.sample.paint.model.Shape;
import com.sample.paint.ui.ShapesToolbar;
import com.sample.paint.util.GLRenderer;

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
            default:
                currentShape = command;
                break;
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
        if (drawing && !currentShape.equals("Eraser")) {
            com.sample.paint.model.Shape ghostShape = createShape();
            if (ghostShape != null) {
                ghostShape.draw(gl);
            }
        }
    }

    // Create appropriate shape based on current settings
    private com.sample.paint.model.Shape createShape() {
        switch (currentShape) {
            case "Line":
                return new Line(startX, startY, endX, endY, currentColor, thickness);
            case "Rectangle":
                return new com.sample.paint.model.Rectangle(startX, startY, endX, endY, currentColor, isFilled,
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
                    brushPoints.add(new com.sample.paint.model.Point(startX, startY));
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
                    eraseShape(endX, endY);
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
                    brushPoints.add(new com.sample.paint.model.Point(endX, endY));
                } else if (currentShape.equals("Eraser")) {
                    eraseShape(endX, endY);
                }
                canvas.display();
            }
        });
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        GLRenderer.setupViewport(gl, width, height);
    }

    private void eraseShape(float x, float y) {
        shapes.removeIf(shape -> shape.isPointInside(x, y, eraserSize));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OpenGLPaintApp::new);
    }
}
