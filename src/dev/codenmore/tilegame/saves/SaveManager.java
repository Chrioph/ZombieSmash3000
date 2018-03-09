package dev.codenmore.tilegame.saves;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.entity.Entity;
import dev.codenmore.tilegame.entity.creatures.Arrow;
import dev.codenmore.tilegame.entity.creatures.Player;
import dev.codenmore.tilegame.items.Item;
import dev.codenmore.tilegame.worlds.WorldGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SaveManager {

    private Handler handler;
    private String saveFilePath;
    private WorldGenerator worldGen;

    public SaveManager(Handler handler, String saveFilePath, WorldGenerator worldGen)
    {
        this.saveFilePath = saveFilePath;
        this.handler = handler;
        this.worldGen = worldGen;
    }

    /**
     * Saves state of game
     *      [CurrentWorld]
     *      WorldID
     *      Entities
     *      [Player]
     *      PlayerInfo
     *      [Homeworld]
     *      HomeworldInfo
     *      Entities
     */
    public long save() {
        String save = "";


        Document dom;
        Element e = null;
        Element e2 = null;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();

            dom = db.newDocument();

            Element rootEle = dom.createElement("save");
            dom.appendChild(rootEle);
            e = dom.createElement("CurrentWorld");

            e2 = dom.createElement("Id");
            e2.appendChild(dom.createTextNode(String.valueOf(handler.getWorld().getId())));
            e.appendChild(e2);


            // Fill world save with entities
            e2 = dom.createElement("Entities");

            String currentWorldDump = handler.getWorld().getEntityManager().dump();

            if(!currentWorldDump.equals("")) {
                for(String entity : currentWorldDump.split("\n")) {
                    String[] entityParts = entity.split(";");
                    String[] coords = entityParts[1].split("/");
                    Element ent = dom.createElement("Entity");
                    Element ent2;
                    // save class name
                    ent2 = dom.createElement("class");
                    ent2.appendChild(dom.createTextNode(entityParts[0]));
                    ent.appendChild(ent2);
                    // save x coord
                    ent2 = dom.createElement("x");
                    ent2.appendChild(dom.createTextNode(coords[0]));
                    ent.appendChild(ent2);
                    // save y coord
                    ent2 = dom.createElement("y");
                    ent2.appendChild(dom.createTextNode(coords[1]));
                    ent.appendChild(ent2);


                    e2.appendChild(ent);
                }
            }

            e.appendChild(e2);
            rootEle.appendChild(e);



            // Fill world save with drops
            e2 = dom.createElement("drops");
            ArrayList<Item> itemDrops = handler.getWorld().getItemManager().getItems();
            for(Item drop : itemDrops) {
                Element drp = dom.createElement("Drop");
                Element drp2;

                drp2 = dom.createElement("id");
                drp2.appendChild(dom.createTextNode(String.valueOf(drop.getId())));
                drp.appendChild(drp2);

                drp2 = dom.createElement("x");
                drp2.appendChild(dom.createTextNode(String.valueOf(drop.getX())));
                drp.appendChild(drp2);

                drp2 = dom.createElement("y");
                drp2.appendChild(dom.createTextNode(String.valueOf(drop.getY())));
                drp.appendChild(drp2);

                e2.appendChild(drp);
            }

            e.appendChild(e2);
            rootEle.appendChild(e);

            // Fill world save with projectiles
            e2 = dom.createElement("projectiles");
            ArrayList<Entity> projectiles = handler.getWorld().getEntityManager().getProjectiles();
            for(Entity projectile : projectiles) {
                Element drp = dom.createElement("Projectile");
                Element drp2;

                drp2 = dom.createElement("class");
                drp2.appendChild(dom.createTextNode(projectile.getClass().getName()));
                drp.appendChild(drp2);

                drp2 = dom.createElement("x");
                drp2.appendChild(dom.createTextNode(String.valueOf(projectile.getX())));
                drp.appendChild(drp2);

                drp2 = dom.createElement("y");
                drp2.appendChild(dom.createTextNode(String.valueOf(projectile.getY())));
                drp.appendChild(drp2);

                if(projectile instanceof Arrow) {
                    drp2 = dom.createElement("direction");
                    drp2.appendChild(dom.createTextNode(String.valueOf(((Arrow) projectile).getDirection())));
                    drp.appendChild(drp2);

                    drp2 = dom.createElement("damage");
                    drp2.appendChild(dom.createTextNode(String.valueOf(((Arrow) projectile).getDamage())));
                    drp.appendChild(drp2);

                    drp2 = dom.createElement("isArrow");
                    drp2.appendChild(dom.createTextNode("true"));
                    drp.appendChild(drp2);
                }else {
                    drp2 = dom.createElement("isArrow");
                    drp2.appendChild(dom.createTextNode("false"));
                    drp.appendChild(drp2);
                }

                e2.appendChild(drp);
            }

            e.appendChild(e2);
            rootEle.appendChild(e);

            // Save Player info
            Player player = handler.getWorld().getEntityManager().getPlayer();

            e = dom.createElement("Player");
            // x position of player
            e2 = dom.createElement("x");
            e2.appendChild(dom.createTextNode(String.valueOf(player.getX())));
            e.appendChild(e2);
            // y position of player
            e2 = dom.createElement("y");
            e2.appendChild(dom.createTextNode(String.valueOf(player.getY())));
            e.appendChild(e2);

            e2 = dom.createElement("health");
            e2.appendChild(dom.createTextNode(String.valueOf(player.getHealth())));
            e.appendChild(e2);

            e2 = dom.createElement("armor");
            e2.appendChild(dom.createTextNode(String.valueOf(player.getArmor())));
            e.appendChild(e2);

            e2 = dom.createElement("ammunition");
            e2.appendChild(dom.createTextNode(String.valueOf(player.getAmmunition())));
            e.appendChild(e2);

            e2 = dom.createElement("maxhealth");
            e2.appendChild(dom.createTextNode(String.valueOf(player.getMaxHealth())));
            e.appendChild(e2);

            e2 = dom.createElement("maxarmor");
            e2.appendChild(dom.createTextNode(String.valueOf(player.getMaxArmor())));
            e.appendChild(e2);

            e2 = dom.createElement("damage");
            e2.appendChild(dom.createTextNode(String.valueOf(player.getDamage())));
            e.appendChild(e2);

            e2 = dom.createElement("rangeddamage");
            e2.appendChild(dom.createTextNode(String.valueOf(player.getRangedDamage())));
            e.appendChild(e2);

            // Inventory
            e2 = dom.createElement("Inventory");
            for(Item i : player.getInventory().getInventoryItems()) {
                Element item = dom.createElement("Item");
                Element clss = dom.createElement("id");
                clss.appendChild(dom.createTextNode(String.valueOf(i.getId())));
                item.appendChild(clss);
                Element cnt = dom.createElement("count");
                cnt.appendChild(dom.createTextNode(String.valueOf(i.getCount())));
                item.appendChild(cnt);
                e2.appendChild(item);
            }
            e.appendChild(e2);

            rootEle.appendChild(e);

            e = dom.createElement("Homeworld");
            e2 = dom.createElement("Entities");

            String homeWorldDump = handler.getHomeWorld().getEntityManager().dump();
            if(!homeWorldDump.equals("")) {
                for(String entity : homeWorldDump.split("\n")) {
                    String[] entityParts = entity.split(";");
                    String[] coords = entityParts[1].split("/");
                    Element ent = dom.createElement("Entity");
                    Element ent2;
                    // save class name
                    ent2 = dom.createElement("class");
                    ent2.appendChild(dom.createTextNode(entityParts[0]));
                    ent.appendChild(ent2);
                    // save x coord
                    ent2 = dom.createElement("x");
                    ent2.appendChild(dom.createTextNode(coords[0]));
                    ent.appendChild(ent2);
                    // save y coord
                    ent2 = dom.createElement("y");
                    ent2.appendChild(dom.createTextNode(coords[1]));
                    ent.appendChild(ent2);


                    e2.appendChild(ent);
                }
            }

            e.appendChild(e2);
            rootEle.appendChild(e);

            // Fill world save with projectiles
            e2 = dom.createElement("projectiles");
            ArrayList<Entity> homeProjectiles = handler.getHomeWorld().getEntityManager().getProjectiles();
            for(Entity projectile : homeProjectiles) {
                Element drp = dom.createElement("Projectile");
                Element drp2;

                drp2 = dom.createElement("class");
                drp2.appendChild(dom.createTextNode(projectile.getClass().getName()));
                drp.appendChild(drp2);

                drp2 = dom.createElement("x");
                drp2.appendChild(dom.createTextNode(String.valueOf(projectile.getX())));
                drp.appendChild(drp2);

                drp2 = dom.createElement("y");
                drp2.appendChild(dom.createTextNode(String.valueOf(projectile.getY())));
                drp.appendChild(drp2);

                if(projectile instanceof Arrow) {
                    drp2 = dom.createElement("direction");
                    drp2.appendChild(dom.createTextNode(String.valueOf(((Arrow) projectile).getDirection())));
                    drp.appendChild(drp2);

                    drp2 = dom.createElement("damage");
                    drp2.appendChild(dom.createTextNode(String.valueOf(((Arrow) projectile).getDamage())));
                    drp.appendChild(drp2);

                    drp2 = dom.createElement("isArrow");
                    drp2.appendChild(dom.createTextNode("true"));
                    drp.appendChild(drp2);
                }else {
                    drp2 = dom.createElement("isArrow");
                    drp2.appendChild(dom.createTextNode("false"));
                    drp.appendChild(drp2);
                }

                e2.appendChild(drp);
            }

            e.appendChild(e2);
            rootEle.appendChild(e);


            e2 = dom.createElement("drops");
            ArrayList<Item> homeworldItemDrops = handler.getHomeWorld().getItemManager().getItems();
            for(Item drop : homeworldItemDrops) {
                Element drp = dom.createElement("Drop");
                Element drp2;

                drp2 = dom.createElement("id");
                drp2.appendChild(dom.createTextNode(String.valueOf(drop.getId())));
                drp.appendChild(drp2);

                drp2 = dom.createElement("x");
                drp2.appendChild(dom.createTextNode(String.valueOf(drop.getX())));
                drp.appendChild(drp2);

                drp2 = dom.createElement("y");
                drp2.appendChild(dom.createTextNode(String.valueOf(drop.getY())));
                drp.appendChild(drp2);
            }

            e.appendChild(e2);
            rootEle.appendChild(e);


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(dom);
            StreamResult result = new StreamResult(new File(saveFilePath));
            transformer.transform(source, result);




        } catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
        return System.currentTimeMillis();

    }

    /**
     * Loads state of game, see @save() for syntax of dump string
     */
    public long load() {
        File file = new File(saveFilePath);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);


            Element currWorld = (Element)document.getElementsByTagName("CurrentWorld").item(0);
            String currWorldId = currWorld.getElementsByTagName("Id").item(0).getTextContent();
            worldGen.setWorld(Integer.parseInt(currWorldId));

            // Fill world with entities
            NodeList currWorldEntities = currWorld.getElementsByTagName("Entities").item(0).getChildNodes();
            worldGen.getCurrentWorld().getEntityManager().fillFromDump(currWorldEntities);

            // Fill world with drops
            NodeList currWorldDrops = currWorld.getElementsByTagName("drops").item(0).getChildNodes();
            worldGen.getCurrentWorld().getItemManager().fillFromDump(currWorldDrops);

            // Fill world with projectiles
            NodeList currWorldProjectiles = currWorld.getElementsByTagName("projectiles").item(0).getChildNodes();
            worldGen.getCurrentWorld().getEntityManager().fillProjectilesFromDump(currWorldProjectiles);

            // set world
            handler.setWorld(worldGen.getCurrentWorld());

            Element homeworld = (Element)document.getElementsByTagName("Homeworld").item(0);

            // Fill homeworld with entities
            NodeList homeworldEntities = homeworld.getElementsByTagName("Entities").item(0).getChildNodes();
            worldGen.getHomeWorld().getEntityManager().fillFromDump(homeworldEntities);

            // Fill homeworld with drops
            NodeList homeworldDrops = homeworld.getElementsByTagName("drops").item(0).getChildNodes();
            worldGen.getHomeWorld().getItemManager().fillFromDump(homeworldDrops);

            // Fill homeworld with projectiles
            NodeList homeWorldProjectiles = homeworld.getElementsByTagName("projectiles").item(0).getChildNodes();
            worldGen.getHomeWorld().getEntityManager().fillProjectilesFromDump(homeWorldProjectiles);




            Element playerElement = (Element)document.getElementsByTagName("Player").item(0);
            String playerX = playerElement.getElementsByTagName("x").item(0).getTextContent();
            String playerY = playerElement.getElementsByTagName("y").item(0).getTextContent();
            int health = Integer.parseInt(playerElement.getElementsByTagName("health").item(0).getTextContent());
            int armor = Integer.parseInt(playerElement.getElementsByTagName("armor").item(0).getTextContent());
            int ammunition = Integer.parseInt(playerElement.getElementsByTagName("ammunition").item(0).getTextContent());
            int maxarmor = Integer.parseInt(playerElement.getElementsByTagName("maxarmor").item(0).getTextContent());
            int maxhealth = Integer.parseInt(playerElement.getElementsByTagName("maxhealth").item(0).getTextContent());
            int damage = Integer.parseInt(playerElement.getElementsByTagName("damage").item(0).getTextContent());
            int rangeddamage = Integer.parseInt(playerElement.getElementsByTagName("rangeddamage").item(0).getTextContent());
            NodeList items = playerElement.getElementsByTagName("Inventory").item(0).getChildNodes();


            Player player = handler.getWorld().getEntityManager().getPlayer();
            player.setHealth(health);
            player.setArmor(armor);
            player.setAmmunition(ammunition);
            player.setMaxArmor(maxarmor);
            player.setMaxHealth(maxhealth);
            player.setDamage(damage);
            player.setRangedDamage(rangeddamage);

            player.getInventory().fillFromDump(items);

            player.setX(Float.parseFloat(playerX));
            player.setY(Float.parseFloat(playerY));

        }catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }catch(IOException | SAXException e){
            System.out.println(e.getMessage());
        }
        return System.currentTimeMillis();


    }
}
