package dev.codenmore.tilegame.items;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.utils.Utils;

public class ItemManager {
	
	private Handler handler;
	private ArrayList<Item> items = new ArrayList<Item>();
	private int x;
	private int y;
	
	
	public ItemManager(Handler handler) {
		this.handler=handler;

	}

	
	public void tick() {
		Iterator<Item>it = items.iterator();
		while (it.hasNext()) {
			Item i= it.next();
			i.tick();
			if(i.isPickedUp())
				it.remove();
		}
	}
	
	public void render(Graphics g) {
		for (Item i : items) {
			i.render(g);
		}
	}
	
	
	public void addItem(Item i, int x, int y ) {
		i.setHandler(handler);
		boolean b=false;
		int r1=0;
		int r2=0;
		while (!b) {
			r1=Utils.generateRandomInt(200)-100;
			r2=Utils.generateRandomInt(200)-100;
			if (i.collisionWithTile((int)(i.x + r1 ), (int) (i.y + r2 )) || i.collisionWithTile((int)(i.x+i.bounds.x + r1 ), (int) (i.y+i.bounds.x + r2 )))
				continue;
			b=true;
		}
		i.setPostion((int)(i.x + x + r1  ),(int) (i.y+ y + r2 ));
		items.add(i);
	}


	//Getters and Setters
	
	public Handler getHandler() {
		return handler;
	}


	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	
}
