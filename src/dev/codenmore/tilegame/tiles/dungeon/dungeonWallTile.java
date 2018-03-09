package dev.codenmore.tilegame.tiles.dungeon;

import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.tiles.Tile;

import java.awt.*;

/**
 * Created by Freddy on 28.02.2018.
 */
public class dungeonWallTile extends Tile {
    public dungeonWallTile( int id) {
        super(Assets.dungeonFloor , id);

    }

    /**
     * Renders dungeon walls, relative to where other walls are placed
     * surroundingTiles is a 3x3 multidimensional array containing the tile data of surrounding tiles
     * @param g
     * @param x
     * @param y
     * @param surrTiles
     */
    public void render(Graphics g, int x, int y, int[][] surrTiles)
    {
        // we can ignore diagonal positions (0,0) (3,3) (0,3) (3,0)
        // id of Wall tile should be 7
        int ownId = 7;
        int leftTile, rightTile, upTile, downTile;
        leftTile = surrTiles[0][1];
        rightTile = surrTiles[2][1];
        upTile = surrTiles[1][0];
        downTile = surrTiles[1][2];



        if(surrTiles.length != 3 || surrTiles[0].length != 3) {
            return;
        }
        if(leftTile != ownId && rightTile != ownId && upTile != ownId && downTile != ownId) {
            // none of surrounding tiles is wall, so we are single block wall
            this.texture = Assets.dungeonWallSingleBlock;
        } else if(leftTile == ownId && rightTile != ownId && upTile != ownId && downTile != ownId) {
            this.texture = Assets.dungeonWallSingleRight;
        } else if(leftTile != ownId && rightTile == ownId && upTile != ownId && downTile != ownId) {
            this.texture = Assets.dungeonWallSingleLeft;
        } else if(leftTile == ownId && rightTile != ownId && upTile == ownId && downTile != ownId) {
            this.texture = Assets.dungeonWallRightCorner;
        } else if(leftTile != ownId && rightTile == ownId && upTile == ownId && downTile != ownId) {
            this.texture = Assets.dungeonWallLeftCorner;
        } else if(leftTile == ownId && rightTile != ownId && upTile != ownId && downTile == ownId) {
            this.texture = Assets.dungeonWallUpperCornerRight;
        } else if(leftTile != ownId && rightTile == ownId && upTile != ownId && downTile == ownId) {
            this.texture = Assets.dungeonWallUpperCornerLeft;
        } else if(leftTile == ownId && rightTile == ownId && upTile == ownId && downTile == ownId) {
            this.texture = Assets.dungeonWallMiddle;
        } else if(leftTile == ownId && rightTile == ownId && upTile != ownId && downTile != ownId) {
            this.texture = Assets.dungeonWallSingleMiddle;
        } else if(leftTile != ownId && rightTile != ownId && upTile == ownId && downTile == ownId) {
            this.texture = Assets.dungeonWallSingleMiddleUpwards;
        } else if(leftTile == ownId && rightTile != ownId && upTile == ownId && downTile == ownId) {
            this.texture = Assets.dungeonWallRight;
        } else if(leftTile != ownId && rightTile == ownId && upTile == ownId && downTile == ownId) {
            this.texture = Assets.dungeonWallLeft;
        } else if(leftTile != ownId && rightTile != ownId && upTile == ownId && downTile != ownId) {
            this.texture = Assets.dungeonWallSingleDown;
        } else if(leftTile != ownId && rightTile != ownId && upTile != ownId && downTile == ownId) {
            this.texture = Assets.dungeonWallSingleUp;
        } else if(leftTile == ownId && rightTile == ownId && upTile == ownId && downTile != ownId) {
            this.texture = Assets.dungeonWallStraightDown;
        } else if(leftTile == ownId && rightTile == ownId && upTile != ownId && downTile == ownId) {
            this.texture = Assets.dungeonWallStraightUp;
        }


        super.render(g, x, y);
    }

    @Override
    public boolean isSolid() {
        return true;
    }
}
