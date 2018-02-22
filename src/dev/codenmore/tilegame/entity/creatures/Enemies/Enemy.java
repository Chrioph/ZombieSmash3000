package dev.codenmore.tilegame.entity.creatures.Enemies;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.Modifiers.Mod;
import dev.codenmore.tilegame.entity.creatures.Creature;
import dev.codenmore.tilegame.items.Item;
import dev.codenmore.tilegame.utils.Utils;

import java.util.ArrayList;

public abstract class Enemy extends Creature {

	
	
    public Enemy(Handler handler , float x, float y, int width, int height) {
        super(handler, x, y,width,height);
        damage=DEFAULT_CREATURE_DAMAGE;
        speed=DEFAULT_SPEED;
        xMove=0;
        yMove=0;
        spawnrate=10;
    }
   
	public void die() {
		this.active=false;
		int[] arr= new int[7]; 
		for(int i=0;i<6;i++) {
			arr[i]=Utils.generateRandomInt(spawnrate);
		}
		if (arr[0]==3||arr[0]==5||arr[0]==6)
			handler.getWorld().getItemManager().addItem(Item.swordItem.createNew((int) (x ),(int) ( y  + bounds.height+100)));
		
		if(arr[1]==3||arr[1]==5||arr[1]==6||arr[1]==2)
			handler.getWorld().getItemManager().addItem(Item.heartItem.createNew((int) x,(int) ( y + bounds.height+100)));
		
		if(arr[2]==3||arr[2]==5||arr[2]==4||arr[2]==8||arr[2]==6||arr[2]==7)
			handler.getWorld().getItemManager().addItem(Item.arrowItem.createNew((int) (x ),(int) ( y + bounds.height+100)));
		if(arr[5]==3||arr[5]==5||arr[5]==4)
			handler.getWorld().getItemManager().addItem(Item.arrowItem.createNew((int) (x ),(int) ( y  + bounds.height+100)));
		
		if(arr[3]==3||arr[3]==5||arr[3]==1)
			handler.getWorld().getItemManager().addItem(Item.armorItem.createNew((int) (x) ,(int) ( y + bounds.height+100)));
		
		if(arr[4]==7)
			handler.getWorld().getItemManager().addItem(Item.healthPlusItem.createNew((int) (x ),(int) ( y + bounds.height+100)));
	}
	public int getSpawnrate() {
		return spawnrate;
	}
	public void setSpawnrate(int spawnrate) {
		this.spawnrate = spawnrate;
	}
}
