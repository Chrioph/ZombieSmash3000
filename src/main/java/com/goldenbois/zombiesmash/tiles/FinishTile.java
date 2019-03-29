package com.goldenbois.zombiesmash.tiles;

import com.goldenbois.zombiesmash.gfx.Assets;

public class FinishTile extends Tile{

	public FinishTile( int id) {
		super(Assets.finish, id);

	}
	public boolean isFinish() {
		return true;
	}
}
