package dev.codenmore.tilegame.worlds.util;

import java.awt.Rectangle;

/**
 * Rectangular room of a generated dungeon, in tile coordinates.
 */
public class Room extends Rectangle {

    public Room(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    /**
     * @param gap number of tiles that have to stay free between the two rooms
     * @return true if the rooms overlap or are closer than {@code gap} tiles
     */
    public boolean overlaps(Room other, int gap) {
        Rectangle grown = new Rectangle(x - gap, y - gap, width + 2 * gap, height + 2 * gap);
        return grown.intersects(other);
    }

    public int centerX() {
        return x + width / 2;
    }

    public int centerY() {
        return y + height / 2;
    }
}
