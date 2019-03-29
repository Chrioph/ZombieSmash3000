package com.goldenbois.zombiesmash.entity.statics;

import com.goldenbois.zombiesmash.Handler;
import com.goldenbois.zombiesmash.gfx.Assets;
import com.goldenbois.zombiesmash.items.CraftableItem;
import com.goldenbois.zombiesmash.tiles.Tile;

import org.newdawn.slick.*;

/**
 * Solid Wood Block that can be crafted
 */
public class SolidWoodBlock extends StaticEntity {
    /**
     * Constructor
     *
     * @param handler
     * @param x
     * @param y
     */
    public SolidWoodBlock(Handler handler, float x, float y) {
        super(handler, x, y, Tile.TILEWIDTH, Tile.TILEHEIGHT);

        bounds.setX(0);
        bounds.setY(0);
        bounds.setWidth(Tile.TILEWIDTH);
        bounds.setHeight(Tile.TILEHEIGHT);
    }

    public void tick() {

    }

    public void die() {
        handler.getWorld().getItemManager().addItem(CraftableItem.solidWoodItem.createNew(1), (int) x, (int) y, true);
    }

    public void render(Graphics g) {
        Assets.solidWood.draw((x - handler.getGameCamera().getxOffset()), (y - handler.getGameCamera().getyOffset()), width, height);
    }
}
