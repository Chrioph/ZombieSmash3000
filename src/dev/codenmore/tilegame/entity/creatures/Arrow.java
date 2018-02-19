package dev.codenmore.tilegame.entity.creatures;

import java.awt.Graphics;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.gfx.Assets;

public class Arrow extends Creature{

	private int direction;
	
	public Arrow(Handler handler, float x, float y, int width, int height, int direction) {
		super(handler, x, y, width, height);
		this.direction=direction;
		this.speed=8.0f;
	}

	@Override
	public void tick() {
		getMovement();
		move();
	}

	@Override
	public void render(Graphics g) {
	if(direction == 0)
		g.drawImage(Assets.arrowRight,(int)(x-handler.getGameCamera().getxOffset()),(int)(y-handler.getGameCamera().getyOffset()),width,height,null);
	if(direction == 1)
		g.drawImage(Assets.arrowDown,(int)(x-handler.getGameCamera().getxOffset()),(int)(y-handler.getGameCamera().getyOffset()),width,height,null);
	if(direction == 2)
		g.drawImage(Assets.arrowLeft,(int)(x-handler.getGameCamera().getxOffset()),(int)(y-handler.getGameCamera().getyOffset()),width,height,null);
	if(direction == 3)
		g.drawImage(Assets.arrowUp,(int)(x-handler.getGameCamera().getxOffset()),(int)(y-handler.getGameCamera().getyOffset()),width,height,null);
		
	}

	@Override
	public void die() {
		
		this.active=false;
	}
	
	public void move() {
		if(!checkEntityCollision(xMove, 0f))
			moveX();
		if (!checkEntityCollision(0f, yMove))
			moveY();
		if(checkEntityCollision(0f, yMove)||checkEntityCollision(xMove, 0f))
			die();
		
	}
	
	private void getMovement() {
		xMove=0;
		yMove=0;
		if (direction==0)
			xMove= speed;
		if (direction==1)
			yMove= speed;
		if (direction==2)
			xMove= -speed;
		if (direction==3)
			yMove= -speed;
	}

}
