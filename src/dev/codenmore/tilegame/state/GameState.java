package dev.codenmore.tilegame.state;

import java.awt.Color;
import java.awt.Graphics;

import dev.codenmore.tilegame.Game;
import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.entity.creatures.Player;
import dev.codenmore.tilegame.entity.statics.Tree;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.gfx.Text;
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
		g.drawImage(Assets.heart, 30, 30,64,64, null);
		g.drawImage(Assets.sword, 144, 30,48,48, null);
		g.drawImage(Assets.arrow, 242, 30,48,48, null);
		g.drawImage(Assets.armor, 340, 30,64,64, null);
		Text.drawString(g,""+handler.getWorld().getEntityManager().getPlayer().getHealth(),       56 , 25, true, Color.WHITE, Assets.font40);
		Text.drawString(g, ""+ handler.getWorld().getEntityManager().getPlayer().getDamage(),     167, 25, true, Color.WHITE, Assets.font40);
		Text.drawString(g, ""+ handler.getWorld().getEntityManager().getPlayer().getAmmunition(), 267, 25, true, Color.WHITE, Assets.font40);
		Text.drawString(g, ""+ handler.getWorld().getEntityManager().getPlayer().getArmor(),      370, 25, true, Color.WHITE, Assets.font40);
		Text.drawString(g, "Mode",     480, 25, true, Color.WHITE, Assets.font40);
		if(handler.getWorld().getEntityManager().getPlayer().isRangedToggled())
			g.drawImage(Assets.bow, 454, 30,48,48, null);
		if(!handler.getWorld().getEntityManager().getPlayer().isRangedToggled())
			g.drawImage(Assets.sword, 454, 30,48,48, null);

	}
	

}
