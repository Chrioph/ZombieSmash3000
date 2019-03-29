package com.goldenbois.zombiesmash.entity.creatures.Enemies;

import com.goldenbois.zombiesmash.Settings;
import org.newdawn.slick.*;


import com.goldenbois.zombiesmash.Handler;
import com.goldenbois.zombiesmash.entity.creatures.Creature;
import com.goldenbois.zombiesmash.gfx.Animation;
import com.goldenbois.zombiesmash.gfx.Assets;
import org.newdawn.slick.geom.Rectangle;

/**
 * Implements a zombie
 */
public class Zombie extends Enemy {


    // TODO: Maybe move these in enemy top class

    private Rectangle headBounds = new Rectangle(0,0,0,0);

    /**
     * Constructor
     *
     * @param handler
     * @param x
     * @param y
     */
    public Zombie(Handler handler, float x, float y) {
        super(handler, x, y, Creature.DEFAULT_CREATURE_WIDTH, Creature.DEFAULT_CREATURE_HEIGHT);

        this.aggroRange = 300;
        this.speed = 1.5f;
        maxHealth = 5;
        health = 5;
        damage = 3;
        bounds.setX(18);
        bounds.setY(26);
        bounds.setWidth(28);
        bounds.setHeight(40);
        headBounds.setX(8);
        headBounds.setY(6);
        headBounds.setWidth(46);
        headBounds.setHeight(24);

    }

    protected void loadAnimations() {
        animDown = new Animation(500, Assets.zombie_down);
        animUp = new Animation(500, Assets.zombie_up);
        animLeft = new Animation(500, Assets.zombie_left);
        animRight = new Animation(500, Assets.zombie_right);

        animAUp = new Animation(400, Assets.zaUp);
        animADown = new Animation(400, Assets.zaDown);
        animALeft = new Animation(400, Assets.zaLeft);
        animARight = new Animation(400, Assets.zaRight);
        idle = Assets.zombie;
    }

    /**
     * TODO: is own tick function needed?
     */
    public void tick() {
        distToPlayer = Math.abs((int) (x - handler.getWorld().getEntityManager().getPlayer().getX() + y - handler.getWorld().getEntityManager().getPlayer().getY()));
        checkAttacks();
        animDown.tick();
        animUp.tick();
        animLeft.tick();
        animRight.tick();
        animADown.tick();
        animALeft.tick();
        animARight.tick();
        //generate is in the checkKnockback method
        checkKnockback();
        move();
    }

    /**
     * TODO: is own render function needed?
     *
     * @param g
     */
    public void render(Graphics g) {
        super.render(g);
        renderHealthbar(g);
        if (renderHurtAnimation())
            handler.getWorld().getEntityManager().getPlayer().hurtAnimation(g);
        g.drawImage(getCurrentAnimationFrame(), (x - handler.getGameCamera().getxOffset()), (y - handler.getGameCamera().getyOffset()));
        if (Settings.getDebug()) {
            g.fillRect((int) (x + headBounds.getX() - handler.getGameCamera().getxOffset()), (int) (y + headBounds.getY() - handler.getGameCamera().getyOffset()), headBounds.getWidth(), headBounds.getHeight());
        }
    }



}
