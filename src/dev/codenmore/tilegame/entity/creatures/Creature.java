package dev.codenmore.tilegame.entity.creatures;


import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.entity.Entity;
import dev.codenmore.tilegame.tiles.Tile;

public abstract class Creature extends Entity{

	public static final float DEFAULT_SPEED=6.0f;
	
	public static final int DEFAULT_CREATURE_WIDTH=64;
	public static final int DEFAULT_CREATURE_HEIGHT=64;
	public static final int DEFAULT_CREATURE_DAMAGE=3;
	
	
	protected float speed;
	protected float xMove;
	protected float yMove;
	protected int yAttack;
	protected int xAttack;
	protected int damage;
	
	public Creature( Handler handler ,float x, float y, int width, int height) {
		super(handler, x, y,width,height);
		damage=DEFAULT_CREATURE_DAMAGE;
		speed=DEFAULT_SPEED;
		xMove=0;
		yMove=0;
	}
	
	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public void move() {
		if(!checkEntityCollision(xMove, 0f))
			moveX();
		if (!checkEntityCollision(0f, yMove))
			moveY();
		
	}
	
	public void moveX() {
		
		if(xMove>0) {				//Moving right
			int tx = (int) (x + xMove + bounds.x + bounds.width) / Tile.TILEWIDTH;
			
			if (!collisionWithTile(tx, (int) (y+ bounds.y)/Tile.TILEHEIGHT) && 
					!collisionWithTile(tx , (int) ((y+bounds.y +bounds.height) / Tile.TILEHEIGHT))) {
				x+= xMove;
			}
			else{
				x = tx * Tile.TILEWIDTH -bounds.x - bounds.width -1 ;
			}
		}
		else if(xMove < 0) {		//Moving left
			int tx = (int) (x + xMove + bounds.x ) / Tile.TILEWIDTH;
			
			if (!collisionWithTile(tx, (int) (y+ bounds.y)/Tile.TILEHEIGHT) && 
					!collisionWithTile(tx , (int) ((y+bounds.y +bounds.height) / Tile.TILEHEIGHT))) {
				x+= xMove;
			}
			else {
				x= tx * Tile.TILEWIDTH+ Tile.TILEWIDTH - bounds.x;
			}
		}
		
		
	}
	public void moveY() {
		
		if(yMove>0) {				//Moving down
			int ty = (int) (y + yMove + bounds.y + bounds.height) / Tile.TILEHEIGHT;
			
			if (!collisionWithTile((int) (x+ bounds.x)/Tile.TILEWIDTH , ty) && 
					!collisionWithTile( (int) ((x+bounds.x +bounds.width) / Tile.TILEHEIGHT), ty)) {
				y+= yMove;
			}
			else {
				y = ty * Tile.TILEHEIGHT - bounds.y -bounds.height -1;
			}
		}
		else if(yMove < 0) {		//Moving up
			int ty = (int) (y + yMove + bounds.y ) / Tile.TILEHEIGHT;
			
			if (!collisionWithTile((int) (x+ bounds.x)/Tile.TILEHEIGHT , ty) && 
					!collisionWithTile( (int) (x+bounds.x +bounds.width ) / Tile.TILEWIDTH ,  ty)) {
				y+= yMove;
			}
			else {
				y= ty* Tile.TILEHEIGHT+ Tile.TILEHEIGHT -bounds.y;
			}
		}
		
		
	}
	
	public boolean isPlayer() {
		return false;
	}
	
	protected boolean collisionWithTile(int x , int y ) {
		
		return handler.getWorld().getTile(x, y).isSolid();
		
	}
	
	
	//GETTER and SETTERS

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getxMove() {
		return xMove;
	}

	public void setxMove(float xMove) {
		this.xMove = xMove;
	}

	public float getyMove() {
		return yMove;
	}

	public void setyMove(float yMove) {
		this.yMove = yMove;
	}
	
	

}
