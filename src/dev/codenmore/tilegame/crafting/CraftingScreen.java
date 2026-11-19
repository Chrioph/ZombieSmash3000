package dev.codenmore.tilegame.crafting;

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
import dev.codenmore.tilegame.inventory.Inventory;
import dev.codenmore.tilegame.items.CraftableItem;
import dev.codenmore.tilegame.items.Item;

/**
 * Crafting screen, opened with C. Lists the recipes on the left and the selected recipe
 * with its ingredients on the right.
 *
 * Controls: W/S select, Enter crafts once, Shift+Enter as often as possible, a click on a
 * recipe selects it, a click on the craft button crafts once. C or Escape closes the screen.
 */
public class CraftingScreen {

    private static final int PANEL_X = 260, PANEL_Y = 140, PANEL_W = 1400, PANEL_H = 800;
    private static final int LIST_X = PANEL_X + 50, LIST_Y = 270, LIST_W = 620;
    private static final int DETAIL_X = PANEL_X + PANEL_W - 50 - 620, DETAIL_W = 620;
    private static final int ROW_H = 74, VISIBLE_ROWS = 7, ICON = 56;

    private static final Color OVERLAY = new Color(0, 0, 0, 140);
    private static final Color PANEL = new Color(16, 22, 34);
    private static final Color ROW = new Color(40, 48, 64);
    private static final Color SELECTED = new Color(255, 204, 0);
    private static final Color SELECTED_FILL = new Color(255, 204, 0, 50);
    private static final Color HINT = new Color(170, 176, 190);
    private static final Color OK = new Color(110, 220, 110);
    private static final Color MISSING = new Color(255, 100, 100);

    private Handler handler;
    private boolean active = false;
    private final List<Recipe> recipes = new ArrayList<Recipe>();
    private int selected = 0;
    private boolean mouseWasPressed;
    /** short feedback after crafting */
    private String message;
    private long messageTime;

    public CraftingScreen(Handler handler) {
        this.handler = handler;
        recipes.add(new Recipe(CraftableItem.woodFloorItem, 2, Item.logItem, 1));
        recipes.add(new Recipe(CraftableItem.woodWallItem, 1, Item.logItem, 2));
        recipes.add(new Recipe(CraftableItem.stoneFloorItem, 2, Item.rockItem, 1));
        recipes.add(new Recipe(CraftableItem.stoneWallItem, 1, Item.rockItem, 2));
        recipes.add(new Recipe(CraftableItem.chestItem, 1, Item.logItem, 3));
    }

    private Inventory inventory() {
        return handler.getWorld().getEntityManager().getPlayer().getInventory();
    }

    public void tick() {
        if (!active)
            return;
        KeyManager keys = handler.getKeyManager();
        if (keys.keyJustPressed(KeyEvent.VK_W) || keys.keyJustPressed(KeyEvent.VK_UP))
            selected--;
        if (keys.keyJustPressed(KeyEvent.VK_S) || keys.keyJustPressed(KeyEvent.VK_DOWN))
            selected++;
        if (selected < 0)
            selected = recipes.size() - 1;
        else if (selected >= recipes.size())
            selected = 0;
        if (keys.keyJustPressed(KeyEvent.VK_ENTER) || keys.keyJustPressed(KeyEvent.VK_SPACE))
            craft(keys.isKeyDown(KeyEvent.VK_SHIFT) ? Integer.MAX_VALUE : 1);
        tickMouse();
    }

    private void craft(int times) {
        Recipe recipe = recipes.get(selected);
        int crafted = recipe.craft(inventory(), times);
        message = crafted > 0
                ? "+" + crafted * recipe.getOutputCount() + " " + recipe.getOutput().getName().toUpperCase()
                : "NOT ENOUGH MATERIAL";
        messageTime = System.currentTimeMillis();
    }

    private void tickMouse() {
        MouseManager mouse = handler.getMouseManager();
        boolean pressed = mouse.isLeftPressed();
        boolean clicked = mouseWasPressed && !pressed;
        mouseWasPressed = pressed;
        if (!clicked)
            return;
        int mx = (int) (mouse.getMouseX() / Settings.getScaleX());
        int my = (int) (mouse.getMouseY() / Settings.getScaleY());
        int first = firstVisible();
        for (int row = 0; row < VISIBLE_ROWS && first + row < recipes.size(); row++) {
            if (rowBounds(row).contains(mx, my)) {
                selected = first + row;
                return;
            }
        }
        if (craftButton().contains(mx, my))
            craft(1);
    }

    private int firstVisible() {
        int first = selected - VISIBLE_ROWS / 2;
        return Math.max(0, Math.min(first, recipes.size() - VISIBLE_ROWS));
    }

    private Rectangle rowBounds(int row) {
        return new Rectangle(LIST_X, LIST_Y + 60 + row * ROW_H, LIST_W, ROW_H - 10);
    }

    private Rectangle craftButton() {
        return new Rectangle(DETAIL_X + DETAIL_W / 2 - 170, PANEL_Y + PANEL_H - 190, 340, 76);
    }

