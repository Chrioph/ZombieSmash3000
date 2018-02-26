package dev.codenmore.tilegame.worlds.util;

import java.awt.*;

/**
 * Room also has walls
 */
public class Room extends Rectangle {

    public Room(int x, int y, int width, int height) {
        super(x,y,width,height);
    }


    public boolean intersects(Room r) {
        int tw = this.width +2;
        int th = this.height+2;
        int rw = r.width+2;
        int rh = r.height+2;
        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
            return false;
        }
        int tx = this.x-1;
        int ty = this.y-1;
        int rx = r.x-1;
        int ry = r.y-1;
        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;
        //      overflow || intersect
        return ((rw < rx || rw > tx) &&
                (rh < ry || rh > ty) &&
                (tw < tx || tw > rx) &&
                (th < ty || th > ry));
    }
}
