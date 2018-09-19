package dev.codenmore.tilegame.entity.creatures.Enemies;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.entity.creatures.Creature;
import dev.codenmore.tilegame.gfx.Animation;
import dev.codenmore.tilegame.gfx.Assets;

/**
 * Implementation of an ogre
 */
public class Ogre extends Enemy {



    /**
     * Constructor
     * @param handler
     * @param x
     * @param y
     */
    public Ogre(Handler handler, float x, float y) {
        super(handler, x, y, Creature.DEFAULT_CREATURE_WIDTH, Creature.DEFAULT_CREATURE_HEIGHT);

        this.aggroRange = 200;
        this.speed = 1.5f;
        width = 64;
        height = 64;
        maxHealth = 15;
        health = 15;
        damage = 1;
        bounds.x = 8 * 2;
        bounds.y = 6 * 2;
        bounds.width = 48 * 2;
        bounds.height = 52 * 2;
    }

    protected void loadAnimations() {
        animDown = new Animation(500, Assets.ogre_down);
        animUp = new Animation(500, Assets.ogre_up);
        animLeft = new Animation(500, Assets.ogre_left);
        animRight = new Animation(500, Assets.ogre_right);

        animAUp = new Animation(400, Assets.oaUp);
        animADown = new Animation(400, Assets.oaDown);
        animALeft = new Animation(400, Assets.oaLeft);
        animARight = new Animation(400, Assets.oaRight);
        idle = Assets.ogre;
    }

    /**
     * TODO: Check if needs overwritten tick
     */
    @Override
    public void tick() {
        distToPlayer = Math.abs((int) (x - handler.getWorld().getEntityManager().getPlayer().getX() + y - handler.getWorld().getEntityManager().getPlayer().getY()));
        checkAttacks();
        animDown.tick();
        animUp.tick();
        animLeft.tick();
        animRight.tick();
        animADown.tick();
        animAUp.tick();
        animALeft.tick();
        animARight.tick();
        //generate is in the checkKnockback method
        checkKnockback();
        move();
    }

    /**
     * Renders the ogre
     * TODO: Why does this need an overwritten render function?
     *
     * @param g
     */
    public void render(Graphics g) {
        super.render(g);
        renderHealthbar(g);
        if (renderHurtAnimation())
            handler.getWorld().getEntityManager().getPlayer().hurtAnimation(g);
        g.drawImage(getCurrentAnimationFrame(), (int) (x - handler.getGameCamera().getxOffset()), (int) (y - handler.getGameCamera().getyOffset()), width * 2, height * 2, null);

    }

    /**
     * Since Ogres are bigger than normal enemies it needs a bigger health bar
     * TODO: make dependant on sprite size in enemy class
     *
     * @param g
     */
    protected void renderHealthbar(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect((int) (x - handler.getGameCamera().getxOffset() + bounds.width / 2 - 5 * maxHealth - 4 + 15), (int) (y - handler.getGameCamera().getyOffset() - 15), 10 * maxHealth + 8, 10);
        g.fillRect((int) (x - handler.getGameCamera().getxOffset() + bounds.width / 2 - 5 * maxHealth + 15), (int) (y - handler.getGameCamera().getyOffset() - 19), 10 * maxHealth, 18);
        g.setColor(Color.RED);
        g.fillRect((int) (x - handler.getGameCamera().getxOffset() + bounds.width / 2 - 5 * maxHealth + 15), (int) (y - handler.getGameCamera().getyOffset() - 15), 10 * maxHealth, 10);
        g.setColor(Color.GREEN);
        g.fillRect((int) (x - handler.getGameCamera().getxOffset() + bounds.width / 2 - 5 * maxHealth + 15), (int) (y - handler.getGameCamera().getyOffset() - 15), 10 * health, 10);

    }
}
