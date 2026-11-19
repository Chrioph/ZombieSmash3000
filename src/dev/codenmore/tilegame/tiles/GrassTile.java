package dev.codenmore.tilegame.tiles;

import java.awt.Graphics;

import dev.codenmore.tilegame.gfx.Assets;

public class GrassTile extends Tile {
	
	public GrassTile(int id) {
		super(Assets.grass,id);
		
	}

	@Override
	public void render(Graphics g, int x, int y, int tileX, int tileY) {
		g.drawImage(variant(Assets.grassVariants, tileX, tileY), x, y, TILEWIDTH, TILEHEIGHT, null);
	}

}
