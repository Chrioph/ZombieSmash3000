package dev.codenmore.tilegame.tiles;


import dev.codenmore.tilegame.gfx.Assets;

public class StoneTile extends Tile {

	public StoneTile( int id) {
		super(Assets.stone, id);
		
	}

	@Override
	public boolean isSolid() {
		return true;
	}
	
}
