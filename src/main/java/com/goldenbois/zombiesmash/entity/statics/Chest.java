package com.goldenbois.zombiesmash.entity.statics;

import com.goldenbois.zombiesmash.Handler;
import com.goldenbois.zombiesmash.Settings;
import com.goldenbois.zombiesmash.gfx.Assets;
import com.goldenbois.zombiesmash.inventory.ChestInventory;
import com.goldenbois.zombiesmash.items.CraftableItem;
import com.goldenbois.zombiesmash.tiles.Tile;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;

public class Chest extends StaticEntity {
    private int capacity , capacityUsed=0;
    private ChestInventory chestInventory;
    private Rectangle openingBounds= new Rectangle(0,0,0,0);

    public Chest (Handler handler, float x , float y) {
        super(handler,x,y, Tile.TILEWIDTH,Tile.TILEHEIGHT);
        capacity=10;
        bounds.setX(6);
        bounds.setY(28);
        bounds.setWidth(53*2);
        bounds.setHeight(40);
        openingBounds.setX(x + 6);
        openingBounds.setY(y + 68);
        openingBounds.setWidth(53*2);
        openingBounds.setHeight(70);
        chestInventory=new ChestInventory(handler, openingBounds);
    }
    public void tick() {
        chestInventory.tick();

    }

    public void die() {
        handler.getWorld().getItemManager().addItem(CraftableItem.chestItem.createNew(1),(int) x,(int)  y, true );
    }
    public void render(Graphics g) {
        g.drawImage(Assets.chest, (x-handler.getGameCamera().getxOffset()) ,(y-handler.getGameCamera().getyOffset()));
        if(Settings.getDebug()){
            g.setColor(Color.white);
            g.fillRect((int)(x+bounds.getX()-handler.getGameCamera().getxOffset()), (int)(y+bounds.getY()-handler.getGameCamera().getyOffset() + 30),bounds.getWidth(),bounds.getHeight());
        }
    }


    protected boolean isChest(){
        return true;
    }

     protected void postrender(Graphics g){
        chestInventory.render(g);
    }
}
