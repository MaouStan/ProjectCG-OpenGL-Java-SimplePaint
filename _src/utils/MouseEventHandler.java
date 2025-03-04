package utils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import controllers.DrawingController;
import shapes.Shape;

public class MouseEventHandler extends MouseAdapter {
    private DrawingController drawingController;
    private int startX, startY;

    public MouseEventHandler(DrawingController drawingController) {
        this.drawingController = drawingController;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        startX = e.getX();
        startY = e.getY();
        Shape currentShape = drawingController.getCurrentShape();
        if (currentShape != null) {
            currentShape.setX(startX);
            currentShape.setY(startY);
            drawingController.addShape(currentShape);
            // System.out.println("Mouse pressed at (" + startX + ", " + startY + ")");
        }
        drawingController.repaintCanvas();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Shape currentShape = drawingController.getCurrentShape();
        if (currentShape != null) {
            currentShape.update(e.getX(), e.getY());
            // System.out.println("Mouse dragged to (" + e.getX() + ", " + e.getY() + ")");
        }
        drawingController.repaintCanvas();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Shape currentShape = drawingController.getCurrentShape();
        if (currentShape != null) {
            currentShape.update(e.getX(), e.getY());
            // System.out.println("Mouse released at (" + e.getX() + ", " + e.getY() + ")");
        }
        drawingController.repaintCanvas();
    }
}
