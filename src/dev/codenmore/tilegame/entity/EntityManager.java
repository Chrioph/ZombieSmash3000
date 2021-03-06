package dev.codenmore.tilegame.entity;

import java.awt.Graphics;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.Modifiers.Mod;
import dev.codenmore.tilegame.entity.creatures.Enemies.Enemy;
import dev.codenmore.tilegame.entity.creatures.Player;
import dev.codenmore.tilegame.entity.statics.Chest;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class EntityManager implements Serializable{

	private Handler handler;
	private Player player;
	private ArrayList<Entity> entities;
	private ArrayList<Entity> projectiles;
	private ArrayList<Entity> queue;
	private ArrayList<Entity>  chests;
	private Comparator<Entity> renderSorter = new Comparator<Entity>() {
		public int compare(Entity a, Entity b) {
			if (a.getY() + a.getHeight() <b.getY() + b.getHeight())
				return -1;
			else 
				return 1;
						
		}
	};




	public EntityManager(Handler handler, Player player) {
		this.handler=handler;
		this.player=player;
		initArrays();
		entities.add(player);

	}

	/**
	 * No args constructor for serialization
	 */
	public EntityManager()
	{
		initArrays();
	}

	private void initArrays()
	{
		chests =  new ArrayList<Entity>();
		entities = new ArrayList<Entity>();
		projectiles= new ArrayList<Entity>();
		queue= new ArrayList<Entity>();
	}

	public String dump()
	{
		StringBuilder dump = new StringBuilder("");

		for(Entity e : entities) {
			if(!(e instanceof Player)) {
				dump.append(e.dump()).append("\n");
			}
		}
		return dump.toString();
	}

	public void fillFromDump(NodeList dump)
	{
		// first clear current entities
		Player myPlayer = this.getPlayer();
		entities.clear();
		entities.add(myPlayer);
			for(int i = 0; i < dump.getLength(); i++) {
				if (dump.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element ent = (Element) dump.item(i);
					try {
						Class<?> clazz = Class.forName(ent.getElementsByTagName("class").item(0).getTextContent());
						Constructor<?> constructor = clazz.getConstructor(Handler.class, float.class, float.class);
						float xCoord = Float.parseFloat(ent.getElementsByTagName("x").item(0).getTextContent());
						float yCoord = Float.parseFloat(ent.getElementsByTagName("y").item(0).getTextContent());
						Object instance = constructor.newInstance(handler, xCoord, yCoord);

						entities.add((Entity) instance);
					} catch (Exception e) {
						System.out.println(e.getMessage() + " not found");
					}
				}
			}
	}

	public void fillProjectilesFromDump(NodeList dump)
	{
		// first clear current entities
		projectiles.clear();
		for(int i = 0; i < dump.getLength(); i++) {
			if (dump.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element ent = (Element) dump.item(i);
				try {

					float xCoord = Float.parseFloat(ent.getElementsByTagName("x").item(0).getTextContent());
					float yCoord = Float.parseFloat(ent.getElementsByTagName("y").item(0).getTextContent());
					boolean isArrow = ent.getElementsByTagName("isArrow").item(0).getTextContent().equals("true");
					Object instance;
					if(isArrow) {
						int direction = Integer.parseInt(ent.getElementsByTagName("direction").item(0).getTextContent());
						int damage = Integer.parseInt(ent.getElementsByTagName("damage").item(0).getTextContent());
						Class<?> clazz = Class.forName(ent.getElementsByTagName("class").item(0).getTextContent());
						Constructor<?> constructor = clazz.getConstructor(Handler.class, float.class, float.class, int.class, int.class);
						instance = constructor.newInstance(handler, xCoord, yCoord, direction, damage);
					}else {
						Class<?> clazz = Class.forName(ent.getElementsByTagName("class").item(0).getTextContent());
						Constructor<?> constructor = clazz.getConstructor(Handler.class, float.class, float.class);
						instance = constructor.newInstance(handler, xCoord, yCoord);
					}



					projectiles.add((Entity) instance);
				} catch (Exception e) {
					System.out.println(e.getMessage() + " not found");
				}
			}
		}
	}
	
	
	public void tick() {
		if(queue.size()>0) {
			Iterator<Entity> it2 = entities.iterator();
			while (it2.hasNext()) {

				Entity e = it2.next();
				projectiles.add(e);
			}
		}

		queue=new ArrayList<Entity>();

		Iterator<Entity> it = entities.iterator();
		while( it.hasNext() ) {
			
			Entity e= it.next();
			e.tick();
			if (!e.isActive())
				it.remove();
			
		}

		entities.sort(renderSorter);
		
		Iterator<Entity> it1 = projectiles.iterator();
		while( it1.hasNext() ) {
			
			Entity e= it1.next();
			e.tick();
			if (!e.isActive())
				it1.remove();

		}
		projectiles.sort(renderSorter);
		
	}
	public void render(Graphics g) {

		

			for(Entity e : projectiles) {
			e.render(g);
		}
		for(Entity e : entities) {
			e.render(g);
		}
		player.postRender(g);
		for(Entity e : chests){
			e.postrender(g);
		}

	}


	public void addEntity(Entity e) {
		entities.add(e);
		if(e.isChest())
			chests.add(e);
	}
	
	public void addProjectile(Entity e) {
		projectiles.add(e);
		if(e.isChest())
			chests.add(e);
	}

	public void addToQueue(Entity e) {queue.add(e);}
	
	
	public void applyMods(ArrayList<Mod> mods)
	{
		for(Entity en : entities) {
			if(en instanceof Enemy) {
				Enemy enem = (Enemy) en;
				enem.setMods(mods);
			}
		}
	}
	
	
	
	//Getters and Setters

	public Handler getHandler() {
		return handler;
	}


	public ArrayList<Entity> getProjectiles() {
		return projectiles;
	}


	public void setProjectiles(ArrayList<Entity> projectiles) {
		this.projectiles = projectiles;
	}


	public void setHandler(Handler handler) {
		this.handler = handler;
	}


	public Player getPlayer() {
		return player;
	}


	public void setPlayer(Player player) {
		this.player = player;
	}

	public ArrayList<Entity> getQueue() {
		return queue;
	}

	public void setQueue(ArrayList<Entity> queue) {
		this.queue = queue;
	}


	public ArrayList<Entity> getEntities() {
		return entities;
	}


	public void setEntities(ArrayList<Entity> entities) {
		this.entities = entities;
	}
	
	
}
