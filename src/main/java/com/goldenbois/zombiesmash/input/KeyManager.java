package com.goldenbois.zombiesmash.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyManager implements KeyListener{

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
		
		
		up = keys [KeyEvent.VK_W];
		down = keys [KeyEvent.VK_S];
		left = keys [KeyEvent.VK_A];
		right = keys [KeyEvent.VK_D];
		
		aUp = keys [KeyEvent.VK_UP];
		aDown = keys [KeyEvent.VK_DOWN];
		aLeft = keys [KeyEvent.VK_LEFT];
		aRight = keys [KeyEvent.VK_RIGHT];
		
		
	}
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()<0 || e.getKeyCode()>= keys.length)
			return;
		keys[e.getKeyCode()]=true;
	}
	
	public boolean keyJustPressed(int keyCode){
		if(keyCode < 0 || keyCode >= keys.length)
			return false;
		return justPressed[keyCode];
	}

	
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()]=false;
		
	}

	
	public void keyTyped(KeyEvent e) {
		
		
	}

}
