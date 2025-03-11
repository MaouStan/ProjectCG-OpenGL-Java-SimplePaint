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
        JPanel buttonPanel = new JPanel(new GridLayout(0, 2, 5, 5)); // 0 rows, 2 columns, with gaps
        buttonPanel.setOpaque(false);

        // Create shape buttons with icons
        String[] shapes = { "Line", "Rectangle", "Circle", "Ellipse", "Triangle", "Brush", "Eraser" }; // Added Star

        for (String shape : shapes) {
            JButton button = createShapeButton(shape, actionListener);
            buttonPanel.add(button);
            buttons.put(shape, button);
        }

        // Add button panel to toolbar
        add(buttonPanel);
        add(Box.createVerticalStrut(10)); // Add some spacing

        // Set default selected button
        selectedButton = buttons.get("Line");
        selectedButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

        // Add eraser options panel (initially invisible)
        eraserPanel = new JPanel(new GridLayout(0, 1));
        eraserPanel.setBorder(BorderFactory.createTitledBorder("Eraser Mode"));
        eraserModeGroup = new ButtonGroup();

        pointEraserRadio = new JRadioButton("Point Eraser");
        pointEraserRadio.setSelected(true); // Default option
        pointEraserRadio.setActionCommand("PointEraser");
        pointEraserRadio.addActionListener(actionListener);
        pointEraserRadio.setToolTipText("Eraser that removes at the point level");

        shapeEraserRadio = new JRadioButton("Shape Eraser");
        shapeEraserRadio.setActionCommand("ShapeEraser");
        shapeEraserRadio.addActionListener(actionListener);
        shapeEraserRadio.setToolTipText("Eraser that removes entire shapes");

        eraserModeGroup.add(pointEraserRadio);
        eraserModeGroup.add(shapeEraserRadio);

        eraserPanel.add(pointEraserRadio);
        eraserPanel.add(shapeEraserRadio);
        eraserPanel.setVisible(false); // Initially hidden

        add(eraserPanel);
        add(Box.createVerticalStrut(10));

        // Add Clear Canvas button in a 2-row layout
        JPanel clearCanvasPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        clearCanvasPanel.setOpaque(false);

        // Create clear canvas button with icon
        clearCanvasButton = new JButton();
        clearCanvasButton.setActionCommand("ClearCanvas");
        clearCanvasButton.addActionListener(actionListener);
        clearCanvasButton.setToolTipText("Clear Canvas");
        try {
            ImageIcon icon = new ImageIcon("assets/img/trash.png");
            Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            clearCanvasButton.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            // Fallback to text if image can't be loaded
            clearCanvasButton.setText("Clear Canvas");
        }

        // Add label below the icon
        // JLabel clearLabel = new JLabel("Clear Canvas", SwingConstants.CENTER);
        // clearLabel.setForeground(Color.RED);
        // clearLabel.setFont(new Font(clearLabel.getFont().getName(), Font.BOLD, 12));

        clearCanvasPanel.add(clearCanvasButton);
        // clearCanvasPanel.add(clearLabel);
        add(clearCanvasPanel);
        add(Box.createVerticalStrut(20));

        // Color picker
        JPanel colorPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        colorPanel.setOpaque(false);

        // Create pick color button with icon
        JButton colorButton = new JButton();
        colorButton.setActionCommand("Pick Color");
        colorButton.addActionListener(actionListener);
        colorButton.setToolTipText("Pick Color");
        try {
            ImageIcon icon = new ImageIcon("assets/img/pickcolor.png");
            Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            colorButton.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            // Fallback to text if image can't be loaded
            colorButton.setText("Pick Color");
        }

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
        colorDisplay.setToolTipText("Current Color");

        colorPanel.add(colorButton);
        colorPanel.add(colorDisplay);
        add(colorPanel);
        add(Box.createVerticalStrut(10));

        // Fill checkbox
        fillCheckBox = new JCheckBox("Fill");
        fillCheckBox.setActionCommand("Fill");
        fillCheckBox.addActionListener(actionListener);
        fillCheckBox.setToolTipText("Fill shapes with color");
        add(fillCheckBox);
        add(Box.createVerticalStrut(10));

        // Thickness slider
        JLabel thicknessLabel = new JLabel("Thickness:");
        thicknessLabel.setToolTipText("Set line thickness");
        add(thicknessLabel);

        thicknessSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 1);
        thicknessSlider.setMajorTickSpacing(1);
        thicknessSlider.setPaintTicks(true);
        thicknessSlider.setPaintLabels(true);
        thicknessSlider.setToolTipText("Set line thickness");

        // Set a fixed width for the vertical toolbar
        Dimension sliderSize = new Dimension(120, 40);
        thicknessSlider.setPreferredSize(sliderSize);
        thicknessSlider.setMaximumSize(sliderSize);
        add(thicknessSlider);

        // Set a fixed width for the toolbar
        setPreferredSize(new Dimension(150, getPreferredSize().height));
    }

    /**
     * Creates a button with an image icon for the shape tool
     */
    private JButton createShapeButton(String shape, ActionListener actionListener) {
        JButton button = new JButton();
        button.setFocusable(false);
        button.setActionCommand(shape);

        // Load image icon
        try {
            String iconPath = "assets/img/" + shape.toLowerCase() + ".png";
            ImageIcon icon = new ImageIcon(iconPath);

            // Scale the icon to fit the button
            Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
            button.setToolTipText(shape); // Show shape name on hover

            // Set a fixed size for the button
            button.setPreferredSize(new Dimension(48, 48));
        } catch (Exception e) {
            // Fallback to text if image can't be loaded
            button.setText(shape);
        }

        // Add action listener
        button.addActionListener(e -> {
            if (selectedButton != null) {
                selectedButton.setBorder(UIManager.getBorder("Button.border"));
            }
            selectedButton = button;
            selectedButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

            // Forward the event to the main application
            actionListener.actionPerformed(e);
        });

        return button;
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
