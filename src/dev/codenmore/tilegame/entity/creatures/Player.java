package dev.codenmore.tilegame.entity.creatures;


import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;


import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.entity.Entity;
import dev.codenmore.tilegame.gfx.Animation;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.inventory.Inventory;


public class Player extends Creature{

	private Animation animDown;
	private Animation animUp;
	private Animation animLeft;
	private Animation animRight;
	private Animation animADown;
	private Animation animAUp;
	private Animation animALeft;
	private Animation animARight;
	
	private int ammunition=0;
	
	private boolean rangedToggled=false;
	
	private long lastAttackTimer, attackCooldown=800, attackTimer = attackCooldown;
	
	private Inventory inventory;
	
	public Player(Handler handler,float x, float y) {
		super(handler ,x, y,Creature.DEFAULT_CREATURE_WIDTH,Creature .DEFAULT_CREATURE_HEIGHT);
		
		health=10;
		
		bounds.x=20;
		bounds.y =28;
		bounds.width =24;
		bounds.height =36;
		
		
		//Animation
		
		animDown = new Animation(500, Assets.player1_down);
		animUp = new Animation(500,Assets.player1_up);
		animLeft= new Animation ( 500, Assets.player1_left);
		animRight = new Animation ( 500, Assets.player1_right);
		
		animAUp= new Animation(400, Assets.aUp);
		animADown= new Animation(400, Assets.aDown);
		animALeft= new Animation(400, Assets.aLeft);
		animARight= new Animation(400, Assets.aRight);
		
		inventory=new Inventory(handler);
		
	}

	@Override
	public void tick() {
		animDown.tick();
		animUp.tick();
		animLeft.tick();
		animRight.tick();
		animADown.tick();
		animAUp.tick();
		animALeft.tick();
		animARight.tick();
		
		
		getInput();
		
		move();
		handler.getGameCamera().centerOnEntity(this);
		
		checkToggledRangeAttacks();
		checkAttacks();
		
		inventory.tick();
	}
	private void checkToggledRangeAttacks() {
		if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_T)) {
			rangedToggled=!rangedToggled;
			System.out.println("Range " + rangedToggled);
		}
		
	}
	private void checkAttacks() {
		attackTimer += System.currentTimeMillis() - lastAttackTimer;
		lastAttackTimer=System.currentTimeMillis();
		if ( attackTimer < attackCooldown)
			return;
		if(inventory.isActive())
			return;
		if(rangedToggled==false) {
			Rectangle cb = getCollisionBounds(0,0);
			
			Rectangle ar =new Rectangle();
			int arSize = 50;
			ar.width = arSize;
			ar.height= arSize;
			
			if (handler.getKeyManager().aUp) {
				ar.x = cb.x + cb.width/2 + arSize/2;
				ar.y = cb.y  -arSize;
			}
			else if (handler.getKeyManager().aDown) {
				ar.x = cb.x + cb.width/2 + arSize/2;
				ar.y = cb.y  +cb.height;
			}
			else if (handler.getKeyManager().aLeft) {
				ar.x = cb.x  -arSize;
				ar.y = cb.y  + cb.height/2 -arSize / 2;
			}
			else if (handler.getKeyManager().aRight) {
				ar.x = cb.x + cb.width;
				ar.y = cb.y  + cb.height/2 -arSize / 2;
			}
			else {
				return;
			}
			
		}
		else {
			if (handler.getKeyManager().aRight)
				handler.getWorld().getEntityManager().addProjectile(new Arrow(handler,(int) x + 100 ,(int) (y), 
						DEFAULT_CREATURE_WIDTH, DEFAULT_CREATURE_HEIGHT, 0  ));
			if (handler.getKeyManager().aDown)
				handler.getWorld().getEntityManager().addProjectile(new Arrow(handler,(int) x  ,(int) (y + 100), 
						DEFAULT_CREATURE_WIDTH, DEFAULT_CREATURE_HEIGHT, 1  ));
			if (handler.getKeyManager().aLeft)
				handler.getWorld().getEntityManager().addProjectile(new Arrow(handler,(int) x - 100 ,(int) (y), 
						DEFAULT_CREATURE_WIDTH, DEFAULT_CREATURE_HEIGHT, 2 ));
			if (handler.getKeyManager().aUp)
				handler.getWorld().getEntityManager().addProjectile(new Arrow(handler,(int) x ,(int) (y - 100), 
						DEFAULT_CREATURE_WIDTH, DEFAULT_CREATURE_HEIGHT, 3  ));
		}
		attackTimer = 0;
		Rectangle ar= new Rectangle(0,0,0,0);
		for (Entity e : handler.getWorld().getEntityManager().getEntities()) {
			if (e.equals(this))
				continue;
			if ( e.getCollisionBounds(0, 0).intersects(ar)) {
				e.hurt(damage);
				
				return;
			}
		}
	}
	
	//GameOver Fenster �ffnen
	public void die() {
		System.out.println("You Lose");
	}
	
	private void getInput() {
		xMove=0;
		yMove=0;
		
		if(inventory.isActive())
			return;
		xAttack=0;
		yAttack=0;
		if (handler.getKeyManager().up)
			yMove=-speed;
		if (handler.getKeyManager().down)
			yMove=speed;
		if (handler.getKeyManager().right)
			xMove=speed;
		if (handler.getKeyManager().left)
			xMove=-speed;
		
		
		if (handler.getKeyManager().aUp)
			yAttack=-1;
		if (handler.getKeyManager().aDown)
			yAttack=1;
		if (handler.getKeyManager().aRight)
			xAttack=1;
		if (handler.getKeyManager().aLeft)
			xAttack=-1;;
	}
	

	@Override
	public void render(Graphics g) {
		g.drawImage(getCurrentAnimationFrame(),(int)(x-handler.getGameCamera().getxOffset()),(int)(y-handler.getGameCamera().getyOffset()),width,height,null);
			
	}
	
	public void postRender(Graphics g) {
		inventory.render(g);
	}
	
	public boolean isPlayer() {
		return true;
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
		
		
		else return Assets.player1;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public int getAmmunition() {
		return ammunition;
	}

	public void setAmmunition(int ammunition) {
		this.ammunition = ammunition;
	}
	

}
