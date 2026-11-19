package dev.codenmore.tilegame.inventory;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.Settings;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.gfx.Text;
import dev.codenmore.tilegame.input.KeyManager;
import dev.codenmore.tilegame.input.MouseManager;
import dev.codenmore.tilegame.items.Item;

/**
 * Contents of a chest and the screen to move items between the player's inventory and the chest.
 *
 * Controls while open: W/S select, A/D switch between inventory (left) and chest (right),
 * Enter moves one item to the other side, Shift+Enter the whole stack, a mouse click on an
 * entry moves one item. The player closes the chest with E or Escape.
 */
public class ChestInventory implements Serializable {

    private static final int PLAYER_SIDE = 0, CHEST_SIDE = 1;

    private static final int PANEL_X = 260, PANEL_Y = 140, PANEL_W = 1400, PANEL_H = 800;
    private static final int COLUMN_W = 620, COLUMN_Y = 270;
    private static final int[] COLUMN_X = {PANEL_X + 50, PANEL_X + PANEL_W - 50 - COLUMN_W};
    private static final int ROW_H = 74, VISIBLE_ROWS = 7, ICON = 56;

    private static final Color OVERLAY = new Color(0, 0, 0, 140);
    private static final Color PANEL = new Color(16, 22, 34);
    private static final Color ROW = new Color(40, 48, 64);
    private static final Color SELECTED = new Color(255, 204, 0);
    private static final Color SELECTED_FILL = new Color(255, 204, 0, 50);
    private static final Color HINT = new Color(170, 176, 190);

    private Handler handler;
    /** Items stored in the chest, own instances with their own count. */
    private final ArrayList<Item> contents = new ArrayList<Item>();

    private boolean open = false;
    private int side = PLAYER_SIDE;
    private final int[] selected = new int[2];
    private transient boolean mouseWasPressed;

    public ChestInventory(Handler handler) {
        this.handler = handler;
    }

    // ------------------------------------------------------------------ contents

    /** Puts items into the chest. */
    public void add(int itemId, int amount) {
        if (amount <= 0)
            return;
        for (Item i : contents) {
            if (i.getId() == itemId) {
                i.setCount(i.getCount() + amount);
                return;
            }
        }
        contents.add(Item.items[itemId].createNew(amount));
    }

    public int count(int itemId) {
        for (Item i : contents)
            if (i.getId() == itemId)
                return i.getCount();
        return 0;
    }

    /** Takes up to {@code amount} items out of the chest and returns how many were taken. */
    public int take(int itemId, int amount) {
        for (int k = 0; k < contents.size(); k++) {
            Item i = contents.get(k);
            if (i.getId() != itemId)
                continue;
            int taken = Math.min(amount, i.getCount());
            i.setCount(i.getCount() - taken);
            if (i.getCount() <= 0)
                contents.remove(k);
            return taken;
        }
        return 0;
    }

    public List<Item> getContents() {
        return contents;
    }

    /** Drops everything in the chest into the world, used when the chest is destroyed. */
    public void dropContents(int x, int y) {
        for (Item i : contents)
            if (i.getCount() > 0)
                handler.getWorld().getItemManager().addItem(Item.items[i.getId()].createNew(i.getCount()), x, y, true);
        contents.clear();
    }

    // ------------------------------------------------------------------ moving items

    private Inventory playerInventory() {
        return handler.getWorld().getEntityManager().getPlayer().getInventory();
    }

    /** Items the player carries that can be stored (the inventory only holds those). */
    private List<Item> playerItems() {
        List<Item> list = new ArrayList<Item>();
        for (Item i : playerInventory().getInventoryItems())
            if (i.getCount() > 0)
                list.add(i);
        return list;
    }

    private List<Item> chestItems() {
        List<Item> list = new ArrayList<Item>();
        for (Item i : contents)
            if (i.getCount() > 0)
                list.add(i);
        return list;
    }

    private List<Item> items(int s) {
        return s == PLAYER_SIDE ? playerItems() : chestItems();
    }

    /** Moves up to {@code amount} items of the given kind from one side to the other. */
    private void move(int from, int itemId, int amount) {
        if (from == PLAYER_SIDE) {
            Item carried = playerInventory().getInventoryItemByID(itemId);
            if (carried == null)
                return;
            int n = Math.min(amount, carried.getCount());
            carried.setCount(carried.getCount() - n);
            add(itemId, n);
        } else {
            int n = take(itemId, amount);
            if (n > 0)
                playerInventory().addItem(Item.items[itemId].createNew(n));
        }
    }

    // ------------------------------------------------------------------ screen

    public boolean isOpen() {
        return open;
    }

    public void open() {
        open = true;
        side = PLAYER_SIDE;
        selected[0] = selected[1] = 0;
        mouseWasPressed = handler.getMouseManager().isLeftPressed();
    }

    public void close() {
        open = false;
    }

    public void tick() {
        if (!open)
            return;
        KeyManager keys = handler.getKeyManager();
        if (keys.keyJustPressed(KeyEvent.VK_A) || keys.keyJustPressed(KeyEvent.VK_LEFT))
            side = PLAYER_SIDE;
        if (keys.keyJustPressed(KeyEvent.VK_D) || keys.keyJustPressed(KeyEvent.VK_RIGHT))
            side = CHEST_SIDE;
        if (keys.keyJustPressed(KeyEvent.VK_W) || keys.keyJustPressed(KeyEvent.VK_UP))
            selected[side]--;
        if (keys.keyJustPressed(KeyEvent.VK_S) || keys.keyJustPressed(KeyEvent.VK_DOWN))
            selected[side]++;
        clampSelection();

        if (keys.keyJustPressed(KeyEvent.VK_ENTER) || keys.keyJustPressed(KeyEvent.VK_SPACE)) {
            List<Item> list = items(side);
            if (!list.isEmpty()) {
                Item item = list.get(selected[side]);
                boolean all = keys.isKeyDown(KeyEvent.VK_SHIFT);
                move(side, item.getId(), all ? item.getCount() : 1);
                clampSelection();
            }
        }
        tickMouse();
    }

