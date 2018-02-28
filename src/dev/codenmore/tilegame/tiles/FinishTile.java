package dev.codenmore.tilegame.tiles;

import java.awt.image.BufferedImage;

import dev.codenmore.tilegame.gfx.Assets;

public class FinishTile extends Tile{

	public FinishTile( int id) {
		super(Assets.finish, id);

	}
	public boolean isFinish() {
		return true;
	}
}
