package dev.codenmore.tilegame.entity.creatures;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;



import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.gfx.Animation;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.items.Item;
import dev.codenmore.tilegame.utils.Utils;

public class Zombie extends Creature {
	
	private long lastMovement, movementCooldown=1500, movementTimer = movementCooldown;
	private long lastAttackTimer, attackCooldown=800, attackTimer = attackCooldown;
	
	private Animation animDown;
	private Animation animUp;
	private Animation animLeft;
	private Animation animRight;
	private Animation animADown;
	private Animation animAUp;
	private Animation animALeft;
	private Animation animARight;
	
	private Rectangle ar = new Rectangle();
	
	public Zombie(Handler handler , float x, float y) {
		super(handler ,x, y,Creature.DEFAULT_CREATURE_WIDTH,Creature .DEFAULT_CREATURE_HEIGHT);
		this.speed= 1.5f;
		health =5;
		damage=3;
		bounds.x=18;
		bounds.y =26;
		bounds.width =28;
		bounds.height =40;
		
		animDown = new Animation(500, Assets.zombie_down);
		animUp = new Animation(500,Assets.zombie_up);
		animLeft= new Animation ( 500, Assets.zombie_left);
		animRight = new Animation ( 500, Assets.zombie_right);
		
		animAUp= new Animation(400, Assets.zaUp);
		animADown= new Animation(400, Assets.zaDown);
		animALeft= new Animation(400, Assets.zaLeft);
		animARight= new Animation(400, Assets.zaRight);
	}
	
	public void tick() {
		generateMovement();
		checkAttacks();
		animDown.tick();
		animUp.tick();
		animLeft.tick();
		animRight.tick();
		animADown.tick();
		animAUp.tick();
		animALeft.tick();
		animARight.tick();
		
		move();
	}
	public void die() {
		this.active=false;
		int[] arr= new int[7]; 
		for(int i=0;i<6;i++) {
			arr[i]=Utils.generateRandomInt(10);
		}
		if (arr[0]==3||arr[0]==5||arr[0]==6)
			handler.getWorld().getItemManager().addItem(Item.swordItem.createNew((int) (x ),(int) ( y  + bounds.height+100)));
		
		if(arr[1]==3||arr[1]==5||arr[1]==9||arr[1]==2)
			handler.getWorld().getItemManager().addItem(Item.heartItem.createNew((int) x,(int) ( y + bounds.height+100)));
		
		if(arr[2]==3||arr[2]==5||arr[2]==4||arr[2]==8||arr[2]==6||arr[2]==10)
			handler.getWorld().getItemManager().addItem(Item.arrowItem.createNew((int) (x ),(int) ( y + bounds.height+100)));
		if(arr[5]==3||arr[5]==5||arr[5]==4)
			handler.getWorld().getItemManager().addItem(Item.arrowItem.createNew((int) (x ),(int) ( y  + bounds.height+100)));
		
		if(arr[3]==3||arr[3]==5||arr[3]==1)
			handler.getWorld().getItemManager().addItem(Item.armorItem.createNew((int) (x) ,(int) ( y + bounds.height+100)));
		
		if(arr[4]==7)
			handler.getWorld().getItemManager().addItem(Item.healthPlusItem.createNew((int) (x ),(int) ( y + bounds.height+100)));
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
	
	private void generateMovement() {
		movementTimer += System.currentTimeMillis()- lastMovement;
		lastMovement = System.currentTimeMillis();
		if(movementTimer<=movementCooldown)
			return;
		getMovement();
		movementTimer=0;
	}
	
	private void checkAttacks() {
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
	
	
	public void render(Graphics g) {
		super.render(g);
		g.drawImage(getCurrentAnimationFrame(),(int)(x-handler.getGameCamera().getxOffset()),(int)(y-handler.getGameCamera().getyOffset()),width,height,null);
		
	}
	
	private BufferedImage getCurrentAnimationFrame() {
		if (xMove<0)
			return animLeft.getCurrentFrame();
		if (xMove>0)
			return animRight.getCurrentFrame();
		if (yMove<0)
			return animUp.getCurrentFrame();
		if (yMove>0)
			return animDown.getCurrentFrame();
		
		
		if (xAttack<0)
			return animALeft.getCurrentFrame();
		if (xAttack>0)
			return animARight.getCurrentFrame();
		if (yAttack<0)
			return animAUp.getCurrentFrame();
		if (yAttack>0)
			return animADown.getCurrentFrame();
		
		
		else return Assets.zombie;
	}
	
}
