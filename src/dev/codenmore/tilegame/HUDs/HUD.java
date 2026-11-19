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
import dev.codenmore.tilegame.worlds.World;
import dev.codenmore.tilegame.worlds.WorldGenerator;

public class HUD {
	private Handler handler;

	/** horizontal centers of the HUD columns */
	private static final int HEALTH_X = 88, SWORD_X = 224, BOW_X = 322, ARMOR_X = 424, AMMO_X = 524, SWITCH_X = 650;
	private static final int TEXT_Y = 40, ICON_Y = 50;

	private static final Color HIGHLIGHT = new Color(255, 204, 0);
	private static final Color LOW = new Color(255, 70, 70);
	private static final Color BADGE = new Color(16, 22, 34, 200);

	/** how long the level title is shown after entering a world, in ms */
	private static final long TITLE_TIME = 2500;
	private World shownWorld;
	private long worldEnteredTime;

	public HUD(Handler handler) {
		this.handler=handler;
	}

	public void render(Graphics g) {
		Player player = handler.getWorld().getEntityManager().getPlayer();
		if (handler.getWorld() != shownWorld) {
			shownWorld = handler.getWorld();
			worldEnteredTime = System.currentTimeMillis();
		}
		if (player.getInventory().isActive() || player.getCraftingScreen().isActive())
			return;

		renderLevel(g);

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

		// the armor icon shows the condition of the armor, without armor it is dimmed
		Graphics2D g2 = (Graphics2D) g;
		Composite before = g2.getComposite();
		if (player.getArmor() == 0)
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
		BufferedImage armorIcon = player.getArmor() == 0 ? Assets.armor : Assets.armorStates[player.getArmorCondition()];
		g.drawImage(armorIcon, ARMOR_X - 32, ICON_Y, 64, 64, null);
		g2.setComposite(before);
		Text.drawString(g, "" + player.getArmor(), ARMOR_X, TEXT_Y, true, Color.WHITE, Assets.font40);
		g.drawImage(Assets.arrow, AMMO_X - 24, ICON_Y, 48, 48, null);
		Text.drawString(g, "" + player.getAmmunition(), AMMO_X, TEXT_Y, true, ammoColor, Assets.font40);

		// hint how to switch the weapon
		Text.drawString(g, "SWITCH", SWITCH_X, TEXT_Y, true, Color.LIGHT_GRAY, Assets.font28);
		Text.drawString(g, "[T]", SWITCH_X, ICON_Y + 30, true, HIGHLIGHT, Assets.font28);

		Text.drawString(g, String.valueOf(handler.getGame().getDisplayFPS()), 1880, 20, true, Color.WHITE, Assets.font40);
	}

	/** Small badge with the level number at the top and a big title when a level starts. */
	private void renderLevel(Graphics g) {
		int level = WorldGenerator.getLevelNumber(handler.getWorld());
		String name = level == 0 ? "HOMEWORLD" : "LEVEL " + level;
		Graphics2D g2d = (Graphics2D) g;
		Composite oldComposite = g2d.getComposite();
		g2d.setComposite(AlphaComposite.SrcOver);
		g.setColor(BADGE);
		g.fillRoundRect(1920 / 2 - 140, 14, 280, 52, 14, 14);
		Text.drawString(g, name, 1920 / 2, 40, true, HIGHLIGHT, Assets.font28);

		long shown = System.currentTimeMillis() - worldEnteredTime;
		if (shown < TITLE_TIME) {
			// fade out during the last second
			float alpha = Math.min(1f, (TITLE_TIME - shown) / 1000f);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			Text.drawString(g, name, 1920 / 2 + 4, 1080 / 3 + 4, true, Color.BLACK, Assets.font56);
			Text.drawString(g, name, 1920 / 2, 1080 / 3, true, Color.WHITE, Assets.font56);
		}
		g2d.setComposite(oldComposite);
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
