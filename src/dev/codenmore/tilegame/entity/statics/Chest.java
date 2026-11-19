package dev.codenmore.tilegame.entity.statics;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.Settings;
import dev.codenmore.tilegame.entity.creatures.Player;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.gfx.Text;
import dev.codenmore.tilegame.inventory.ChestInventory;
import dev.codenmore.tilegame.items.CraftableItem;
import dev.codenmore.tilegame.tiles.Tile;

import java.awt.*;

/**
 * A chest that stores items. The player opens it with E when standing next to it, see
 * {@link ChestInventory} for moving items in and out.
 */
public class Chest extends StaticEntity {

    /** How far (in pixels) around the chest the player can reach it. */
    private static final int REACH = 48;

    private ChestInventory chestInventory;

    public Chest (Handler handler, float x , float y) {
        super(handler,x,y, Tile.TILEWIDTH,Tile.TILEHEIGHT);
        bounds.x=6;
        bounds.y = 28;
        bounds.width=53*2;
        bounds.height =40;
        chestInventory=new ChestInventory(handler);
    }

    public void tick() {
    }

    public void die() {
        // nothing stored in a destroyed chest is lost
        chestInventory.close();
        chestInventory.dropContents((int) x, (int) y);
        handler.getWorld().getItemManager().addItem(CraftableItem.chestItem.createNew(1),(int) x,(int)  y, true );
    }

    /** Whether the player stands close enough to open the chest. */
    public boolean isInReach(Player player) {
        Rectangle area = getCollisionBounds(0, 0);
        area.grow(REACH, REACH);
        return player.getCollisionBounds(0, 0).intersects(area);
    }

    public ChestInventory getInventory() {
        return chestInventory;
    }

    public void render(Graphics g) {
        int sx = (int) (x - handler.getGameCamera().getxOffset());
        int sy = (int) (y - handler.getGameCamera().getyOffset());
        g.drawImage(Assets.chest, sx, sy, width, height, null);
        Player player = handler.getWorld().getEntityManager().getPlayer();
        if (!chestInventory.isOpen() && !player.isDead() && isInReach(player)) {
            // hint how to open the chest
            Text.drawString(g, "[E] OPEN", sx + width / 2 + 2, sy - 6 + 2, true, Color.BLACK, Assets.font28);
            Text.drawString(g, "[E] OPEN", sx + width / 2, sy - 6, true, Color.WHITE, Assets.font28);
        }
        if(Settings.getDebug()){
            g.setColor(Color.WHITE);
            g.fillRect((int)(x+bounds.x-handler.getGameCamera().getxOffset()), (int)(y+bounds.y-handler.getGameCamera().getyOffset() + 30),bounds.width,bounds.height);
        }
    }

    protected boolean isChest(){
        return true;
    }
}
