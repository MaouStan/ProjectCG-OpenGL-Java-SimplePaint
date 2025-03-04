package com.sample.paint.ui;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class ShapesToolbar extends JToolBar {
    private Map<String, JButton> buttons = new HashMap<>();
    private JButton selectedButton;
    private JPanel colorDisplay;
    private JSlider thicknessSlider;
    private JCheckBox fillCheckBox;
    private Color currentColor = Color.RED;
    private ButtonGroup eraserModeGroup;
    private JRadioButton pointEraserRadio;
    private JRadioButton shapeEraserRadio;
    private JPanel eraserPanel;
    private JButton clearCanvasButton;

    /**
     * Constructor accepting any ActionListener (which will be the OpenGLPaintApp)
     */
    public ShapesToolbar(ActionListener actionListener) {
        super(SwingConstants.VERTICAL); // Make toolbar vertical

        // Panel for shape buttons in 2 columns
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 5, 5)); // 0 rows, 2 columns, with gaps
        buttonPanel.setOpaque(false);

        // Create shape buttons
        String[] shapes = { "Line", "Rectangle", "Circle", "Ellipse", "Triangle", "Brush", "Eraser" }; // Added Triangle

        for (String shape : shapes) {
            JButton button = new JButton(shape);
            button.setFocusable(false);
            button.setActionCommand(shape);
            button.addActionListener(e -> {
                if (selectedButton != null) {
                    selectedButton.setForeground(Color.BLACK);
                }
                selectedButton = button;
                selectedButton.setForeground(Color.RED);

                // Forward the event to the main application
                actionListener.actionPerformed(e);
            });

            buttonPanel.add(button);
            buttons.put(shape, button);
        }

        // Add button panel to toolbar
        add(buttonPanel);
        add(Box.createVerticalStrut(10)); // Add some spacing

        // Set default selected button
        selectedButton = buttons.get("Line");
        selectedButton.setForeground(Color.RED);

        // Add eraser options panel (initially invisible)
        eraserPanel = new JPanel(new GridLayout(0, 1));
        eraserPanel.setBorder(BorderFactory.createTitledBorder("Eraser Mode"));
        eraserModeGroup = new ButtonGroup();

        pointEraserRadio = new JRadioButton("Point Eraser");
        pointEraserRadio.setSelected(true); // Default option
        pointEraserRadio.setActionCommand("PointEraser");
        pointEraserRadio.addActionListener(actionListener);

        shapeEraserRadio = new JRadioButton("Shape Eraser");
        shapeEraserRadio.setActionCommand("ShapeEraser");
        shapeEraserRadio.addActionListener(actionListener);

        eraserModeGroup.add(pointEraserRadio);
        eraserModeGroup.add(shapeEraserRadio);

        eraserPanel.add(pointEraserRadio);
        eraserPanel.add(shapeEraserRadio);
        eraserPanel.setVisible(false); // Initially hidden

        add(eraserPanel);
        add(Box.createVerticalStrut(10));

        // Add Clear Canvas button in a 2-column panel
        JPanel clearCanvasPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        clearCanvasPanel.setOpaque(false);

        clearCanvasButton = new JButton("Clear Canvas");
        clearCanvasButton.setActionCommand("ClearCanvas");
        clearCanvasButton.addActionListener(actionListener);
        clearCanvasButton.setForeground(Color.RED);
        clearCanvasButton.setFont(new Font(clearCanvasButton.getFont().getName(), Font.BOLD, 12));
        clearCanvasPanel.add(clearCanvasButton);
        add(clearCanvasPanel);
        add(Box.createVerticalStrut(20));

        // Color picker
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        colorPanel.setOpaque(false);

        JButton colorButton = new JButton("Pick Color");
        colorButton.setActionCommand("Pick Color");
        colorButton.addActionListener(actionListener);
        colorPanel.add(colorButton);

        // Color display
        colorDisplay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(currentColor);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        colorDisplay.setPreferredSize(new Dimension(40, 30));
        colorDisplay.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        colorPanel.add(colorDisplay);

        add(colorPanel);
        add(Box.createVerticalStrut(10));

        // Fill checkbox
        fillCheckBox = new JCheckBox("Fill");
        fillCheckBox.setActionCommand("Fill");
        fillCheckBox.addActionListener(actionListener);
        add(fillCheckBox);
        add(Box.createVerticalStrut(10));

        // Thickness slider
        add(new JLabel("Thickness:"));
        thicknessSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 1);
        thicknessSlider.setMajorTickSpacing(1);
        thicknessSlider.setPaintTicks(true);
        thicknessSlider.setPaintLabels(true);

        // Set a fixed width for the vertical toolbar
        Dimension sliderSize = new Dimension(120, 40);
        thicknessSlider.setPreferredSize(sliderSize);
        thicknessSlider.setMaximumSize(sliderSize);
        add(thicknessSlider);

        // Set a fixed width for the toolbar
        setPreferredSize(new Dimension(150, getPreferredSize().height));
    }

    /**
     * Get the current thickness value from the slider
     */
    public float getThickness() {
        return thicknessSlider.getValue();
    }

    /**
     * Add a listener that will be notified when the thickness slider changes
     */
    public void addThicknessListener(ActionListener listener) {
        thicknessSlider.addChangeListener(e -> listener.actionPerformed(null));
    }

    /**
     * Update the color display panel with a new color
     */
    public void updateColorDisplay(Color color) {
        this.currentColor = color;
        colorDisplay.repaint();
    }

    /**
     * Check if the "Fill" checkbox is selected
     */
    public boolean isFilled() {
        return fillCheckBox.isSelected();
    }

    /**
     * Get the currently selected color
     */
    public Color getCurrentColor() {
        return currentColor;
    }

    /**
     * Get the current eraser mode
     */
    public String getEraserMode() {
        return pointEraserRadio.isSelected() ? "point" : "shape";
    }

    /**
     * Set visibility of eraser options
     */
    public void setEraserOptionsVisible(boolean visible) {
        eraserPanel.setVisible(visible);
        revalidate();
        repaint();
    }
}
