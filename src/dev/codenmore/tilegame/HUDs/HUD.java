package dev.codenmore.tilegame.HUDs;

import java.awt.Color;
import java.awt.Graphics;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.gfx.Text;

public class HUD {
	private Handler handler;
	
	public HUD(Handler handler) {
		this.handler=handler;
	}
	
	public void render(Graphics g) {
		if (!handler.getWorld().getEntityManager().getPlayer().getInventory().isActive()) {
			g.drawImage(Assets.HUDWindow, 5, 5, 750, 120, null);
			g.drawImage(Assets.heart, 30, 50,64,64, null);
			g.drawImage(Assets.sword, 144, 50,48,48, null);
			g.drawImage(Assets.bow, 242, 50,48,48, null);
			g.drawImage(Assets.armor, 340, 50,64,64, null);
			g.drawImage(Assets.arrow, 454, 50,48,48, null);
			
			
			Text.drawString(g,""+handler.getWorld().getEntityManager().getPlayer().getHealth(),       				56 , 40, true, Color.WHITE, Assets.font40);
			Text.drawString(g, ""+ handler.getWorld().getEntityManager().getPlayer().getDamage(),     				167, 40, true, Color.WHITE, Assets.font40);
			Text.drawString(g, ""+ handler.getWorld().getEntityManager().getPlayer().getRangedDamage(),      	    267, 40, true, Color.WHITE, Assets.font40);
			Text.drawString(g, ""+ handler.getWorld().getEntityManager().getPlayer().getArmor(),      				370, 40, true, Color.WHITE, Assets.font40);
			Text.drawString(g, ""+ handler.getWorld().getEntityManager().getPlayer().getAmmunition(), 				480, 40, true, Color.WHITE, Assets.font40);
			Text.drawString(g, "Mode",     															  				590, 40, true, Color.WHITE, Assets.font40);
			if(handler.getWorld().getEntityManager().getPlayer().isRangedToggled())
				g.drawImage(Assets.bow, 552, 50,48,48, null);
			if(!handler.getWorld().getEntityManager().getPlayer().isRangedToggled())
				g.drawImage(Assets.sword, 552, 50,48,48, null);
		}
	}
}
