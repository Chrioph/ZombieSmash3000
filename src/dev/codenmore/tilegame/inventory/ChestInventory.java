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

public class ChestInventory {
    private Handler handler;
    private boolean active=false;
    private ArrayList<Item> inventoryItems;
    private ArrayList<Item> displayInventoryItems;
    private ArrayList<Item> chestItems;
    private ArrayList<Item> activeList;
    private Rectangle openingHitbox;


    private int invX=192, invY=108, invWidth=1920-192*2, invHeight=1080-108*2,
            invListCenterX = invX + invWidth/3-1, invListCenterY= invY + invHeight / 2 + 5*(invHeight/384),
            invListSpacing=68;

    private int invImageX = 1380,
            invImageY= 190,
            invImageWidth= 128,
            invImageHeight=128;

    private int invCountX=  1450, invCountY= 388;

    private int selectedItem=0;

    public ChestInventory(Handler handler,Rectangle openingHitbox) {
        this.handler=handler;
        this.openingHitbox=openingHitbox;
        this.openingHitbox=getCollisionBounds(0f,0f);
        inventoryItems = new ArrayList<Item>();
        inventoryItems.add(Item.logItem);
        inventoryItems.add(Item.rockItem);
        inventoryItems.add(CraftableItem.woodItem);
        inventoryItems.add(CraftableItem.solidWoodItem);
        inventoryItems.add(Item.seedItem);
        inventoryItems.add(CraftableItem.chestItem);
        displayInventoryItems = new ArrayList<Item>();
        chestItems=new ArrayList<Item>();
        activeList=displayInventoryItems;

    }


    public void tick() {

        inventoryItems = handler.getWorld().getEntityManager().getPlayer().getInventory().getInventoryItems();
        for(int i=0; i< inventoryItems.size();i++){
            if(inventoryItems.get(i).getCount()>0) {
                addIfNotAlreadyAdded(inventoryItems.get(i));
            }
        }

        Iterator<Item> it = displayInventoryItems.iterator();
        while( it.hasNext() ) {
            Item i= it.next();
            if (i.getCount()<=0)
                it.remove();
        }

        if(handler.getWorld().getEntityManager().getPlayer().getCollisionBounds(0f,0f).intersects(openingHitbox)&&handler.getKeyManager().keyJustPressed(KeyEvent.VK_E)){
            System.out.println("Player in opening area");
            handler.getWorld().getEntityManager().getPlayer().getInventory().setActive(false);
            active=!active;
        }

        if(!handler.getWorld().getEntityManager().getPlayer().getCollisionBounds(0f,0f).intersects(openingHitbox))
            active=false;

        handler.getWorld().getEntityManager().getPlayer().setInputDisabled(active);
        if(!active)
            return;

        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_W))
            selectedItem--;

        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_S)) {

            selectedItem++;
        }
        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_A)) {
            selectedItem=0;
            activeList = displayInventoryItems;
        }

        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_T)) {
            if(handler.getWorld().getEntityManager().getPlayer().getInventory().getDisplayInventoryItems().get(selectedItem).getCount()>0)
                chestItems.add(displayInventoryItems.get(selectedItem));
                handler.getWorld().getEntityManager().getPlayer().getInventory().getDisplayInventoryItems().get(selectedItem).
                        setCount(handler.getWorld().getEntityManager().getPlayer().getInventory().getDisplayInventoryItems().get(selectedItem).getCount()-1);
                handler.getWorld().getEntityManager().getPlayer().getInventory().getInventoryItemByID(handler.getWorld().getEntityManager().getPlayer().getInventory().getDisplayInventoryItems().get(selectedItem).getId()).
                        setCount( handler.getWorld().getEntityManager().getPlayer().getInventory().getInventoryItemByID(handler.getWorld().getEntityManager().getPlayer().getInventory().getDisplayInventoryItems().get(selectedItem).getId()).getCount()-1);
        }

        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_D)) {
            selectedItem=0;
            if(chestItems.size()>0)
                activeList = chestItems;
        }
        if (selectedItem<0)
            selectedItem= displayInventoryItems.size()-1;
        else if (selectedItem >= displayInventoryItems.size())
            selectedItem=0;
    }

    public void render(Graphics g){
        if(!active)
            return;

        if(activeList.equals(displayInventoryItems)){
            Text.drawString(g,"Inventory" ,invListCenterX,invImageY-40,true,Color.WHITE,Assets.font56);
        }

        else {
            Text.drawString(g, "ChestInventory", invListCenterX, invImageY-40, true, Color.WHITE, Assets.font56);
        }

        g.drawImage(Assets.inventoryScreen, invX, invY, invWidth, invHeight, null);
        int len = activeList.size();
        if(len == 0)
            return;

        for(int i = -5;i < 6;i++){
            if(selectedItem + i < 0 || selectedItem + i >= len)
                continue;
            if(i == 0){
                Text.drawString(g, "> " +activeList.get(selectedItem + i).getName() + " <", invListCenterX,
                        invListCenterY + i * invListSpacing, true, Color.YELLOW, Assets.font56);
             }else{
                Text.drawString(g, activeList.get(selectedItem + i).getName(), invListCenterX,
                        invListCenterY + i * invListSpacing, true, Color.WHITE, Assets.font56);
            }
        }
        Item item = activeList.get(selectedItem);
        g.drawImage(item.getTexture(), invImageX, invImageY, invImageWidth, invImageHeight, null);
        Text.drawString(g, Integer.toString(item.getCount()), invCountX, invCountY, true, Color.WHITE, Assets.font56);

        if (Settings.getDebug()){
            g.setColor(Color.ORANGE);
            g.fillRect(openingHitbox.x,openingHitbox.y,openingHitbox.width,openingHitbox.height);
        }
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
    public Rectangle getCollisionBounds(float xOffset, float yOffset) {
        return new Rectangle ((int) (openingHitbox.x + xOffset - handler.getGameCamera().getxOffset()),(int) (openingHitbox.y + yOffset - handler.getGameCamera().getyOffset()), openingHitbox.width, openingHitbox.height );
    }
}
