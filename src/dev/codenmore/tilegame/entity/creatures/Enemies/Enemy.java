package dev.codenmore.tilegame.entity.creatures.Enemies;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.Modifiers.Mod;
import dev.codenmore.tilegame.entity.creatures.Creature;
import dev.codenmore.tilegame.items.Item;
import dev.codenmore.tilegame.utils.Utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

public abstract class Enemy extends Creature {

	private long lastMovement, movementCooldown=1500, movementTimer = movementCooldown;
	private long lastAttackTimer, attackCooldown=800, attackTimer = attackCooldown;
	
	private Rectangle ar = new Rectangle();
	protected int distToPlayer;
	private int playerDirection=0;
	protected int aggroRange;
	
    public Enemy(Handler handler , float x, float y, int width, int height) {
        super(handler, x, y,width,height);
        damage=DEFAULT_CREATURE_DAMAGE;
        speed=DEFAULT_SPEED;
        xMove=0;
        yMove=0;
        spawnrate=20;
    }
   
    protected void renderHealthbar(Graphics g) {
    	g.setColor(Color.BLACK);
    	g.fillRect((int)(x - handler.getGameCamera().getxOffset() + bounds.width - 5*maxHealth-4),(int) (y-handler.getGameCamera().getyOffset()-15), 10*maxHealth+8, 10);
    	g.fillRect((int)(x - handler.getGameCamera().getxOffset() + bounds.width - 5*maxHealth),(int) (y-handler.getGameCamera().getyOffset()-19), 10*maxHealth, 18);
    	g.setColor(Color.RED);
    	g.fillRect((int)(x - handler.getGameCamera().getxOffset() + bounds.width - 5*maxHealth),(int) (y-handler.getGameCamera().getyOffset()-15), 10*maxHealth, 10);
    	g.setColor(Color.GREEN);
    	g.fillRect((int)(x - handler.getGameCamera().getxOffset() + bounds.width - 5*maxHealth),(int) (y-handler.getGameCamera().getyOffset()-15), 10*health, 10);
    	
    }
    
    
	public void die() {
		this.active=false;
		spawnItems();
		
		
	}
	
	private void getMovement() {
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
	
	private void getAggroMovement() {
		
		xMove=0;
		yMove=0;
		getPlayerDirection();
		if (playerDirection==4)
			xMove=-speed;
		if (playerDirection==6)
			xMove=speed;
		if (playerDirection==7)
			yMove=-speed;
		if (playerDirection==8)
			yMove=speed;
		if (playerDirection==0) {
			yMove=-speed;
			xMove=-speed;
		}
		if (playerDirection==1) {
			yMove=speed;
			xMove=-speed;
		}
		if (playerDirection==2) {
			yMove=-speed;
			xMove=speed;
		}
		if (playerDirection==3) {
			yMove=speed;
			xMove=speed;
		}
	}
	
	
	private void getPlayerDirection() {
		//Direction 0 : oben links
		//Direction 1 : unten links
		//Direction 2 : oben rechts
		//Direction 3 : unten rechts
		//Direction 4 : links
		//Direction 6 : rechts
		//Direction 7 : oben
		//Direction 8 : unten
		if(x-handler.getWorld().getEntityManager().getPlayer().getX()<0)  		//rechts
			playerDirection=2;
		else if (x-handler.getWorld().getEntityManager().getPlayer().getX()>=2*-speed&&x-handler.getWorld().getEntityManager().getPlayer().getX()<=2*speed) //mitte
				playerDirection=7;
		else 																	//links
				playerDirection=0;
				
				
		if(y-handler.getWorld().getEntityManager().getPlayer().getY()<0)		//unten
				playerDirection+=1;
		else if (y-handler.getWorld().getEntityManager().getPlayer().getY()>=2*-speed&&y-handler.getWorld().getEntityManager().getPlayer().getY()<=2*speed)	//mitte
			playerDirection+=4;
		else 																	//oben
			playerDirection+=0;
	}
	
	protected void generateMovement() {
		if (distToPlayer<=aggroRange) {
			getAggroMovement();
			return;
			
		}
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
			handler.getWorld().getEntityManager().getPlayer().hurt( (Math.max(0,damage - (Math.min((handler.getWorld().getEntityManager().getPlayer().getArmor()),(damage-1))))));
			handler.getWorld().getEntityManager().getPlayer().setArmor(Math.max(handler.getWorld().getEntityManager().getPlayer().getArmor()-1, 0));
			handler.getWorld().getEntityManager().getPlayer().knockback(playerDirection);
			handler.getWorld().getEntityManager().getPlayer().setKnockbackCounter(7);
			attackTimer=0;
		}
	}
	
	public void knockback() {
		xMove=0;
		yMove=0;
		if(playerDirection==0) {
			xMove=5.0f;
			yMove=5.0f;
		}
		if(playerDirection==1) {
			xMove=5.0f;
			yMove=-5.0f;
		}
		if(playerDirection==2) {
			xMove=-5.0f;
			yMove=5.0f;
		}
		if(playerDirection==3) {
			xMove=-5.0f;
			yMove=-5.0f;
		}
		if(playerDirection==4) {
			xMove=5.0f;
		}
		if(playerDirection==6) {
			xMove=-5.0f;
		}
		if(playerDirection==7) {
			yMove=5.0f;
		}
		if(playerDirection==8) {
			yMove=-5.0f;
		}
		move();
		
	}
	
	protected void checkKnockback() {
		if(knockbackCounter>0) {
			knockback();
			knockbackCounter--;
		}
		
		if(!(knockbackCounter>0))
			generateMovement();
	}
	
	protected boolean renderHurtAnimation() {
		return ar.intersects(handler.getWorld().getEntityManager().getPlayer().getCollisionBounds(0f,0f));
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
			handler.getWorld().getItemManager().addItem(Item.swordItem.createNew(), (int)x,(int)y);
				
		if(arr[1]==3||arr[1]==5||arr[1]==6||arr[1]==2||arr[1]==15||arr[1]==16||arr[1]==12)
			handler.getWorld().getItemManager().addItem(Item.heartItem.createNew(), (int)x,(int)y);
				
		if(arr[2]==3||arr[2]==5||arr[2]==4||arr[2]==8||arr[2]==6||arr[2]==7||arr[2]==13||arr[2]==15||arr[2]==14||arr[2]==18||arr[2]==16||arr[2]==17)
			handler.getWorld().getItemManager().addItem(Item.arrowItem.createNew(), (int)x,(int)y);
		if(arr[5]==3||arr[5]==5||arr[5]==4||arr[5]==13||arr[5]==15||arr[5]==14)
			handler.getWorld().getItemManager().addItem(Item.arrowItem.createNew(),(int)x,(int)y);
				
		if(arr[3]==3||arr[3]==5||arr[3]==1||arr[3]==7||arr[3]==13)
			handler.getWorld().getItemManager().addItem(Item.armorItem.createNew(), (int)x,(int)y);
				
		if(arr[4]==7)
			handler.getWorld().getItemManager().addItem(Item.healthPlusItem.createNew(), (int)x,(int)y);
				
		if(arr[5]==3)
			handler.getWorld().getItemManager().addItem(Item.bowItem.createNew(), (int)x,(int)y);
	}

	
}
