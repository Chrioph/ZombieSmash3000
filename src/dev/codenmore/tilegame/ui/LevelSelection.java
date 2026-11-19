package dev.codenmore.tilegame.ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.Settings;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.gfx.Text;
import dev.codenmore.tilegame.input.KeyManager;
import dev.codenmore.tilegame.input.MouseManager;

/**
 * Menu shown when the player reaches the finish of a level: go on to the next level, go to
 * the homeworld, quit, or close the menu and keep playing.
 *
 * Controls: W/S or the arrow keys select, Enter or Space confirms, Escape closes. The mouse
 * selects the option under it, a click confirms.
 */
public class LevelSelection {

    /** An entry of the menu. */
    public static class Option {
        final String label;
        final String hint;
        /** null closes the menu */
        final Runnable action;

        public Option(String label, String hint, Runnable action) {
            this.label = label;
            this.hint = hint;
            this.action = action;
        }
    }

    private static final int PANEL_W = 820, ROW_W = 700, ROW_H = 104, ROW_GAP = 18;
    private static final int TITLE_SPACE = 150, HINT_SPACE = 90;

    private static final Color OVERLAY = new Color(0, 0, 0, 140);
    private static final Color PANEL = new Color(16, 22, 34);
    private static final Color ROW = new Color(40, 48, 64);
    private static final Color SELECTED = new Color(255, 204, 0);
    private static final Color SELECTED_FILL = new Color(255, 204, 0, 50);
    private static final Color HINT = new Color(170, 176, 190);

    private final Handler handler;
    private final Runnable onClose;
    private boolean open;
    private String title, subtitle;
    private final List<Option> options = new ArrayList<Option>();
    private int selected;
    private boolean mouseWasPressed;
    private int lastMouseX = -1, lastMouseY = -1;

    /**
     * @param onClose called when the menu is closed without choosing an option
     */
    public LevelSelection(Handler handler, Runnable onClose) {
        this.handler = handler;
        this.onClose = onClose;
    }

    /**
     * Opens the menu with the given options, the first one is selected.
     */
    public void open(String title, String subtitle, List<Option> options) {
        this.title = title;
        this.subtitle = subtitle;
        this.options.clear();
        this.options.addAll(options);
        // the last entry always closes the menu
        this.options.add(new Option("CLOSE", "BACK TO THE GAME", null));
        selected = 0;
        open = true;
        MouseManager mouse = handler.getMouseManager();
        // a click that is still held from playing must not choose an option
        mouseWasPressed = mouse.isLeftPressed();
        // only a mouse movement after opening changes the selection, so the keyboard keeps it
        lastMouseX = mouse.getMouseX();
        lastMouseY = mouse.getMouseY();
    }

    /** Shows the menu again as it was, e.g. after a confirmation dialog on top of it was declined. */
    public void reopen() {
        open = true;
        mouseWasPressed = handler.getMouseManager().isLeftPressed();
    }

    /** Closes the menu without choosing anything. */
    public void close() {
        if (!open)
            return;
        open = false;
        onClose.run();
    }

    public boolean isOpen() {
        return open;
    }

    /** Closes the menu after an option was chosen, without the close callback. */
    private void choose(int index) {
        Option option = options.get(index);
        if (option.action == null) {
            close();
            return;
        }
        open = false;
        option.action.run();
    }

    public void tick() {
        if (!open)
            return;
        KeyManager keys = handler.getKeyManager();
        if (keys.keyJustPressed(KeyEvent.VK_ESCAPE)) {
            close();
            return;
        }
        if (keys.keyJustPressed(KeyEvent.VK_W) || keys.keyJustPressed(KeyEvent.VK_UP))
            selected = (selected + options.size() - 1) % options.size();
        if (keys.keyJustPressed(KeyEvent.VK_S) || keys.keyJustPressed(KeyEvent.VK_DOWN))
            selected = (selected + 1) % options.size();
        if (keys.keyJustPressed(KeyEvent.VK_ENTER) || keys.keyJustPressed(KeyEvent.VK_SPACE)) {
            choose(selected);
            return;
        }
        tickMouse();
    }

