package dev.codenmore.tilegame.items;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.entity.Entity;
import dev.codenmore.tilegame.entity.creatures.Player;
import dev.codenmore.tilegame.entity.statics.StaticEntity;
import dev.codenmore.tilegame.tiles.Tile;
import dev.codenmore.tilegame.worlds.World;
import dev.codenmore.tilegame.utils.Utils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ItemManager implements Serializable {
	
	private Handler handler;
	private ArrayList<Item> items = new ArrayList<Item>();
	private int x;
	private int y;
	
	
	public ItemManager(Handler handler) {
		this.handler=handler;

	}

	
	public void tick() {
		Iterator<Item>it = items.iterator();
		while (it.hasNext()) {
			Item i= it.next();
			i.tick();
			if(i.isPickedUp())
				it.remove();
		}
	}
	
	public void render(Graphics g) {
		for (Item i : items) {
			i.render(g);
		}
	}
	
	
	/** How far (in pixels) a dropped item may land from the drop point. */
	private static final int DROP_SCATTER = 100;
	private static final int RANDOM_DROP_ATTEMPTS = 40;
	/** How many tiles around the drop point are searched when no random spot was free. */
	private static final int FALLBACK_SEARCH_RADIUS = 6;

	/**
	 * Adds an item to the world.
	 *
	 * @param random if true the item is scattered randomly around (x, y), but only onto
	 *               walkable ground the player can reach (not on solid tiles, not outside
	 *               the map and not under rocks, trees or placed blocks)
	 */
	public void addItem(Item i, int x, int y, boolean random) {
		i.setHandler(handler);
		if (random) {
			Point p = findDropPosition(x, y);
			i.setPosition(p.x, p.y);
		} else {
			i.setPosition((int) (i.x + x), (int) (i.y + y));
		}
		items.add(i);
	}

	private Point findDropPosition(int x, int y) {
		// first try random spots around the drop point, like before
		for (int attempt = 0; attempt < RANDOM_DROP_ATTEMPTS; attempt++) {
			int px = x + Utils.generateRandomInt(2 * DROP_SCATTER) - DROP_SCATTER;
			int py = y + Utils.generateRandomInt(2 * DROP_SCATTER) - DROP_SCATTER;
			if (isFreeSpot(px, py))
				return new Point(px, py);
		}

		// otherwise use the centre of the nearest free tile, searching outwards ring by ring
		int tileX = Math.floorDiv(x + Item.ITEMWIDTH / 2, Tile.TILEWIDTH);
		int tileY = Math.floorDiv(y + Item.ITEMHEIGHT / 2, Tile.TILEHEIGHT);
		for (int r = 0; r <= FALLBACK_SEARCH_RADIUS; r++) {
			for (int dy = -r; dy <= r; dy++) {
				for (int dx = -r; dx <= r; dx++) {
					if (Math.max(Math.abs(dx), Math.abs(dy)) != r)
						continue;
					int px = (tileX + dx) * Tile.TILEWIDTH + (Tile.TILEWIDTH - Item.ITEMWIDTH) / 2;
					int py = (tileY + dy) * Tile.TILEHEIGHT + (Tile.TILEHEIGHT - Item.ITEMHEIGHT) / 2;
					if (isFreeSpot(px, py))
						return new Point(px, py);
				}
			}
		}

		// nothing free nearby (should not happen), keep the item where it was dropped
		return new Point(x, y);
	}

	/**
	 * Checks whether an item at (px, py) lies completely on walkable ground inside the map
	 * and is not covered by a solid object.
	 */
	private boolean isFreeSpot(int px, int py) {
		World world = handler.getWorld();
		int right = px + Item.ITEMWIDTH - 1;
		int bottom = py + Item.ITEMHEIGHT - 1;
		if (px < 0 || py < 0 || right >= world.getWidth() * Tile.TILEWIDTH || bottom >= world.getHEight() * Tile.TILEHEIGHT)
			return false;

		// every tile the item touches has to be walkable
		for (int ty = py / Tile.TILEHEIGHT; ty <= bottom / Tile.TILEHEIGHT; ty++)
			for (int tx = px / Tile.TILEWIDTH; tx <= right / Tile.TILEWIDTH; tx++)
				if (world.getTile(tx, ty).isSolid())
					return false;

		Rectangle itemRect = new Rectangle(px, py, Item.ITEMWIDTH, Item.ITEMHEIGHT);
		return !overlapsSolidObject(itemRect, world.getEntityManager().getEntities())
				&& !overlapsSolidObject(itemRect, world.getEntityManager().getProjectiles());
	}

	/** Rocks, trees, chests and placed blocks block the player, items under them can't be picked up. */
	private boolean overlapsSolidObject(Rectangle itemRect, ArrayList<Entity> entities) {
		// collision bounds of entities are in screen space, shift them back into world space
		int camX = (int) handler.getGameCamera().getxOffset();
		int camY = (int) handler.getGameCamera().getyOffset();
		for (Entity e : entities) {
			// the entity that is dropping the item is already inactive
			if (!(e instanceof StaticEntity) || !e.isActive())
				continue;
			Rectangle b = e.getCollisionBounds(0, 0);
			b.translate(camX, camY);
			if (b.intersects(itemRect))
				return true;
		}
		return false;
	}

	public void fillFromDump(NodeList dump)
	{
		// first clear current items
		items.clear();
		for(int i = 0; i < dump.getLength(); i++) {
			if (dump.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element it = (Element) dump.item(i);
				int xCoord = Integer.parseInt(it.getElementsByTagName("x").item(0).getTextContent());
				int yCoord = Integer.parseInt(it.getElementsByTagName("y").item(0).getTextContent());
				this.addItem(Item.items[Integer.parseInt(it.getElementsByTagName("id").item(0).getTextContent())].createNew(1), xCoord, yCoord, false);
			}
		}
	}


	//Getters and Setters
	
	public Handler getHandler() {
		return handler;
	}

	public ArrayList<Item> getItems()
	{
		return items;
	}


	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	
}
