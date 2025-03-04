package com.sample.paint.drawing;

import com.jogamp.opengl.GL2;
import com.sample.paint.util.Point;
import java.awt.Color;
import java.util.List;

public abstract class Shape {
    protected String type;
    protected Color color;
    protected List<Point> points;

    public Shape(String type, Color color, List<Point> points) {
        this.type = type;
        this.color = color;
        this.points = points;
    }

    public abstract void draw(GL2 gl);

    public abstract boolean isPointInside(float x, float y, float size);
}
