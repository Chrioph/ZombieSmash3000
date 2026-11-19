package dev.codenmore.tilegame.gfx;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;

/**
 * Short animation played when the player shoots an arrow: the bow is held towards the
 * shooting direction, its string snaps forward and vibrates, the bow recoils and a few
 * speed lines show the arrow leaving. Purely visual, the arrow itself is a separate entity.
 */
public class BowShot {

    /** Shooting directions, same numbering as {@link SwordSwing}. */
    public static final int RIGHT = SwordSwing.RIGHT, DOWN = SwordSwing.DOWN, LEFT = SwordSwing.LEFT, UP = SwordSwing.UP;

    private static final long DURATION = 320;
    private static final int BOW_SIZE = 48;
    /** How far in front of the hand the bow is held. */
    private static final int HOLD_DISTANCE = 10;
    /** Initial pull of the string in sprite pixels when the arrow is released. */
    private static final double MAX_PULL = 7;
    private static final double RECOIL = 6;
    private static final long SPEED_LINES_DURATION = 140;

    private final int direction;
    private final double angle;
    private final long startTime;

    public BowShot(int direction) {
        this.direction = direction;
        this.startTime = System.currentTimeMillis();
        switch (direction) {
            case RIGHT: angle = 0; break;
            case DOWN:  angle = Math.PI / 2; break;
            case LEFT:  angle = Math.PI; break;
            default:    angle = -Math.PI / 2; break;
        }
    }

    public int getDirection() {
        return direction;
    }

    public boolean isFinished() {
        return elapsed() >= DURATION;
    }

    /** When shooting upwards the bow is behind the player and has to be drawn first. */
    public boolean isBehindPlayer() {
        return direction == UP;
    }

    private long elapsed() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * @param handX screen x of the player's hand
     * @param handY screen y of the player's hand
     */
    public void render(Graphics2D g, int handX, int handY) {
        long t = elapsed();
        if (t >= DURATION)
            return;

        double dx = Math.cos(angle), dy = Math.sin(angle);
        // damped vibration of the string after release
        double pull = MAX_PULL * Math.exp(-t / 45.0) * Math.cos(2 * Math.PI * t / 70.0);
        // the bow is pushed back a little and returns
        double recoil = RECOIL * Math.exp(-t / 60.0);
        double gripX = handX + dx * (HOLD_DISTANCE - recoil);
        double gripY = handY + dy * (HOLD_DISTANCE - recoil);

        WeaponDrawing.drawBow(g, gripX, gripY, angle, BOW_SIZE, pull);
        renderSpeedLines(g, gripX, gripY, dx, dy, t);
    }

    private void renderSpeedLines(Graphics2D g, double x, double y, double dx, double dy, long t) {
        if (t >= SPEED_LINES_DURATION)
            return;
        double p = t / (double) SPEED_LINES_DURATION;
        Composite oldComposite = g.getComposite();
        Stroke oldStroke = g.getStroke();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (0.8 * (1 - p))));
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // perpendicular to the shooting direction
        double nx = -dy, ny = dx;
        double start = 20 + 50 * p;
        double length = 22 * (1 - p) + 6;
        for (int i = -1; i <= 1; i++) {
            double ox = nx * i * 9, oy = ny * i * 9;
            double s = start - Math.abs(i) * 8;
            g.draw(new Line2D.Double(x + dx * s + ox, y + dy * s + oy,
                    x + dx * (s + length) + ox, y + dy * (s + length) + oy));
        }
        g.setComposite(oldComposite);
        g.setStroke(oldStroke);
    }
}
