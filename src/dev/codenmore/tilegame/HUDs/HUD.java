package dev.codenmore.tilegame.HUDs;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.entity.creatures.Player;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.gfx.Text;

public class HUD {
	private Handler handler;

	/** horizontal centers of the HUD columns */
	private static final int HEALTH_X = 88, SWORD_X = 224, BOW_X = 322, ARMOR_X = 424, AMMO_X = 524, SWITCH_X = 650;
	private static final int TEXT_Y = 40, ICON_Y = 50;

	private static final Color HIGHLIGHT = new Color(255, 204, 0);
	private static final Color LOW = new Color(255, 70, 70);

	public HUD(Handler handler) {
		this.handler=handler;
	}

	public void render(Graphics g) {
		Player player = handler.getWorld().getEntityManager().getPlayer();
		if (player.getInventory().isActive() || player.getCraftingScreen().isActive())
			return;

		g.drawImage(Assets.HUDWindow, 5, 5, 750, 120, null);

		// health as current / maximum, red when low
		int health = player.getHealth();
		int maxHealth = player.getMaxHealth();
		Color healthColor = health * 10 <= maxHealth * 3 ? LOW : Color.WHITE;
		g.drawImage(Assets.heart, HEALTH_X - 32, ICON_Y, 64, 64, null);
		Text.drawString(g, health + "/" + maxHealth, HEALTH_X, TEXT_Y, true, healthColor, Assets.font40);

		// weapons: the equipped one is framed, the other one is dimmed
		boolean bowEquipped = player.isRangedToggled();
		renderWeapon(g, Assets.sword, player.getDamage(), SWORD_X, !bowEquipped, Color.WHITE);
		Color ammoColor = player.getAmmunition() == 0 ? LOW : Color.WHITE;
		renderWeapon(g, Assets.bow, player.getRangedDamage(), BOW_X, bowEquipped, Color.WHITE);

		g.drawImage(Assets.armor, ARMOR_X - 32, ICON_Y, 64, 64, null);
		Text.drawString(g, "" + player.getArmor(), ARMOR_X, TEXT_Y, true, Color.WHITE, Assets.font40);
		g.drawImage(Assets.arrow, AMMO_X - 24, ICON_Y, 48, 48, null);
		Text.drawString(g, "" + player.getAmmunition(), AMMO_X, TEXT_Y, true, ammoColor, Assets.font40);

		// hint how to switch the weapon
		Text.drawString(g, "SWITCH", SWITCH_X, TEXT_Y, true, Color.LIGHT_GRAY, Assets.font28);
		Text.drawString(g, "[T]", SWITCH_X, ICON_Y + 30, true, HIGHLIGHT, Assets.font28);

		Text.drawString(g, String.valueOf(handler.getGame().getDisplayFPS()), 1880, 20, true, Color.WHITE, Assets.font40);
	}

	private void renderWeapon(Graphics g, BufferedImage icon, int damage, int centerX, boolean equipped, Color textColor) {
		Graphics2D g2d = (Graphics2D) g;
		Composite oldComposite = g2d.getComposite();
		Stroke oldStroke = g2d.getStroke();
		if (equipped) {
			g2d.setColor(new Color(255, 204, 0, 60));
			g2d.fillRoundRect(centerX - 42, 12, 84, 100, 18, 18);
			g2d.setColor(HIGHLIGHT);
			g2d.setStroke(new BasicStroke(4));
			g2d.drawRoundRect(centerX - 42, 12, 84, 100, 18, 18);
		} else {
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
		}
		g.drawImage(icon, centerX - 24, ICON_Y, 48, 48, null);
		Text.drawString(g, "" + damage, centerX, TEXT_Y, true, textColor, Assets.font40);
		g2d.setComposite(oldComposite);
		g2d.setStroke(oldStroke);
	}
}
