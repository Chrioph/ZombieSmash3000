package dev.codenmore.tilegame.items;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import dev.codenmore.tilegame.gfx.Assets;

public class CraftableItem extends Item{
	
	private int len = 4;
	private int[] resources;
	public static CraftableItem woodItem = new CraftableItem(Assets.wood,"Wood", 2, true) ;
	public static CraftableItem solidWoodItem = new CraftableItem(Assets.solidWood,"Solid Wood", 3, true);
	public static CraftableItem chestItem = new CraftableItem(Assets.chest,"Chest", 5, true);

	public CraftableItem(BufferedImage texture, String name, int id, boolean placeable) {
		super(texture, name, id, placeable);
		resources= new int [len];
		initialiseResources();
	}
	
	private void initialiseResources() {
		for(int i=0;i<len;i++)
			resources[i] = 0;
		if (this.getId()==2) {
			resources[logItem.getId()]=2;
		}
		if (this.getId()==3) {
			resources[logItem.getId()]=3;
		}
		if(this.id==5) {
			resources[logItem.getId()]=1;
			resources[rockItem.getId()]=0;
			resources[woodItem.getId()]=0;
			resources[solidWoodItem.getId()]=0;
		}
	}

	public int getResources(int i) {
		return resources[i];
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	
}
