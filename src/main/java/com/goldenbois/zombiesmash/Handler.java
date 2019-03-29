package com.goldenbois.zombiesmash;

import com.goldenbois.zombiesmash.gfx.GameCamera;
import com.goldenbois.zombiesmash.input.KeyManager;
import com.goldenbois.zombiesmash.input.MouseManager;
import com.goldenbois.zombiesmash.worlds.World;

import java.io.Serializable;

public class Handler implements Serializable {
	
	private transient Game game;
	private World world;
	private World nextWorld;
	private World homeWorld;
	
	public Handler(Game game){
		
		this.game=game;
		
	}

	public int getWidth() {
		return game.getWidth();
	}
	
	public int getHeight() {
		return game.getHeight();
	}
	
	public KeyManager getKeyManager() {
		return game.getKeyManager();
	}
	
	public MouseManager getMouseManager() {
		return game.getMouseManager();
	}
	
	public GameCamera getGameCamera() {
		return game.getGameCamera();
	}
	
	//Getters and Setters

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public World getHomeWorld() {
		return homeWorld;
	}

	public void setHomeWorld(World world) {
		this.homeWorld = world;
	}

	public World getNextWorld() {
		return nextWorld;
	}

	public void setNextWorld(World world) {
		this.nextWorld = world;
	}

	public void destroy()
	{
		this.game = null;
		this.world.getEntityManager().getPlayer().getInventory().destroy();
		this.world = null;
		this.homeWorld = null;
	}
	
	
}
