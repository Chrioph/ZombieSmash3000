package dev.codenmore.tilegame.crafting;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.gfx.Text;
import dev.codenmore.tilegame.items.Item;
import dev.codenmore.tilegame.items.CraftableItem;

public class CraftingScreen {

	private Handler handler;
	private boolean active=false;
	private ArrayList<Item> inventoryItems;
	private ArrayList<CraftableItem> craftableItems;
	
	private int invX=192, invY=108, invWidth=1920-192*2, invHeight=1080-108*2,
			craftListCenterX = invX + invWidth/5, craftListCenterY= invY + invHeight / 2 + 5*(invHeight/384),
			invListSpacing=68;
	
	private int invListCenterX=1225  , invListCenterY=210;
	
	private int craftImageX = 800, 
			craftImageY= invY + invHeight / 2 - 38,
			craftImageWidth= 96, 
			craftImageHeight=96;
	
	private int resCountX=  1550, resCountY= 210;
	
	private int selectedItem=0;
	
	public CraftingScreen(Handler handler) {
		this.handler=handler;
		inventoryItems = new ArrayList<Item>();
		inventoryItems.add(Item.logItem);
		inventoryItems.add(Item.rockItem);
		
		
		craftableItems = new ArrayList<CraftableItem>();
		craftableItems.add(CraftableItem.woodItem);
		craftableItems.add(CraftableItem.solidWoodItem);
	}
	public void tick() {
		if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_C))
			active=!active;
		if(!active)
			return;
		 if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_W))
			 selectedItem--;
		 if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_S))
			 selectedItem++;
		 if (selectedItem<0)
			 selectedItem= craftableItems.size()-1;
		 else if (selectedItem >= craftableItems.size())
			 selectedItem=0;
	}
	
	public void render(Graphics g){
		if(!active)
			return;
		
		g.drawImage(Assets.craftingScreen, invX,invY, invWidth, invHeight, null);
		int len = craftableItems.size();
		if(len == 0) {
			System.out.println("return");
			return;
		}
		for(int i = -5;i < 6;i++){
			if(selectedItem + i < 0 || selectedItem + i >= len)
				continue;
			if(i == 0){
				Text.drawString(g, ">" + craftableItems.get(selectedItem + i).getName() + "<", craftListCenterX, 
						craftListCenterY + i * invListSpacing, true, Color.YELLOW, Assets.font56);
			}else{
				Text.drawString(g, craftableItems.get(selectedItem + i).getName(), craftListCenterX, 
						craftListCenterY + i * invListSpacing, true, Color.WHITE, Assets.font56);
			}
		}
		Item item = craftableItems.get(selectedItem);
		g.drawImage(item.getTexture(), craftImageX, craftImageY, craftImageWidth, craftImageHeight, null);
		
		
		
		for(int i = 0;i < inventoryItems.size();i++){
				Text.drawString(g, inventoryItems.get(i).getName(), invListCenterX, invListCenterY + i * invListSpacing, true, Color.WHITE, Assets.font56);
		}
		
		for(int i =0; i< craftableItems.get(selectedItem).getLen(); i++ ) {
			if (craftableItems.get(selectedItem).getResources(i)<=handler.getWorld().getEntityManager().getPlayer().getInventory().getInventoryItems().get(i).getCount())
				Text.drawString(g, " "+ craftableItems.get(selectedItem).getResources(i), resCountX, resCountY+ i * invListSpacing, true, Color.GREEN, Assets.font56);
			else
				Text.drawString(g, " "+ craftableItems.get(selectedItem).getResources(i), resCountX, resCountY+ i * invListSpacing, true, Color.RED, Assets.font56);
		}
		
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
}
