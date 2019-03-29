package com.goldenbois.zombiesmash.items;

import com.goldenbois.zombiesmash.utils.Utils;
import org.newdawn.slick.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import com.goldenbois.zombiesmash.Handler;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ItemManager implements Serializable {
	
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
	
	
	public void addItem(Item i, int x, int y, boolean random) {
		i.setHandler(handler);
		boolean b=false;
		int r1=0;
		int r2=0;
		if(random)  {
			while (!b) {
				r1= Utils.generateRandomInt(200)-100;
				r2=Utils.generateRandomInt(200)-100;
				if (i.collisionWithTile((int)(i.x + r1 ), (int) (i.y + r2 )) || i.collisionWithTile((int)(i.x+i.bounds.getX() + r1 ), (int) (i.y+i.bounds.getX() + r2 )))
					continue;
				b=true;
			}
		}

		i.setPosition((int)(i.x + x + r1  ),(int) (i.y+ y + r2 ));
		items.add(i);
	}

	public void fillFromDump(NodeList dump)
	{
		// first clear current items
		items.clear();
		for(int i = 0; i < dump.getLength(); i++) {
			if (dump.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element it = (Element) dump.item(i);
				int xCoord = Integer.parseInt(it.getElementsByTagName("x").item(0).getTextContent());
				int yCoord = Integer.parseInt(it.getElementsByTagName("y").item(0).getTextContent());
				this.addItem(Item.items[Integer.parseInt(it.getElementsByTagName("id").item(0).getTextContent())].createNew(1), xCoord, yCoord, false);
			}
		}
	}


	//Getters and Setters
	
	public Handler getHandler() {
		return handler;
	}

	public ArrayList<Item> getItems()
	{
		return items;
	}


	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	
}
