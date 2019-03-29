package com.goldenbois.zombiesmash.ui;

import org.newdawn.slick.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import com.goldenbois.zombiesmash.Handler;

public class UIManager {

	
	private Handler handler;
	private ArrayList<UIObject> objects;
	private ArrayList<UIObject> tmpObjects;
	
	
	public UIManager(Handler handler) {
		this.handler=handler;
		objects = new ArrayList<UIObject>();
		tmpObjects = new ArrayList<UIObject>();
	}
	
	public void tick() {
		for(UIObject o: objects)
			o.tick();
		for(UIObject o: tmpObjects)
			o.tick();
	}
	
	public void render(Graphics g) {
		for(UIObject o: objects)
			o.render(g);
		for(UIObject o: tmpObjects)
			o.render(g);
	}
	
	public void onMouseMove(int oldx, int oldy, int newx, int newy) {
		for(UIObject o: objects)
			o.onMouseMove(oldx, oldy, newx, newy);
		for(UIObject o: tmpObjects)
			o.onMouseMove(oldx, oldy, newx, newy);
	}
	
	public void onMouseRelease(int button, int x, int y) {
		if(button == 0) {
			for(UIObject o: objects)
				o.onMouseRelease(button, x, y);
			for(UIObject o: tmpObjects)
				o.onMouseRelease(button, x, y);
		}
	}

	public void updateAllBounds()
	{
		for(UIObject obj : objects) {
			obj.updateBounds();
		}
		for(UIObject obj : tmpObjects) {
			obj.updateBounds();
		}
	}
	
	public void addObject(UIObject o) {
		objects.add(o);
	}
	public void addTmpObject(UIObject o) {
		tmpObjects.add(o);
	}

	public void removeObject(UIObject o) {
		objects.remove(o);
	}
	public void removeTmpObject(UIObject o) {
		tmpObjects.remove(o);
	}

	public void removeAllTmpObjects()
	{
		tmpObjects.clear();
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public ArrayList<UIObject> getObjects() {
		return objects;
	}
	public ArrayList<UIObject> getTmpObjects() {
		return tmpObjects;
	}

	public void setObjects(ArrayList<UIObject> objects) {
		this.objects = objects;
	}
	
}
