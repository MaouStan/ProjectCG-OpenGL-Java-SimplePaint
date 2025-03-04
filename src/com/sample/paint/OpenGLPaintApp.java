package com.sample.paint;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.sample.paint.gui.ToolBar;
import com.sample.paint.events.MouseHandler;
import com.sample.paint.drawing.Shape;
import javax.swing.*;
import java.awt.*;

public class OpenGLPaintApp extends JFrame implements GLEventListener {
    private GLCanvas canvas;
    private ToolBar toolBar;
    private MouseHandler mouseHandler;
    private String currentShape = "Line";
    private Color currentColor = Color.RED;

    public OpenGLPaintApp() {
        setTitle("OpenGL Paint Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set up OpenGL profile and capabilities
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        canvas = new GLCanvas(capabilities);
        canvas.addGLEventListener(this);

        // Initialize toolbar and mouse handler
        toolBar = new ToolBar(this);
        mouseHandler = new MouseHandler(this, canvas);

        add(toolBar, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        setVisible(true);
    }

    public static void main(String[] args) {
        // Run the application on the event dispatch thread
        SwingUtilities.invokeLater(() -> new OpenGLPaintApp());
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
        for (Shape shape : mouseHandler.getShapes()) {
            shape.draw(gl);
        }

        // Draw ghost shape if currently drawing
        if (mouseHandler.isDrawing()) {
            Shape ghostShape = mouseHandler.createGhostShape();
            if (ghostShape != null) {
                ghostShape.draw(gl);
            }
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, width, height); // Adjust the viewport to the new window size
    }

    public String getCurrentShape() {
        return currentShape;
    }

    public void setCurrentShape(String currentShape) {
        this.currentShape = currentShape;
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(Color currentColor) {
        this.currentColor = currentColor;
    }
}
