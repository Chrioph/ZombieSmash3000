package dev.codenmore.tilegame.building;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.Settings;
import dev.codenmore.tilegame.entity.Entity;
import dev.codenmore.tilegame.entity.creatures.Player;
import dev.codenmore.tilegame.entity.statics.Chest;
import dev.codenmore.tilegame.entity.statics.StaticEntity;
import dev.codenmore.tilegame.entity.statics.Plant;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.gfx.Text;
import dev.codenmore.tilegame.input.KeyManager;
import dev.codenmore.tilegame.input.MouseManager;
import dev.codenmore.tilegame.items.CraftableItem;
import dev.codenmore.tilegame.items.Item;
import dev.codenmore.tilegame.tiles.Tile;
import dev.codenmore.tilegame.worlds.World;

/**
 * Placing items in worlds that allow building (the homeworld).
 *
 * Started from the inventory (select an item, press Q). The target is the tile under the
 * mouse if it is in reach, otherwise the tile in front of the player. A preview shows the
 * item there, green if it can be placed and red if not.
 *
 * Controls: left click or R places, right click or X removes a built floor or wall and
 * gives the material back, Q or Escape stops building.
 *
 * Floors and walls change the tile, chests and seeds (planted on grass or earth, they grow
 * into trees, see {@link Plant}) are placed as objects. Walls, chests and trees can't be placed where they would block someone or cover
 * an item lying on the ground.
 */
public class BuildMode {

    /** How far (in tiles) from the player the mouse can build. */
    private static final double REACH = 4.5;
    private static final long MESSAGE_DURATION = 1200;

    private static final Color VALID = new Color(90, 220, 90);
    private static final Color INVALID = new Color(240, 70, 70);
    private static final Color PANEL = new Color(16, 22, 34, 230);
    private static final Color HINT = new Color(170, 176, 190);

    /** Items that are built as tiles, used to give the material back when removing. */
    private static final List<CraftableItem> TILE_ITEMS = Arrays.asList(
            CraftableItem.woodFloorItem, CraftableItem.woodWallItem,
            CraftableItem.stoneFloorItem, CraftableItem.stoneWallItem);

    private final Handler handler;
    private final Player player;
    private int itemId = -1;
    private boolean leftWasPressed, rightWasPressed;
    private String message;
    private boolean messageGood;
    private long messageTime;

    public BuildMode(Handler handler, Player player) {
        this.handler = handler;
        this.player = player;
    }

    public boolean isActive() {
        return itemId >= 0;
    }

    public int getItemId() {
        return itemId;
    }

    public void start(int itemId) {
        this.itemId = itemId;
        message = null;
        MouseManager mouse = handler.getMouseManager();
        // a click that is still held down must not place right away
        leftWasPressed = mouse.isLeftPressed();
        rightWasPressed = mouse.isRightPressed();
    }

    public void stop() {
        itemId = -1;
    }

    private World world() {
        return handler.getWorld();
    }

    private int carried() {
        Item item = player.getInventory().getInventoryItemByID(itemId);
        return item == null ? 0 : item.getCount();
    }

    // ------------------------------------------------------------------ input

    public void tick() {
        if (!isActive())
            return;
        if (!world().isPlaceable()) {
            stop();
            return;
        }
        KeyManager keys = handler.getKeyManager();
        if (keys.keyJustPressed(KeyEvent.VK_Q)) {
            stop();
            return;
        }
        MouseManager mouse = handler.getMouseManager();
        boolean left = mouse.isLeftPressed(), right = mouse.isRightPressed();
        boolean leftClick = leftWasPressed && !left, rightClick = rightWasPressed && !right;
        leftWasPressed = left;
        rightWasPressed = right;

        Point target = target();
        if (keys.keyJustPressed(KeyEvent.VK_R) || leftClick)
            place(target.x, target.y);
        else if (keys.keyJustPressed(KeyEvent.VK_X) || rightClick)
            remove(target.x, target.y);
    }

