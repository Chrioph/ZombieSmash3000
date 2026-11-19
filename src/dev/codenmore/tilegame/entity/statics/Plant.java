package dev.codenmore.tilegame.entity.statics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.entity.creatures.Creature;
import dev.codenmore.tilegame.entity.creatures.Player;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.gfx.Text;
import dev.codenmore.tilegame.items.Item;
import dev.codenmore.tilegame.tiles.Tile;
import dev.codenmore.tilegame.utils.Utils;

/**
 * A planted seed. It grows one stage after every completed level, from a sprout (stage 1)
 * to a full tree (stage {@value #MAX_STAGE}). Young plants can be walked over, from stage 3
 * on the trunk blocks the way. A full tree drops logs and seeds when it is cut down, a young
 * plant gives its seed back.
 *
 * Like a tree it is two tiles high and stands on its lower tile.
 */
public class Plant extends StaticEntity {

    public static final int MAX_STAGE = 5;
    /** topmost row of each stage's 32x64 sprite, to put the label just above the plant */
    private static final int[] TOP_ROW = {51, 42, 31, 16, 1};

    private int stage = 1;

    public Plant(Handler handler, float x, float y) {
        super(handler, x, y, Tile.TILEWIDTH, Tile.TILEHEIGHT * 2);
        health = Creature.DEFAULT_HEALTH;
        updateBounds();
    }

    public int getStage() {
        return stage;
    }

    public boolean isFullyGrown() {
        return stage >= MAX_STAGE;
    }

    /** Grows one stage, called after the player completes a level. */
    public void grow() {
        if (stage < MAX_STAGE) {
            stage++;
            updateBounds();
        }
    }

    private void updateBounds() {
        if (stage < 3) {
            // too small to block anyone
            bounds.x = 56;
            bounds.y = 240;
            bounds.width = 0;
            bounds.height = 0;
        } else {
            // the trunk gets thicker as the plant grows, a full tree matches Tree
            int trunk = stage == 3 ? 16 : stage == 4 ? 24 : width - 95;
            bounds.x = (width - trunk) / 2;
            bounds.y = 220;
            bounds.width = trunk;
            bounds.height = height - 220;
        }
    }

    @Override
    public void hurt(int amt) {
        // young plants have no wood yet, one hit pulls them out
        super.hurt(isFullyGrown() ? amt : health);
    }

    public void tick() {
    }

    public void die() {
        if (isFullyGrown()) {
            for (int i = 0; i < Utils.generateRandomInt(2) + 2; i++)
                handler.getWorld().getItemManager().addItem(Item.logItem.createNew(1), (int) x, (int) y, true);
            for (int i = 0; i < Utils.generateRandomInt(2) + 1; i++)
                handler.getWorld().getItemManager().addItem(Item.seedItem.createNew(1), (int) x, (int) y, true);
        } else {
            handler.getWorld().getItemManager().addItem(Item.seedItem.createNew(1), (int) x, (int) y, true);
        }
    }

    public void render(Graphics g) {
        int sx = (int) (x - handler.getGameCamera().getxOffset());
        int sy = (int) (y - handler.getGameCamera().getyOffset());
        g.drawImage(Assets.treeStages[stage - 1], sx, sy, width, height, null);

        // show the growth stage when the player stands next to the plant
        Player player = handler.getWorld().getEntityManager().getPlayer();
        Rectangle near = new Rectangle(sx, sy + Tile.TILEHEIGHT, width, Tile.TILEHEIGHT);
        near.grow(48, 48);
        if (!player.isDead() && player.getCollisionBounds(0, 0).intersects(near)) {
            String text = isFullyGrown() ? "FULLY GROWN" : stage + "/" + MAX_STAGE;
            int ty = sy + TOP_ROW[stage - 1] * height / 64 - 18;
            Text.drawString(g, text, sx + width / 2 + 2, ty + 2, true, Color.BLACK, Assets.font28);
            Text.drawString(g, text, sx + width / 2, ty, true, Color.WHITE, Assets.font28);
        }
    }
}
