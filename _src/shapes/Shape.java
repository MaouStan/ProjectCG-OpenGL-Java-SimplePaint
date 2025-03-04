package shapes;

import com.jogamp.opengl.GL2;
import java.awt.Color;

public abstract class Shape {
    protected int x, y;
    protected Color color;

    public Shape(int x, int y) {
        this.x = x;
        this.y = y;
        this.color = Color.RED; // Default color
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public abstract void update(int x, int y);

    public abstract void draw(GL2 gl);

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " at (" + x + ", " + y + ")";
    }
}
