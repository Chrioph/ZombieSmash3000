package com.goldenbois.zombiesmash.entity.statics;

import com.goldenbois.zombiesmash.Handler;
import com.goldenbois.zombiesmash.gfx.Assets;
import com.goldenbois.zombiesmash.items.CraftableItem;
import com.goldenbois.zombiesmash.tiles.Tile;

import org.newdawn.slick.*;

/**
 * WoodBlock
 */
public class WoodBlock extends StaticEntity {
    /**
     * Constructor
     *
     * @param handler
     * @param x
     * @param y
     */
    public WoodBlock(Handler handler, float x, float y) {
        super(handler, x, y, Tile.TILEWIDTH, Tile.TILEHEIGHT);
        bounds.setY(0);
        bounds.setWidth(0);
        bounds.setHeight(0);
    }

    public void tick() {

    }

    public void die() {
        handler.getWorld().getItemManager().addItem(CraftableItem.woodItem.createNew(1), (int) x, (int) y, true);
    }

    public void render(Graphics g) {
        Assets.wood.draw((x - handler.getGameCamera().getxOffset()), (y - handler.getGameCamera().getyOffset()), width, height);
    }
}
