package dev.codenmore.tilegame.items;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.utils.Utils;

public class Item {
	
	//Handler

	//whenever a new item is created please increase the length of the ressources array in the class craftable items by one and add it to inventoryItems in the inventory class
	//items used for crafting get ids up to 127, other items ids start at 128
	public static Item[] items = new Item[256];
	public static Item logItem = new Item(Assets.log,"Log",0);
	public static Item rockItem = new Item(Assets.rock,"Rock",1);
	//public static CraftableItem woodItem = new CraftableItem(Assets.wood,"Wood", 3) ;
	//public static CraftableItem solidWoodItem = new CraftableItem(Assets.solidWood,"Solid Wood", 4);


	public static Item swordItem = new Item(Assets.sword,"Sword",128);
	public static Item heartItem = new Item(Assets.heart,"Heart",129);
	public static Item arrowItem = new Item(Assets.arrow,"Arrow",130);
	public static Item armorItem = new Item(Assets.armor,"Armor",131);
	public static Item healthPlusItem = new Item(Assets.healthPlus,"Health Plus",132);
	public static Item bowItem = new Item(Assets.bow,"Bow",133);
	public static Item seedItem = new Item(Assets.seeds, "Seed", 134);
	
	
	//Class
	
	public static final int ITEMWIDTH=64, ITEMHEIGHT=64;
	
	protected Handler handler;
	protected BufferedImage texture;
	protected String name;
	protected final int id;
	
	protected Rectangle bounds;
	
	protected int  x , y , count;
	protected boolean pickedUp=false;
	
	public Item(BufferedImage texture, String name, int id) {
		this.texture=texture;
		this.name=name;
		this.id=id;
		count=0;
		this.handler=handler;
		
		bounds= new Rectangle((int)(x), (int)(y), ITEMWIDTH, ITEMHEIGHT);
		
		items[id]=this;
	}
	
	
	
	public void tick() {
		if(handler.getWorld().getEntityManager().getPlayer().getCollisionBounds(0f, 0f).intersects(this.getCollisionBounds(0,0))) {
			if((!(this.getId()==armorItem.getId() && handler.getWorld().getEntityManager().getPlayer().getArmor()>=handler.getWorld().getEntityManager().getPlayer().getMaxArmor()) &&
					!(this.getId()==heartItem.getId() && handler.getWorld().getEntityManager().getPlayer().getHealth()>=handler.getWorld().getEntityManager().getPlayer().getMaxHealth())))
				pickedUp=true;
			if(this.getId()!=swordItem.getId() && this.getId()!=heartItem.getId() && this.getId()!=arrowItem.getId()
					&& this.getId()!=armorItem.getId() && this.getId()!=healthPlusItem.getId() &&this.getId()!=bowItem.getId())
				handler.getWorld().getEntityManager().getPlayer().getInventory().addItem(this);
			if(this.getId()==swordItem.getId())
				handler.getWorld().getEntityManager().getPlayer().setDamage(handler.getWorld().getEntityManager().getPlayer().getDamage()+1);
			if(this.getId()==heartItem.getId()&& pickedUp==true)
				handler.getWorld().getEntityManager().getPlayer().setHealth(handler.getWorld().getEntityManager().getPlayer().getHealth()+1);
			if(this.getId()==arrowItem.getId())
				handler.getWorld().getEntityManager().getPlayer().setAmmunition(handler.getWorld().getEntityManager().getPlayer().getAmmunition()+1);
			if(this.getId()==armorItem.getId() && pickedUp==true)
				handler.getWorld().getEntityManager().getPlayer().setArmor(handler.getWorld().getEntityManager().getPlayer().getArmor()+1);
			if(this.getId()==healthPlusItem.getId() )
				handler.getWorld().getEntityManager().getPlayer().setMaxHealth(handler.getWorld().getEntityManager().getPlayer().getMaxHealth()+1);
			if(this.getId()==bowItem.getId() )
				handler.getWorld().getEntityManager().getPlayer().setRangedDamage(handler.getWorld().getEntityManager().getPlayer().getRangedDamage()+1);
		}
		
	}
	
	public void render(Graphics g ) {
		if(handler==null)
			return;
		render(g, (int) (x - handler.getGameCamera().getxOffset()) , (int) (y - handler.getGameCamera().getyOffset()));
	}
	
	public void render(Graphics g , int x, int y ){
		g.drawImage(texture, x, y, ITEMWIDTH, ITEMHEIGHT,null);
		
	}

	
	
	public void setPostion(int x, int y ) {
		this.x=x;
		this.y=y;
		bounds.x=x;
		bounds.y=y;
	}
	
	public Item createNew(int count) {
		
	
		Item i= new Item(texture,name,id);
		i.setCount(count);
		i.setPickedUp(true);
		return i;
		
	}
	
	public Item createNew() {
		Item i= new Item(texture,name,id);
		return i;
		
	}
	
	protected boolean collisionWithTile(int x , int y ) {
		
		return handler.
				getWorld().
				getTile(x, y).
				isSolid();
		
	}
		
	
	public Rectangle getCollisionBounds(float xOffset, float yOffset) {
		return new Rectangle ((int) (bounds.x + xOffset - handler.getGameCamera().getxOffset()),(int) (bounds.y + yOffset - handler.getGameCamera().getyOffset()), bounds.width, bounds.height );
	}
	
	//Getters and Setters
	
	
	
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

	public BufferedImage getTexture() {
		return texture;
	}

	public void setTexture(BufferedImage texture) {
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
