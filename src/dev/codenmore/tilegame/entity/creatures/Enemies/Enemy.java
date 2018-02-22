package dev.codenmore.tilegame.entity.creatures.Enemies;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.Modifiers.Mod;
import dev.codenmore.tilegame.entity.creatures.Creature;
import dev.codenmore.tilegame.items.Item;
import dev.codenmore.tilegame.utils.Utils;

import java.awt.Rectangle;
import java.util.ArrayList;

public abstract class Enemy extends Creature {

	private long lastMovement, movementCooldown=1500, movementTimer = movementCooldown;
	private long lastAttackTimer, attackCooldown=800, attackTimer = attackCooldown;
	
	private Rectangle ar = new Rectangle();
	
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
		spawnItems();
		
		
	}
	
	public void getMovement() {
		xMove=0;
		yMove=0;
		
		int x = Utils.generateRandomInt(250);
		
		if (x<=25)
			yMove=-speed;
		if (x>25 && x<=50)
			yMove=speed;
		if (x>50 && x<=75)
			xMove=speed;
		if (x>75 && x<=100)
			xMove=-speed;
		if (x>100 && x<=125) {
			xMove=-speed;
			yMove=-speed;
		}
		if (x>125 && x<=150) {
			xMove=+speed;
			yMove=-speed;
		}
		if (x>150 && x<=175) {
			xMove=-speed;
			yMove=+speed;
		}
		if (x>175 && x<=200) {
			xMove=+speed;
			yMove=+speed;
		}
				
	}
	
	protected void generateMovement() {
		movementTimer += System.currentTimeMillis()- lastMovement;
		lastMovement = System.currentTimeMillis();
		if(movementTimer<=movementCooldown)
			return;
		getMovement();
		movementTimer=0;
	}
	
	protected void checkAttacks() {
		ar.x=(int) x + bounds.x - 6 - (int) handler.getGameCamera().getxOffset();
		ar.y=(int) y + bounds.y - 6 - (int) handler.getGameCamera().getyOffset();
		ar.width= bounds.width+12;
		ar.height=bounds.height+12;
		attackTimer += System.currentTimeMillis() - lastAttackTimer;
		lastAttackTimer=System.currentTimeMillis();
		if ( attackTimer < attackCooldown)
			return;
		if(ar.intersects(handler.getWorld().getEntityManager().getPlayer().getCollisionBounds(0f,0f))) {
			handler.getWorld().getEntityManager().getPlayer().hurt( (damage - (Math.min((handler.getWorld().getEntityManager().getPlayer().getArmor()),(damage-1)))));
			handler.getWorld().getEntityManager().getPlayer().setArmor(Math.max(handler.getWorld().getEntityManager().getPlayer().getArmor()-1, 0));
			attackTimer=0;
		}
	}
	
	
	public int getSpawnrate() {
		return spawnrate;
	}
	public void setSpawnrate(int spawnrate) {
		this.spawnrate = spawnrate;
	}
	
	private void spawnItems() {
		// Nur Zahlen im Bereich von 1 - 8 ebnutzen, da bei Schwierigkeit Easy nur Zhalen bis 8 generiert werden.
		int[] arr= new int[7]; 
		for(int i=0;i<6;i++) {
			arr[i]=Utils.generateRandomInt(spawnrate);
			}
		if (arr[0]==3||arr[0]==5||arr[0]==6)
			handler.getWorld().getItemManager().addItem(Item.swordItem.createNew((int)x,(int)y));
				
		if(arr[1]==3||arr[1]==5||arr[1]==6||arr[1]==2)
			handler.getWorld().getItemManager().addItem(Item.heartItem.createNew((int)x,(int)y));
				
		if(arr[2]==3||arr[2]==5||arr[2]==4||arr[2]==8||arr[2]==6||arr[2]==7)
			handler.getWorld().getItemManager().addItem(Item.arrowItem.createNew((int)x,(int)y));
		if(arr[5]==3||arr[5]==5||arr[5]==4)
			handler.getWorld().getItemManager().addItem(Item.arrowItem.createNew((int)x,(int)y));
				
		if(arr[3]==3||arr[3]==5||arr[3]==1)
			handler.getWorld().getItemManager().addItem(Item.armorItem.createNew((int)x,(int)y));
				
		if(arr[4]==7)
			handler.getWorld().getItemManager().addItem(Item.healthPlusItem.createNew((int) (x ),(int) ( y + bounds.height+100)));
				
		if(arr[5]==3)
			handler.getWorld().getItemManager().addItem(Item.bowItem.createNew((int) (x ),(int) ( y + bounds.height+100)));
	}
}
