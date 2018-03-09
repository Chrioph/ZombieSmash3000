package dev.codenmore.tilegame.inventory;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.Settings;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.gfx.Text;
import dev.codenmore.tilegame.items.CraftableItem;
import dev.codenmore.tilegame.items.Item;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;

public class ChestInventory{

    private Handler handler;
    private boolean active=false;
    private ArrayList<Item> inventoryItems;
    private ArrayList<Item> displayInventoryItems;
    private Rectangle openingHitbox=new Rectangle();
    private ArrayList<Item> activeList = new ArrayList<>();
    private ArrayList<Item> chestInventoryItems = new ArrayList<>();

    private boolean openable=true;

    private int invX=192, invY=108, invWidth=1920-192*2, invHeight=1080-108*2,
            invListCenterX = invX + invWidth/3-1, invListCenterY= invY + invHeight / 2 + 5*(invHeight/384),
            invListSpacing=68;

    private int invImageX = 1380,
            invImageY= 190,
            invImageWidth= 128,
            invImageHeight=128;

    private int invCountX=  1450, invCountY= 388;

    private int selectedItem=0;



    public ChestInventory(Handler handler, Rectangle openingHitbox) {
        this.handler=handler;
        this.openingHitbox=openingHitbox;
        inventoryItems = new ArrayList<Item>();
        inventoryItems.add(Item.logItem);
        inventoryItems.add(Item.rockItem);
        inventoryItems.add(CraftableItem.woodItem);
        inventoryItems.add(CraftableItem.solidWoodItem);
        inventoryItems.add(Item.seedItem);
        inventoryItems.add(CraftableItem.chestItem);
        displayInventoryItems = new ArrayList<Item>();
        activeList = displayInventoryItems;
        chestInventoryItems = new ArrayList<>();
        chestInventoryItems.add(Item.logItem);
        chestInventoryItems.add(Item.logItem);
    }


