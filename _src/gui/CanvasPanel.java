package gui;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import controllers.DrawingController;
import utils.MouseEventHandler;

public class CanvasPanel extends GLCanvas implements GLEventListener {
    private DrawingController drawingController;
    private Animator animator;

    public CanvasPanel() {
        this.addGLEventListener(this);
        animator = new Animator(this);
        animator.start();
    }

    public void setDrawingController(DrawingController drawingController) {
        this.drawingController = drawingController;
        this.addMouseListener(new MouseEventHandler(drawingController));
        this.addMouseMotionListener(new MouseEventHandler(drawingController));
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        // Initialize OpenGL settings
        // System.out.println("Canvas initialized");
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // Cleanup resources
        // System.out.println("Canvas disposed");
        animator.stop();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        // Render the canvas
        final GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        drawingController.drawShapes(gl);
        // System.out.println("Canvas displayed");
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        // Handle canvas resizing
        final GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, width, height);
        // System.out.println("Canvas reshaped to " + width + "x" + height);
    }

    public DrawingController getDrawingController() {
        return drawingController;
    }
}
