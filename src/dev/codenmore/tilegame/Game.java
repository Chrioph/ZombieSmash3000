package dev.codenmore.tilegame;

//Test
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.io.*;
import java.util.Properties;


import dev.codenmore.tilegame.display.Display;
import dev.codenmore.tilegame.entity.creatures.Player;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.gfx.GameCamera;
import dev.codenmore.tilegame.gfx.Text;
import dev.codenmore.tilegame.input.KeyManager;
import dev.codenmore.tilegame.input.MouseManager;

import dev.codenmore.tilegame.state.MenuState;

import dev.codenmore.tilegame.state.State;
import dev.codenmore.tilegame.ui.UIObject;
import dev.codenmore.tilegame.worlds.WorldGenerator;

public class Game implements Runnable{
	
	private Display display;
	private int width, height;
	private boolean running=false;
	private Thread thread;
	public String title;
	private boolean afterScale = false;
	private BufferStrategy bs;
	private int displayFPS = 0;
	private Dimension newDisplaySize = null;

	private boolean restartRender = false;
	private boolean restartTick = false;

	//States
	public State gameState;
	public State menuState;
	public State settingsState;
	
	//Input
	private KeyManager keyManager;
	private MouseManager mouseManager;
	
	//Camera
	private GameCamera camera;


	// worlds
	private WorldGenerator worldGen;

	
	
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
		// load properties
		display=new Display(title,width, height);
		display.getFrame().addKeyListener(keyManager);
		display.getFrame().addMouseListener(mouseManager);
		display.getFrame().addMouseMotionListener(mouseManager);
		display.getCanvas().addMouseListener(mouseManager);
		display.getCanvas().addMouseMotionListener(mouseManager);
		Assets.init();

		initGameComponents();

		State.setState(new MenuState(handler));
	}

	private void initGameComponents()
	{
		handler = new Handler(this);
		camera= new GameCamera(handler, 0, 0);
		worldGen = new WorldGenerator(handler, new Player(handler,0,0));
	}

	private void destroyGameComponents()
	{
		handler.destroy();
		worldGen.resetWorlds();
		State.setState(null);
	}
	
	private void tick() {
		if(restartTick) {
			destroyGameComponents();
			initGameComponents();
			State.setState(new MenuState(handler));
			restartTick = false;
		}
		keyManager.tick();
		if (State.getState() != null)
			State.getState().tick();

	}
	private void render () {
		if(restartRender) {
			restartRender = false;
			return;
		}
		if(newDisplaySize != null) {
			changeDisplaySize();
		}
		bs = display.getCanvas().getBufferStrategy();
		if(bs == null || afterScale) {
			display.getCanvas().createBufferStrategy(3);
			afterScale = false;
			return;
		}
		Graphics graphics = bs.getDrawGraphics();
		Graphics2D g = (Graphics2D) graphics;

		//System.out.println("Do Scale " + Settings.getScaleX() + "/" + Settings.getScaleY());
		//g.scale(Settings.getScaleX(), Settings.getScaleY());
		g.scale(Settings.getScaleX(), Settings.getScaleY());


		//clear screen
		g.clearRect(0, 0, 1920, 1080);
		//draw here
		if (State.getState() != null)
			State.getState().render(g);
		
		
		
		
		//End here
		bs.show();
		// fixes some stuttering issues on mac/linux (https://stackoverflow.com/questions/19480076/java-animation-stutters-when-not-moving-mouse-cursor)
		Toolkit.getDefaultToolkit().sync();
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
				displayFPS = ticks;
				ticks=0;
				timer=0;
			}
		}
		display.killDisplay();
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

	public WorldGenerator getWorldGenerator(){
		return worldGen;
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

	public void changeDisplaySize()
	{
		this.width = newDisplaySize.width;
		this.height = newDisplaySize.height;

		Settings.setResolutionX(width);
		Settings.setResolutionY(height);
		Settings.save();
		handler.getGame().getMouseManager().getUIManager().updateAllBounds();
		display.resize(width, height);
		display.getFrame().addKeyListener(keyManager);
		display.getFrame().addMouseListener(mouseManager);
		display.getFrame().addMouseMotionListener(mouseManager);
		display.getCanvas().addMouseListener(mouseManager);
		display.getCanvas().addMouseMotionListener(mouseManager);
		afterScale = true;
		newDisplaySize = null;
	}
	
	
	public void resizeDisplay(int width, int height)
	{
		newDisplaySize = new Dimension(width, height);

	}

	public int getDisplayFPS()
	{
		return displayFPS;
	}
	
	public void close() {
		running=false;
	}

	public void restart()
	{
		restartRender = true;
		restartTick = true;
	}

	public void save()
	{
		try {
			FileOutputStream fileOut = new FileOutputStream("save.state");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(handler);
			out.close();
			fileOut.close();
		}catch(Exception e) {
            System.out.println(e.getMessage());
		}

	}

	public void load()
    {
        try{
            FileInputStream fileOut = new FileInputStream("save.state");
            ObjectInputStream out = new ObjectInputStream(fileOut);
            handler = (Handler) out.readObject();
            out.close();
            fileOut.close();
            handler.setGame(this);
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
	

