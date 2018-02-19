package dev.codenmore.tilegame.entity;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.entity.creatures.Player;

public class EntityManager {

	private Handler handler;
	private Player player;
	private ArrayList<Entity> entities;
	private ArrayList<Entity> projectiles;
	private Comparator<Entity> renderSorter = new Comparator<Entity>() {
		@Override
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
		entities = new ArrayList<Entity>();
		entities.add(player);
		projectiles= new ArrayList<Entity>();
	}
	
	
	public void tick() {
		Iterator<Entity> it = entities.iterator();
		while( it.hasNext() ) {
			
			Entity e= it.next();
			e.tick();
			if (!e.isActive())
				it.remove();
			
		}
		entities.sort(renderSorter);
		
		Iterator<Entity> ite = projectiles.iterator();
		while( ite.hasNext() ) {
			
			Entity e= ite.next();
			e.tick();
			if (!e.isActive())
				ite.remove();
			
		}
		projectiles.sort(renderSorter);
	}
	public void render(Graphics g) {
		for(Entity e : entities) {
			e.render(g);
		}
		player.postRender(g);
		
		for(Entity e : projectiles) {
			e.render(g);
		}
	}

	public void addEntity(Entity e) {
		entities.add(e);
	}
	
	public void addProjectile(Entity e) {
		projectiles.add(e);
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


	public ArrayList<Entity> getEntities() {
		return entities;
	}


	public void setEntities(ArrayList<Entity> entities) {
		this.entities = entities;
	}
	
	
}
