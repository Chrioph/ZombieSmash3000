package dev.codenmore.tilegame.entity.creatures.Enemies;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.entity.creatures.Creature;
import dev.codenmore.tilegame.gfx.Animation;
import dev.codenmore.tilegame.gfx.Assets;

public class Ogre extends Enemy {
	
	private Animation animDown;
	private Animation animUp;
	private Animation animLeft;
	private Animation animRight;
	private Animation animADown;
	private Animation animAUp;
	private Animation animALeft;
	private Animation animARight;
	
	public Ogre(Handler handler, float x, float y) {
		super(handler, x , y, Creature.DEFAULT_CREATURE_WIDTH,Creature .DEFAULT_CREATURE_HEIGHT);
		
		this.speed= 1.5f;
		health =15;
		damage=2;
		bounds.x=18;
		bounds.y =26;
		bounds.width =28;
		bounds.height =40;
		
		animDown = new Animation(500, Assets.ogre_down);
		animUp = new Animation(500,Assets.ogre_up);
		animLeft= new Animation ( 500, Assets.ogre_left);
		animRight = new Animation ( 500, Assets.ogre_right);
		
		animAUp= new Animation(400, Assets.oaUp);
		animADown= new Animation(400, Assets.oaDown);
		animALeft= new Animation(400, Assets.oaLeft);
		animARight= new Animation(400, Assets.oaRight);
	}

	@Override
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
		
		
		else return Assets.ogre;
	}
	public void render(Graphics g) {
		super.render(g);
		g.drawImage(getCurrentAnimationFrame(),(int)(x-handler.getGameCamera().getxOffset()),(int)(y-handler.getGameCamera().getyOffset()),width,height,null);
		
	}
}
