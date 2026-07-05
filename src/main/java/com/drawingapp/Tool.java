package com.drawingapp;

/**
 * Enumeration of available drawing tools.
 */
public enum Tool {
    PENCIL("Pencil"),
    LINE("Line"),
    RECTANGLE("Rectangle"),
    ELLIPSE("Ellipse"),
    ERASER("Eraser"),
    FILL("Fill");

    private final String displayName;

    Tool(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
