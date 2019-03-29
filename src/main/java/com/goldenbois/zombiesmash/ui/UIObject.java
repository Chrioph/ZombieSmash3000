package com.goldenbois.zombiesmash.ui;

import com.goldenbois.zombiesmash.Settings;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import java.awt.event.MouseEvent;

public abstract class UIObject {

	protected float x,y;
	protected int width, height;
	protected boolean hovering =false;
	protected Rectangle bounds;
	
	public boolean isHovering() {
		return hovering;
	}

	public void setHovering(boolean hovering) {
		this.hovering = hovering;
	}

	public UIObject(float x , float y , int width, int height) {
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		bounds=new Rectangle((int) (x * Settings.getScaleX()), (int)(y * Settings.getScaleY()),(int)(width * Settings.getScaleX()),(int)(height * Settings.getScaleY()));
	}

	public void updateBounds()
	{
		this.bounds = new Rectangle((int) (x * Settings.getScaleX()), (int) (y  * Settings.getScaleY()),(int)(width * Settings.getScaleX()), (int)(height * Settings.getScaleY()));
	}

	public abstract void tick();
	
	public void render(Graphics g) {
	}
	
	public abstract void onClick();
	
	public void onMouseMove(int oldx, int oldy, int newx, int newy) {
		if (bounds.contains(newx, newy))
			hovering=true;
		else
			hovering=false;
	}
	
	public void onMouseRelease(int button, int x, int y) {
		if (hovering)
			onClick();
	}

	
	//Getters and Setters
	
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
}
