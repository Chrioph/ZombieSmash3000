package dev.codenmore.tilegame.entity.creatures;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.Settings;
import dev.codenmore.tilegame.entity.Entity;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.tiles.Tile;

public class Arrow extends Creature{

	private int direction;
	private Rectangle bounds1;
	
	public Arrow(Handler handler, float x, float y, int width, int height, int direction, int damage) {
		super(handler, x, y, width, height);
		bounds1=new Rectangle();
		this.direction=direction;
		this.speed=8.0f;
		this.damage=damage;
		bounds.x=4   ;
		bounds.y=26;
		bounds.width= 56  ;
		bounds.height= 14;
		if (direction == 1  ) {
			bounds.x=24;
			bounds.y=4  ;
			bounds.width= 14;
			bounds.height= 56 ;
		}
		if(direction == 3) {
			bounds.x=24;
			bounds.y=4  ;
			bounds.width= 14;
			bounds.height= 56 ;
		}
		if (direction==0) {
			bounds1.x=bounds.x   ;
			bounds1.y=bounds.y;
			bounds1.width= 56 +(int)speed*3  ;
			bounds1.height= 14;
		}
		if (direction == 1 ) {
			bounds1.x=24;
			bounds1.y=4  ;
			bounds1.width= 14;
			bounds1.height= 56 +(int)speed *3;
		}
		if (direction==2) {
			bounds1.x=bounds.x -(int) speed  *3 ;
			bounds1.y=bounds.y;
			bounds1.width= 56   ;
			bounds1.height= 14;
		}
		if (direction == 3 ) {
			bounds1.x=24;
			bounds1.y=4 -(int)speed *3;
			bounds1.width= 14;
			bounds1.height= 56  ;
		}
	}

	@Override
	public void tick() {
		getMovement();
		move();
	}

	@Override
	public void render(Graphics g) {
		super.render(g);
		if(direction == 0)
			g.drawImage(Assets.arrowRight,(int)(x-handler.getGameCamera().getxOffset()),(int)(y-handler.getGameCamera().getyOffset()),width,height,null);
		if(direction == 1)
			g.drawImage(Assets.arrowDown,(int)(x-handler.getGameCamera().getxOffset()),(int)(y-handler.getGameCamera().getyOffset()),width,height,null);
		if(direction == 2)
			g.drawImage(Assets.arrowLeft,(int)(x-handler.getGameCamera().getxOffset()),(int)(y-handler.getGameCamera().getyOffset()),width,height,null);
		if(direction == 3)
			g.drawImage(Assets.arrowUp,(int)(x-handler.getGameCamera().getxOffset()),(int)(y-handler.getGameCamera().getyOffset()),width,height,null);
		if (Settings.getDebug()) {
			g.setColor(Color.PINK);
			g.fillRect((int) (bounds1.x - handler.getGameCamera().getxOffset() + x) ,(int) (bounds1.y - handler.getGameCamera().getyOffset() + y), bounds1.width, bounds1.height);
		}
	
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
		if(checkEntityCollision(0f, yMove)||checkEntityCollision(xMove, 0f)) {
			for (Entity e : handler.getWorld().getEntityManager().getEntities()) {
				if (e.equals(this))
					continue;
				if (e.isPlayer())
					continue;
				if ( e.getCollisionBounds(0,0).intersects((int) (x + bounds1.x - handler.getGameCamera().getxOffset()),(int) (y + bounds1.y - handler.getGameCamera().getyOffset() ), bounds1.width, bounds1.height)) {
					e.hurt(damage);
				}
			}
			die();
		}

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

	
	
	public void moveY() {
		
		if(yMove>0) {				//Moving down
			int ty = (int) (y + yMove + bounds.y + bounds.height) / Tile.TILEHEIGHT;
			
			if (!collisionWithTile((int) (x+ bounds.x)/Tile.TILEWIDTH , ty) && 
					!collisionWithTile( (int) ((x+bounds.x +bounds.width) / Tile.TILEHEIGHT), ty)) {
				y+= yMove;
			}
			else {
				die();
			}
		}
		else if(yMove < 0) {		//Moving up
			int ty = (int) (y + yMove + bounds.y ) / Tile.TILEHEIGHT;
			
			if (!collisionWithTile((int) (x+ bounds.x)/Tile.TILEHEIGHT , ty) && 
					!collisionWithTile( (int) (x+bounds.x +bounds.width ) / Tile.TILEWIDTH ,  ty)) {
				y+= yMove;
			}
			else {
				die();
			}
		}
		
		
	}
	public void moveX() {
		
		if(xMove>0) {				//Moving right
			int tx = (int) (x + xMove + bounds.x + bounds.width) / Tile.TILEWIDTH;
			
			if (!collisionWithTile(tx, (int) (y+ bounds.y)/Tile.TILEHEIGHT) && 
					!collisionWithTile(tx , (int) ((y+bounds.y +bounds.height) / Tile.TILEHEIGHT))) {
				x+= xMove;
			}
			else{
				die ();
			}
		}
		else if(xMove < 0) {		//Moving left
			int tx = (int) (x + xMove + bounds.x ) / Tile.TILEWIDTH;
			
			if (!collisionWithTile(tx, (int) (y+ bounds.y)/Tile.TILEHEIGHT) && 
					!collisionWithTile(tx , (int) ((y+bounds.y +bounds.height) / Tile.TILEHEIGHT))) {
				x+= xMove;
			}
			else {
				die();
			}
		}
		
		
	}
	
}
