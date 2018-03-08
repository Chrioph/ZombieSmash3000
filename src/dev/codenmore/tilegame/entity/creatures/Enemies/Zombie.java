package dev.codenmore.tilegame.entity.creatures.Enemies;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;


import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.Settings;
import dev.codenmore.tilegame.entity.creatures.Creature;
import dev.codenmore.tilegame.gfx.Animation;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.items.Item;
import dev.codenmore.tilegame.utils.Utils;

/**
 * Implements a zombie
 */
public class Zombie extends Enemy {


    // TODO: Maybe move these in enemy top class
    private Animation animDown;
    private Animation animUp;
    private Animation animLeft;
    private Animation animRight;
    private Animation animADown;
    private Animation animAUp;
    private Animation animALeft;
    private Animation animARight;
    private Rectangle headBounds = new Rectangle();

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
        bounds.x = 18;
        bounds.y = 26;
        bounds.width = 28;
        bounds.height = 40;
        headBounds.x = 8;
        headBounds.y = 6;
        headBounds.width = 46;
        headBounds.height = 24;


        animDown = new Animation(500, Assets.zombie_down);
        animUp = new Animation(500, Assets.zombie_up);
        animLeft = new Animation(500, Assets.zombie_left);
        animRight = new Animation(500, Assets.zombie_right);

        animAUp = new Animation(400, Assets.zaUp);
        animADown = new Animation(400, Assets.zaDown);
        animALeft = new Animation(400, Assets.zaLeft);
        animARight = new Animation(400, Assets.zaRight);
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
        g.drawImage(getCurrentAnimationFrame(), (int) (x - handler.getGameCamera().getxOffset()), (int) (y - handler.getGameCamera().getyOffset()), width, height, null);
        if (Settings.getDebug()) {
            g.fillRect((int) (x + headBounds.x - handler.getGameCamera().getxOffset()), (int) (y + headBounds.y - handler.getGameCamera().getyOffset()), headBounds.width, headBounds.height);
        }
    }

    /**
     * TODO: Maybe move to enemy top class
     *
     * @return
     */
    private BufferedImage getCurrentAnimationFrame() {

        if (distToPlayer <= aggroRange) {
            if (xMove < 0)
                return animALeft.getCurrentFrame();
            if (xMove > 0)
                return animARight.getCurrentFrame();
            if (yMove < 0)
                return animUp.getCurrentFrame();
            if (yMove > 0)
                return animADown.getCurrentFrame();
        }


        if (xMove < 0)
            return animLeft.getCurrentFrame();
        if (xMove > 0)
            return animRight.getCurrentFrame();
        if (yMove < 0)
            return animUp.getCurrentFrame();
        if (yMove > 0)
            return animDown.getCurrentFrame();


        else return Assets.zombie;
    }

}
