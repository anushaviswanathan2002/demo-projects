package com.drawingapp;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

/**
 * The main application window. Hosts the toolbar, color palette, stroke-width
 * selector, and the {@link DrawingCanvas}.
 */
public class DrawingFrame extends JFrame {

    private final DrawingCanvas canvas;

    // Palette colors
    private static final Color[] PALETTE = {
        Color.BLACK,       new Color(80, 80, 80),   Color.WHITE,
        new Color(220, 50, 50),  new Color(230, 120, 30), new Color(240, 200, 20),
        new Color(60, 180, 60),  new Color(30, 160, 210), new Color(90, 60, 200),
        new Color(200, 80, 170), new Color(140, 90, 50),  new Color(20, 160, 140)
    };

    public DrawingFrame() {
        super("Java Drawing App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        canvas = new DrawingCanvas();

        setJMenuBar(buildMenuBar());
        add(buildToolbar(), BorderLayout.NORTH);
        add(new JScrollPane(canvas), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    // -------------------------------------------------------------------------
    // Menu bar
    // -------------------------------------------------------------------------

    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem saveItem = new JMenuItem("Save As…", KeyEvent.VK_S);
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        saveItem.addActionListener(e -> saveCanvas());

        JMenuItem clearItem = new JMenuItem("Clear Canvas", KeyEvent.VK_N);
        clearItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        clearItem.addActionListener(e -> canvas.clearAll());

        JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_Q);
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(saveItem);
        fileMenu.add(clearItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);

        JMenuItem undoItem = new JMenuItem("Undo", KeyEvent.VK_Z);
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        undoItem.addActionListener(e -> canvas.undo());

        JMenuItem redoItem = new JMenuItem("Redo", KeyEvent.VK_Y);
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
        redoItem.addActionListener(e -> canvas.redo());

        editMenu.add(undoItem);
        editMenu.add(redoItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        return menuBar;
    }

    // -------------------------------------------------------------------------
    // Toolbar
    // -------------------------------------------------------------------------

    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        toolbar.setBackground(new Color(245, 245, 245));

        // --- Tool buttons ---
        ButtonGroup toolGroup = new ButtonGroup();
        for (Tool tool : Tool.values()) {
            JToggleButton btn = createToolButton(tool);
            toolGroup.add(btn);
            toolbar.add(btn);
            if (tool == Tool.PENCIL) btn.setSelected(true);
        }

        toolbar.add(new JSeparator(SwingConstants.VERTICAL));
        toolbar.add(Box.createHorizontalStrut(4));

        // --- Stroke width ---
        toolbar.add(new JLabel("Size:"));
        SpinnerNumberModel sizeModel = new SpinnerNumberModel(3, 1, 50, 1);
        JSpinner strokeSpinner = new JSpinner(sizeModel);
        strokeSpinner.setPreferredSize(new Dimension(60, 28));
        strokeSpinner.setToolTipText("Stroke width (pixels)");
        strokeSpinner.addChangeListener(e ->
            canvas.setStrokeWidth(((Number) strokeSpinner.getValue()).floatValue())
        );
        toolbar.add(strokeSpinner);

        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(new JSeparator(SwingConstants.VERTICAL));
        toolbar.add(Box.createHorizontalStrut(4));

        // --- Color palette ---
        toolbar.add(new JLabel("Color:"));
        for (Color color : PALETTE) {
            toolbar.add(createColorSwatch(color));
        }

        // Custom color picker
        JButton customColorBtn = new JButton("…");
        customColorBtn.setToolTipText("Pick custom color");
        customColorBtn.setPreferredSize(new Dimension(36, 28));
        customColorBtn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Choose Color", canvas.getCurrentColor());
            if (chosen != null) canvas.setCurrentColor(chosen);
        });
        toolbar.add(customColorBtn);

        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(new JSeparator(SwingConstants.VERTICAL));
        toolbar.add(Box.createHorizontalStrut(4));

        // --- Action buttons ---
        JButton undoBtn = new JButton("↩ Undo");
        undoBtn.addActionListener(e -> canvas.undo());
        toolbar.add(undoBtn);

        JButton redoBtn = new JButton("↪ Redo");
        redoBtn.addActionListener(e -> canvas.redo());
        toolbar.add(redoBtn);

        JButton clearBtn = new JButton("🗑 Clear");
        clearBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this, "Clear the entire canvas?", "Confirm Clear",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) canvas.clearAll();
        });
        toolbar.add(clearBtn);

        return toolbar;
    }

    private JToggleButton createToolButton(Tool tool) {
        JToggleButton btn = new JToggleButton(tool.getDisplayName());
        btn.setToolTipText(tool.getDisplayName());
        btn.setFocusPainted(false);
        btn.addActionListener(e -> canvas.setCurrentTool(tool));
        return btn;
    }

    private JButton createColorSwatch(Color color) {
        JButton swatch = new JButton();
        swatch.setBackground(color);
        swatch.setOpaque(true);
        swatch.setBorderPainted(true);
        swatch.setPreferredSize(new Dimension(24, 24));
        swatch.setToolTipText(String.format("RGB(%d,%d,%d)", color.getRed(), color.getGreen(), color.getBlue()));
        swatch.addActionListener(e -> canvas.setCurrentColor(color));
        return swatch;
    }

    // -------------------------------------------------------------------------
    // File operations
    // -------------------------------------------------------------------------

    private void saveCanvas() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Drawing");
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG Image (*.png)", "png"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("JPEG Image (*.jpg)", "jpg"));
        chooser.setAccelerator(null);

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            // Ensure correct extension
            if (!file.getName().contains(".")) {
                file = new File(file.getAbsolutePath() + ".png");
            }
            try {
                canvas.saveToFile(file);
                JOptionPane.showMessageDialog(this, "Saved to " + file.getAbsolutePath(),
                    "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
