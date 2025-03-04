package controllers;

import shapes.Shape;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

import com.jogamp.opengl.GL2;
import gui.CanvasPanel;

public class DrawingController {
    private List<Shape> shapes;
    private Shape currentShape;
    private Color currentColor;
    private CanvasPanel canvasPanel;

    public DrawingController() {
        shapes = new ArrayList<>();
        currentColor = Color.BLACK; // Default color
    }

    public void setCanvasPanel(CanvasPanel canvasPanel) {
        this.canvasPanel = canvasPanel;
    }

    public void addShape(Shape shape) {
        shapes.add(shape);
        System.out.println("Shape added: " + shape);
        repaintCanvas();
    }

    public void drawShapes(GL2 gl) {
        for (Shape shape : shapes) {
            shape.draw(gl);
        }
    }

    public void clearShapes() {
        shapes.clear();
        System.out.println("Shapes cleared");
        repaintCanvas();
    }

    public void setCurrentShape(Shape shape) {
        currentShape = shape;
        if (currentShape != null) {
            currentShape.setColor(currentColor);
        }
        System.out.println("Current shape set: " + shape);
    }

    public Shape getCurrentShape() {
        return currentShape;
    }

    public void setCurrentColor(Color color) {
        currentColor = color;
        if (currentShape != null) {
            currentShape.setColor(color);
        }
        System.out.println("Current color set: " + color);
    }

    public void repaintCanvas() {
        canvasPanel.display();
    }
}
