package com.goldenbois.zombiesmash.entity.statics;

import org.newdawn.slick.*;

import com.goldenbois.zombiesmash.Handler;
import com.goldenbois.zombiesmash.gfx.Assets;
import com.goldenbois.zombiesmash.items.Item;
import com.goldenbois.zombiesmash.tiles.Tile;
import com.goldenbois.zombiesmash.utils.Utils;

/**
 * Implementation of a rock
 */
public class Rock extends StaticEntity {

    /**
     * Constructor
     *
     * @param handler
     * @param x
     * @param y
     */
    public Rock(Handler handler, float x, float y) {
        super(handler, x, y, Tile.TILEWIDTH, Tile.TILEHEIGHT);

        bounds.setX(16);
        bounds.setY(64);
        bounds.setWidth(width - 24);
        bounds.setHeight(32);
    }

    /**
     * Rocks do nothing on tick
     */
    public void tick() {
    }

    public void die() {
        for (int i = 0; i < Utils.generateRandomInt(2) + 1; i++) {
            handler.getWorld().getItemManager().addItem(Item.rockItem.createNew(1), (int) x, (int) y, true);
        }
    }

    public void render(Graphics g) {
        Assets.rock.draw((x - handler.getGameCamera().getxOffset()), (y - handler.getGameCamera().getyOffset()), width, height);
    }
}
