package com.goldenbois.zombiesmash.input;


import com.goldenbois.zombiesmash.ui.UIManager;
import org.newdawn.slick.Input;
import org.newdawn.slick.MouseListener;

public class MouseManager implements MouseListener {

	private boolean leftPressed , rightPressed;
	private int mouseX, mouseY;
	private UIManager uiManager;
	
	public MouseManager() {
		
	}


	public void setInput(Input var1) {

	}

	public boolean isAcceptingInput() {
		return true;
	}

	public void inputEnded() {

	}

	public void inputStarted() {

	}
	//Getters and Setters
	
	public void setUIManager(UIManager uiManager) {
		this.uiManager=uiManager;
	}
	public UIManager getUIManager() {
		return this.uiManager;
	}
	
	public boolean isLeftPressed() {
		return leftPressed;
	}
	
	public boolean isRightPressed() {
		return rightPressed;
	}
	
	public int getMouseX() {
		return mouseX;
	}
	public int getMouseY() {
		return mouseY;
	}
	
	
	//Override
	
	@Override
	public void mouseDragged(int var1, int var2, int var3, int var4) {

		
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		mouseX = newx;
		mouseY = newy;
		
		if(uiManager != null)
			uiManager.onMouseMove(oldx, oldy, newx, newy);
		
	}

	@Override
	public void mousePressed(int button, int x, int y) {
		if (button == 0)
			leftPressed=true;
		else if (button == 1)
			rightPressed=true;
	}

	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {
	}

	@Override
	public void mouseReleased(int button, int x, int y) {
		if (button == 0)
			leftPressed=false;
		else if (button == 1)
			rightPressed=false;
		
		if(uiManager != null)
			uiManager.onMouseRelease(button, x, y);
		
	}

	@Override
	public void mouseWheelMoved(int value) {

	}
	

}
