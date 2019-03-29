package com.goldenbois.zombiesmash;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.goldenbois.zombiesmash.display.Display;
import com.goldenbois.zombiesmash.entity.creatures.Player;
import com.goldenbois.zombiesmash.gfx.Assets;
import com.goldenbois.zombiesmash.gfx.GameCamera;
import com.goldenbois.zombiesmash.input.KeyManager;
import com.goldenbois.zombiesmash.input.MouseManager;

import com.goldenbois.zombiesmash.state.MenuState;

import com.goldenbois.zombiesmash.state.State;
import com.goldenbois.zombiesmash.worlds.WorldGenerator;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Graphics;

public class Game extends BasicGame {
	
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
	
	public Game (String title) {
		super(title);
		this.title=title;
		keyManager= new KeyManager();
		mouseManager= new MouseManager();	 
		
	}

	@Override
	public void init(GameContainer gc) throws SlickException {}

	@Override
	public void update(GameContainer gc, int i) throws SlickException {}

	public void render(GameContainer gc, Graphics g) throws SlickException
	{
		if(restartRender) {
			restartRender = false;
			return;
		}
		if(newDisplaySize != null) {
			changeDisplaySize();
		}
		if(bs == null || afterScale) {
			afterScale = false;
			return;
		}
		g.clear();
		//draw here
		if (State.getState() != null)
			State.getState().render(g);




		//End here
		bs.show();
		// fixes some stuttering issues on mac/linux (https://stackoverflow.com/questions/19480076/java-animation-stutters-when-not-moving-mouse-cursor)
		Toolkit.getDefaultToolkit().sync();
	}

	public static void main(String[] args) {
		try
		{
			System.setProperty("java.library.path", "lib/native");
			System.setProperty("org.lwjgl.librarypath", new File("lib/native").getAbsolutePath());
			System.setProperty("net.java.games.input.librarypath", new File("lib/native").getAbsolutePath());
			Settings.init();
			parseArgs(args);
			AppGameContainer appgc;
			appgc = new AppGameContainer(new Game("Simple Slick Game"));
			appgc.setDisplayMode(Settings.getResolutionX(), Settings.getResolutionY(), false);
			appgc.start();
		}
		catch (SlickException ex)
		{
			Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
		}
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



	private static void parseArgs(String[] args)
	{
		for(String arg : args)
		{
			if(arg.equals("--debug")) {
				Settings.setDebug(true);
			}else if(arg.equals("--dungeon")) {
				Settings.setRenderDungeon(true);
			}
		}
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
	

