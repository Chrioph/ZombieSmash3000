package dev.codenmore.tilegame.entity.statics;

import java.awt.Graphics;
import java.util.Random;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.entity.creatures.Creature;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.items.Item;
import dev.codenmore.tilegame.tiles.Tile;
import dev.codenmore.tilegame.utils.Utils;

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
        bounds.x = 50;
        bounds.y = 220;
        bounds.width = width - 95;
        bounds.height = height - 220;
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
        g.drawImage(Assets.tree, (int) (x - handler.getGameCamera().getxOffset()), (int) (y - handler.getGameCamera().getyOffset()), width, height, null);
    }

}
