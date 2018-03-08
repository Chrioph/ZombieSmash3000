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
import dev.codenmore.tilegame.tiles.dungeon.dungeonWallTile;
import dev.codenmore.tilegame.utils.Utils;

public class World {

	private Handler handler;
	private int width, height;
	private int [][] tiles;
	private int spawnX, spawnY;
	private int id;
	private String path;

	
	//Entities
	
	private EntityManager entityManager; 
	
	//Items
	
	private ItemManager itemManager;
	
	public World(int id, Handler handler, String path) {
		this.id = id;

		this.handler=handler;
		
		itemManager = new ItemManager ( handler);
		this.path = path;
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

		Tile tmpTile;
		int xStart=(int) Math.max(0, handler.getGameCamera().getxOffset()/Tile.TILEWIDTH );
		int xEnd=(int) Math.min(width,( handler.getGameCamera().getxOffset()+1920 )/Tile.TILEWIDTH+1);
		int yStart=(int) Math.max(0,  handler.getGameCamera().getyOffset()/Tile.TILEHEIGHT );
		int yEnd=(int) Math.min(height, ( handler.getGameCamera().getyOffset()+1080 )/Tile.TILEHEIGHT+1);
		
		for (int y=yStart; y<yEnd;y++) {
			for (int x=xStart;x<xEnd;x++) {
				tmpTile = getTile(x,y);
				if(tmpTile instanceof dungeonWallTile) {
					int[][] surrTiles = new int [3][3];
					if(x-1 >=0) {
						surrTiles[0][1] = tiles[x-1][y];
					}
					if(y-1 >=0) {
						surrTiles[1][0] = tiles[x][y-1];
					}
					if(x+1 < tiles.length) {
						surrTiles[2][1] = tiles[x+1][y];
					}
					if(y+1 < tiles[0].length) {
						surrTiles[1][2] = tiles[x][y+1];
					}
					dungeonWallTile tmpTile2 = (dungeonWallTile) tmpTile;
					tmpTile2.render(g,(int) (x*Tile.TILEWIDTH - handler.getGameCamera().getxOffset()),(int) (y*Tile.TILEHEIGHT - handler.getGameCamera().getyOffset()), surrTiles);
				}else {
					tmpTile.render(g,(int) (x*Tile.TILEWIDTH - handler.getGameCamera().getxOffset()),(int) (y*Tile.TILEHEIGHT - handler.getGameCamera().getyOffset()));
				}

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

	public void reset()
	{}
	
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

	public int getId()
	{
		return id;
	}

	
	public int getHEight() {
		return height;
	}

	public void setTile(int x, int y, int id){
		tiles[x/128][y/128]= id;
	}

	public String getPath()
	{
		return path;
	}

	public void save()
	{
		String dump = "";
		dump += "[Player]\n";

		entityManager.dump();
	}
}























