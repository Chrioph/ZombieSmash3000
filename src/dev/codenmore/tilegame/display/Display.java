package dev.codenmore.tilegame.display;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLProfile;

import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;

public class Display {
	
	private JFrame frame;
	private Canvas  canvas;
	private String title;
	private int width ;
	private int height ;


	// OpenGL
	private static GLWindow window;
	
	public Display(String title , int width, int height) {
		this.width=width;
		this.height=height;
		this.title= title; 
		createDisplay();
		
	}
	
	private void createDisplay(){

		// OpenGL
		GLProfile glProfile = GLProfile.get(GLProfile.GL4);
		GLCapabilities capabilities = new GLCapabilities(glProfile);

		window = GLWindow.create(capabilities);

		window.setTitle(title);
		window.setSize(width, height);

		window.setContextCreationFlags(GLContext.CTX_OPTION_DEBUG);
		window.setVisible(true);

		//window.addGLEventListener(this);
		//window.addKeyListener(this);

		frame = new JFrame(title);
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(width,height)); 
		canvas.setMaximumSize(new Dimension(width,height));
		canvas.setMinimumSize(new Dimension(width,height));
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

	public void resize(int width, int height)
	{
		this.width = width;
		this.height = height;
		// reinitialize display
		frame.dispose();
		frame = new JFrame();
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		canvas = null;
		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(width,height));
		canvas.setMaximumSize(new Dimension(width,height));
		canvas.setMinimumSize(new Dimension(width,height));
		canvas.setFocusable(false);
		frame.add(canvas);
		frame.pack();
	}
}
