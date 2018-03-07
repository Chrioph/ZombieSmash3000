package dev.codenmore.tilegame.entity.creatures;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;


import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.Settings;
import dev.codenmore.tilegame.crafting.CraftingScreen;
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
	
	private int knockbackCounter;
	private int ammunition=5;
	private int armor=4;
	private int maxArmor=5;
	private int rangedDamage=2;
	private boolean isDead=false;
	private int enemyDirection;
	

	private boolean rangedToggled=false;
	
	private long lastAttackTimer, attackCooldown=800, attackTimer = attackCooldown;
	private Arrow arrow;
	
	private Inventory inventory;
	private CraftingScreen craftingScreen;
	
	public Player(Handler handler,float x, float y) {
		super(handler ,x, y,Creature.DEFAULT_CREATURE_WIDTH,Creature .DEFAULT_CREATURE_HEIGHT);
		maxHealth=10;
		health=10;
		damage=3;
		bounds.x=20;
		bounds.y =5;
		bounds.width =22;
		bounds.height =59;
		
		
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
		craftingScreen=new CraftingScreen(handler);
	}

	@Override
	public void tick() {
		if(knockbackCounter>0) {
			knockback(enemyDirection);
			knockbackCounter--;
		}
		animDown.tick();
		animUp.tick();
		animLeft.tick();
		animRight.tick();
		animADown.tick();
		animAUp.tick();
		animALeft.tick();
		animARight.tick();
		checkAlive();
		if(!(knockbackCounter>0))
			getInput();
		
		move();
		handler.getGameCamera().centerOnEntity(this);
		
		checkToggledRangeAttacks();
		checkAttacks();
		
		
		inventory.tick();
		craftingScreen.tick();
	}
	
	 private void checkAlive() {
		 if (health<=0) {
			 health=0;
			 isDead=true;
		 }
		 
	 }
	 
	private void checkToggledRangeAttacks() {
		if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_T)) {
			rangedToggled=!rangedToggled;
		}
		
	}
	
	private void checkAttacks() {
		attackTimer += System.currentTimeMillis() - lastAttackTimer;
		lastAttackTimer=System.currentTimeMillis();
		if ( attackTimer < attackCooldown)
			return;
		if(inventory.isActive())
			return;
		Rectangle ar =new Rectangle();
		if(rangedToggled==false) {
			Rectangle cb = getCollisionBounds(0,0);
			
			
			int arSize = 50;
			ar.width = arSize;
			ar.height= arSize;
			
			if (handler.getKeyManager().aUp) {
				ar.x = cb.x + cb.width/2 - arSize/2;
				ar.y = cb.y  -arSize;
			}
			else if (handler.getKeyManager().aDown) {
				ar.x = cb.x + cb.width/2 - arSize/2;
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
		else 
			if(ammunition > 0){
				if (handler.getKeyManager().aRight) {
					arrow = new Arrow(handler,(int) x+40 ,(int) (y), DEFAULT_CREATURE_WIDTH, DEFAULT_CREATURE_HEIGHT, 0 , rangedDamage );
					handler.getWorld().getEntityManager().addProjectile(arrow);
					ammunition -= 1;
					ar=arrow.getBounds();
				}
				else if (handler.getKeyManager().aDown) {
					arrow = new Arrow(handler,(int) x ,(int) (y + 64), DEFAULT_CREATURE_WIDTH, DEFAULT_CREATURE_HEIGHT, 1 , rangedDamage );
					handler.getWorld().getEntityManager().addProjectile(arrow);
					ammunition -= 1;
					ar=arrow.getBounds();
				}
				else if (handler.getKeyManager().aLeft) {
					arrow = new Arrow(handler,(int) x-40 ,(int) (y), DEFAULT_CREATURE_WIDTH, DEFAULT_CREATURE_HEIGHT, 2 , rangedDamage );
					handler.getWorld().getEntityManager().addProjectile(arrow);
					ammunition -= 1;
					ar=arrow.getBounds();
				}
				else if (handler.getKeyManager().aUp) {
					arrow = new Arrow(handler,(int) x ,(int) (y - 49), DEFAULT_CREATURE_WIDTH, DEFAULT_CREATURE_HEIGHT, 3 , rangedDamage );
					handler.getWorld().getEntityManager().addProjectile(arrow);
					ammunition -= 1;
					ar=arrow.getBounds();
					
				}
				
				
		}
		attackTimer = 0;

		for (Entity e : handler.getWorld().getEntityManager().getEntities()) {
			if (e.equals(this))
				continue;
			if ( e.getCollisionBounds(0, 0).intersects(ar)) {
				e.hurt(damage);
				e.knockback();
				e.setKnockbackCounter(7);
				return;
			}
		}
	}
	
	//GameOver Fenster �ffnen
	public void die() {
	}
	
	public void hurtAnimation(Graphics g) {
		g.setColor(Color.RED);
		g.fillRect(0, 0, 1920, 4);
		g.fillRect(0, 0, 4, 1080);
		g.fillRect(0, 1080-4, 1920, 4);
		g.fillRect(1920-4, 0, 4, 1080);
	}
	
	
	private void getInput() {
		xMove=0;
		yMove=0;
		
		if(inventory.isActive()||craftingScreen.isActive())
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
		else if (handler.getKeyManager().aDown)
			yAttack=1;
		else if (handler.getKeyManager().aRight)
			xAttack=1;
		else if (handler.getKeyManager().aLeft)
			xAttack=-1;;
	}
	

	@Override
	public void render(Graphics g) {
		super.render(g);
		int arSize = 50;
		g.drawImage(getCurrentAnimationFrame(),(int)(x-handler.getGameCamera().getxOffset()),(int)(y-handler.getGameCamera().getyOffset()),width,height,null);
		if(Settings.getDebug()) {	
			Rectangle ar=new Rectangle();
			Rectangle cb = getCollisionBounds(0,0);
			ar.width = arSize;
			ar.height= arSize;
			// jeweilige Hitbox hier eintragen
			ar.x = cb.x + cb.width;
			ar.y = cb.y  + cb.height/2 -arSize / 2;
			//
			g.drawImage(getCurrentAnimationFrame(),(int)(x-handler.getGameCamera().getxOffset()),(int)(y-handler.getGameCamera().getyOffset()),width,height,null);
			g.setColor(Color.BLACK);	
			g.fillRect(ar.x, ar.y, ar.width, ar.height);
		}
		g.setColor(Color.red);
		g.fillRect((int)(bounds.x+x-handler.getGameCamera().getxOffset()),(int)(bounds.y+y-handler.getGameCamera().getyOffset()),(int)bounds.width,(int)bounds.height);
	}
	
	public void knockback(int direction) {
		xMove=0;
		yMove=0;
		enemyDirection=direction;
		if(direction==0) {
			xMove=-5.0f;
			yMove=-5.0f;
		}
		if(direction==1) {
			xMove=-5.0f;
			yMove=5.0f;
		}
		if(direction==2) {
			xMove=5.0f;
			yMove=-5.0f;
		}
		if(direction==3) {
			xMove=5.0f;
			yMove=5.0f;
		}
		if(direction==4) {
			xMove=-5.0f;
		}
		if(direction==6) {
			xMove=5.0f;
		}
		if(direction==7) {
			yMove=-5.0f;
		}
		if(direction==8) {
			yMove=5.0f;
		}
		move();
		
	}
	
	public void postRender(Graphics g) {
		inventory.render(g);
		craftingScreen.render(g);
	}
	
	public boolean isPlayer() {
		return true;
	}
	
	public boolean collisionWithFinish(int x, int y) {
		return handler.getWorld().getTile(x, y).isFinish();
	}
	
	private BufferedImage getCurrentAnimationFrame() {
		if(isDead)
			return Assets.gravestone;
		
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
	
	public Arrow getArrow() {
		return arrow;
	}

	public void setArrow(Arrow arrow) {
		this.arrow = arrow;
	}


	public int getMaxArmor() {
		return maxArmor;
	}

	public void setMaxArmor(int maxArmor) {
		this.maxArmor = maxArmor;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
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

	public boolean isRangedToggled() {
		return rangedToggled;
	}

	public void setRangedToggled(boolean rangedToggled) {
		this.rangedToggled = rangedToggled;
	}
	public int getArmor() {
		return armor;
	}

	public void setArmor(int armor) {
		this.armor = armor;
	}

	public int getRangedDamage() {
		return rangedDamage;
	}

	public void setRangedDamage(int rangedDamage) {
		this.rangedDamage = rangedDamage;
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}

	public CraftingScreen getCraftingScreen() {
		return craftingScreen;
	}

	public void setCraftingScreen(CraftingScreen craftingScreen) {
		this.craftingScreen = craftingScreen;
	}

	public int getKnockbackCounter() {
		return knockbackCounter;
	}

	public void setKnockbackCounter(int knockbackCounter) {
		this.knockbackCounter = knockbackCounter;
	}

	@Override
	public void knockback() {
		// TODO Auto-generated method stub
		
	}
	
	

}
