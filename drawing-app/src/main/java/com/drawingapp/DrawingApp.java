package com.drawingapp;

import javax.swing.*;

/**
 * Application entry point. Launches the drawing app on the Swing Event
 * Dispatch Thread to ensure thread safety.
 */
public class DrawingApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Use the system look and feel for a native feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Fall back to the default Swing L&F
            }

            DrawingFrame frame = new DrawingFrame();
            frame.setVisible(true);
        });
    }
}
