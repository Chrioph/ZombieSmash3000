package com.goldenbois.zombiesmash.tiles;


import com.goldenbois.zombiesmash.gfx.Assets;

public class SolidWoodTile extends Tile {

	public SolidWoodTile( int id) {
		super(Assets.solidWood, id);
	}
	
	public boolean isSolid() {
		return true;
	}
	
}
