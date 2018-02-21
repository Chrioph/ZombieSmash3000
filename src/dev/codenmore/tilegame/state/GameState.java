package dev.codenmore.tilegame.state;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;



import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.HUDs.HUD;

import dev.codenmore.tilegame.worlds.World;

public class GameState extends State {
	
	
	

	private World world;
	
	private HUD hud;
	
	
	public GameState(Handler handler) {
		super(handler);
		world= new World(handler,"res/worlds/world2.txt");
		handler.setWorld(world);

		hud=new HUD(handler);
		
	}
	
	@Override
	public void tick() {
		// TODO Auto-generated method stub
		world.tick();
		listenExit();
	}

	@Override
	public void render(Graphics g) {
		// TODO Auto-generated method stub
		world.render(g);
		hud.render(g);

	}
	private void listenExit() {
		if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_ESCAPE))
			State.setState(new MenuState(handler));
	}

}
