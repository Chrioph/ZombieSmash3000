package com.goldenbois.zombiesmash.entity.creatures;


import com.goldenbois.zombiesmash.Handler;
import com.goldenbois.zombiesmash.Modifiers.DamageMod;
import com.goldenbois.zombiesmash.Modifiers.HPMod;
import com.goldenbois.zombiesmash.Modifiers.Mod;
import com.goldenbois.zombiesmash.Modifiers.SpeedMod;
import com.goldenbois.zombiesmash.Modifiers.itemspawnMod;
import com.goldenbois.zombiesmash.entity.Entity;
import com.goldenbois.zombiesmash.gfx.Animation;
import com.goldenbois.zombiesmash.tiles.Tile;
import org.newdawn.slick.Image;

import java.util.ArrayList;

/**
 * Top class for all creatures
 */
public abstract class Creature extends Entity {

    public static final float DEFAULT_SPEED = 6.0f;

    public static final int DEFAULT_CREATURE_WIDTH = 64;
    public static final int DEFAULT_CREATURE_HEIGHT = 64;
    public static final int DEFAULT_CREATURE_DAMAGE = 3;


    protected float speed;
    protected float xMove;
    protected float yMove;
    protected int yAttack;
    protected int xAttack;
    protected int damage;
    protected int spawnrate;
    protected int maxHealth;

    protected transient Image idle;

    protected transient Animation animDown, animUp, animLeft, animRight, animADown, animAUp, animALeft, animARight;

    protected ArrayList<Mod> mods;

    /**
     * Constructor
     *
     * @param handler
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public Creature(Handler handler, float x, float y, int width, int height) {
        super(handler, x, y, width, height);
        mods = new ArrayList<Mod>();
        damage = DEFAULT_CREATURE_DAMAGE;
        speed = DEFAULT_SPEED;
        xMove = 0;
        yMove = 0;

        loadAnimations();
    }

    public Creature() {
        loadAnimations();
    }

    /**
     * Creatures can possess mods which influence their attributes
     *
     * @param modifications
     */
    public void setMods(ArrayList<Mod> modifications) {
        mods = modifications;
        applyMods();
    }

    /**
     * Mod subfunction, calculates changed modifiers for attributes
     */
    private void applyMods() {
        for (Mod mod : mods) {
            if (mod instanceof HPMod) {
                health = (int) Math.round(health * mod.getModifier());
            } else if (mod instanceof DamageMod) {
                damage = (int) Math.round(damage * mod.getModifier());
            } else if (mod instanceof SpeedMod) {
                speed = (int) Math.round(speed * mod.getModifier());
            } else if (mod instanceof itemspawnMod) {
                spawnrate = (int) Math.round(spawnrate * mod.getModifier());
            }
        }
    }

    /**
     * returns damage of creature
     *
     * @return
     */
    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * general movement function for creatures
     */
    public void move() {
        if (!checkEntityCollision(xMove, 0f))
            moveX();
        if (!checkEntityCollision(0f, yMove))
            moveY();

    }

    /**
     * general horizontal movement
     */
    public void moveX() {

        if (xMove > 0) {                //Moving right
            int tx = (int) (x + xMove + bounds.getX() + bounds.getWidth()) / Tile.TILEWIDTH;

            if (!collisionWithTile(tx, (int) (y + bounds.getY()) / Tile.TILEHEIGHT) &&
                    !collisionWithTile(tx, (int) ((y + bounds.getY() + bounds.getHeight()) / Tile.TILEHEIGHT))) {
                x += xMove;
            } else {
                x = tx * Tile.TILEWIDTH - bounds.getX() - bounds.getWidth() - 1;
            }
        } else if (xMove < 0) {        //Moving left
            int tx = (int) (x + xMove + bounds.getX()) / Tile.TILEWIDTH;

            if (!collisionWithTile(tx, (int) (y + bounds.getY()) / Tile.TILEHEIGHT) &&
                    !collisionWithTile(tx, (int) ((y + bounds.getY() + bounds.getHeight()) / Tile.TILEHEIGHT))) {
                x += xMove;
            } else {
                x = tx * Tile.TILEWIDTH + Tile.TILEWIDTH - bounds.getX();
            }
        }


    }

    /**
     * general vertical movement
     */
    public void moveY() {

        if (yMove > 0) {                //Moving down
            int ty = (int) (y + yMove + bounds.getY() + bounds.getHeight()) / Tile.TILEHEIGHT;

            if (!collisionWithTile((int) (x + bounds.getX()) / Tile.TILEWIDTH, ty) &&
                    !collisionWithTile((int) ((x + bounds.getX() + bounds.getWidth()) / Tile.TILEHEIGHT), ty)) {
                y += yMove;
            } else {
                y = ty * Tile.TILEHEIGHT - bounds.getY() - bounds.getHeight() - 1;
            }
        } else if (yMove < 0) {        //Moving up
            int ty = (int) (y + yMove + bounds.getY()) / Tile.TILEHEIGHT;

            if (!collisionWithTile((int) (x + bounds.getX()) / Tile.TILEHEIGHT, ty) &&
                    !collisionWithTile((int) (x + bounds.getX() + bounds.getWidth()) / Tile.TILEWIDTH, ty)) {
                y += yMove;
            } else {
                y = ty * Tile.TILEHEIGHT + Tile.TILEHEIGHT - bounds.getY();
            }
        }


    }

    /**
     * TODO: do we need this? why no instanceof check instead?
     *
     * @return
     */
    public boolean isPlayer() {
        return false;
    }

    protected boolean collisionWithTile(int x, int y) {

        return handler.getWorld().getTile(x, y).isSolid();

    }

    protected abstract void loadAnimations();


    //GETTER and SETTERS

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getxMove() {
        return xMove;
    }

    public void setxMove(float xMove) {
        this.xMove = xMove;
    }

    public float getyMove() {
        return yMove;
    }

    public void setyMove(float yMove) {
        this.yMove = yMove;
    }


}
