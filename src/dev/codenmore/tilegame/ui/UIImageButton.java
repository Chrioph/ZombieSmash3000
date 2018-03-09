package dev.codenmore.tilegame.ui;

import dev.codenmore.tilegame.Maths.Matrix4f;
import dev.codenmore.tilegame.Maths.Vector3f;
import dev.codenmore.tilegame.Settings;
import dev.codenmore.tilegame.gfx.Shader;
import dev.codenmore.tilegame.gfx.Texture;
import dev.codenmore.tilegame.gfx.VertexArray;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class UIImageButton extends UIObject{

	private float SIZE = 2.0f;
	private BufferedImage[] images;
	private ClickListener clicker;
	private VertexArray mesh;
	private Texture[] textures;
	private Vector3f position;
	
	public UIImageButton(float x, float y , int width, int height, BufferedImage[] images, ClickListener clicker) {
		super (x,y,width, height);
		this.images=images;
		this.clicker=clicker;
		if(Settings.getOpenGl()) {
			float[] vertices = new float[] {
					-SIZE / 2.0f, -SIZE / 4.0f, 0.2f,
					-SIZE / 2.0f,  SIZE / 4.0f, 0.2f,
					SIZE / 2.0f,  SIZE / 4.0f, 0.2f,
					SIZE / 2.0f, -SIZE / 4.0f, 0.2f
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
			position = new Vector3f(1f, 1f, 0f);
			textures = new Texture[2];
			textures[0] = new Texture(images[0]);
			textures[1] = new Texture(images[1]);
		}
	}

	@Override
	public void tick() {
		
	}

	@Override
	public void render(Graphics g) {
		super.render(g);
		if (hovering)
			g.drawImage(images[1], (int) x, (int)y,width,height,null);
		else
			g.drawImage(images[0], (int) x, (int)y,width,height,null);
	}

	@Override
	public void renderOpenGL()
	{
		super.renderOpenGL();
		Shader.ENTITY.enable();
		Shader.ENTITY.setUniformMat4f("ml_matrix", Matrix4f.translate(position));
		if(hovering) {
			textures[1].bind();
		}else {
			textures[0].bind();
		}
		mesh.render();
		Shader.ENTITY.disable();
	}

	@Override
	public void onClick() {
		clicker.onClick();
	}
	
	
	
	

}
