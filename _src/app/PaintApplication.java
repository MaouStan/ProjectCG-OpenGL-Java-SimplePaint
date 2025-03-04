package app;

import gui.PaintFrame;

public class PaintApplication {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PaintFrame frame = new PaintFrame();
                frame.setVisible(true);
            }
        });
    }
}