    private void tickMouse() {
        MouseManager mouse = handler.getMouseManager();
        int mx = (int) (mouse.getMouseX() / Settings.getScaleX());
        int my = (int) (mouse.getMouseY() / Settings.getScaleY());
        int hovered = -1;
        for (int i = 0; i < options.size(); i++)
            if (rowBounds(i).contains(mx, my))
                hovered = i;
        boolean moved = mouse.getMouseX() != lastMouseX || mouse.getMouseY() != lastMouseY;
        lastMouseX = mouse.getMouseX();
        lastMouseY = mouse.getMouseY();
        if (moved && hovered >= 0)
            selected = hovered;

        boolean pressed = mouse.isLeftPressed();
        boolean clicked = mouseWasPressed && !pressed;
        mouseWasPressed = pressed;
        if (clicked && hovered >= 0)
            choose(hovered);
    }

    private int panelHeight() {
        return TITLE_SPACE + options.size() * (ROW_H + ROW_GAP) - ROW_GAP + HINT_SPACE;
    }

    private int panelY() {
        return (1080 - panelHeight()) / 2;
    }

    private Rectangle rowBounds(int i) {
        return new Rectangle(1920 / 2 - ROW_W / 2, panelY() + TITLE_SPACE + i * (ROW_H + ROW_GAP), ROW_W, ROW_H);
    }

    public void render(Graphics g) {
        if (!open)
            return;
        Graphics2D g2d = (Graphics2D) g;
        Composite oldComposite = g2d.getComposite();
        Stroke oldStroke = g2d.getStroke();
        g2d.setComposite(AlphaComposite.SrcOver);

        g.setColor(OVERLAY);
        g.fillRect(0, 0, 1920, 1080);
        int panelX = 1920 / 2 - PANEL_W / 2, panelY = panelY(), panelH = panelHeight();
        g.setColor(PANEL);
        g.fillRoundRect(panelX, panelY, PANEL_W, panelH, 40, 40);
        g2d.setStroke(new BasicStroke(8));
        g.setColor(Color.BLACK);
        g.drawRoundRect(panelX, panelY, PANEL_W, panelH, 40, 40);

        Text.drawString(g, title, 1920 / 2, panelY + 60, true, Color.WHITE, Assets.font56);
        if (subtitle != null)
            Text.drawString(g, subtitle, 1920 / 2, panelY + 110, true, HINT, Assets.font28);

        for (int i = 0; i < options.size(); i++) {
            Option option = options.get(i);
            Rectangle r = rowBounds(i);
            boolean isSelected = i == selected;
            g.setColor(isSelected ? SELECTED_FILL : ROW);
            g.fillRoundRect(r.x, r.y, r.width, r.height, 20, 20);
            if (isSelected) {
                g2d.setStroke(new BasicStroke(5));
                g.setColor(SELECTED);
                g.drawRoundRect(r.x, r.y, r.width, r.height, 20, 20);
                // arrow pointing at the selected option
                int ay = r.y + r.height / 2;
                g.fillPolygon(new int[] { r.x + 24, r.x + 24, r.x + 46 }, new int[] { ay - 16, ay + 16, ay }, 3);
            }
            int labelY = option.hint != null ? r.y + 40 : r.y + r.height / 2;
            Text.drawString(g, option.label, 1920 / 2, labelY, true, isSelected ? SELECTED : Color.WHITE, Assets.font40);
            if (option.hint != null)
                Text.drawString(g, option.hint, 1920 / 2, r.y + 78, true, HINT, Assets.font28);
        }

        Text.drawString(g, "W/S SELECT    ENTER CONFIRM    ESC CLOSE", 1920 / 2, panelY + panelH - 45, true, HINT, Assets.font28);

        g2d.setComposite(oldComposite);
        g2d.setStroke(oldStroke);
    }
}
