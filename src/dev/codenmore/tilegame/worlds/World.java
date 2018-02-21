package dev.codenmore.tilegame.worlds;

import java.awt.Graphics;
import java.util.ArrayList;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.Modifiers.DamageMod;
import dev.codenmore.tilegame.Modifiers.HPMod;
import dev.codenmore.tilegame.Modifiers.Mod;
import dev.codenmore.tilegame.Modifiers.SpeedMod;
import dev.codenmore.tilegame.entity.EntityManager;
import dev.codenmore.tilegame.entity.creatures.Player;
import dev.codenmore.tilegame.entity.creatures.Enemies.Zombie;
import dev.codenmore.tilegame.entity.statics.Rock;
import dev.codenmore.tilegame.entity.statics.Tree;
import dev.codenmore.tilegame.items.ItemManager;
import dev.codenmore.tilegame.tiles.Tile;
import dev.codenmore.tilegame.utils.Utils;

public class World {

	private Handler handler;
	private int width, height;
	private int [][] tiles;
	private int spawnX, spawnY;

	
	//Entities
	
	private EntityManager entityManager; 
	
	//Items
	
	private ItemManager itemManager;
	
	public World(Handler handler, String path) {

		this.handler=handler;
		
		itemManager = new ItemManager ( handler);
		
		loadWorld(path);
		
	}

	public void start()
	{
		entityManager.getPlayer().setX(spawnX);
		entityManager.getPlayer().setY(spawnY);
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
	public void setEntityManager(EntityManager en) {
		entityManager = en;
	}

	public void tick() {
		itemManager.tick();
		entityManager.tick();
	}
	
	public void render ( Graphics g) {
		
		int xStart=(int) Math.max(0, handler.getGameCamera().getxOffset()/Tile.TILEWIDTH );
		int xEnd=(int) Math.min(width,( handler.getGameCamera().getxOffset()+1920 )/Tile.TILEWIDTH+1);
		int yStart=(int) Math.max(0,  handler.getGameCamera().getyOffset()/Tile.TILEHEIGHT );
		int yEnd=(int) Math.min(height, ( handler.getGameCamera().getyOffset()+1080 )/Tile.TILEHEIGHT+1);
		
		for (int y=yStart; y<yEnd;y++) {
			for (int x=xStart;x<xEnd;x++) {
				getTile(x,y).render(g,(int) (x*Tile.TILEWIDTH - handler.getGameCamera().getxOffset()),(int) (y*Tile.TILEHEIGHT - handler.getGameCamera().getyOffset()));
			}
		}
		//items
		itemManager.render(g);
		
		//Entities
		entityManager.render(g);
		
	}
	
	
	public Tile getTile(int x, int y) {
		if (x<0 || y<0 || x>= width || y >= height )
			return Tile.grassTile;
		
		
		Tile t=Tile.tiles[tiles[x][y]];
		if (t== null)
			return Tile.dirtTile;
		else
			return t;
		
	}
	
	
	private void loadWorld(String path) {
		String file=Utils.loadFileAsString(path);
		String[] tokens= file.split("\\s+");
		width=Utils.parseInt(tokens[0]);
		height=Utils.parseInt(tokens[1]);
		spawnX=Utils.parseInt(tokens[2]);
		spawnY=Utils.parseInt(tokens[3]);
		
		
		tiles = new int [width][height];
		for (int y=0;y<height;y++) {
			for ( int x =0; x <width;x++) {
				tiles[x][y]= Utils.parseInt(tokens[(x+y*width) + 4]);
			}
		}
		
	}
	
	
	
	
	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public ItemManager getItemManager() {
		return itemManager;
	}

	public void setItemManager(ItemManager itemManager) {
		this.itemManager = itemManager;
	}

	public int getWidth() {
		return width;
		
	}

	
	public int getHEight() {
		return height;
	}
}