    /** Tile under the mouse when it is in reach, otherwise the tile in front of the player. */
    public Point target() {
        MouseManager mouse = handler.getMouseManager();
        double camX = handler.getGameCamera().getxOffset(), camY = handler.getGameCamera().getyOffset();
        double mx = mouse.getMouseX() / Settings.getScaleX() + camX;
        double my = mouse.getMouseY() / Settings.getScaleY() + camY;
        Rectangle body = worldBounds(player);
        double px = body.getCenterX(), py = body.getCenterY();
        int tx = (int) Math.floor(mx / Tile.TILEWIDTH), ty = (int) Math.floor(my / Tile.TILEHEIGHT);
        double dx = (tx + 0.5) * Tile.TILEWIDTH - px, dy = (ty + 0.5) * Tile.TILEHEIGHT - py;
        if (Math.hypot(dx, dy) <= REACH * Tile.TILEWIDTH)
            return new Point(tx, ty);
        int ptx = (int) Math.floor(px / Tile.TILEWIDTH), pty = (int) Math.floor(py / Tile.TILEHEIGHT);
        switch (player.getFacing()) {
            case 0:  return new Point(ptx + 1, pty);
            case 2:  return new Point(ptx - 1, pty);
            case 3:  return new Point(ptx, pty - 1);
            default: return new Point(ptx, pty + 1);
        }
    }

    // ------------------------------------------------------------------ rules

    /**
     * @return null if the item can be placed on the tile, otherwise the reason why not
     */
    public String whyNot(int tx, int ty) {
        World world = world();
        if (tx < 0 || ty < 0 || tx >= world.getWidth() || ty >= world.getHEight())
            return "OUTSIDE THE WORLD";
        if (carried() <= 0)
            return "NOTHING LEFT TO PLACE";
        Tile tile = world.getTile(tx, ty);
        if (tile.isFinish())
            return "THE EXIT MUST STAY FREE";
        if (tile.isSolid())
            return "THERE IS A WALL";
        Item item = Item.items[itemId];
        int buildTile = item instanceof CraftableItem ? ((CraftableItem) item).getBuildTileId() : -1;
        if (buildTile >= 0) {
            if (tile.getId() == buildTile)
                return "ALREADY BUILT";
            if (Tile.tiles[buildTile].isSolid() && isOccupied(tileBounds(tx, ty), true))
                return "SOMETHING IS IN THE WAY";
            return null;
        }
        if (itemId == Item.seedItem.getId() && tile != Tile.grassTile && tile != Tile.dirtTile)
            return "SEEDS NEED GRASS OR EARTH";
        StaticEntity object = createObject(tx, ty);
        if (object == null)
            return "CAN'T BE PLACED";
        if (isOccupied(worldBounds(object), false))
            return "SOMETHING IS IN THE WAY";
        return null;
    }

    /** Whether the area overlaps the player, a creature or object, or (optionally) an item. */
    private boolean isOccupied(Rectangle area, boolean checkItems) {
        if (worldBounds(player).intersects(area))
            return true;
        for (List<Entity> list : Arrays.asList(world().getEntityManager().getEntities(),
                world().getEntityManager().getProjectiles())) {
            for (Entity e : list) {
                if (e.isActive() && e != player && worldBounds(e).intersects(area))
                    return true;
            }
        }
        if (checkItems) {
            for (Item i : world().getItemManager().getItems()) {
                if (new Rectangle(i.getX(), i.getY(), Item.ITEMWIDTH, Item.ITEMHEIGHT).intersects(area))
                    return true;
            }
        }
        return false;
    }

    private StaticEntity createObject(int tx, int ty) {
        if (itemId == CraftableItem.chestItem.getId())
            return new Chest(handler, tx * Tile.TILEWIDTH, ty * Tile.TILEHEIGHT);
        if (itemId == Item.seedItem.getId())
            // plants are two tiles high and stand on the target tile
            return new Plant(handler, tx * Tile.TILEWIDTH, (ty - 1) * Tile.TILEHEIGHT);
        return null;
    }

    private Rectangle worldBounds(Entity e) {
        // collision bounds are in screen space
        Rectangle b = e.getCollisionBounds(0, 0);
        b.translate((int) handler.getGameCamera().getxOffset(), (int) handler.getGameCamera().getyOffset());
        return b;
    }

    private static Rectangle tileBounds(int tx, int ty) {
        return new Rectangle(tx * Tile.TILEWIDTH, ty * Tile.TILEHEIGHT, Tile.TILEWIDTH, Tile.TILEHEIGHT);
    }

    // ------------------------------------------------------------------ actions

