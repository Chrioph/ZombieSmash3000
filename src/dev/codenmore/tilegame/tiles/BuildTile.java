package dev.codenmore.tilegame.tiles;

import java.awt.image.BufferedImage;

/**
 * Tile the player builds in the homeworld: floors are walkable, walls are solid.
 */
public class BuildTile extends Tile {

    private final boolean solid;

    public BuildTile(BufferedImage texture, int id, boolean solid) {
        super(texture, id);
        this.solid = solid;
    }

    @Override
    public boolean isSolid() {
        return solid;
    }

    /** Built tiles can be taken down again, the natural ones can't. */
    public boolean isBuilt() {
        return true;
    }
}
