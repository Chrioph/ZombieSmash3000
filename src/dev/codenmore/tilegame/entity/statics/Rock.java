package dev.codenmore.tilegame.entity.statics;

import java.awt.Graphics;
import java.util.Random;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.items.Item;
import dev.codenmore.tilegame.tiles.Tile;
import dev.codenmore.tilegame.utils.Utils;

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

        bounds.x = 16;
        bounds.y = 64;
        bounds.width = width - 24;
        bounds.height = 32;
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
        g.drawImage(Assets.rock, (int) (x - handler.getGameCamera().getxOffset()), (int) (y - handler.getGameCamera().getyOffset()), width, height, null);
    }
}
