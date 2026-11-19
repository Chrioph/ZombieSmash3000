package dev.codenmore.tilegame.gfx;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Short texts that rise above an entity and fade out, e.g. "SWORD" after switching weapons
 * or "+2 LOGS" after picking up items. Pickups of the same kind shortly after each other are
 * counted together, different ones are stacked, the newest closest to the entity.
 */
public class Popups {

    private static final long DURATION = 1200;
    private static final int LINE_HEIGHT = 36, ICON = 30, RISE = 20;
    private static final int MAX_LINES = 4;

    private static class Popup {
        final String key;
        String label;
        BufferedImage icon;
        boolean plural;
        /** 0 for a plain text, otherwise shown as "+count label" */
        int count;
        long start;
        Color color;

        Popup(String key) {
            this.key = key;
        }
    }

    private final List<Popup> popups = new ArrayList<Popup>();

    /** Shows a plain text, replacing an earlier text with the same key. */
    public void showText(String key, String text, Color color) {
        Popup p = find(key);
        if (p == null)
            p = add(key);
        p.label = text;
        p.icon = null;
        p.count = 0;
        p.color = color;
        restart(p);
    }

    /**
     * Shows "+amount label" with an icon. A pickup of the same kind that is still shown is
     * counted up instead of adding a new line.
     *
     * @param plural whether an "S" is added to the label for more than one, e.g. LOG -> LOGS
     */
    public void showPickup(String key, String label, BufferedImage icon, int amount, boolean plural) {
        Popup p = find(key);
        if (p == null || p.count == 0) {
            if (p != null)
                popups.remove(p);
            p = add(key);
        }
        p.label = label;
        p.icon = icon;
        p.plural = plural;
        p.count += amount;
        p.color = Color.WHITE;
        restart(p);
    }

    private Popup find(String key) {
        for (Popup p : popups)
            if (p.key.equals(key))
                return p;
        return null;
    }

    private Popup add(String key) {
        Popup p = new Popup(key);
        popups.add(p);
        return p;
    }

    private void restart(Popup p) {
        p.start = System.currentTimeMillis();
        // the newest popup is drawn closest to the entity
        popups.remove(p);
        popups.add(p);
        while (popups.size() > MAX_LINES)
            popups.remove(0);
    }

    /**
     * Draws the popups centered above the given screen position.
     */
    public void render(Graphics g, int centerX, int bottomY) {
        long now = System.currentTimeMillis();
        popups.removeIf(p -> now - p.start >= DURATION);
        if (popups.isEmpty())
            return;
        Graphics2D g2d = (Graphics2D) g;
        Composite old = g2d.getComposite();
        int line = 0;
        for (int i = popups.size() - 1; i >= 0; i--, line++) {
            Popup p = popups.get(i);
            double t = (now - p.start) / (double) DURATION;
            // rise a little and fade out during the last third
            float alpha = (float) Math.max(0, Math.min(1, (1 - t) * 3));
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            int y = bottomY - line * LINE_HEIGHT - (int) (RISE * t);
            String text = p.count == 0 ? p.label
                    : "+" + p.count + " " + p.label + (p.plural && p.count > 1 ? "S" : "");
            int textW = g.getFontMetrics(Assets.font28).stringWidth(text);
            int x = centerX;
            if (p.icon != null) {
                int total = ICON + 6 + textW;
                int left = centerX - total / 2;
                g.drawImage(p.icon, left, y - ICON / 2 - 2, ICON, ICON, null);
                x = left + ICON + 6 + textW / 2;
            }
            Text.drawString(g, text, x + 2, y + 2, true, Color.BLACK, Assets.font28);
            Text.drawString(g, text, x, y, true, p.color, Assets.font28);
        }
        g2d.setComposite(old);
    }
}
