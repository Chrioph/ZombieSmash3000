package dev.codenmore.tilegame.state;

import java.awt.Graphics;

import dev.codenmore.tilegame.Game;
import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.entity.creatures.Player;
import dev.codenmore.tilegame.entity.statics.Tree;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.tiles.Tile;
import dev.codenmore.tilegame.worlds.World;

public class GameState extends State {
	
	
	

	private World world;
	
	
	
	public GameState(Handler handler) {
		super(handler);
		world= new World(handler,"res/worlds/world2.txt");
		handler.setWorld(world);

		
		
	}
	
	@Override
	public void tick() {
		// TODO Auto-generated method stub
		world.tick();
	}

	@Override
	public void render(Graphics g) {
		// TODO Auto-generated method stub
		world.render(g);

	}
	

}
