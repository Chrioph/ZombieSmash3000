package dev.codenmore.tilegame.inventory;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.gfx.Text;
import dev.codenmore.tilegame.input.KeyManager;
import dev.codenmore.tilegame.items.CraftableItem;
import dev.codenmore.tilegame.items.Item;

public class Inventory {
	
	private Handler handler;
	private boolean active=false;
	private ArrayList<Item> inventoryItems;
	private ArrayList<Item> inventoryItems1;
	
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
		inventoryItems1 = new ArrayList<Item>();

	}
	
	
	public void tick() {
		for(int i=0; i< inventoryItems.size();i++){
			if(inventoryItems.get(i).getCount()>0) {
				addIfNotAlreadyAdded(inventoryItems.get(i));
			}
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
			 selectedItem= inventoryItems1.size()-1;
		 else if (selectedItem >= inventoryItems1.size())
			 selectedItem=0;

		if (inventoryItems1.size()>0 && inventoryItems1.get(selectedItem).isPlaceable() && handler.getKeyManager().keyJustPressed(KeyEvent.VK_Q)){
			active=!active;
			handler.getWorld().getEntityManager().getPlayer().setPlacingItem(inventoryItems.get(selectedItem).getId());

		}
	}
	
	public void render(Graphics g){
		if(!active)
			return;
		
		g.drawImage(Assets.inventoryScreen, invX, invY, invWidth, invHeight, null);
		int len = inventoryItems1.size();
		if(len == 0)
			return;

		for(int i = -5;i < 6;i++){
			if(selectedItem + i < 0 || selectedItem + i >= len)
				continue;
			if(i == 0){
				Text.drawString(g, "> " + inventoryItems1.get(selectedItem + i).getName() + " <", invListCenterX,
						invListCenterY + i * invListSpacing, true, Color.YELLOW, Assets.font56);
			}else{
				Text.drawString(g, inventoryItems1.get(selectedItem + i).getName(), invListCenterX,
						invListCenterY + i * invListSpacing, true, Color.WHITE, Assets.font56);
			}
		}
		Item item = inventoryItems1.get(selectedItem);
		g.drawImage(item.getTexture(), invImageX, invImageY, invImageWidth, invImageHeight, null);
		Text.drawString(g, Integer.toString(item.getCount()), invCountX, invCountY, true, Color.WHITE, Assets.font56);
		if (inventoryItems1.get(selectedItem).isPlaceable()) {
			Text.drawString(g, "Press Q to", invImageX + 60, invImageY + 300, true, Color.WHITE, Assets.font56);
			Text.drawString(g, "place item", invImageX + 60, invImageY + 300 + invListSpacing, true, Color.WHITE, Assets.font56);
		}
	}
	
	//Inventory Methods
	
	public void addItem(Item item) {
		for (Item i: inventoryItems) {
			if(i.getId()==item.getId()){
				i.setCount(i.getCount()+item.getCount()+1);
				return;
			}
		}
		inventoryItems.add(item);
	}

	public void addIfNotAlreadyAdded(Item item){
		for (int i=0;i<inventoryItems1.size();i++){
			if (inventoryItems1.get(i).equals(item)){
				return;
			}
		}
		inventoryItems1.add(item);
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
