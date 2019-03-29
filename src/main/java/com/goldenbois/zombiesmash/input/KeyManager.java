package com.goldenbois.zombiesmash.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyManager{

	private boolean[] keys, justPressed, cantPress;
	public boolean up, down, left, right, aUp,aDown,aLeft,aRight;
	public KeyManager() {
		keys =new boolean [1024];
		justPressed =new boolean [keys.length];
		cantPress=new boolean [keys.length];
	}
	
	
	public void tick() {
		for (int i=0; i< keys.length;i++){
			if(cantPress[i]&& !keys[i]) {
				cantPress[i]=false;
			}
			else if( justPressed[i]) {
				cantPress[i]=true;
				justPressed[i]=false;
			}
			if (!cantPress[i] && keys[i]) {
				justPressed[i]=true;
			}
		}
		
		
		up = keys [17];
		down = keys [31];
		left = keys [30];
		right = keys [32];
		
		aUp = keys [200];
		aDown = keys [208];
		aLeft = keys [203];
		aRight = keys [205];
		
		
	}
	public void keyPressed(int key, char c) {
		if(key<0 || key>= keys.length)
			return;
		keys[key]=true;
	}
	
	public boolean keyJustPressed(int keyCode){
		if(keyCode < 0 || keyCode >= keys.length)
			return false;
		return justPressed[keyCode];
	}

	
	public void keyReleased(int key) {
		keys[key]=false;
		
	}

}
