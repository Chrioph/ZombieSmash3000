package dev.codenmore.tilegame.entity;

import java.awt.*;
import java.util.ArrayList;


import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.Maths.Matrix4f;
import dev.codenmore.tilegame.Maths.Vector3f;
import dev.codenmore.tilegame.Modifiers.Mod;
import dev.codenmore.tilegame.Settings;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.gfx.Shader;
import dev.codenmore.tilegame.gfx.Texture;
import dev.codenmore.tilegame.gfx.VertexArray;

public abstract class Entity {
	
	protected float x,y;

	protected int width, height;

	protected Handler handler;
	protected Rectangle bounds;
	protected int health;
	protected boolean active=true;
	protected int knockbackCounter;


	// OpenGL
	private VertexArray mesh;
	private Texture texture;
	private float SIZE = 1.0f;
	private Vector3f position = new Vector3f();
	private float rot;
	

	public static final int DEFAULT_HEALTH = 10;
	
	public Entity(Handler handler, float x,float y, int width, int height) {

		if(Settings.getOpenGl()) {
			float[] vertices = new float[] {
					-SIZE / 2.0f, -SIZE / 2.0f, 0.2f,
					-SIZE / 2.0f,  SIZE / 2.0f, 0.2f,
					SIZE / 2.0f,  SIZE / 2.0f, 0.2f,
					SIZE / 2.0f, -SIZE / 2.0f, 0.2f
			};

			byte[] indices = new byte[] {
					0, 1, 2,
					2, 3, 0
			};

			float[] tcs = new float[] {
					0, 1,
					0, 0,
					1, 0,
					1, 1
			};


			mesh = new VertexArray(vertices, indices, tcs);
			texture = new Texture(Assets.player1LookingDown);
		}


		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.handler=handler;
		health =DEFAULT_HEALTH;
		bounds = new Rectangle(0,0, width , height);
	}

	public String dump() {
		String dump = "";
		// just save type of entity and position
		dump += this.getClass().getName() + ";" + this.x + "/" + this.y;

		return dump;
	}
	
	public abstract void tick();
	
	public void render(Graphics g) {
		if(Settings.getDebug()) {
			Rectangle collBounds = getCollisionBounds(0f, 0f);
			g.setColor(Color.RED);
			g.fillRect(collBounds.x,collBounds.y, bounds.width, bounds.height);
		}

	}

	public void renderOpenGL()
	{
		Shader.ENTITY.enable();
		//Shader.ENTITY.setUniformMat4f("ml_matrix", Matrix4f.translate(position).multiply(Matrix4f.rotate(rot)));
		texture.bind();
		mesh.render();
		Shader.ENTITY.disable();
	}

	public void knockback() {
		
	}
	
	public abstract void die();
	
	public void hurt (int amt) {
		health -=amt;
		if (health<=0&& !this.isPlayer()) {
			active=false;
			die();
		}
	}
	
	public boolean checkEntityCollision(float xOffset, float yOffset) {
		
		for (Entity e: handler.getWorld().getEntityManager().getEntities()) {
			if (e.equals(this))
				continue;
			if ( e.getCollisionBounds(0f, 0f).intersects(getCollisionBounds(xOffset, yOffset)))
				return true;
		}
		for (Entity e: handler.getWorld().getEntityManager().getProjectiles()) {
			if (e.equals(this))
				continue;
			if ( e.getCollisionBounds(0f, 0f).intersects(getCollisionBounds(xOffset, yOffset)))
				return true;
		}
		return false;
	}
	
	
	public boolean isPlayer() {
		return false;
	}
	
	

	public Rectangle getCollisionBounds(float xOffset, float yOffset) {
		return new Rectangle ((int) (x+bounds.x + xOffset - handler.getGameCamera().getxOffset()),(int) (y +bounds.y + yOffset - handler.getGameCamera().getyOffset()), bounds.width, bounds.height );
	}

	
	
	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	

	public int getKnockbackCounter() {
		return knockbackCounter;
	}

	public void setKnockbackCounter(int knockbackCounter) {
		this.knockbackCounter = knockbackCounter;
	}


	

}
