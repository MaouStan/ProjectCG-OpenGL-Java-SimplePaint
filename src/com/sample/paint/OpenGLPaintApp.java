package com.sample.paint;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import com.sample.paint.model.*;
import com.sample.paint.model.Point;
import com.sample.paint.model.Rectangle;
import com.sample.paint.model.Shape;
import com.sample.paint.ui.ShapesToolbar;
import com.sample.paint.util.GLRenderer;
import com.sample.paint.util.DrawingAlgorithms;
import com.sample.paint.util.GLReadBufferUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OpenGLPaintApp extends JFrame implements GLEventListener, ActionListener {
  private GLCanvas canvas;
  private float startX, startY, endX, endY;
  private boolean drawing = false;
  private String currentShape = "Line";
  private List<com.sample.paint.model.Shape> shapes = new ArrayList<>();
  private List<com.sample.paint.model.Point> brushPoints = new ArrayList<>();
  private float eraserSize = 0.05f;
  private Color currentColor = Color.RED;
  private boolean isFilled = false;
  private float thickness = 1.0f;
  private ShapesToolbar toolbar;
  private Color backgroundColor = Color.WHITE;
  private List<com.sample.paint.model.Point> eraserPoints = new ArrayList<>();
  private String eraserMode = "point"; // Default to point eraser
  // Track current mouse position for showing brush/eraser borders
  private float currentMouseX = 0;
  private float currentMouseY = 0;
  private boolean showBrushBorder = false;
  // Zoom related variables
  private float zoomFactor = 1.0f;
  private float zoomIncrement = 0.1f;
  private float minZoom = 0.1f;
  private float maxZoom = 5.0f;
  private float panX = 0.0f;
  private float panY = 0.0f;
  // Add panning support
  private boolean isPanning = false;
  private float lastPanX = 0, lastPanY = 0;
  private Cursor defaultCursor;
  // Zoom Area tool variables
  private boolean isZoomAreaActive = false;
  private float zoomStartX, zoomStartY, zoomEndX, zoomEndY;
  // Status bar reference
  private JLabel statusLabel;
  private JScrollPane scrollableToolbar;

  public OpenGLPaintApp() {
    setTitle("OpenGL Algorithm-Based Paint Application");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // Initialize OpenGL canvas
    GLProfile profile = GLProfile.get(GLProfile.GL2);
    GLCapabilities capabilities = new GLCapabilities(profile);
    canvas = new GLCanvas(capabilities);
    canvas.addGLEventListener(this);

    // Set up animator to redraw canvas at regular intervals
    final com.jogamp.opengl.util.FPSAnimator animator = new com.jogamp.opengl.util.FPSAnimator(canvas, 30);
    animator.start();

    // Set up UI components
    toolbar = new ShapesToolbar(this);
    toolbar.addThicknessListener(e -> {
      thickness = toolbar.getThickness();
      if (currentShape.equals("Eraser")) {
        eraserSize = 0.01f * thickness * 2;
      }
    });

    // Create a scrollable toolbar by wrapping it in a JScrollPane
    scrollableToolbar = new JScrollPane(toolbar);
    scrollableToolbar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollableToolbar.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollableToolbar.setBorder(null); // Remove border
    scrollableToolbar.setPreferredSize(new Dimension(150, getHeight()));

    // Add component listener to handle main frame resizing
    addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            // Update canvas and trigger display refresh
            canvas.display();
        }
    });

    setupMouseListeners();

    // Add components to frame
    add(scrollableToolbar, BorderLayout.WEST);
    add(canvas, BorderLayout.CENTER);

    // Add status bar for zoom information
    JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
    statusLabel = new JLabel("Zoom: 100%");
    statusBar.add(statusLabel);
    add(statusBar, BorderLayout.SOUTH);

    // Initialize cursors
    defaultCursor = Cursor.getDefaultCursor();

    // Pack the frame to adjust its size based on preferred sizes of components
    pack();

    setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    switch (command) {
      case "Pick Color":
        Color selectedColor = JColorChooser.showDialog(this, "Pick a Color", currentColor);
        if (selectedColor != null) {
          currentColor = selectedColor;
          toolbar.updateColorDisplay(currentColor);
        }
        break;
      case "Background Color":
        Color selectedBgColor = JColorChooser.showDialog(this, "Pick Background Color", backgroundColor);
        if (selectedBgColor != null) {
          backgroundColor = selectedBgColor;
          canvas.display();
        }
        break;
      case "Fill":
        isFilled = toolbar.isFilled();
        break;
      case "Eraser":
        currentShape = command;
        showBrushBorder = true;
        // Show eraser options when Eraser is selected
        toolbar.setEraserOptionsVisible(true);
        break;
      case "PointEraser":
        eraserMode = "point";
        break;
      case "ShapeEraser":
        eraserMode = "shape";
        break;
      case "ClearCanvas":
        clearCanvas();
        break;
      case "Brush":
        currentShape = command;
        showBrushBorder = true;
        toolbar.setEraserOptionsVisible(false);
        break;
      case "ZoomArea":
        currentShape = command;
        isZoomAreaActive = true;
        showBrushBorder = false;
        toolbar.setEraserOptionsVisible(false);
        break;
      case "SaveCanvas":
        saveCanvasToImage();
        break;
      // Handle toolbar state changes
      case "ToolbarFloating":
        handleToolbarStateChange(true);
        break;
      case "ToolbarDocked":
        handleToolbarStateChange(false);
        break;
      case "ThicknessChanged":
        thickness = toolbar.getThickness();
        if (currentShape.equals("Eraser")) {
          eraserSize = 0.01f * thickness * 2;
        }
        break;
      default:
        currentShape = command;
        isZoomAreaActive = false;
        canvas.setCursor(defaultCursor);
        showBrushBorder = command.equals("Brush") || command.equals("Eraser");
        if (!command.equals("Eraser")) {
          toolbar.setEraserOptionsVisible(false);
        }
        break;
    }
  }

  /**
   * Handle toolbar state changes (docked/floating)
   * @param isFloating true if the toolbar is now floating, false if docked
   */
  private void handleToolbarStateChange(boolean isFloating) {
    if (isFloating) {
      // Toolbar is floating - remove it from the layout and pack the frame
      getContentPane().remove(scrollableToolbar);
      validate();
      pack();
      canvas.display();
    } else {
      // Toolbar was docked back - add it to the layout again
      add(scrollableToolbar, BorderLayout.WEST);
      validate();
      pack();
      canvas.display();
    }
  }

  /**
   * Clear the entire canvas
   */
  private void clearCanvas() {
    // Show confirmation dialog before clearing
    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to clear the entire canvas?",
        "Clear Canvas",
        JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
      shapes.clear(); // Remove all shapes
      canvas.display(); // Refresh display
    }
  }

  /**
   * Save the canvas as an image file
   */
  private void saveCanvasToImage() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save Canvas as Image");
    fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
        "PNG Images", "png"));

    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      File file = fileChooser.getSelectedFile();
      // Add .png extension if not present
      if (!file.getPath().toLowerCase().endsWith(".png")) {
        file = new File(file.getPath() + ".png");
      }

      // Make sure canvas is properly sized before saving
      if (canvas.getWidth() <= 0 || canvas.getHeight() <= 0) {
        JOptionPane.showMessageDialog(this,
            "Canvas has invalid dimensions. Please resize the window and try again.",
            "Save Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      // Ensure GL context is current
      try {
        canvas.getContext().makeCurrent();
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
            "Error accessing the graphics context: " + e.getMessage(),
            "Save Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      // Capture the canvas content as an image
      GLReadBufferUtil readBufferUtil = new GLReadBufferUtil(true);
      boolean readSuccess = readBufferUtil.readPixels(canvas.getGL(), true);

      if (!readSuccess) {
        JOptionPane.showMessageDialog(this,
            "Failed to read pixels from canvas. The canvas may be too small or not properly initialized.",
            "Save Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      try {
        if (readBufferUtil.write(file)) {
          JOptionPane.showMessageDialog(this,
              "Canvas saved to " + file.getPath(),
              "Save Successful", JOptionPane.INFORMATION_MESSAGE);
        } else {
          JOptionPane.showMessageDialog(this,
              "Failed to write image file. The image might be empty or corrupted.",
              "Save Error", JOptionPane.ERROR_MESSAGE);
        }
      } catch (IOException e) {
        JOptionPane.showMessageDialog(this,
            "Error saving file: " + e.getMessage(),
            "Save Error", JOptionPane.ERROR_MESSAGE);
      } finally {
        try {
          canvas.getContext().release();
        } catch (Exception e) {
          // Ignore release errors
        }
      }
    }
  }

  @Override
  public void init(GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();
    gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    canvas.display();
  }

  @Override
  public void dispose(GLAutoDrawable drawable) {
    // Nothing to dispose
  }

  @Override
  public void display(GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();

    // Use the current background color
    gl.glClearColor(
        backgroundColor.getRed() / 255.0f,
        backgroundColor.getGreen() / 255.0f,
        backgroundColor.getBlue() / 255.0f,
        1.0f);
    gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

    // Draw all shapes
    for (com.sample.paint.model.Shape shape : shapes) {
      shape.draw(gl);
    }

    // Draw the shape being currently drawn (ghost shape)
    if (drawing) {
      if (currentShape.equals("Eraser")) {
        // Draw eraser trail with background color
        drawEraserTrail(gl);
      } else if (currentShape.equals("ZoomArea")) {
        // Draw zoom selection rectangle
        drawZoomRectangle(gl);
      } else if (!currentShape.equals("Eraser")) {
        Shape ghostShape = createShape();
        if (ghostShape != null) {
          ghostShape.draw(gl);
        }
      }
    }

    // Draw brush/eraser border indicator when appropriate
    if (showBrushBorder && (currentShape.equals("Brush") || currentShape.equals("Eraser"))) {
      Color borderColor = currentShape.equals("Brush") ? Color.BLACK : Color.RED;
      float size = (currentShape.equals("Brush") ? thickness : thickness * 2) * 10;
      // Pass the current zoom factor to ensure proper border size
      GLRenderer.drawPointBorder(gl, currentMouseX, currentMouseY, borderColor, size, zoomFactor);
    }
  }

  // Create appropriate shape based on current settings
  private Shape createShape() {
    switch (currentShape) {
      case "Line":
        return new Line(startX, startY, endX, endY, currentColor, thickness);
      case "Rectangle":
        return new Rectangle(startX, startY, endX, endY, currentColor, isFilled,
            thickness);
      case "Circle":
        return new Circle(startX, startY, endX, endY, currentColor, isFilled, thickness);
      case "Ellipse":
        return new Ellipse(startX, startY, endX, endY, currentColor, isFilled, thickness);
      case "Triangle":
        return new Triangle(startX, startY, endX, endY, currentColor, isFilled, thickness);
      case "Brush":
        return new BrushStroke(new ArrayList<>(brushPoints), currentColor, thickness);
      default:
        return null;
    }
  }

  // New method to draw eraser trail - modified to consistently use BrushStroke
  private void drawEraserTrail(GL2 gl) {
    float thickerSize = thickness * 2; // Make eraser thicker than brush

    if (eraserPoints.size() > 1) {
      for (int i = 0; i < eraserPoints.size() - 1; i++) {
        Point p1 = eraserPoints.get(i);
        Point p2 = eraserPoints.get(i + 1);
        DrawingAlgorithms.bresenhamLine(gl, p1.x, p1.y, p2.x, p2.y, backgroundColor, thickerSize);
      }
    } else if (eraserPoints.size() == 1) {
      // Draw a single point if there's only one point
      Point p = eraserPoints.get(0);
      GLRenderer.drawThickPoint(gl, p.x, p.y, backgroundColor, thickerSize);
    }
  }

  /**
   * Draw the zoom selection rectangle
   */
  private void drawZoomRectangle(GL2 gl) {
    gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue color for zoom rectangle
    gl.glLineWidth(1.0f);
    gl.glBegin(GL2.GL_LINE_LOOP);
    gl.glVertex2f(zoomStartX, zoomStartY);
    gl.glVertex2f(zoomEndX, zoomStartY);
    gl.glVertex2f(zoomEndX, zoomEndY);
    gl.glVertex2f(zoomStartX, zoomEndY);
    gl.glEnd();
    gl.glLineWidth(1.0f);
  }

  private void setupMouseListeners() {
    canvas.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        // Check if move tool is active or middle/right mouse button is pressed
        if (e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON3) {
          isPanning = true;
          lastPanX = e.getX();
          lastPanY = e.getY();
          return;
        }

        float[] coords = transformCoordinates(e.getX(), e.getY());
        startX = coords[0];
        startY = coords[1];
        drawing = true;

        if (currentShape.equals("ZoomArea")) {
          zoomStartX = startX;
          zoomStartY = startY;
          zoomEndX = startX; // Initialize end to same as start
          zoomEndY = startY;
        } else if (currentShape.equals("Brush")) {
          brushPoints.clear();
          brushPoints.add(new Point(startX, startY));
        } else if (currentShape.equals("Eraser")) {
          // Start a new eraser trail - clear the previous points
          eraserPoints.clear();
          eraserPoints.add(new Point(startX, startY));
        }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        // Handle panning end
        if (isPanning) {
          isPanning = false;
          return;
        }

        if (!drawing)
          return; // Don't process if we weren't drawing

        float[] coords = transformCoordinates(e.getX(), e.getY());
        endX = coords[0];
        endY = coords[1];
        drawing = false;

        if (currentShape.equals("ZoomArea")) {
          zoomEndX = endX;
          zoomEndY = endY;
          applyZoomToArea();
        } else if (currentShape.equals("Brush")) {
          shapes.add(new BrushStroke(new ArrayList<>(brushPoints), currentColor, thickness));
        } else if (currentShape.equals("Eraser")) {
          if (eraserMode.equals("point") && !eraserPoints.isEmpty()) {
            // Add the eraser stroke as a brush stroke with background color
            shapes.add(new BrushStroke(new ArrayList<>(eraserPoints), backgroundColor, thickness * 2));
          }
          // Clear for next drawing operation
          eraserPoints.clear();
        } else {
          Shape shape = createShape();
          if (shape != null) {
            shapes.add(shape);
          }
        }
        canvas.display();
      }
    });

    canvas.addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseDragged(MouseEvent e) {
        // Handle panning with move tool or right/middle mouse button
        if (isPanning) {
          float dx = (e.getX() - lastPanX) / (float) canvas.getWidth();
          float dy = (e.getY() - lastPanY) / (float) canvas.getHeight();

          // Adjust pan based on zoom level
          panX += dx * 2 / zoomFactor;
          panY -= dy * 2 / zoomFactor; // y is inverted

          lastPanX = e.getX();
          lastPanY = e.getY();
          canvas.display();
          return;
        }

        if (!drawing)
          return; // Don't process if we're not drawing

        float[] coords = transformCoordinates(e.getX(), e.getY());
        endX = coords[0];
        endY = coords[1];

        // Update current mouse position
        currentMouseX = endX;
        currentMouseY = endY;

        if (currentShape.equals("ZoomArea")) {
          zoomEndX = endX;
          zoomEndY = endY;
        } else if (currentShape.equals("Brush")) {
          brushPoints.add(new Point(endX, endY));
        } else if (currentShape.equals("Eraser")) {
          if (eraserMode.equals("point")) {
            // Add to eraser trail for point eraser
            eraserPoints.add(new Point(endX, endY));
          } else {
            // Shape eraser - remove entire shapes
            eraseShapes(endX, endY);
          }
        }
        canvas.display();
      }

      @Override
      public void mouseMoved(MouseEvent e) {
        float[] coords = transformCoordinates(e.getX(), e.getY());
        currentMouseX = coords[0];
        currentMouseY = coords[1];
        canvas.display(); // For real-time border display
      }
    });

    // Add mouse wheel listener for zooming - but now only with Ctrl key
    canvas.addMouseWheelListener(new MouseWheelListener() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        // Only zoom if Ctrl key is pressed
        if (!e.isControlDown()) {
          return;
        }

        // Determine zoom direction based on wheel rotation
        float zoomChange = e.getWheelRotation() < 0 ? zoomIncrement : -zoomIncrement;

        // Calculate new zoom factor
        float newZoom = zoomFactor + zoomChange;

        // Enforce zoom limits
        if (newZoom >= minZoom && newZoom <= maxZoom) {
          zoomFactor = newZoom;

          // Update zoom label (safely, to avoid the exception)
          updateZoomStatusLabel();

          // Trigger redraw with new zoom
          canvas.display();
        }
      }
    });

    // Add key listener for zoom keyboard shortcuts and panning
    canvas.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
          case KeyEvent.VK_PLUS:
          case KeyEvent.VK_EQUALS:
            // Zoom in
            if (zoomFactor < maxZoom) {
              zoomFactor += zoomIncrement;
              canvas.display();
            }
            break;
          case KeyEvent.VK_MINUS:
            // Zoom out
            if (zoomFactor > minZoom) {
              zoomFactor -= zoomIncrement;
              canvas.display();
            }
            break;
          case KeyEvent.VK_0:
            // Reset zoom
            zoomFactor = 1.0f;
            panX = 0.0f;
            panY = 0.0f;
            canvas.display();
            break;
          // Arrow keys for panning
          case KeyEvent.VK_LEFT:
            panX += 0.1f / zoomFactor;
            canvas.display();
            break;
          case KeyEvent.VK_RIGHT:
            panX -= 0.1f / zoomFactor;
            canvas.display();
            break;
          case KeyEvent.VK_UP:
            panY -= 0.1f / zoomFactor;
            canvas.display();
            break;
          case KeyEvent.VK_DOWN:
            panY += 0.1f / zoomFactor;
            canvas.display();
            break;
        }

        // Update zoom label (safely)
        updateZoomStatusLabel();
      }
    });

    // Make sure canvas can receive key events
    canvas.setFocusable(true);
    canvas.requestFocus();
  }

  private float[] transformCoordinates(float x, float y) {
    // Transform mouse coordinates based on zoom level and panning
    float aspectRatio = (float) canvas.getWidth() / canvas.getHeight();
    float transformedX = ((x / canvas.getWidth() * 2 - 1) * aspectRatio) / zoomFactor - panX;
    float transformedY = (1 - y / canvas.getHeight() * 2) / zoomFactor - panY;

    return new float[] { transformedX, transformedY };
  }

  /**
   * Erase entire shapes that the cursor touches
   */
  private void eraseShapes(float x, float y) {
    shapes.removeIf(shape -> shape.isPointInside(x, y, eraserSize));
  }

  /**
   * Apply zoom to the selected area
   */
  private void applyZoomToArea() {
    // Make sure we have a valid rectangle
    if (Math.abs(zoomEndX - zoomStartX) < 0.01f || Math.abs(zoomEndY - zoomStartY) < 0.01f) {
      return; // Rectangle too small, ignore
    }

    // Calculate the center of the selected area
    float centerX = (zoomStartX + zoomEndX) / 2;
    float centerY = (zoomStartY + zoomEndY) / 2;

    // Calculate the width and height of selection in GL coordinates
    float width = Math.abs(zoomEndX - zoomStartX);
    float height = Math.abs(zoomEndY - zoomStartY);

    // Calculate the viewport aspect ratio
    float viewportAspect = (float) canvas.getWidth() / canvas.getHeight();

    // Calculate the required zoom to fit the selection
    float zoomX = 2.0f * viewportAspect / width;
    float zoomY = 2.0f / height;
    float newZoom = Math.min(zoomX, zoomY);

    // Enforce zoom limits
    if (newZoom >= minZoom && newZoom <= maxZoom) {
      zoomFactor = newZoom;

      // Set pan to center the selected area
      panX = -centerX;
      panY = -centerY;

      // Update zoom label
      updateZoomStatusLabel();
    }
  }

  /**
   * Safely update the zoom status label
   */
  private void updateZoomStatusLabel() {
    if (statusLabel != null) {
      statusLabel.setText(String.format("Zoom: %.0f%%", zoomFactor * 100));
    }
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL2 gl = drawable.getGL().getGL2();

    if (width <= 0 || height <= 0) return; // Prevent division by zero

    // Apply custom viewport setup that includes zoom
    GLRenderer.setupViewport(gl, width, height, zoomFactor, panX, panY);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(OpenGLPaintApp::new);
  }
}
