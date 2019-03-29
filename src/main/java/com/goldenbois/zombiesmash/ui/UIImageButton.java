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
			g.drawImage(images[1], (int) x, (int)y);
		else
			g.drawImage(images[0], (int) x, (int)y);
	}

	@Override
	public void onClick() {
		clicker.onClick();
	}
	
	
	
	

}
