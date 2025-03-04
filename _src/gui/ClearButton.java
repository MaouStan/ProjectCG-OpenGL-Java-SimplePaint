package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import controllers.DrawingController;

public class ClearButton extends JButton {
    public ClearButton(DrawingController drawingController, CanvasPanel canvasPanel) {
        setText("Clear");
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Clear the canvas
                drawingController.clearShapes();
                canvasPanel.display();
            }
        });
    }
}
