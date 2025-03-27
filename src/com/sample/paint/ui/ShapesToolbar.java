package com.sample.paint.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

public class ShapesToolbar extends JToolBar {
    private Map<String, JButton> buttons = new HashMap<>();
    private JButton selectedButton;
    private JPanel colorDisplay;
    private JSpinner thicknessSpinner; // Changed from JSlider to JSpinner
    private JCheckBox fillCheckBox;
    private Color currentColor = Color.RED;
    private ButtonGroup eraserModeGroup;
    private JRadioButton pointEraserRadio;
    private JRadioButton shapeEraserRadio;
    private JPanel eraserPanel;
    private JButton clearCanvasButton;
    private ActionListener mainAppListener; // To notify the main app about toolbar state changes

    /**
     * Constructor accepting any ActionListener (which will be the OpenGLPaintApp)
     */
    public ShapesToolbar(ActionListener actionListener) {
        super(SwingConstants.VERTICAL); // Make toolbar vertical
        this.mainAppListener = actionListener;

        // Set toolbar to be floatable so it can be dragged out
        setFloatable(false);

        // Add listener to make the toolbar window resizable when floated
        // Also notify main application about toolbar state changes
        addPropertyChangeListener("ancestor", evt -> {
            if (evt.getNewValue() instanceof JFrame) {
                JFrame floatingFrame = (JFrame) evt.getNewValue();
                floatingFrame.setResizable(true);
                // Set reasonable minimum size for the floating frame
                floatingFrame.setMinimumSize(new Dimension(120, 200));

                // Notify the main app that toolbar is now floating
                ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "ToolbarFloating");
                mainAppListener.actionPerformed(e);
            } else if (evt.getOldValue() instanceof JFrame && evt.getNewValue() instanceof JComponent) {
                // Toolbar was docked back, notify main app
                ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "ToolbarDocked");
                mainAppListener.actionPerformed(e);
            }
        });

        // Panel for shape buttons in 2 columns
        JPanel buttonPanel = new JPanel(new GridLayout(0, 2, 5, 5)); // 0 rows, 2 columns, with gaps
        buttonPanel.setOpaque(false);

        // Create shape buttons with icons
        String[] shapes = { "Line", "Rectangle", "Circle", "Ellipse", "Triangle", "Brush", "Eraser", "Fill" };

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

        // Add buttons for file operations (like save)
        JPanel filePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        filePanel.setOpaque(false);
        filePanel.setBorder(BorderFactory.createTitledBorder("File Operations"));

        // Create save button
        JButton saveButton = new JButton();
        saveButton.setActionCommand("SaveCanvas");
        saveButton.addActionListener(actionListener);
        saveButton.setToolTipText("Save Canvas as Image");
        try {
            ImageIcon icon = new ImageIcon("assets/img/save.png");
            Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            saveButton.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            saveButton.setText("Save");
        }

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
            clearCanvasButton.setText("Clear Canvas");
        }

        filePanel.add(saveButton);
        filePanel.add(clearCanvasButton);
        add(filePanel);
        add(Box.createVerticalStrut(10));

        // Color picker panel with foreground and background color options
        JPanel colorPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        colorPanel.setOpaque(false);

        // Create pick color button with icon
        JButton colorButton = new JButton();
        colorButton.setActionCommand("Pick Color");
        colorButton.addActionListener(actionListener);
        colorButton.setToolTipText("Change Foreground Color");
        try {
            ImageIcon icon = new ImageIcon("assets/img/pickcolor.png");
            Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            colorButton.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            colorButton.setText("FG");
        }

        // Background color button
        JButton bgColorButton = new JButton();
        bgColorButton.setActionCommand("Background Color");
        bgColorButton.addActionListener(actionListener);
        bgColorButton.setToolTipText("Change Background Color");
        try {
            // Use the specified background color icon
            ImageIcon icon = new ImageIcon("assets/img/bg-changing.png");
            Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            bgColorButton.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            bgColorButton.setText("BG");
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

        // Add all components to the color panel
        colorPanel.add(new JLabel("Colors:"));
        colorPanel.add(new JLabel("")); // Empty cell for alignment
        colorPanel.add(colorButton);
        colorPanel.add(bgColorButton);
        add(colorPanel);
        add(Box.createVerticalStrut(10));
        add(colorDisplay);
        add(Box.createVerticalStrut(10));

        // Fill checkbox
        fillCheckBox = new JCheckBox("Fill");
        fillCheckBox.setActionCommand("Fill");
        fillCheckBox.addActionListener(actionListener);
        fillCheckBox.setToolTipText("Fill shapes with color");
        add(fillCheckBox);
        add(Box.createVerticalStrut(10));

        // Thickness control with spinner instead of slider
        JLabel thicknessLabel = new JLabel("Thickness:");
        thicknessLabel.setToolTipText("Set line thickness");
        add(thicknessLabel);

        // Create spinner model with min=1, max=10, step=1, initial=1
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 9999, 1);
        thicknessSpinner = new JSpinner(spinnerModel);
        thicknessSpinner.setToolTipText("Set line thickness");

        // Make the spinner more compact
        Dimension spinnerSize = new Dimension(60, 25);
        thicknessSpinner.setPreferredSize(spinnerSize);
        thicknessSpinner.setMaximumSize(spinnerSize);

        // Add change listener
        thicknessSpinner.addChangeListener(e -> {
            // Notify the action listener when thickness changes
            ActionEvent actionEvent = new ActionEvent(thicknessSpinner, ActionEvent.ACTION_PERFORMED, "ThicknessChanged");
            actionListener.actionPerformed(actionEvent);
        });

        add(thicknessSpinner);
        add(Box.createVerticalStrut(10));

        // Set a reduced width for the toolbar
        setPreferredSize(new Dimension(120, getPreferredSize().height));
    }

    /**
     * Creates a button with an image icon for the shape tool
     */
    private JButton createShapeButton(String shape, ActionListener actionListener) {
        JButton button = new JButton();
        button.setFocusable(false);

        // Set the correct action command for the Fill tool
        if (shape.equals("Fill")) {
            button.setActionCommand("FillTool");
        } else {
            button.setActionCommand(shape);
        }

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
            ActionEvent newEvent = new ActionEvent(
                e.getSource(),
                e.getID(),
                button.getActionCommand() // Use the button's action command
            );
            actionListener.actionPerformed(newEvent);
        });

        return button;
    }

    /**
     * Get the current thickness value from the spinner
     */
    public float getThickness() {
        return ((Number)thicknessSpinner.getValue()).floatValue();
    }

    /**
     * Add a listener that will be notified when the thickness changes
     */
    public void addThicknessListener(ActionListener listener) {
        thicknessSpinner.addChangeListener(e -> listener.actionPerformed(null));
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
