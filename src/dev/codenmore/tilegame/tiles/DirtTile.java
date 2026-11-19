package dev.codenmore.tilegame.tiles;


import java.awt.Graphics;

import dev.codenmore.tilegame.gfx.Assets;

public class DirtTile extends  Tile{

	public DirtTile( int id) {
		super(Assets.dirt , id);
		
	}
	

	@Override
	public void render(Graphics g, int x, int y, int tileX, int tileY) {
		g.drawImage(variant(Assets.dirtVariants, tileX, tileY), x, y, TILEWIDTH, TILEHEIGHT, null);
	}

}
