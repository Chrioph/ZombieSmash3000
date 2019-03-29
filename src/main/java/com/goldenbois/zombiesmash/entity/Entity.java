package com.goldenbois.zombiesmash.entity;

import com.goldenbois.zombiesmash.Settings;
import org.newdawn.slick.*;
import java.io.Serializable;


import com.goldenbois.zombiesmash.Handler;
import org.newdawn.slick.geom.Rectangle;

public abstract class Entity implements Serializable {
	
	protected float x,y;
	protected int width, height;
	protected Handler handler;
	protected Rectangle bounds;
	protected int health;
	protected boolean active=true;
	protected int knockbackCounter;
	

	public static final int DEFAULT_HEALTH = 10;
	
	public Entity(Handler handler, float x,float y, int width, int height) {

		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.handler=handler;
		health =DEFAULT_HEALTH;
		bounds = new Rectangle(0,0, width , height);
	}

	/**
	 * No args constructor for serialization
	 */
	public Entity() {
	}

	public String dump() {
		String dump = "";
		// just save type of entity and position
		dump += this.getClass().getName() + ";" + this.x + "/" + this.y;

		return dump;
	}
	
	public abstract void tick();
	
	public void render(Graphics g) {
		if(Settings.getDebug()) {
			Rectangle collBounds = getCollisionBounds(0f, 0f);
			g.setColor(Color.red);
			g.fillRect(collBounds.getX(),collBounds.getY(), bounds.getWidth(), bounds.getHeight());
		}

	}
	public void knockback() {
		
	}
	
	public abstract void die();
	
	public void hurt (int amt) {
		health -=amt;
		if (health<=0&& !this.isPlayer()) {
			active=false;
			die();
		}
	}
	
	public boolean checkEntityCollision(float xOffset, float yOffset) {
		
		for (Entity e: handler.getWorld().getEntityManager().getEntities()) {
			if (e.equals(this))
				continue;
			if ( e.getCollisionBounds(0f, 0f).intersects(getCollisionBounds(xOffset, yOffset)))
				return true;
		}
		for (Entity e: handler.getWorld().getEntityManager().getProjectiles()) {
			if (e.equals(this))
				continue;
			if ( e.getCollisionBounds(0f, 0f).intersects(getCollisionBounds(xOffset, yOffset)))
				return true;
		}
		return false;
	}
	
	
	public boolean isPlayer() {
		return false;
	}
	
	

	public Rectangle getCollisionBounds(float xOffset, float yOffset) {
		return new Rectangle ((int) (x+bounds.getX() + xOffset - handler.getGameCamera().getxOffset()),(int) (y +bounds.getY() + yOffset - handler.getGameCamera().getyOffset()), bounds.getWidth(), bounds.getHeight() );
	}

	protected void postrender(Graphics g){

	}

	protected boolean isChest(){
		return false;
	}
	
	
	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	

	public int getKnockbackCounter() {
		return knockbackCounter;
	}

	public void setKnockbackCounter(int knockbackCounter) {
		this.knockbackCounter = knockbackCounter;
	}
	

}
