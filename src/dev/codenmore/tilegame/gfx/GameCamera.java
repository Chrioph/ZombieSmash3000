package dev.codenmore.tilegame.gfx;

import dev.codenmore.tilegame.Game;
import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.entity.Entity;
import dev.codenmore.tilegame.tiles.Tile;

public class GameCamera {

	private float xOffset,yOffset;
	private Handler handler;
	
	public GameCamera(Handler handler, float xOffset, float yOffset) {
		
		this.handler=handler;
		this.xOffset=xOffset;
		this.yOffset=yOffset;
	}

	public void move(float xAmt, float yAmt) {
		xOffset += xAmt;
		yOffset += yAmt;
		checkBlankSpace();
	}
	
	public void checkBlankSpace() {
		if (xOffset <0)
			xOffset =0;
		else if ( xOffset > handler.getWorld().getWidth() * Tile.TILEWIDTH - 1920)
			xOffset =  handler.getWorld().getWidth() * Tile.TILEWIDTH - 1920;
		if (yOffset<0)
			yOffset = 0;
		else if ( yOffset > handler.getWorld().getHEight() * Tile.TILEHEIGHT - 1080)
			yOffset = handler.getWorld().getHEight() * Tile.TILEHEIGHT - 1080;
		
			
	}
	
	
	public void centerOnEntity(Entity e) {
		xOffset = (e.getX()-1920 / 2)+ e.getWidth()/2;
		yOffset = e.getY()-1080 / 2 +e.getHeight()/2;
		checkBlankSpace();
	}
	
	//GETTERS and SETTERS

	public float getxOffset() {
		return xOffset;
	}


	public void setxOffset(float xOffset) {
		this.xOffset = xOffset;
	}


	public float getyOffset() {
		return yOffset;
	}


	public void setyOffset(float yOffset) {
		this.yOffset = yOffset;
	}
	
}
