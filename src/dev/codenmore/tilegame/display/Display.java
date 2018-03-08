package dev.codenmore.tilegame.display;

import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;

/**
 * Display class creates frame with given settings (size etc)
 */
public class Display {

    private JFrame frame;
    private Canvas canvas;
    private String title;
    private int width;
    private int height;

    /**
     * Constructor
     *
     * @param title
     * @param width
     * @param height
     */
    public Display(String title, int width, int height) {
        this.width = width;
        this.height = height;
        this.title = title;
        createDisplay();

    }

    /**
     * Main function, executed on construct
     */
    private void createDisplay() {

        frame = new JFrame(title);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));
        canvas.setMaximumSize(new Dimension(width, height));
        canvas.setMinimumSize(new Dimension(width, height));
        canvas.setFocusable(false);
        frame.add(canvas);
        frame.pack();
    }

    public Canvas getCanvas() {
        return this.canvas;
    }

    public JFrame getFrame() {
        return frame;
    }

    /**
     * Resize function, is executed when new resolution was chosen in the settings
     *
     * @param width
     * @param height
     */
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        // reinitialize display
        frame.dispose();
        frame = new JFrame();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        canvas = null;
        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));
        canvas.setMaximumSize(new Dimension(width, height));
        canvas.setMinimumSize(new Dimension(width, height));
        canvas.setFocusable(false);
        frame.add(canvas);
        frame.pack();
    }

    /**
     * Kill function, used on exit
     */
    public void killDisplay() {
        frame.dispose();
    }

}
