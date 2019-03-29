package com.goldenbois.zombiesmash.crafting;

import org.newdawn.slick.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.goldenbois.zombiesmash.Handler;
import com.goldenbois.zombiesmash.gfx.Assets;
import com.goldenbois.zombiesmash.items.Item;
import com.goldenbois.zombiesmash.items.CraftableItem;

/**
 * Implements the crafting screen and its functions
 */
public class CraftingScreen {

    private Handler handler;
    private boolean active = false;
    private ArrayList<Item> inventoryItems;
    private ArrayList<CraftableItem> craftableItems;

    private int invX = 192, invY = 108, invWidth = 1920 - 192 * 2, invHeight = 1080 - 108 * 2,
            craftListCenterX = invX + invWidth / 5, craftListCenterY = invY + invHeight / 2 + 5 * (invHeight / 384),
            invListSpacing = 68;

    private int invListCenterX = 1225, invListCenterY = 210;

    private int craftImageX = 800,
            craftImageY = invY + invHeight / 2 - 38,
            craftImageWidth = 96,
            craftImageHeight = 96;

    private int resCountX = 1575, resCountY = 210;

    /**
     * Currently selected item (index)
     */
    private int selectedItem = 0;

    /**
     * Constructor
     *
     * @param handler
     */
    public CraftingScreen(Handler handler) {
        this.handler = handler;
        inventoryItems = new ArrayList<Item>();
        inventoryItems.add(Item.logItem);
        inventoryItems.add(Item.rockItem);
        inventoryItems.add(CraftableItem.woodItem);
        inventoryItems.add(CraftableItem.solidWoodItem);


        craftableItems = new ArrayList<CraftableItem>();
        craftableItems.add(CraftableItem.woodItem);
        craftableItems.add(CraftableItem.solidWoodItem);
        craftableItems.add(CraftableItem.chestItem);
    }

    /**
     * Functions executed on tick
     */
    public void tick() {
        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_C))
            active = !active;
        if (!active)
            return;
        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_ENTER))
            craftItem();
        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_W))
            selectedItem--;
        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_S))
            selectedItem++;
        if (selectedItem < 0)
            selectedItem = craftableItems.size() - 1;
        else if (selectedItem >= craftableItems.size())
            selectedItem = 0;
    }

    /**
     * Render component
     *
     * @param g
     */
    public void render(Graphics g) {
        if (!active)
            return;

        g.drawImage(Assets.craftingScreen, invX, invY);
        g.drawString("Press Enter", craftImageX + 50, craftImageY + 150);
        g.drawString("to craft", craftImageX + 50, craftImageY + 150 + invListSpacing);
        int len = craftableItems.size();
        if (len == 0) {
            return;
        }
        for (int i = -5; i < 6; i++) {
            if (selectedItem + i < 0 || selectedItem + i >= len)
                continue;
            if (i == 0) {
                g.drawString(">" + craftableItems.get(selectedItem + i).getName() + "<", craftListCenterX,
                        craftListCenterY + i * invListSpacing);
            } else {
                g.drawString(craftableItems.get(selectedItem + i).getName(), craftListCenterX,
                        craftListCenterY + i * invListSpacing);
            }
        }
        Item item = craftableItems.get(selectedItem);
        g.drawImage(item.getTexture(), craftImageX, craftImageY);


        for (int i = 0; i < inventoryItems.size(); i++) {
            g.drawString(inventoryItems.get(i).getName(), invListCenterX, invListCenterY + i * invListSpacing);
        }

        for (int i = 0; i < craftableItems.get(selectedItem).getLen(); i++) {
            if (craftableItems.get(selectedItem).getResources(i) <= handler.getWorld().getEntityManager().getPlayer().getInventory().getInventoryItems().get(i).getCount())
                g.drawString(" " + craftableItems.get(selectedItem).getResources(i), resCountX, resCountY + i * invListSpacing);
            else
                g.drawString(" " + craftableItems.get(selectedItem).getResources(i), resCountX, resCountY + i * invListSpacing);
        }

    }

    /**
     * Uses resources to craft an item and adds it to the player inventory
     */
    private void craftItem() {
        ArrayList<Item> InventoryItems = handler.getWorld().getEntityManager().getPlayer().getInventory().getInventoryItems();
        for (int j = 0; j < craftableItems.get(selectedItem).getLen(); j++) {
            if (craftableItems.get(selectedItem).getResources(j) <=
                    InventoryItems.get(j).getCount()) {
                for (int i = 0; i < craftableItems.get(selectedItem).getResources(j); i++) {
                    InventoryItems.get(j).setCount(InventoryItems.get(j).getCount() - 1);
				}
			}
			else return;
		}
		Item itemToAdd = craftableItems.get(selectedItem).createNew(1);
		handler.getWorld().getEntityManager().getPlayer().getInventory().addItem(itemToAdd);
	
	}
	
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
}
