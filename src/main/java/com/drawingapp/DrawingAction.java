package com.drawingapp;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single user drawing action. Stores the tool type, color, stroke
 * width, and all geometry needed to replay or undo the action.
 */
public class DrawingAction {

    private final Tool tool;
    private final Color color;
    private final float strokeWidth;

    // For freehand pencil/eraser strokes: an ordered list of points
    private final List<Point> points;

    // For shape tools (line, rectangle, ellipse): start and end coordinates
    private int x1, y1, x2, y2;

    // For fill tool: snapshot of the canvas before the fill was applied
    private BufferedImage fillSnapshot;

    public DrawingAction(Tool tool, Color color, float strokeWidth) {
        this.tool = tool;
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.points = new ArrayList<>();
    }

    // --- Freehand stroke API ---

    public void addPoint(Point p) {
        points.add(p);
    }

    public List<Point> getPoints() {
        return points;
    }

    // --- Shape tool API ---

    public void setStartPoint(int x, int y) {
        this.x1 = x;
        this.y1 = y;
    }

    public void setEndPoint(int x, int y) {
        this.x2 = x;
        this.y2 = y;
    }

    // --- Fill tool API ---

    public void setFillSnapshot(BufferedImage snapshot) {
        this.fillSnapshot = snapshot;
    }

    public BufferedImage getFillSnapshot() {
        return fillSnapshot;
    }

    // --- Accessors ---

    public Tool getTool() { return tool; }
    public Color getColor() { return color; }
    public float getStrokeWidth() { return strokeWidth; }
    public int getX1() { return x1; }
    public int getY1() { return y1; }
    public int getX2() { return x2; }
    public int getY2() { return y2; }
}
