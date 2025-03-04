package shapes;

import com.jogamp.opengl.GL2;

public class Line extends Shape {
    private int x2, y2;

    public Line(int x, int y, int x2, int y2) {
        super(x, y);
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public void update(int x, int y) {
        this.x2 = x;
        this.y2 = y;
    }

    @Override
    public void draw(GL2 gl) {
        gl.glColor3f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex2i(x, y);
        gl.glVertex2i(x2, y2);
        gl.glEnd();
    }
}