    private void clampSelection() {
        for (int s = 0; s < 2; s++) {
            int size = items(s).size();
            if (size == 0)
                selected[s] = 0;
            else if (selected[s] < 0)
                selected[s] = size - 1;
            else if (selected[s] >= size)
                selected[s] = s == side ? 0 : size - 1;
        }
    }

    private void tickMouse() {
        MouseManager mouse = handler.getMouseManager();
        boolean pressed = mouse.isLeftPressed();
        boolean clicked = mouseWasPressed && !pressed;
        mouseWasPressed = pressed;
        if (!clicked)
            return;
        // mouse coordinates are in window pixels, the screen is laid out in 1920x1080
        int mx = (int) (mouse.getMouseX() / Settings.getScaleX());
        int my = (int) (mouse.getMouseY() / Settings.getScaleY());
        for (int s = 0; s < 2; s++) {
            List<Item> list = items(s);
            int first = firstVisible(s, list.size());
            for (int row = 0; row < VISIBLE_ROWS && first + row < list.size(); row++) {
                if (rowBounds(s, row).contains(mx, my)) {
                    side = s;
                    selected[s] = first + row;
                    move(s, list.get(first + row).getId(), 1);
                    clampSelection();
                    return;
                }
            }
        }
    }

    private int firstVisible(int s, int size) {
        int first = selected[s] - VISIBLE_ROWS / 2;
        return Math.max(0, Math.min(first, size - VISIBLE_ROWS));
    }

    private Rectangle rowBounds(int s, int row) {
        return new Rectangle(COLUMN_X[s], COLUMN_Y + 60 + row * ROW_H, COLUMN_W, ROW_H - 10);
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
        g.setColor(PANEL);
        g.fillRoundRect(PANEL_X, PANEL_Y, PANEL_W, PANEL_H, 40, 40);
        g2d.setStroke(new BasicStroke(8));
        g.setColor(Color.BLACK);
        g.drawRoundRect(PANEL_X, PANEL_Y, PANEL_W, PANEL_H, 40, 40);

        Text.drawString(g, "CHEST", 1920 / 2, PANEL_Y + 60, true, Color.WHITE, Assets.font56);
        g.drawImage(Assets.chest, 1920 / 2 - 48, PANEL_Y + 90, 96, 96, null);
        Text.drawString(g, ">", 1920 / 2, 1080 / 2 + 40, true, side == PLAYER_SIDE ? SELECTED : HINT, Assets.font56);
        Text.drawString(g, "<", 1920 / 2, 1080 / 2 + 110, true, side == CHEST_SIDE ? SELECTED : HINT, Assets.font56);

        renderColumn(g2d, PLAYER_SIDE, "INVENTORY");
        renderColumn(g2d, CHEST_SIDE, "CHEST");

        int hintY = PANEL_Y + PANEL_H - 70;
        Text.drawString(g, "W/S SELECT    A/D SWITCH SIDE    E/ESC CLOSE", 1920 / 2, hintY, true, HINT, Assets.font28);
        Text.drawString(g, "ENTER MOVE ONE    SHIFT+ENTER MOVE ALL    CLICK MOVE ONE", 1920 / 2, hintY + 36, true, HINT, Assets.font28);

        g2d.setComposite(oldComposite);
        g2d.setStroke(oldStroke);
    }

    private void renderColumn(Graphics2D g, int s, String title) {
        int x = COLUMN_X[s];
        Text.drawString(g, title, x + COLUMN_W / 2, COLUMN_Y + 20, true, s == side ? SELECTED : Color.WHITE, Assets.font40);
        List<Item> list = items(s);
        if (list.isEmpty()) {
            Text.drawString(g, "EMPTY", x + COLUMN_W / 2, COLUMN_Y + 60 + ROW_H * 3, true, HINT, Assets.font40);
            return;
        }
        int first = firstVisible(s, list.size());
        for (int row = 0; row < VISIBLE_ROWS && first + row < list.size(); row++) {
            Item item = list.get(first + row);
            Rectangle r = rowBounds(s, row);
            boolean isSelected = first + row == selected[s];
            g.setColor(isSelected && s == side ? SELECTED_FILL : ROW);
            g.fillRoundRect(r.x, r.y, r.width, r.height, 16, 16);
            if (isSelected) {
                g.setStroke(new BasicStroke(s == side ? 4 : 2));
                g.setColor(s == side ? SELECTED : HINT);
                g.drawRoundRect(r.x, r.y, r.width, r.height, 16, 16);
            }
            g.drawImage(item.getTexture(), r.x + 10, r.y + (r.height - ICON) / 2, ICON, ICON, null);
            Text.drawString(g, item.getName().toUpperCase(), r.x + 84, r.y + r.height / 2 + 14, false, Color.WHITE, Assets.font40);
            String count = "x" + item.getCount();
            int w = g.getFontMetrics(Assets.font40).stringWidth(count);
            Text.drawString(g, count, r.x + r.width - 20 - w, r.y + r.height / 2 + 14, false, Color.WHITE, Assets.font40);
        }
        if (first > 0)
            Text.drawString(g, "...", x + COLUMN_W / 2, COLUMN_Y + 46, true, HINT, Assets.font28);
        if (first + VISIBLE_ROWS < list.size())
            Text.drawString(g, "...", x + COLUMN_W / 2, COLUMN_Y + 60 + VISIBLE_ROWS * ROW_H, true, HINT, Assets.font28);
    }
}
