package com.goldenbois.zombiesmash.ui;

import org.newdawn.slick.Image;

import org.newdawn.slick.*;
import java.awt.image.BufferedImage;

public class UIImageButton extends UIObject{
	
	private Image[] images;
	private ClickListener clicker;
	
	public UIImageButton(float x, float y , int width, int height, Image[] images, ClickListener clicker) {
		super (x,y,width, height);
		this.images=images;
		this.clicker=clicker;
	}

	@Override
	public void tick() {
		
	}

	@Override
	public void render(Graphics g) {
		super.render(g);
		if (hovering)
			images[1].draw(x, y, width, height);
		else
			images[0].draw(x, y, width, height);
	}

	@Override
	public void onClick() {
		clicker.onClick();
	}
	
	
	
	

}