    private void place(int tx, int ty) {
        String reason = whyNot(tx, ty);
        if (reason != null) {
            say(reason, false);
            return;
        }
        Item item = Item.items[itemId];
        Item carried = player.getInventory().getInventoryItemByID(itemId);
        carried.setCount(carried.getCount() - 1);
        int buildTile = item instanceof CraftableItem ? ((CraftableItem) item).getBuildTileId() : -1;
        if (buildTile >= 0) {
            // replacing a built floor gives its material back
            refund(world().getTile(tx, ty));
            world().setTile(tx, ty, buildTile);
        } else {
            // the player ticks while the entity list is iterated, so new objects go with the projectiles
            world().getEntityManager().getProjectiles().add(createObject(tx, ty));
        }
        if (carried.getCount() <= 0) {
            say("NO " + item.getName().toUpperCase() + " LEFT", false);
            stop();
        }
    }

    private void remove(int tx, int ty) {
        World world = world();
        if (tx < 0 || ty < 0 || tx >= world.getWidth() || ty >= world.getHEight() || !world.getTile(tx, ty).isBuilt()) {
            say("NOTHING BUILT HERE", false);
            return;
        }
        CraftableItem item = refund(world.getTile(tx, ty));
        world.setTile(tx, ty, world.getOriginalTile(tx, ty));
        if (item != null)
            say("+1 " + item.getName().toUpperCase(), true);
    }

    /** Gives the material of a built tile back to the player. */
    private CraftableItem refund(Tile tile) {
        for (CraftableItem item : TILE_ITEMS) {
            if (item.getBuildTileId() == tile.getId()) {
                player.getInventory().addItem(item.createNew(1));
                return item;
            }
        }
        return null;
    }

    private void say(String text, boolean good) {
        message = text;
        messageGood = good;
        messageTime = System.currentTimeMillis();
    }

    // ------------------------------------------------------------------ drawing

    public void render(Graphics g) {
        if (!isActive())
            return;
        Graphics2D g2d = (Graphics2D) g;
        Composite oldComposite = g2d.getComposite();
        Stroke oldStroke = g2d.getStroke();

        Point t = target();
        boolean valid = whyNot(t.x, t.y) == null;
        int sx = (int) (t.x * Tile.TILEWIDTH - handler.getGameCamera().getxOffset());
        int sy = (int) (t.y * Tile.TILEHEIGHT - handler.getGameCamera().getyOffset());
        Item item = Item.items[itemId];

        // preview of the item on the target tile
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        if (itemId == Item.seedItem.getId())
            g.drawImage(Assets.treeStages[0], sx, sy - Tile.TILEHEIGHT, Tile.TILEWIDTH, Tile.TILEHEIGHT * 2, null);
        else
            g.drawImage(item.getTexture(), sx, sy, Tile.TILEWIDTH, Tile.TILEHEIGHT, null);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
        g.setColor(valid ? VALID : INVALID);
        g.fillRect(sx, sy, Tile.TILEWIDTH, Tile.TILEHEIGHT);
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.setStroke(new BasicStroke(4));
        g.drawRect(sx + 2, sy + 2, Tile.TILEWIDTH - 4, Tile.TILEHEIGHT - 4);

        // info panel at the bottom
        int pw = 1100, ph = 110, px = (1920 - pw) / 2, py = 1080 - ph - 20;
        g.setColor(PANEL);
        g.fillRoundRect(px, py, pw, ph, 24, 24);
        g.setColor(Color.BLACK);
        g.drawRoundRect(px, py, pw, ph, 24, 24);
        BufferedImage icon = item.getTexture();
        g.drawImage(icon, px + 20, py + 19, 72, 72, null);
        Text.drawString(g, "BUILDING  " + item.getName().toUpperCase() + "  x" + carried(), px + 112, py + 48, false, Color.WHITE, Assets.font40);
        Text.drawString(g, "CLICK/R PLACE    RIGHT CLICK/X REMOVE    Q/ESC STOP", px + 112, py + 88, false, HINT, Assets.font28);

        if (message != null && System.currentTimeMillis() - messageTime < MESSAGE_DURATION)
            Text.drawString(g, message, 1920 / 2, py - 30, true, messageGood ? VALID : INVALID, Assets.font40);
        else if (!valid)
            Text.drawString(g, whyNot(t.x, t.y), 1920 / 2, py - 30, true, INVALID, Assets.font28);

        g2d.setComposite(oldComposite);
        g2d.setStroke(oldStroke);
    }
}
