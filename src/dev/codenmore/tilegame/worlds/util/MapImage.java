package dev.codenmore.tilegame.worlds.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import dev.codenmore.tilegame.tiles.Tile;

/**
 * Writes an overview image of a tile map, used in debug mode to look at generated levels.
 */
public class MapImage {

    private static final int SCALE = 4;

    private MapImage() {
    }

    public static void write(int[][] tiles, File file) {
        int width = tiles.length, height = tiles[0].length;
        int tw = Tile.TILEWIDTH / SCALE, th = Tile.TILEHEIGHT / SCALE;
        BufferedImage image = new BufferedImage(width * tw, height * th, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        // tiles always draw themselves in full size, so draw scaled down
        g.scale(1.0 / SCALE, 1.0 / SCALE);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = Tile.tiles[tiles[x][y]];
                if (tile != null)
                    tile.render(g, x * Tile.TILEWIDTH, y * Tile.TILEHEIGHT, x, y);
            }
        }
        g.dispose();
        try {
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            System.out.println("Could not write map image: " + e.getMessage());
        }
    }
}
