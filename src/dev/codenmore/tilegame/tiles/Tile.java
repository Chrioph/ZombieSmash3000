package dev.codenmore.tilegame.tiles;

import dev.codenmore.tilegame.tiles.dungeon.dungeonFloorTile;
import dev.codenmore.tilegame.tiles.dungeon.dungeonWallTile;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Tile {
	//STATIC
	
	public static Tile[] tiles= new Tile[256];
	public static Tile grassTile=new GrassTile(0);
	public static Tile stoneTile=new StoneTile(2);
	public static Tile dirtTile=new DirtTile(1);
	public static Tile finishTile=new FinishTile(3);
	public static Tile dungeonFloor = new dungeonFloorTile(6);
	public static Tile dungeonWallTile = new dungeonWallTile(7);
	
	//CLASS

	public static final int TILEWIDTH=128,TILEHEIGHT=128;
	
	protected BufferedImage texture;
	protected final int id ;
	
	public Tile(BufferedImage texture, int id) {
		this.id=id;
		this.texture=texture;
		tiles[id]=this;
	}
	
	public void tick() {
	
	}
	
	public void render(Graphics g,int x, int y) {
		
		g.drawImage(texture, x, y,TILEWIDTH,TILEHEIGHT,null );
		
	}

	/**
	 * Renders the tile at screen position (x, y). tileX and tileY are the tile's position in
	 * the world, tiles with several texture variants use them to pick one.
	 */
	public void render(Graphics g, int x, int y, int tileX, int tileY) {
		render(g, x, y);
	}

	/** Picks one of the variants, always the same one for the same tile position. */
	protected static BufferedImage variant(BufferedImage[] variants, int tileX, int tileY) {
		int h = tileX * 73856093 ^ tileY * 19349663;
		h ^= h >>> 13;
		h *= 0x5bd1e995;
		h ^= h >>> 15;
		return variants[Math.floorMod(h, variants.length)];
	}
	
	public boolean isSolid() {
		return false;
	}
	
	public int getId() {
		return id;
	}
	public boolean isFinish() {
		 return false;
	 }


}
