package shapes;

import com.jogamp.opengl.GL2;

public class Ellipse extends Shape {
    private int radiusX, radiusY;

    public Ellipse(int x, int y, int radiusX, int radiusY) {
        super(x, y);
        this.radiusX = radiusX;
        this.radiusY = radiusY;
    }

    @Override
    public void update(int x, int y) {
        this.radiusX = Math.abs(x - this.x);
        this.radiusY = Math.abs(y - this.y);
    }

    @Override
    public void draw(GL2 gl) {
        gl.glColor3f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glVertex2i(x, y);
        for (int i = 0; i <= 360; i++) {
            double angle = Math.toRadians(i);
            int dx = (int) (radiusX * Math.cos(angle));
            int dy = (int) (radiusY * Math.sin(angle));
            gl.glVertex2i(x + dx, y + dy);
        }
        gl.glEnd();
    }
}
