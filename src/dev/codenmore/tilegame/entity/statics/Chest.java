package dev.codenmore.tilegame.entity.statics;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.Settings;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.inventory.ChestInventory;
import dev.codenmore.tilegame.items.CraftableItem;
import dev.codenmore.tilegame.tiles.Tile;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Chest extends StaticEntity {
    private int capacity , capacityUsed=0;
    private ChestInventory chestInventory;
    private Rectangle openingBounds= new Rectangle();

    public Chest (Handler handler, float x , float y) {
        super(handler,x,y, Tile.TILEWIDTH,Tile.TILEHEIGHT);
        capacity=10;
        bounds.x=6;
        bounds.y = 28;
        bounds.width=53*2;
        bounds.height =40;
        openingBounds.x= (int) x + 6;
        openingBounds.y =(int) y + 68;
        openingBounds.width=53*2;
        openingBounds.height =70;
        chestInventory=new ChestInventory(handler, openingBounds);
    }
    public void tick() {
        chestInventory.tick();

    }

    public void die() {
        handler.getWorld().getItemManager().addItem(CraftableItem.chestItem.createNew(1),(int) x,(int)  y, true );
    }
    public void render(Graphics g) {
        g.drawImage(Assets.chest, (int) (x-handler.getGameCamera().getxOffset()) ,(int) (y-handler.getGameCamera().getyOffset()), width, height, null);
        if(Settings.getDebug()){
            g.setColor(Color.WHITE);
            g.fillRect((int)(x+bounds.x-handler.getGameCamera().getxOffset()), (int)(y+bounds.y-handler.getGameCamera().getyOffset() + 30),bounds.width,bounds.height);
        }
    }


    protected boolean isChest(){
        return true;
    }

     protected void postrender(Graphics g){
        chestInventory.render(g);
    }
}
