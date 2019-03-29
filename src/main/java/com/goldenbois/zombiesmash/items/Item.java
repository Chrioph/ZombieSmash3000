package com.goldenbois.zombiesmash.items;

import com.goldenbois.zombiesmash.entity.creatures.Player;
import org.newdawn.slick.*;
import java.io.Serializable;

import com.goldenbois.zombiesmash.Handler;
import com.goldenbois.zombiesmash.gfx.Assets;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;

public class Item implements Serializable {
	
	//Handler

	//whenever a new item(usd for crafting) is created please increase the length of the ressources array in the class craftable items by one and add it to inventoryItems in the inventory class
	//items used for crafting or items which are placeable get ids up to 127, other items ids start at 128
	public static Item[] items = new Item[256];
	public static Item logItem = new Item(Assets.log,"Log",0, false);
	public static Item rockItem = new Item(Assets.rock,"Rock",1, false);
	//public static CraftableItem woodItem = new CraftableItem(Assets.wood,"Wood", 2) ;
	//public static CraftableItem solidWoodItem = new CraftableItem(Assets.solidWood,"Solid Wood", 3);
	public static Item seedItem = new Item(Assets.seeds, "Seed", 4, true);
	//public static CraftableItem chestItem = new CraftableItem(Assets.chest,"Chest", 5, true);


	public static Item swordItem = new Item(Assets.sword,"Sword",128, false);
	public static Item heartItem = new Item(Assets.heart,"Heart",129, false);
	public static Item arrowItem = new Item(Assets.arrow,"Arrow",130, false);
	public static Item armorItem = new Item(Assets.armor,"Armor",131, false);
	public static Item healthPlusItem = new Item(Assets.healthPlus,"Health Plus",132, false);
	public static Item bowItem = new Item(Assets.bow,"Bow",133, false);
	
	
	//Class
	
	public static final int ITEMWIDTH=64, ITEMHEIGHT=64;
	
	protected Handler handler;
	protected transient Image texture;
	protected String name;
	protected final int id;
	protected boolean placeable;
	
	protected Rectangle bounds;
	
	protected int  x , y , count;
	protected boolean pickedUp=false;
	
	public Item(Image texture, String name, int id, boolean placeable) {
		this.texture=texture;
		this.placeable=placeable;
		this.name=name;
		this.id=id;
		count=0;
		this.handler=handler;
		
		bounds= new Rectangle((int)(x), (int)(y), ITEMWIDTH, ITEMHEIGHT);
		
		items[id]=this;
	}
	
	
	
	public void tick() {
		Player player = handler.getWorld().getEntityManager().getPlayer();
		if(player.getCollisionBounds(0f, 0f).intersects(this.getCollisionBounds(0,0))) {
			if((!(this.getId()==armorItem.getId() && player.getArmor()>=player.getMaxArmor()) &&
					!(this.getId()==heartItem.getId() && player.getHealth()>=player.getMaxHealth())))
				pickedUp=true;
			if(this.getId()!=swordItem.getId() && this.getId()!=heartItem.getId() && this.getId()!=arrowItem.getId()
					&& this.getId()!=armorItem.getId() && this.getId()!=healthPlusItem.getId() &&this.getId()!=bowItem.getId())
				player.getInventory().addItem(this);
			if(this.getId()==swordItem.getId())
				player.setDamage(player.getDamage()+1);
			if(this.getId()==heartItem.getId()&& pickedUp==true)
				player.setHealth(player.getHealth()+1);
			if(this.getId()==arrowItem.getId())
				player.setAmmunition(player.getAmmunition()+1);
			if(this.getId()==armorItem.getId() && pickedUp==true)
				player.setArmor(player.getArmor()+1);
			if(this.getId()==healthPlusItem.getId() )
				player.setMaxHealth(player.getMaxHealth()+1);
			if(this.getId()==bowItem.getId() )
				player.setRangedDamage(player.getRangedDamage()+1);
		}
		
	}
	
	public void render(Graphics g ) {
		if(handler==null)
			return;
		render(g, (int) (x - handler.getGameCamera().getxOffset()) , (int) (y - handler.getGameCamera().getyOffset()));
	}
	
	public void render(Graphics g , int x, int y ){
		g.drawImage(texture, x, y);
		
	}

	
	
	public void setPosition(int x, int y ) {
		this.x=x;
		this.y=y;
		bounds.setSize(x, y);
	}
	
	public Item createNew(int count) {
		
	
		Item i= new Item(texture,name,id,placeable);
		i.setCount(count);
		return i;
		
	}
	
	public Item createNew() {
		Item i= new Item(texture,name,id,placeable);
		return i;
		
	}
	
	protected boolean collisionWithTile(int x , int y ) {
		
		return handler.
				getWorld().
				getTile(x, y).
				isSolid();
		
	}
		
	
	public Rectangle getCollisionBounds(float xOffset, float yOffset) {
		return new Rectangle ((int) (bounds.getX() + xOffset - handler.getGameCamera().getxOffset()),(int) (bounds.getY() + yOffset - handler.getGameCamera().getyOffset()), bounds.getWidth(), bounds.getHeight() );
	}
	
	//Getters and Setters
	
	public boolean isPlaceable(){
	    return placeable;
    }
	
	public Handler getHandler() {
		return handler;
	}

	public boolean isPickedUp() {
		return pickedUp;
	}

	public void setPickedUp(boolean pickedUp) {
		this.pickedUp = pickedUp;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public Image getTexture() {
		return texture;
	}

	public void setTexture(Image texture) {
		this.texture = texture;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getId() {
		return id;
	}

	
}
