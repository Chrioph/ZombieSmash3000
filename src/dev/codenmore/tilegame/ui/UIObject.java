package dev.codenmore.tilegame.ui;

import dev.codenmore.tilegame.Settings;
import dev.codenmore.tilegame.input.InputHandler;

import java.awt.*;
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

	public void renderOpenGL() {

	}
	
	public abstract void onClick();
	
	public void onMouseMove(MouseEvent e) {
		if (bounds.contains(e.getX(), e.getY()))
			hovering=true;
		else
			hovering=false;
	}
	
	public void onMouseRelease(MouseEvent e) {
		if (hovering)
			onClick();
	}

	public void onMouseMoveGL() {
		if (bounds.contains(InputHandler.posX, InputHandler.posY))
			hovering=true;
		else
			hovering=false;
	}

	public void onMouseReleaseGL() {
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
