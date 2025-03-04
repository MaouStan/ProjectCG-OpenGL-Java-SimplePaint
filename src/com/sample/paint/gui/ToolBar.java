package com.sample.paint.gui;

import com.sample.paint.OpenGLPaintApp;
import javax.swing.*;
import java.awt.*;

public class ToolBar extends JToolBar {
    private OpenGLPaintApp app;
    private JButton selectedButton;

    public ToolBar(OpenGLPaintApp app) {
        this.app = app;
        JButton lineButton = createShapeButton("Line");
        add(lineButton);
        JButton rectButton = createShapeButton("Rectangle");
        add(rectButton);
        JButton brushButton = createShapeButton("Brush");
        add(brushButton);
        JButton eraserButton = createShapeButton("Eraser");
        add(eraserButton);
        JButton colorButton = new JButton("Pick Color");
        colorButton.setFocusable(false);
        colorButton.setForeground(app.getCurrentColor());
        colorButton.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(app, "Pick a Color", app.getCurrentColor());
            if (selectedColor != null) {
                app.setCurrentColor(selectedColor);
                colorButton.setForeground(selectedColor);
            }
        });
        add(colorButton);
    }

    private JButton createShapeButton(String shape) {
        JButton button = new JButton(shape);
        button.setBorder(UIManager.getBorder("Button.border"));
        button.setPreferredSize(new Dimension(40, 40));
        button.setSize(getPreferredSize());
        button.setFocusable(false);
        button.addActionListener(e -> {
            app.setCurrentShape(shape);
            if (selectedButton != null) {
                selectedButton.setForeground(Color.BLACK);
            }
            selectedButton = button;
            selectedButton.setForeground(Color.RED);
            app.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        });
        return button;
    }
}
