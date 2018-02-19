package dev.codenmore.tilegame;


import java.awt.Graphics;
import java.awt.image.BufferStrategy;


import dev.codenmore.tilegame.display.Display;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.gfx.GameCamera;


import dev.codenmore.tilegame.input.KeyManager;
import dev.codenmore.tilegame.input.MouseManager;

import dev.codenmore.tilegame.state.MenuState;

import dev.codenmore.tilegame.state.State;

public class Game implements Runnable{
	
	private Display display;
	private int width, height;
	private boolean running=false;
	private Thread thread;
	public String title;
	private BufferStrategy bs;	
	private Graphics g;
	
	//States
	public State gameState;
	public State menuState;
	public State settingsState;
	
	//Input
	private KeyManager keyManager;
	private MouseManager mouseManager;
	//Camera
	private GameCamera camera;
	
	
	//Handler
	private Handler handler;
	
	public Game (String title , int width, int height) {
		this.width=width;
		this.height=height;
		this.title=title;
		keyManager= new KeyManager();
		mouseManager= new MouseManager();	 
	}
	
	public void init() {
		display=new Display(title,width,height);
		display.getFrame().addKeyListener(keyManager);
		display.getFrame().addMouseListener(mouseManager);
		display.getFrame().addMouseMotionListener(mouseManager);
		display.getCanvas().addMouseListener(mouseManager);
		display.getCanvas().addMouseMotionListener(mouseManager);
		Assets.init();
		
		handler = new Handler(this);
		camera= new GameCamera(handler, 0, 0);
		
		
		State.setState(new MenuState(handler));
	}
	
	private void tick() {
		keyManager.tick();
		if (State.getState() != null)
			State.getState().tick();

	}
	private void render () {
		bs = display.getCanvas().getBufferStrategy();
		if(bs == null) {
			display.getCanvas().createBufferStrategy(3);
			return;
		}
		g = bs.getDrawGraphics();
		//clear screen
		g.clearRect(0, 0, width, height);
		//draw here
		if (State.getState() != null)
			State.getState().render(g);
		
		
		
		//End here
		bs.show();
		g.dispose();
	}
	
	public void run() {
		init();
		
		int fps = 60;
		double timePerTick=1000000000/fps;
		double delta=0;
		long now;
		long lastTime=System.nanoTime();
		long timer=0;
		int ticks=0;
		
		while(running) {
			now=System.nanoTime();
			delta += (now -lastTime) /timePerTick;
			timer+=now-lastTime;
			lastTime=now;
			if (delta>=1) {
				tick();
				render();
				ticks++;
				delta--;
			}
			if(timer >= 1000000000) {
				System.out.println("Ticks and Frames: "+ticks+"FPS");
				ticks=0;
				timer=0;
			}
		}
		stop();
		
	}
	
	public KeyManager getKeyManager() {
		return keyManager;
	}
	
	public MouseManager getMouseManager() {
		return mouseManager;
	}
	
	public GameCamera getGameCamera() {
		return camera;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public synchronized void start() {
		if (running) 
			return;
		running = true;
		thread = new Thread(this);
		thread.start();
		
	}
	
	public synchronized void stop() {
		if(!running)
			return; 
		try {
			thread.join();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		 
	}

	public void resizeDisplay(int width, int height)
	{
		this.width = width;
		this.height = height;
		display.resize(width, height);
	}
	
}
	

