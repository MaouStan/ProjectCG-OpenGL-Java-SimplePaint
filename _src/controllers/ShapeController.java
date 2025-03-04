package controllers;

import shapes.Shape;

public class ShapeController {
    private Shape currentShape;

    public void setCurrentShape(Shape shape) {
        currentShape = shape;
    }

    public Shape getCurrentShape() {
        return currentShape;
    }
}
