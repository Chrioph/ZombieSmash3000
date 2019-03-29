package com.goldenbois.zombiesmash.entity.creatures;

import com.goldenbois.zombiesmash.Settings;
import org.newdawn.slick.*;

import com.goldenbois.zombiesmash.Handler;
import com.goldenbois.zombiesmash.entity.Entity;
import com.goldenbois.zombiesmash.gfx.Assets;
import com.goldenbois.zombiesmash.tiles.Tile;
import org.newdawn.slick.geom.Rectangle;

/**
 * Apparently arrows are creatures
 */
public class Arrow extends Creature {


    private int direction;
    private Rectangle bounds1;

    /**
     * Constructor
     * @param handler
     * @param x
     * @param y
     * @param direction
     * @param damage
     */
    public Arrow(Handler handler, float x, float y, int direction, int damage) {
        super(handler, x, y, DEFAULT_CREATURE_WIDTH, DEFAULT_CREATURE_HEIGHT);
        bounds1 = new Rectangle(0,0,0,0);
        this.direction = direction;
        this.speed = 12.0f;
        this.damage = damage;
        bounds.setX(4);
        bounds.setY(26);
        bounds.setWidth(56);
        bounds.setHeight(14);
        calcBoundsByDirection(direction);

    }

    /**
     * Changes bounds depending on direction of arrow
     * @param direction identifier for the direction
     */
    private void calcBoundsByDirection(int direction)
    {
        if (direction == 1) {
            bounds.setX(24);
            bounds.setY(4);
            bounds.setWidth(14);
            bounds.setHeight(56);
        }
        if (direction == 3) {
            bounds.setX(24);
            bounds.setY(4);
            bounds.setWidth(14);
            bounds.setHeight(56);
        }
        if (direction == 0) {
            bounds1.setX(bounds.getX());
            bounds1.setY(bounds.getY());
            bounds1.setWidth(56 + (int) speed * 3);
            bounds1.setHeight(14);
        }
        if (direction == 1) {
            bounds1.setX(24);
            bounds1.setY(4);
            bounds1.setWidth(14);
            bounds1.setHeight(56 + (int) speed * 3);
        }
        if (direction == 2) {
            bounds1.setX(bounds.getX() - (int) speed * 3);
            bounds1.setY(bounds.getY());
            bounds1.setWidth(56);
            bounds1.setHeight(14);
        }
        if (direction == 3) {
            bounds1.setX(24);
            bounds1.setY(4 - (int) speed * 3);
            bounds1.setWidth(14);
            bounds1.setHeight(56);
        }
    }

    /**
     * Arrow is exception, it has no animation
     */
    protected void loadAnimations() {

    }


    /**
     * Arrow has own tick logic
     */
    @Override
    public void tick() {
        getMovement();
        move();
    }

    /**
     * Render arrow depending on flight direction
     * @param g
     */
    @Override
    public void render(Graphics g) {
        super.render(g);
        if (direction == 0)
            g.drawImage(Assets.arrowRight, (x - handler.getGameCamera().getxOffset()), (y - handler.getGameCamera().getyOffset()));
        if (direction == 1)
            g.drawImage(Assets.arrowDown, (x - handler.getGameCamera().getxOffset()), (y - handler.getGameCamera().getyOffset()));
        if (direction == 2)
            g.drawImage(Assets.arrowLeft, (x - handler.getGameCamera().getxOffset()), (y - handler.getGameCamera().getyOffset()));
        if (direction == 3)
            g.drawImage(Assets.arrowUp, (x - handler.getGameCamera().getxOffset()), (y - handler.getGameCamera().getyOffset()));
        if (Settings.getDebug()) {
            g.setColor(Color.pink);
            g.fillRect((int) (bounds1.getX() - handler.getGameCamera().getxOffset() + x), (int) (bounds1.getY() - handler.getGameCamera().getyOffset() + y), bounds1.getWidth(), bounds1.getHeight());
        }
    }

    /**
     * Called on death
     */
    @Override
    public void die() {

        this.active = false;
    }

    /**
     * Movement logic
     */
    public void move() {

        if (!checkEntityCollision(xMove, 0f))
            moveX();
        if (!checkEntityCollision(0f, yMove))
            moveY();
        if (checkEntityCollision(0f, yMove) || checkEntityCollision(xMove, 0f)) {
            for (Entity e : handler.getWorld().getEntityManager().getEntities()) {
                if (e.equals(this))
                    continue;
                if (e.isPlayer())
                    continue;
                if (e.getCollisionBounds(0, 0).intersects(new Rectangle(x + bounds1.getX() - handler.getGameCamera().getxOffset(), (y + bounds1.getY() - handler.getGameCamera().getyOffset()), bounds1.getWidth(), bounds1.getHeight()))) {
                    e.hurt(damage);
                }
            }
            die();
        }

    }

    /**
     * Calculates amount of movement
     */
    private void getMovement() {
        xMove = 0;
        yMove = 0;
        if (direction == 0)
            xMove = speed;
        if (direction == 1)
            yMove = speed;
        if (direction == 2)
            xMove = -speed;
        if (direction == 3)
            yMove = -speed;
    }


    /**
     * Vertical movement
     */
    public void moveY() {

        if (yMove > 0) {                //Moving down
            int ty = (int) (y + yMove + bounds.getY() + bounds.getHeight()) / Tile.TILEHEIGHT;

            if (!collisionWithTile((int) (x + bounds.getX()) / Tile.TILEWIDTH, ty) &&
                    !collisionWithTile((int) ((x + bounds.getX() + bounds.getWidth()) / Tile.TILEHEIGHT), ty)) {
                y += yMove;
            } else {
                die();
            }
        } else if (yMove < 0) {        //Moving up
            int ty = (int) (y + yMove + bounds.getY()) / Tile.TILEHEIGHT;

            if (!collisionWithTile((int) (x + bounds.getX()) / Tile.TILEHEIGHT, ty) &&
                    !collisionWithTile((int) (x + bounds.getX() + bounds.getWidth()) / Tile.TILEWIDTH, ty)) {
                y += yMove;
            } else {
                die();
            }
        }


    }

    /**
     * Horizontal movement
     */
    public void moveX() {

        if (xMove > 0) {                //Moving right
            int tx = (int) (x + xMove + bounds.getX() + bounds.getWidth()) / Tile.TILEWIDTH;

            if (!collisionWithTile(tx, (int) (y + bounds.getY()) / Tile.TILEHEIGHT) &&
                    !collisionWithTile(tx, (int) ((y + bounds.getY() + bounds.getHeight()) / Tile.TILEHEIGHT))) {
                x += xMove;
            } else {
                die();
            }
        } else if (xMove < 0) {        //Moving left
            int tx = (int) (x + xMove + bounds.getX()) / Tile.TILEWIDTH;

            if (!collisionWithTile(tx, (int) (y + bounds.getY()) / Tile.TILEHEIGHT) &&
                    !collisionWithTile(tx, (int) ((y + bounds.getY() + bounds.getHeight()) / Tile.TILEHEIGHT))) {
                x += xMove;
            } else {
                die();
            }
        }


    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

}
