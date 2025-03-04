package com.sample.paint.events;

import com.jogamp.opengl.awt.GLCanvas;
import com.sample.paint.OpenGLPaintApp;
import com.sample.paint.drawing.*;
import com.sample.paint.util.Point;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MouseHandler extends MouseAdapter {
    private OpenGLPaintApp app;
    private GLCanvas canvas;
    private float startX, startY, endX, endY;
    private boolean drawing = false;
    private List<Shape> shapes = new ArrayList<>();
    private List<Point> brushPoints = new ArrayList<>();
    private float eraserSize = 0.05f;

    public MouseHandler(OpenGLPaintApp app, GLCanvas canvas) {
        this.app = app;
        this.canvas = canvas;
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        startX = (float) e.getX() / canvas.getWidth() * 2 - 1;
        startY = 1 - (float) e.getY() / canvas.getHeight() * 2;
        drawing = true;
        if (app.getCurrentShape().equals("Brush")) {
            brushPoints.clear();
            brushPoints.add(new Point(startX, startY));
        }
        if (app.getCurrentShape().equals("Eraser")) {
            eraseShape(startX, startY);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        endX = (float) e.getX() / canvas.getWidth() * 2 - 1;
        endY = 1 - (float) e.getY() / canvas.getHeight() * 2;
        drawing = false;
        if (app.getCurrentShape().equals("Brush")) {
            shapes.add(new Brush(app.getCurrentColor(), new ArrayList<>(brushPoints)));
        } else if (!app.getCurrentShape().equals("Eraser")) {
            shapes.add(createShape(app.getCurrentShape(), app.getCurrentColor(), startX, startY, endX, endY));
        }
        if (app.getCurrentShape().equals("Eraser")) {
            eraseShape(endX, endY);
        }
        canvas.display();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        endX = (float) e.getX() / canvas.getWidth() * 2 - 1;
        endY = 1 - (float) e.getY() / canvas.getHeight() * 2;
        if (app.getCurrentShape().equals("Brush")) {
            brushPoints.add(new Point(endX, endY));
        }
        if (app.getCurrentShape().equals("Eraser")) {
            eraseShape(endX, endY);
        }
        canvas.display();
    }

    private void eraseShape(float x, float y) {
        shapes.removeIf(shape -> shape.isPointInside(x, y, eraserSize));
        canvas.display();
    }

    public List<Shape> getShapes() {
        return shapes;
    }

    public boolean isDrawing() {
        return drawing;
    }

    public Shape createGhostShape() {
        if (app.getCurrentShape().equals("Brush")) {
            return new Brush(app.getCurrentColor(), new ArrayList<>(brushPoints));
        } else if (!app.getCurrentShape().equals("Eraser")) {
            return createShape(app.getCurrentShape(), app.getCurrentColor(), startX, startY, endX, endY);
        }
        return null;
    }

    private Shape createShape(String type, Color color, float startX, float startY, float endX, float endY) {
        switch (type) {
            case "Line":
                return new Line(color, startX, startY, endX, endY);
            case "Rectangle":
                return new Rectangle(color, startX, startY, endX, endY);
            case "Circle":
                float radius = (float) Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
                return new Circle(color, startX, startY, radius);
            case "Ellipse":
                float radiusX = Math.abs(endX - startX);
                float radiusY = Math.abs(endY - startY);
                return new Ellipse(color, startX, startY, radiusX, radiusY);
            default:
                throw new IllegalArgumentException("Unknown shape type: " + type);
        }
    }
}
