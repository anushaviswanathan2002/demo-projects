package com.drawingapp;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * The main drawing surface. Renders a persistent off-screen {@link BufferedImage}
 * that accumulates completed actions, plus a live preview overlay while the user
 * is dragging a shape tool.
 */
public class DrawingCanvas extends JPanel {

    private static final int CANVAS_WIDTH  = 1200;
    private static final int CANVAS_HEIGHT = 800;
    private static final Color BACKGROUND_COLOR = Color.WHITE;

    // Off-screen canvas that holds all committed drawing history
    private BufferedImage canvasImage;
    private Graphics2D    canvasGraphics;

    // Undo / redo stacks — each entry is a snapshot of canvasImage
    private final Deque<BufferedImage> undoStack = new ArrayDeque<>();
    private final Deque<BufferedImage> redoStack = new ArrayDeque<>();
    private static final int MAX_HISTORY = 50;

    // Current tool state
    private Tool    currentTool  = Tool.PENCIL;
    private Color   currentColor = Color.BLACK;
    private float   strokeWidth  = 3f;

    // Drag state for shape tools
    private int dragStartX, dragStartY;
    private int dragCurrentX, dragCurrentY;
    private boolean isDragging = false;

    // In-progress freehand action (pencil / eraser)
    private DrawingAction activeStroke;

    public DrawingCanvas() {
        setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        setBackground(BACKGROUND_COLOR);
        initCanvasImage();
        attachMouseListeners();
    }

    // -------------------------------------------------------------------------
    // Initialisation
    // -------------------------------------------------------------------------

    private void initCanvasImage() {
        canvasImage    = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        canvasGraphics = canvasImage.createGraphics();
        applyRenderingHints(canvasGraphics);
        clearCanvas(canvasGraphics);
    }

