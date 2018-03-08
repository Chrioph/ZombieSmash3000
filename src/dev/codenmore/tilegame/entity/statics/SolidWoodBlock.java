package dev.codenmore.tilegame.entity.statics;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.items.CraftableItem;
import dev.codenmore.tilegame.tiles.Tile;

import java.awt.*;

public class SolidWoodBlock extends StaticEntity {
    public SolidWoodBlock (Handler handler, float x , float y) {
        super(handler,x,y, Tile.TILEWIDTH,Tile.TILEHEIGHT);

        bounds.x=0;
        bounds.y = 0;
        bounds.width=Tile.TILEWIDTH;
        bounds.height =Tile.TILEHEIGHT;
    }
    public void tick() {

    }

    public void die() {
        handler.getWorld().getItemManager().addItem(CraftableItem.solidWoodItem.createNew(1),(int) x,(int)  y );
    }
    public void render(Graphics g) {
        g.drawImage(Assets.solidWood, (int) (x-handler.getGameCamera().getxOffset()) ,(int) (y-handler.getGameCamera().getyOffset()), width, height, null);
    }
}