    public void render(Graphics g) {
        if (!active)
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
        Text.drawString(g, "CRAFTING", 1920 / 2, PANEL_Y + 60, true, Color.WHITE, Assets.font56);

        renderRecipes(g2d);
        renderDetails(g2d);

        int hintY = PANEL_Y + PANEL_H - 70;
        Text.drawString(g, "W/S SELECT    ENTER CRAFT    SHIFT+ENTER CRAFT ALL    C/ESC CLOSE", 1920 / 2, hintY, true, HINT, Assets.font28);
        Text.drawString(g, "BUILD IN THE HOMEWORLD: OPEN THE INVENTORY (E), SELECT AN ITEM, PRESS Q", 1920 / 2, hintY + 36, true, HINT, Assets.font28);

        g2d.setComposite(oldComposite);
        g2d.setStroke(oldStroke);
    }

    private void renderRecipes(Graphics2D g) {
        Text.drawString(g, "RECIPES", LIST_X + LIST_W / 2, LIST_Y + 20, true, SELECTED, Assets.font40);
        Inventory inv = inventory();
        int first = firstVisible();
        for (int row = 0; row < VISIBLE_ROWS && first + row < recipes.size(); row++) {
            Recipe recipe = recipes.get(first + row);
            Rectangle r = rowBounds(row);
            boolean isSelected = first + row == selected;
            g.setColor(isSelected ? SELECTED_FILL : ROW);
            g.fillRoundRect(r.x, r.y, r.width, r.height, 16, 16);
            if (isSelected) {
                g.setStroke(new BasicStroke(4));
                g.setColor(SELECTED);
                g.drawRoundRect(r.x, r.y, r.width, r.height, 16, 16);
            }
            g.drawImage(recipe.getOutput().getTexture(), r.x + 10, r.y + (r.height - ICON) / 2, ICON, ICON, null);
            boolean possible = recipe.timesCraftable(inv) > 0;
            Text.drawString(g, recipe.getOutput().getName().toUpperCase(), r.x + 84, r.y + r.height / 2 + 14, false,
                    possible ? Color.WHITE : HINT, Assets.font40);
            String count = "x" + recipe.getOutputCount();
            int w = g.getFontMetrics(Assets.font40).stringWidth(count);
            Text.drawString(g, count, r.x + r.width - 20 - w, r.y + r.height / 2 + 14, false, possible ? Color.WHITE : HINT, Assets.font40);
        }
    }

    private void renderDetails(Graphics2D g) {
        Recipe recipe = recipes.get(selected);
        Inventory inv = inventory();
        int cx = DETAIL_X + DETAIL_W / 2;
        g.drawImage(recipe.getOutput().getTexture(), cx - 64, LIST_Y - 10, 128, 128, null);
        Text.drawString(g, recipe.getOutput().getName().toUpperCase() + "  x" + recipe.getOutputCount(), cx, LIST_Y + 150, true, Color.WHITE, Assets.font40);
        Text.drawString(g, "YOU HAVE " + Recipe.have(inv, recipe.getOutput()), cx, LIST_Y + 195, true, HINT, Assets.font28);

        Text.drawString(g, "NEEDS", DETAIL_X + 20, LIST_Y + 255, false, HINT, Assets.font28);
        for (int i = 0; i < recipe.getIngredientCount(); i++) {
            Item ingredient = recipe.getIngredient(i);
            int y = LIST_Y + 275 + i * 64;
            int have = Recipe.have(inv, ingredient), need = recipe.getAmount(i);
            g.drawImage(ingredient.getTexture(), DETAIL_X + 20, y, 52, 52, null);
            Text.drawString(g, ingredient.getName().toUpperCase(), DETAIL_X + 90, y + 40, false, Color.WHITE, Assets.font40);
            String text = have + "/" + need;
            int w = g.getFontMetrics(Assets.font40).stringWidth(text);
            Text.drawString(g, text, DETAIL_X + DETAIL_W - 20 - w, y + 40, false, have >= need ? OK : MISSING, Assets.font40);
        }

        Rectangle b = craftButton();
        boolean possible = recipe.timesCraftable(inv) > 0;
        g.setColor(possible ? new Color(170, 30, 30) : ROW);
        g.fillRoundRect(b.x, b.y, b.width, b.height, 20, 20);
        g.setStroke(new BasicStroke(4));
        g.setColor(possible ? Color.WHITE : Color.BLACK);
        g.drawRoundRect(b.x, b.y, b.width, b.height, 20, 20);
        Text.drawString(g, "CRAFT", b.x + b.width / 2, b.y + b.height / 2, true, possible ? Color.WHITE : HINT, Assets.font40);

        if (message != null && System.currentTimeMillis() - messageTime < 1500) {
            Text.drawString(g, message, b.x + b.width / 2, b.y - 30, true,
                    message.startsWith("+") ? OK : MISSING, Assets.font28);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        if (active && !this.active)
            mouseWasPressed = handler.getMouseManager().isLeftPressed();
        this.active = active;
    }
}
