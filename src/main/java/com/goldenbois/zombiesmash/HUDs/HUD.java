package com.goldenbois.zombiesmash.HUDs;

import org.newdawn.slick.*;

import com.goldenbois.zombiesmash.Handler;
import com.goldenbois.zombiesmash.gfx.Assets;

public class HUD {
	private Handler handler;
	
	public HUD(Handler handler) {
		this.handler=handler;
	}

	// TODO fix rendering for slick
	public void render(Graphics g) {
		if (!handler.getWorld().getEntityManager().getPlayer().getInventory().isActive()&&!handler.getWorld().getEntityManager().getPlayer().getCraftingScreen().isActive()) {
			g.drawImage(Assets.HUDWindow, 5, 5);
			g.drawImage(Assets.heart, 30, 50);
			g.drawImage(Assets.sword, 144, 50);
			g.drawImage(Assets.bow, 242, 50);
			g.drawImage(Assets.armor, 340, 50);
			g.drawImage(Assets.arrow, 454, 50);
			
			
			g.drawString(""+handler.getWorld().getEntityManager().getPlayer().getHealth(),       				56 , 40);
			g.drawString(""+ handler.getWorld().getEntityManager().getPlayer().getDamage(),     				167, 40);
			g.drawString(""+ handler.getWorld().getEntityManager().getPlayer().getRangedDamage(),      	    267, 40);
			g.drawString(""+ handler.getWorld().getEntityManager().getPlayer().getArmor(),      				370, 40);
			g.drawString(""+ handler.getWorld().getEntityManager().getPlayer().getAmmunition(), 				480, 40);
			g.drawString("Mode",     															  				590, 40);
			if(handler.getWorld().getEntityManager().getPlayer().isRangedToggled())
				g.drawImage(Assets.bow, 552, 50);
			if(!handler.getWorld().getEntityManager().getPlayer().isRangedToggled())
				g.drawImage(Assets.sword, 552, 50);

			g.drawString(String.valueOf(handler.getGame().getDisplayFPS()), 1880, 20);
		}
	}
}