    public void tick() {

        synchronizeDisplayInventoryItems ();
        Iterator<Item> it = displayInventoryItems.iterator();
        while( it.hasNext() ) {
            Item i= it.next();
            if (i.getCount()<=0)
                it.remove();
        }
        if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_E) && handler.getWorld().getEntityManager().getPlayer().getCollisionBounds(0f,0f).intersects(
                getCollisionBounds(0f,0f)
        )) {
            active = !active;
            handler.getWorld().getEntityManager().getPlayer().getInventory().setOpenable(!active);
        }

        if(!active)
            return;
        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_W))
            selectedItem--;
        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_S))
            selectedItem++;
        if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_A)){
            selectedItem=0;
            activeList=displayInventoryItems;
        }
        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_T )&& activeList.get(selectedItem).getCount()>0){
            if (  activeList.equals(displayInventoryItems) ){
                handler.getWorld().getEntityManager().getPlayer().getInventory().getInventoryItemByID(activeList.get(selectedItem).getId()).setCount(
                        handler.getWorld().getEntityManager().getPlayer().getInventory().getInventoryItemByID(activeList.get(selectedItem).getId()).getCount()-1
                );

                inventoryItems.get(selectedItem).setCount(inventoryItems.get(selectedItem).getCount()-1);

                chestInventoryItems.get(selectedItem).setCount(chestInventoryItems.get(selectedItem).getCount()+1);
            }
            else {
                chestInventoryItems.get(selectedItem).setCount(chestInventoryItems.get(selectedItem).getCount()-1);

                inventoryItems.get(selectedItem).setCount(inventoryItems.get(selectedItem).getCount()+1);

                handler.getWorld().getEntityManager().getPlayer().getInventory().getInventoryItems().get(selectedItem).setCount(
                        handler.getWorld().getEntityManager().getPlayer().getInventory().getInventoryItems().get(selectedItem).getCount()+1
                );
            }
        }

        if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_S)&&displayInventoryItems.size()>0){
            selectedItem=0;
            activeList=chestInventoryItems;
        }

        if (selectedItem<0)
            selectedItem= displayInventoryItems.size()-1;
        else if (selectedItem >= displayInventoryItems.size())
            selectedItem=0;


    }

    private void synchronizeDisplayInventoryItems(){
        for(int i=0; i< inventoryItems.size();i++){
            if(inventoryItems.get(i).getCount()>0) {
                addIfNotAlreadyAdded(inventoryItems.get(i));
            }
        }
    }

    public void render(Graphics g){
        if(!active)
            return;

        g.drawImage(Assets.inventoryScreen, invX, invY, invWidth, invHeight, null);
        int len = activeList.size();
        if ( activeList.equals(displayInventoryItems)){
            Text.drawString(g, "INVENTORY ITEMS", invListCenterX, invImageY-30, true, Color.WHITE, Assets.font56);
        }
        if ( activeList.equals(chestInventoryItems)){
            Text.drawString(g, "CHEST ITEMS", invListCenterX, invImageY-30, true, Color.WHITE, Assets.font56);
        }
        if(len == 0)
            return;

        for(int i = -5;i < 6;i++){
            if(selectedItem + i < 0 || selectedItem + i >= len)
                continue;
            if(i == 0){
                Text.drawString(g, "> " + activeList.get(selectedItem + i).getName() + " <", invListCenterX,
                        invListCenterY + i * invListSpacing, true, Color.YELLOW, Assets.font56);
            }else{
                Text.drawString(g, activeList.get(selectedItem + i).getName(), invListCenterX,
                        invListCenterY + i * invListSpacing, true, Color.WHITE, Assets.font56);
            }
        }
        Item item = activeList.get(selectedItem);
        g.drawImage(item.getTexture(), invImageX, invImageY, invImageWidth, invImageHeight, null);
        Text.drawString(g, Integer.toString(item.getCount()), invCountX, invCountY, true, Color.WHITE, Assets.font56);

    }

    //Inventory Methods

    public void addItem(Item item) {
        for (Item i: inventoryItems) {
            if(i.getId()==item.getId()){
                i.setCount(i.getCount()+item.getCount());
                return;
            }
        }
        inventoryItems.add(item);
    }

    public void addIfNotAlreadyAdded(Item item){
        for (int i=0;i<displayInventoryItems.size();i++){
            if (displayInventoryItems.get(i).equals(item)){
                return;
            }
        }
        displayInventoryItems.add(item);
    }

    public void destroy()
    {
        for(Item i : inventoryItems) {
            i.setCount(0);
        }
        displayInventoryItems.clear();
    }

    public void fillFromDump(NodeList items) {

        destroy();
        for(int i = 0; i<items.getLength(); i++) {
            if(items.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element item = (Element)items.item(i);
                int itemid = Integer.parseInt(item.getElementsByTagName("id").item(0).getTextContent());
                int itemcount = Integer.parseInt(item.getElementsByTagName("count").item(0).getTextContent());
                Item addItem = Item.items[itemid].createNew(itemcount);
                if(itemcount > 0) {
                    this.addItem(addItem);
                }

            }

        }
    }


    public Item getInventoryItemByID(int id){
        Iterator<Item> it = inventoryItems.iterator();
        while (it.hasNext()) {
            Item i = it.next();
            if (i.getId() == id)
                return i;
        }
        return null;
    }

    //Getters and Setters

    public ArrayList<Item> getInventoryItems() {
        return inventoryItems;
    }


    public void setInventoryItems(ArrayList<Item> inventoryItems) {
        this.inventoryItems = inventoryItems;
    }


    public Handler getHandler() {
        return handler;
    }


    public boolean isActive() {
        return active;
    }


    public void setHandler(Handler handler) {
        this.handler = handler;
    }


    public void setActive(boolean active) {
        this.active = active;
    }

    public ArrayList<Item> getDisplayInventoryItems() {
        return displayInventoryItems;
    }

    public void setDisplayInventoryItems(ArrayList<Item> displayInventoryItems) {
        this.displayInventoryItems = displayInventoryItems;
    }

    public boolean isOpenable() {
        return openable;
    }

    public void setOpenable(boolean openable) {
        this.openable = openable;
    }

    public Rectangle getCollisionBounds(float xOffset, float yOffset) {
        return new Rectangle ((int) (openingHitbox.x + xOffset - handler.getGameCamera().getxOffset()),(int) (openingHitbox.y + yOffset - handler.getGameCamera().getyOffset()), openingHitbox.width, openingHitbox.height );
    }
}
