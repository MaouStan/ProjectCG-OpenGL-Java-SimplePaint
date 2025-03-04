package com.sample.paint.model;

public class Point {
    public float x;
    public float y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public double distanceTo(Point other) {
        return Math.hypot(other.x - x, other.y - y);
    }

    public double distanceTo(float x, float y) {
        return Math.hypot(x - this.x, y - this.y);
    }
}
