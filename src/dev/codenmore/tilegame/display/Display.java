package dev.codenmore.tilegame.display;

import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;


import dev.codenmore.tilegame.Settings;

import dev.codenmore.tilegame.input.InputHandler;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.system.MemoryStack.*;
import org.lwjgl.glfw.GLFWVidMode;
import static org.lwjgl.system.MemoryUtil.*;
/**
 * Display class creates frame with given settings (size etc)
 */
public class Display {

    private JFrame frame;
    private Canvas canvas;
    private String title;
    private int width;
    private int height;

	//OpenGl
	private long window;

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
		// OpenGl
		if(Settings.getOpenGl()) {
			createDisplayOpenGl();
		}else {
			createDisplay();
		}

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



	public void destroy()
	{
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void createDisplayOpenGl()
	{
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        //

		// Create the window
		window = glfwCreateWindow(width, height, title, NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});

		glfwMakeContextCurrent(window);
        glfwShowWindow(window);


        glfwSetKeyCallback(window, InputHandler.keyboard);
        glfwSetCursorPosCallback(window, InputHandler.cursor);
        glfwSetMouseButtonCallback(window, InputHandler.mouse);
		GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glActiveTexture(GL_TEXTURE1);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
	}

	public long getWindow()
	{
		return window;
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
