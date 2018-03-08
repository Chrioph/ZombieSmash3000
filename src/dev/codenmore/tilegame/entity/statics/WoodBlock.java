package dev.codenmore.tilegame.entity.statics;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.items.Item;
import dev.codenmore.tilegame.items.CraftableItem;
import dev.codenmore.tilegame.tiles.Tile;

import java.awt.*;

public class WoodBlock extends StaticEntity {
    public WoodBlock (Handler handler, float x , float y) {
        super(handler,x,y, Tile.TILEWIDTH,Tile.TILEHEIGHT);
        bounds.y = 0;
        bounds.width=0;
        bounds.height =0;
    }

    public void tick() {

    }

    public void die() {
        handler.getWorld().getItemManager().addItem(CraftableItem.woodItem.createNew(1),(int) x,(int) y, true);
    }
    public void render(Graphics g) {
        g.drawImage(Assets.wood, (int) (x-handler.getGameCamera().getxOffset()) ,(int) (y-handler.getGameCamera().getyOffset()), width, height, null);
    }
}
