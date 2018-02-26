package dev.codenmore.tilegame.state;

import java.awt.Graphics;
import java.awt.event.KeyEvent;



import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.HUDs.HUD;

import dev.codenmore.tilegame.worlds.World;
import dev.codenmore.tilegame.worlds.WorldGenerator;

public class GameState extends State {
	
	
	

	private World world;
	private WorldGenerator worldGen;

	private HUD hud;
	
	
	public GameState(Handler handler) {
		super(handler);
		this.worldGen = handler.getGame().getWorldGenerator();
		world= this.worldGen.world1();
		handler.setWorld(world);

		hud=new HUD(handler);
		
	}
	
	@Override
	public void tick() {
		// TODO Auto-generated method stub
		world.tick();
		listenExit();
		if(handler.getWorld().getEntityManager().getPlayer().collisionWithFinish(
				(int)(handler.getWorld().getEntityManager().getPlayer().getX() /128),
				(int) (handler.getWorld().getEntityManager().getPlayer().getY() /128 ))) {
			this.world=this.worldGen.world2();
			handler.setWorld(this.world);
		}
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
