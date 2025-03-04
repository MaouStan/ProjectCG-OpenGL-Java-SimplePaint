package gui;

import javax.swing.*;
import java.awt.*;
import controllers.DrawingController;
import shapes.*;
import shapes.Rectangle;

public class Toolbar extends JPanel {
    public Toolbar(DrawingController drawingController) {
        // Add tool buttons (line, rectangle, etc.)
        JButton lineButton = new JButton("Line");
        JButton rectangleButton = new JButton("Rectangle");
        JButton circleButton = new JButton("Circle");
        JButton ellipseButton = new JButton("Ellipse");

        lineButton.addActionListener(_ -> drawingController.setCurrentShape(new Line(0, 0, 0, 0)));
        rectangleButton.addActionListener(_ -> drawingController.setCurrentShape(new Rectangle(0, 0, 0, 0)));
        circleButton.addActionListener(_ -> drawingController.setCurrentShape(new Circle(0, 0, 0)));
        ellipseButton.addActionListener(_ -> drawingController.setCurrentShape(new Ellipse(0, 0, 0, 0)));

        add(lineButton);
        add(rectangleButton);
        add(circleButton);
        add(ellipseButton);

        // Add color picker
        JButton colorButton = new JButton("Pick Color");
        colorButton.addActionListener(_ -> {
            Color color = JColorChooser.showDialog(null, "Choose a color", Color.BLACK);
            if (color != null) {
                drawingController.setCurrentColor(color);
            }
        });
        add(colorButton);
    }
}
