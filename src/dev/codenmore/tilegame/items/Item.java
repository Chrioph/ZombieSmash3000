package dev.codenmore.tilegame.items;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.gfx.Assets;

public class Item {
	
	//Handler
	
	
	public static Item[] items = new Item[256];
	public static Item woodItem = new Item(Assets.wood,"Wood",0);
	public static Item rockItem = new Item(Assets.rock,"Rock",1);
	public static Item swordItem = new Item(Assets.sword,"Sword",2);
	public static Item heartItem = new Item(Assets.heart,"Heart",3);
	public static Item arrowItem = new Item(Assets.arrow,"Arrow",4);
	public static Item armorItem = new Item(Assets.armor,"Armor",5);
	
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
		count=1;
		
		bounds= new Rectangle(x, y, ITEMWIDTH, ITEMHEIGHT);
		
		items[id]=this;
	}
	
	public void tick() {
		if(handler.getWorld().getEntityManager().getPlayer().getCollisionBounds(0f, 0f).intersects(bounds)) {
			pickedUp=true;
			if(this.getId()!=2 && this.getId()!=3 && this.getId()!=4&& this.getId()!=5)
				handler.getWorld().getEntityManager().getPlayer().getInventory().addItem(this);
			if(this.getId()==2)
				handler.getWorld().getEntityManager().getPlayer().setDamage(handler.getWorld().getEntityManager().getPlayer().getDamage()+1);
			if(this.getId()==3)
				handler.getWorld().getEntityManager().getPlayer().setHealth(handler.getWorld().getEntityManager().getPlayer().getHealth()+1);
			if(this.getId()==4)
				handler.getWorld().getEntityManager().getPlayer().setAmmunition(handler.getWorld().getEntityManager().getPlayer().getAmmunition()+1);
			if(this.getId()==5)
				handler.getWorld().getEntityManager().getPlayer().setArmor(handler.getWorld().getEntityManager().getPlayer().getArmor()+1);
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
	
	public Item createNew(int x, int y) {
		Item i= new Item(texture,name,id);
		i.setPostion(x, y);		
		return i;
		
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
