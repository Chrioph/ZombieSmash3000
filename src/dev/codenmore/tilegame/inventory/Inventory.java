package dev.codenmore.tilegame.inventory;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.entity.Entity;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.gfx.Text;
import dev.codenmore.tilegame.input.KeyManager;
import dev.codenmore.tilegame.items.CraftableItem;
import dev.codenmore.tilegame.items.Item;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Inventory {
	
	private Handler handler;
	private boolean active=false;
	private ArrayList<Item> inventoryItems;
	private ArrayList<Item> displayInventoryItems;
	
	private int invX=192, invY=108, invWidth=1920-192*2, invHeight=1080-108*2,
			invListCenterX = invX + invWidth/3-1, invListCenterY= invY + invHeight / 2 + 5*(invHeight/384),
			invListSpacing=68;
	
	private int invImageX = 1380, 
			invImageY= 190,
			invImageWidth= 128, 
			invImageHeight=128;
	
	private int invCountX=  1450, invCountY= 388;
	
	private int selectedItem=0;
	
	public Inventory(Handler handler) {
		this.handler=handler;
		inventoryItems = new ArrayList<Item>();
		inventoryItems.add(Item.logItem);
		inventoryItems.add(Item.rockItem);
		inventoryItems.add(CraftableItem.woodItem);
		inventoryItems.add(CraftableItem.solidWoodItem);
        inventoryItems.add(Item.seedItem);
		displayInventoryItems = new ArrayList<Item>();

	}
	
	
	public void tick() {
		for(int i=0; i< inventoryItems.size();i++){
			if(inventoryItems.get(i).getCount()>0) {
				addIfNotAlreadyAdded(inventoryItems.get(i));
			}
		}
		Iterator<Item> it = displayInventoryItems.iterator();
		while( it.hasNext() ) {
			Item i= it.next();
			if (i.getCount()<=0)
				it.remove();
		}
		if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_E))
			active=!active;
		if(!active)
			return;
		 if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_W))
			 selectedItem--;
		 if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_S))
			 selectedItem++;
		 if (selectedItem<0)
			 selectedItem= displayInventoryItems.size()-1;
		 else if (selectedItem >= displayInventoryItems.size())
			 selectedItem=0;

		if (displayInventoryItems.size()>0 && displayInventoryItems.get(selectedItem).isPlaceable() && handler.getKeyManager().keyJustPressed(KeyEvent.VK_Q)){
			active=!active;
			handler.getWorld().getEntityManager().getPlayer().setPlacingItem(displayInventoryItems.get(selectedItem).getId());

		}
	}

	public void render(Graphics g){
		if(!active)
			return;
		
		g.drawImage(Assets.inventoryScreen, invX, invY, invWidth, invHeight, null);
		int len = displayInventoryItems.size();
		if(len == 0)
			return;

		for(int i = -5;i < 6;i++){
			if(selectedItem + i < 0 || selectedItem + i >= len)
				continue;
			if(i == 0){
				Text.drawString(g, "> " + displayInventoryItems.get(selectedItem + i).getName() + " <", invListCenterX,
						invListCenterY + i * invListSpacing, true, Color.YELLOW, Assets.font56);
			}else{
				Text.drawString(g, displayInventoryItems.get(selectedItem + i).getName(), invListCenterX,
						invListCenterY + i * invListSpacing, true, Color.WHITE, Assets.font56);
			}
		}
		Item item = displayInventoryItems.get(selectedItem);
		g.drawImage(item.getTexture(), invImageX, invImageY, invImageWidth, invImageHeight, null);
		Text.drawString(g, Integer.toString(item.getCount()), invCountX, invCountY, true, Color.WHITE, Assets.font56);
		if (displayInventoryItems.get(selectedItem).isPlaceable()) {
			Text.drawString(g, "Press Q to", invImageX + 60, invImageY + 300, true, Color.WHITE, Assets.font56);
			Text.drawString(g, "place item", invImageX + 60, invImageY + 300 + invListSpacing, true, Color.WHITE, Assets.font56);
		}
	}
	
	//Inventory Methods
	
	public void addItem(Item item) {
		for (Item i: inventoryItems) {
			if(i.getId()==item.getId()){
				i.setCount(i.getCount()+item.getCount());
				return;
			}
		}
		inventoryItems.add(item);
	}

	public void addIfNotAlreadyAdded(Item item){
		for (int i=0;i<displayInventoryItems.size();i++){
			if (displayInventoryItems.get(i).equals(item)){
				return;
			}
		}
		displayInventoryItems.add(item);
	}

	public void destroy()
	{
		for(Item i : inventoryItems) {
			i.setCount(0);
		}
		displayInventoryItems.clear();
	}

	public void fillFromDump(NodeList items) {

		destroy();
		for(int i = 0; i<items.getLength(); i++) {
			if(items.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element item = (Element)items.item(i);
				int itemid = Integer.parseInt(item.getElementsByTagName("id").item(0).getTextContent());
				int itemcount = Integer.parseInt(item.getElementsByTagName("count").item(0).getTextContent());
				Item addItem = Item.items[itemid].createNew(itemcount);
				if(itemcount > 0) {
					this.addItem(addItem);
				}

			}

		}
	}


	//Getters and Setters
	
	public ArrayList<Item> getInventoryItems() {
		return inventoryItems;
	}


	public void setInventoryItems(ArrayList<Item> inventoryItems) {
		this.inventoryItems = inventoryItems;
	}


	public Handler getHandler() {
		return handler;
	}


	public boolean isActive() {
		return active;
	}


	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	
}
