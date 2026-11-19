package dev.codenmore.tilegame.items;

import java.awt.image.BufferedImage;

import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.tiles.Tile;

/**
 * Items made in the crafting screen (see {@link dev.codenmore.tilegame.crafting.Recipe}).
 * Building materials know the tile they become when placed in the homeworld.
 */
public class CraftableItem extends Item {

	public static CraftableItem woodFloorItem = new CraftableItem(Assets.woodFloor, "Wood Floor", 2, Tile.woodFloorTile.getId());
	public static CraftableItem woodWallItem = new CraftableItem(Assets.woodWall, "Wood Wall", 3, Tile.woodWallTile.getId());
	public static CraftableItem chestItem = new CraftableItem(Assets.chest, "Chest", 5, -1);
	public static CraftableItem stoneFloorItem = new CraftableItem(Assets.stoneFloor, "Stone Floor", 6, Tile.stoneFloorTile.getId());
	public static CraftableItem stoneWallItem = new CraftableItem(Assets.stoneWall, "Stone Wall", 7, Tile.stoneWallTile.getId());

	/** id of the tile this item becomes when placed, -1 if it is placed as an object */
	private final int buildTileId;

	public CraftableItem(BufferedImage texture, String name, int id, int buildTileId) {
		super(texture, name, id, true);
		this.buildTileId = buildTileId;
	}

	public int getBuildTileId() {
		return buildTileId;
	}

	@Override
	public Item createNew(int count) {
		CraftableItem i = new CraftableItem(texture, name, id, buildTileId);
		i.setCount(count);
		return i;
	}

	@Override
	public Item createNew() {
		return new CraftableItem(texture, name, id, buildTileId);
	}
}
