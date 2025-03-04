/PaintProgram
│
├── /src
│   ├── /gui
│   │   ├── PaintFrame.java           # JFrame window with controls (buttons, canvas)
│   │   ├── CanvasPanel.java         # Canvas area where drawing occurs (GLCanvas)
│   │   ├── Toolbar.java             # UI for tool selection (line, rectangle, etc.)
│   │   └── ClearButton.java         # A class to handle clear button functionality
│   │
│   ├── /shapes
│   │   ├── Shape.java               # Abstract class or interface for shapes
│   │   ├── Line.java                # Line shape class (extends Shape)
│   │   ├── Rectangle.java           # Rectangle shape class (extends Shape)
│   │   ├── Circle.java              # Circle shape class (extends Shape)
│   │   └── Ellipse.java             # Ellipse shape class (extends Shape)
│   │
│   ├── /controllers
│   │   ├── DrawingController.java   # Handles the logic for drawing on the canvas
│   │   └── ShapeController.java     # Logic to handle shape selection and properties
│   │
│   ├── /utils
│   │   ├── MouseEventHandler.java   # Handles mouse events (click, drag, release)
│   │   └── ColorUtils.java          # Utility class for handling color changes
│   │
│   └── /app
│       └── PaintApplication.java    # Main class to start the application
│
├── /lib                           # External libraries (like jogamp, OpenGL)
│
├── /assets                        # Any resources like images, icons, etc.
│
└── /build                         # Compiled classes, output
