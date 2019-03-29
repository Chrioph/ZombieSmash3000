package com.goldenbois.zombiesmash.tiles;


import com.goldenbois.zombiesmash.gfx.Assets;

public class StoneTile extends Tile {

	public StoneTile( int id) {
		super(Assets.stone, id);
		
	}

	@Override
	public boolean isSolid() {
		return true;
	}
	
}
