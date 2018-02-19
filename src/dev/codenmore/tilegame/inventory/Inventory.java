package dev.codenmore.tilegame.inventory;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.gfx.Text;
import dev.codenmore.tilegame.items.Item;

public class Inventory {
	
	private Handler handler;
	private boolean active=false;
	private ArrayList<Item> inventoryItems;
	
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
		
		
	}
	
	
	public void tick() {
		if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_E))
			active=!active;
		if(!active)
			return;
		 if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_W))
			 selectedItem--;
		 if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_S))
			 selectedItem++;
		 if (selectedItem<0)
			 selectedItem= inventoryItems.size()-1;
		 else if (selectedItem >= inventoryItems.size())
			 selectedItem=0;
	}
	
	public void render(Graphics g){
		if(!active)
			return;
		
		g.drawImage(Assets.inventoryScreen, invX, invY, invWidth, invHeight, null);
		g.drawImage(Assets.heart, 1175, 400,256,256, null);
		g.drawImage(Assets.sword, 1200, 675,192,192, null);
		g.drawImage(Assets.bow, 1485, 688,192,192, null);
		Text.drawString(g, "  "+ handler.getWorld().getEntityManager().getPlayer().getHealth()+"  ", 1303, 655, true, Color.WHITE, Assets.font40);
		Text.drawString(g, "  "+ handler.getWorld().getEntityManager().getPlayer().getDamage()+"  ", 1303, 915, true, Color.WHITE, Assets.font40);
		Text.drawString(g, "  "+ handler.getWorld().getEntityManager().getPlayer().getAmmunition()+"  ", 1578,915, true, Color.WHITE, Assets.font40);
		int len = inventoryItems.size();
		if(len == 0)
			return;
		
		for(int i = -5;i < 6;i++){
			if(selectedItem + i < 0 || selectedItem + i >= len)
				continue;
			if(i == 0){
				Text.drawString(g, "> " + inventoryItems.get(selectedItem + i).getName() + " <", invListCenterX, 
						invListCenterY + i * invListSpacing, true, Color.YELLOW, Assets.font56);
			}else{
				Text.drawString(g, inventoryItems.get(selectedItem + i).getName(), invListCenterX, 
						invListCenterY + i * invListSpacing, true, Color.WHITE, Assets.font56);
			}
		}
		Item item = inventoryItems.get(selectedItem);
		g.drawImage(item.getTexture(), invImageX, invImageY, invImageWidth, invImageHeight, null);
		Text.drawString(g, Integer.toString(item.getCount()), invCountX, invCountY, true, Color.WHITE, Assets.font56);
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


	//Getters and Setters
	
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
