package dev.codenmore.tilegame.tiles;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Tile {
	//STATIC
	
	
	public static Tile[] tiles= new Tile[256];
	public static Tile grassTile=new GrassTile(0);
	public static Tile stoneTile=new StoneTile(2);
	public static Tile dirtTile=new DirtTile(1);
	public static Tile finishTile=new FinishTile(3);
	public static Tile solidWoodTile=new SolidWoodTile(4);
	public static Tile woodTile=new WoodTile(5);
	
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