    private void applyRenderingHints(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,      RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY);
    }

    private void clearCanvas(Graphics2D g) {
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
    }

    // -------------------------------------------------------------------------
    // Mouse interaction
    // -------------------------------------------------------------------------

    private void attachMouseListeners() {
        MouseAdapter handler = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                saveUndoSnapshot();
                redoStack.clear();

                dragStartX   = x;
                dragStartY   = y;
                dragCurrentX = x;
                dragCurrentY = y;
                isDragging   = true;

                if (currentTool == Tool.PENCIL || currentTool == Tool.ERASER) {
                    activeStroke = new DrawingAction(currentTool, currentColor, strokeWidth);
                    activeStroke.addPoint(new Point(x, y));
                } else if (currentTool == Tool.FILL) {
                    floodFill(x, y, currentColor);
                    isDragging = false;
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (!isDragging) return;

                dragCurrentX = e.getX();
                dragCurrentY = e.getY();

                if (currentTool == Tool.PENCIL || currentTool == Tool.ERASER) {
                    Point prev = activeStroke.getPoints().get(activeStroke.getPoints().size() - 1);
                    Point curr = new Point(dragCurrentX, dragCurrentY);
                    activeStroke.addPoint(curr);
                    drawSegment(canvasGraphics, prev, curr);
                }

                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!isDragging) return;
                isDragging = false;

                dragCurrentX = e.getX();
                dragCurrentY = e.getY();

                if (currentTool == Tool.LINE
                        || currentTool == Tool.RECTANGLE
                        || currentTool == Tool.ELLIPSE) {
                    commitShape(canvasGraphics);
                }

                activeStroke = null;
                repaint();
            }
        };

        addMouseListener(handler);
        addMouseMotionListener(handler);
    }

    // -------------------------------------------------------------------------
    // Rendering
    // -------------------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        applyRenderingHints(g2);

        // Draw committed canvas
        g2.drawImage(canvasImage, 0, 0, null);

        // Draw live preview for shape tools
        if (isDragging && isShapeTool(currentTool)) {
            g2.setColor(currentColor);
            g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            drawShapePreview(g2, dragStartX, dragStartY, dragCurrentX, dragCurrentY);
        }
    }

    private boolean isShapeTool(Tool tool) {
        return tool == Tool.LINE || tool == Tool.RECTANGLE || tool == Tool.ELLIPSE;
    }

    // -------------------------------------------------------------------------
    // Drawing primitives
    // -------------------------------------------------------------------------

    private void drawSegment(Graphics2D g, Point from, Point to) {
        Color drawColor = (currentTool == Tool.ERASER) ? BACKGROUND_COLOR : currentColor;
        float width     = (currentTool == Tool.ERASER) ? strokeWidth * 4 : strokeWidth;
        g.setColor(drawColor);
        g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(from.x, from.y, to.x, to.y);
    }

    private void commitShape(Graphics2D g) {
        g.setColor(currentColor);
        g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        drawShapePreview(g, dragStartX, dragStartY, dragCurrentX, dragCurrentY);
    }

    private void drawShapePreview(Graphics2D g, int x1, int y1, int x2, int y2) {
        int rx = Math.min(x1, x2);
        int ry = Math.min(y1, y2);
        int rw = Math.abs(x2 - x1);
        int rh = Math.abs(y2 - y1);

        switch (currentTool) {
            case LINE:      g.drawLine(x1, y1, x2, y2);      break;
            case RECTANGLE: g.drawRect(rx, ry, rw, rh);       break;
            case ELLIPSE:   g.drawOval(rx, ry, rw, rh);       break;
            default: break;
        }
    }

    // -------------------------------------------------------------------------
    // Flood fill (paint-bucket tool)
    // -------------------------------------------------------------------------

    private void floodFill(int x, int y, Color fillColor) {
        int targetRgb = canvasImage.getRGB(x, y);
        int fillRgb   = fillColor.getRGB();

        if (targetRgb == fillRgb) return;

        // Iterative BFS flood fill to avoid stack overflow on large areas
        Deque<Point> queue = new ArrayDeque<>();
        queue.add(new Point(x, y));

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            if (p.x < 0 || p.x >= CANVAS_WIDTH || p.y < 0 || p.y >= CANVAS_HEIGHT) continue;
            if (canvasImage.getRGB(p.x, p.y) != targetRgb) continue;

            canvasImage.setRGB(p.x, p.y, fillRgb);
            queue.add(new Point(p.x + 1, p.y));
            queue.add(new Point(p.x - 1, p.y));
            queue.add(new Point(p.x,     p.y + 1));
            queue.add(new Point(p.x,     p.y - 1));
        }

        repaint();
    }

    // -------------------------------------------------------------------------
    // Undo / Redo
    // -------------------------------------------------------------------------

    private void saveUndoSnapshot() {
        BufferedImage snapshot = copyImage(canvasImage);
        undoStack.push(snapshot);
        if (undoStack.size() > MAX_HISTORY) {
            // Drop the oldest snapshot to cap memory usage
            BufferedImage[] all = undoStack.toArray(new BufferedImage[0]);
            undoStack.clear();
            for (int i = 0; i < all.length - 1; i++) undoStack.push(all[i]);
        }
    }

    public void undo() {
        if (undoStack.isEmpty()) return;
        redoStack.push(copyImage(canvasImage));
        BufferedImage previous = undoStack.pop();
        restoreSnapshot(previous);
    }

    public void redo() {
        if (redoStack.isEmpty()) return;
        undoStack.push(copyImage(canvasImage));
        BufferedImage next = redoStack.pop();
        restoreSnapshot(next);
    }

    private void restoreSnapshot(BufferedImage snapshot) {
        canvasGraphics.drawImage(snapshot, 0, 0, null);
        repaint();
    }

    private BufferedImage copyImage(BufferedImage source) {
        BufferedImage copy = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics2D g = copy.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return copy;
    }

    // -------------------------------------------------------------------------
    // Canvas operations
    // -------------------------------------------------------------------------

    public void clearAll() {
        saveUndoSnapshot();
        redoStack.clear();
        clearCanvas(canvasGraphics);
        repaint();
    }

    public void saveToFile(File file) throws IOException {
        String name = file.getName().toLowerCase();
        String format = name.endsWith(".png") ? "PNG" : "JPEG";
        ImageIO.write(canvasImage, format, file);
    }

    // -------------------------------------------------------------------------
    // Tool/style setters
    // -------------------------------------------------------------------------

    public void setCurrentTool(Tool tool)    { this.currentTool  = tool; }
    public void setCurrentColor(Color color) { this.currentColor = color; }
    public void setStrokeWidth(float width)  { this.strokeWidth  = width; }

    public Tool  getCurrentTool()  { return currentTool; }
    public Color getCurrentColor() { return currentColor; }
    public float getStrokeWidth()  { return strokeWidth; }
}
