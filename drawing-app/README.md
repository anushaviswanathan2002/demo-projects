# Java Drawing App

A simple desktop drawing application built with Java Swing.

## Features

| Feature | Details |
|---|---|
| **Tools** | Pencil, Line, Rectangle, Ellipse, Eraser, Fill (paint-bucket) |
| **Colors** | 12-color quick palette + full JColorChooser for custom colors |
| **Stroke width** | Adjustable 1–50 px via spinner |
| **Undo / Redo** | Up to 50 history steps (Ctrl+Z / Ctrl+Y) |
| **Save** | Export canvas as PNG or JPEG (Ctrl+S) |
| **Clear** | Wipe canvas with confirmation dialog (Ctrl+N) |

## Prerequisites

- Java 11+
- Maven 3.6+

## Build & Run

```bash
# Build
mvn package

# Run
java -jar target/drawing-app.jar
```

## Project Structure

```
drawing-app/
├── pom.xml
└── src/main/java/com/drawingapp/
    ├── DrawingApp.java      # Entry point
    ├── DrawingFrame.java    # Main JFrame + toolbar + menus
    ├── DrawingCanvas.java   # Paintable panel + undo/redo/fill logic
    ├── DrawingAction.java   # Data model for a single drawing action
    └── Tool.java            # Enum of available tools
```

## Keyboard Shortcuts

| Shortcut | Action |
|---|---|
| `Ctrl+Z` | Undo |
| `Ctrl+Y` | Redo |
| `Ctrl+S` | Save As |
| `Ctrl+N` | Clear canvas |
