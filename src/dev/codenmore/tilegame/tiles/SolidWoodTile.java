package dev.codenmore.tilegame.tiles;


import dev.codenmore.tilegame.gfx.Assets;

public class SolidWoodTile extends Tile {

	public SolidWoodTile( int id) {
		super(Assets.solidWood, id);
	}
	
	public boolean isSolid() {
		return true;
	}
	
}
