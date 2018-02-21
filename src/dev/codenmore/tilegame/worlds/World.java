package dev.codenmore.tilegame.worlds;

import java.awt.Graphics;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.entity.EntityManager;
import dev.codenmore.tilegame.entity.creatures.Arrow;
import dev.codenmore.tilegame.entity.creatures.Player;
import dev.codenmore.tilegame.entity.creatures.Zombie;
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
	
	public World(Handler handler,String path) {
		this.handler=handler;
		entityManager = new EntityManager(handler, new Player(handler, 100 , 100 ));
		entityManager.addEntity(new Tree(handler, 300, 450));
		entityManager.addEntity(new Tree(handler, 2000, 1300));
		entityManager.addEntity(new Tree(handler, 400, 2000));
		entityManager.addEntity(new Rock(handler, 3000, 2100));
		entityManager.addEntity(new Rock(handler, 1700, 900));
		entityManager.addEntity(new Rock(handler, 1400, 1900 ));
		entityManager.addEntity(new Rock(handler, 2450, 900 ));
		entityManager.addEntity(new Rock(handler, 2450, 850 ));
		
		entityManager.addEntity(new Zombie(handler, 1500, 1500 ));
		entityManager.addEntity(new Zombie(handler, 1500, 1750 ));
		entityManager.addEntity(new Zombie(handler, 1000, 750 ));
		entityManager.addEntity(new Zombie(handler, 200, 450 ));
		entityManager.addEntity(new Zombie(handler, 500, 170 ));
		entityManager.addEntity(new Zombie(handler, 1000, 1900 ));
		
		
		
		itemManager = new ItemManager ( handler);
		
		loadWorld(path);
		
		entityManager.getPlayer().setX(spawnX);
		entityManager.getPlayer().setY(spawnY);
		
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
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























