package shapes;

import com.jogamp.opengl.GL2;

public class Circle extends Shape {
    private int radius;

    public Circle(int x, int y, int radius) {
        super(x, y);
        this.radius = radius;
    }

    @Override
    public void update(int x, int y) {
        this.radius = (int) Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
    }

    @Override
    public void draw(GL2 gl) {
        gl.glColor3f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glVertex2i(x, y);
        for (int i = 0; i <= 360; i++) {
            double angle = Math.toRadians(i);
            int dx = (int) (radius * Math.cos(angle));
            int dy = (int) (radius * Math.sin(angle));
            gl.glVertex2i(x + dx, y + dy);
        }
        gl.glEnd();
    }
}
