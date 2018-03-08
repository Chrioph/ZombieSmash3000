package dev.codenmore.tilegame.items;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import dev.codenmore.tilegame.gfx.Assets;

public class CraftableItem extends Item{
	
	private int len = 4;
	private int[] resources;
	public static CraftableItem woodItem = new CraftableItem(Assets.wood,"Wood", 3, true) ;
	public static CraftableItem solidWoodItem = new CraftableItem(Assets.solidWood,"Solid Wood", 4, true);

	public CraftableItem(BufferedImage texture, String name, int id, boolean placeable) {
		super(texture, name, id, placeable);
		resources= new int [len];
		initialiseResources();
	}
	
	private void initialiseResources() {
		for(int i=0;i<len;i++)
			resources[i] = 0;
		if (this.getId()==3) {
			resources[logItem.getId()]=2;
		}
		if (this.getId()==4) {
			resources[logItem.getId()]=3;
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
