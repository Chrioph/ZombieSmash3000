package dev.codenmore.tilegame.items;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import dev.codenmore.tilegame.gfx.Assets;

public class CraftableItem extends Item{
	
	private int len = 2;
	private int[] resources;
	public static CraftableItem woodItem = new CraftableItem(Assets.wood,"Wood", 8) ;
	public static CraftableItem solidWoodItem = new CraftableItem(Assets.solidWood,"Solid Wood", 9);

	public CraftableItem(BufferedImage texture, String name, int id) {
		super(texture, name, id);
		resources= new int [len];
		initialiseResources();
	}
	
	private void initialiseResources() {
		if (this.getId()==8) {
			resources[0]=2;
			resources[1]=0;
		}
		if (this.getId()==9) {
			resources[0]=3;
			resources[1]=0;
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
