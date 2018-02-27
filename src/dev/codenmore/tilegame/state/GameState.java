package dev.codenmore.tilegame.state;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.HUDs.HUD;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.gfx.Text;
import dev.codenmore.tilegame.worlds.World;
import dev.codenmore.tilegame.worlds.WorldGenerator;

public class GameState extends State {
	
	
	private boolean b=false;
	private long timeUntilNextRender;
	private float v = 0.01f;
	
	private World world;
	private WorldGenerator worldGen;

	private HUD hud;
	
	
	public GameState(Handler handler) {
		super(handler);
		this.worldGen = handler.getGame().getWorldGenerator();
		world= this.worldGen.getFirstWorld();
		world.start();
		handler.setWorld(world);

		hud=new HUD(handler);
		
	}
	
	@Override
	public void tick() {
		// TODO Auto-generated method stub
		if(!handler.getWorld().getEntityManager().getPlayer().isDead())
			world.tick();
			listenExit();
			if(handler.getWorld().getEntityManager().getPlayer().collisionWithFinish(
					(int)(handler.getWorld().getEntityManager().getPlayer().getX() /128),
					(int) (handler.getWorld().getEntityManager().getPlayer().getY() /128 ))) {
				World nextWorld;
				if((nextWorld = this.worldGen.getNextWorld()) != null) {
					this.world=nextWorld;
					handler.setWorld(this.world);
					this.world.start();
				}
	
			}
	}

	@Override
	public void render(Graphics g) {
		// TODO Auto-generated method stub
		world.render(g);
		hud.render(g);
		checkPlayerDie(g);

	}
	
	private void checkPlayerDie(Graphics g) {
		if(handler.getWorld().getEntityManager().getPlayer().isDead()) {
			if(!b) {
				timeUntilNextRender=System.currentTimeMillis()+10;
				b=true;
			}
			paintComponent(Assets.deathScreen,g,v);
			Text.drawString(g, "YOU DIED", 	handler.getWidth()/2, handler.getHeight()/2 + 200, true, Color.RED, Assets.font100);
			if(System.currentTimeMillis()>timeUntilNextRender&&v<0.99) {
				v+=0.01;
				b=false;
			}
			handler.getWorld().getEntityManager().getPlayer().render(g);
		}
	}
	
	public void paintComponent(BufferedImage i,Graphics g, float v) {
	    Graphics2D g2d = (Graphics2D)g;
	    AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, v);
	    g2d.setComposite(composite);
	    g2d.drawImage(i, 0, 0, handler.getWidth(), handler.getHeight(),null);
	}
	
	private void listenExit() {
		if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_ESCAPE)) {
			handler.getWorld().getEntityManager().getPlayer().setDead(false);
			handler.getWorld().getEntityManager().getPlayer().setAmmunition(5);
			handler.getWorld().getEntityManager().getPlayer().setDamage(3);
			handler.getWorld().getEntityManager().getPlayer().setArmor(4);
			handler.getWorld().getEntityManager().getPlayer().setMaxHealth(10);
			handler.getWorld().getEntityManager().getPlayer().setRangedDamage(2);
			handler.getWorld().getEntityManager().getPlayer().setHealth(10);
			handler.getWorld().getEntityManager().getPlayer().setRangedToggled(false);
			State.setState(new MenuState(handler));
		}
	}

}
