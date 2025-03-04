package gui;

import javax.swing.*;
import java.awt.*;
import controllers.DrawingController;

public class PaintFrame extends JFrame {
    public PaintFrame() {
        setTitle("Paint Program");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        CanvasPanel canvasPanel = new CanvasPanel();
        DrawingController drawingController = new DrawingController();
        drawingController.setCanvasPanel(canvasPanel);
        canvasPanel.setDrawingController(drawingController);

        // Add toolbar
        Toolbar toolbar = new Toolbar(drawingController);
        add(toolbar, BorderLayout.NORTH);

        // Add canvas panel
        add(canvasPanel, BorderLayout.CENTER);

        // Add clear button
        ClearButton clearButton = new ClearButton(drawingController, canvasPanel);
        add(clearButton, BorderLayout.SOUTH);
    }
}
