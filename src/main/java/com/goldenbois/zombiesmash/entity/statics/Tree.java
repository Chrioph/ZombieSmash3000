package com.goldenbois.zombiesmash.entity.statics;

import com.goldenbois.zombiesmash.entity.creatures.Creature;
import org.newdawn.slick.*;

import com.goldenbois.zombiesmash.Handler;
import com.goldenbois.zombiesmash.gfx.Assets;
import com.goldenbois.zombiesmash.items.Item;
import com.goldenbois.zombiesmash.tiles.Tile;
import com.goldenbois.zombiesmash.utils.Utils;

/**
 * Tree class
 */
public class Tree extends StaticEntity {

    /**
     * Constructor
     *
     * @param handler
     * @param x
     * @param y
     */
    public Tree(Handler handler, float x, float y) {
        super(handler, x, y, Tile.TILEWIDTH, Tile.TILEHEIGHT * 2);
        health = Creature.DEFAULT_HEALTH;
        bounds.setX(50);
        bounds.setY(220);
        bounds.setWidth(width - 95);
        bounds.setHeight(height - 220);
    }

    public void tick() {
    }

    public void die() {
        for (int i = 0; i < Utils.generateRandomInt(2) + 1; i++) {
            handler.getWorld().getItemManager().addItem(Item.logItem.createNew(1), (int) x, (int) y, true);
        }
        if (1 == Utils.generateRandomInt(5))
            handler.getWorld().getItemManager().addItem(Item.seedItem.createNew(1), (int) x, (int) y, true);
    }

    public void render(Graphics g) {
        g.drawImage(Assets.tree, (int) (x - handler.getGameCamera().getxOffset()), (int) (y - handler.getGameCamera().getyOffset()));
    }

}
