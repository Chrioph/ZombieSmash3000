package dev.codenmore.tilegame.worlds.util;

import dev.codenmore.tilegame.tiles.Tile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by Freddy on 26.02.2018.
 */
public class Map {
    private int[][] tiles;
    private int height;
    private int width;

    public Map(int[][] tiles) {
        this.tiles = tiles;
        this.width = tiles.length;
        this.height = tiles[0].length;
    }

    public void renderMapImage()
    {
        BufferedImage mapImage = new BufferedImage(this.width * Tile.TILEWIDTH/4, this.height * Tile.TILEHEIGHT/4, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = mapImage.createGraphics();

        for (int y=0; y<height;y++) {
            for (int x=0;x<width;x++) {
                getTile(x,y).render(g,x*Tile.TILEWIDTH/4,y*Tile.TILEHEIGHT/4);
            }
        }
        try {
            ImageIO.write(mapImage, "png", new File("map.png"));
        }catch(Exception e) {

        }

    }

    private Tile getTile(int x, int y) {
        if (x<0 || y<0 || x>= width || y >= height )
            return Tile.grassTile;


        Tile t=Tile.tiles[tiles[x][y]];
        if (t== null)
            return Tile.dirtTile;
        else
            return t;

    }
}
