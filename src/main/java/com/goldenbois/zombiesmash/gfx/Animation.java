package com.goldenbois.zombiesmash.gfx;

import org.newdawn.slick.Image;

public class Animation  {
	
	private int speed, index;
	private Image[] frames;
	private long lastTime, timer;


	public Animation(int speed, Image[] frames) {
		this.speed=speed;
		this.frames=frames;
		index=0;
		timer=0;
		lastTime = System.currentTimeMillis();
	}
	
	
	public void tick() {
		timer+=System.currentTimeMillis()-lastTime;
		lastTime=System.currentTimeMillis();
		
		if(timer>speed) {
			index++;
			timer=0;
			if(index>=frames.length) {
				index=0;
			}
		}

	}
	
	public Image getCurrentFrame() {
		return frames[index];
	}
	


	
	
	
	
}
