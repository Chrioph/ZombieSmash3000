package dev.codenmore.tilegame.gfx;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;

/**
 * Short sword swing that is drawn on top of the player when a melee attack is made.
 *
 * The sword sprite is rotated in an arc around the player's hand towards the attack
 * direction, followed by a fading slash trail. Purely visual, the hit itself is
 * handled by the player.
 */
public class SwordSwing {

    /** Attack directions, same numbering as the player's facing direction. */
    public static final int RIGHT = 0, DOWN = 1, LEFT = 2, UP = 3;

    private static final long DURATION = 220;
    /** Total angle the blade sweeps through. */
    private static final double SWEEP = Math.toRadians(150);
    /** Drawn size of the 32px sword sprite. */
    private static final int SPRITE_SIZE = 56;
    /** Grip of the sword inside the 32px sprite, the point the sword rotates around. */
    private static final double GRIP_X = 9, GRIP_Y = 24;
    /** The blade in the sprite points up-right. */
    private static final double SPRITE_ANGLE = -Math.PI / 4;
    /** Distance from the grip to the tip in drawn pixels, used for the trail radius. */
    private static final double BLADE_LENGTH = Math.hypot(27 - GRIP_X, 7 - GRIP_Y) * SPRITE_SIZE / 32.0;

    private static final Color TRAIL = new Color(255, 255, 255);

    private final int direction;
    private final double centerAngle;
    private final long startTime;

    public SwordSwing(int direction) {
        this.direction = direction;
        this.startTime = System.currentTimeMillis();
        switch (direction) {
            case RIGHT: centerAngle = 0; break;
            case DOWN:  centerAngle = Math.PI / 2; break;
            case LEFT:  centerAngle = Math.PI; break;
            default:    centerAngle = -Math.PI / 2; break;
        }
    }

    public int getDirection() {
        return direction;
    }

    public boolean isFinished() {
        return System.currentTimeMillis() - startTime >= DURATION;
    }

    /** When attacking upwards the sword is behind the player and has to be drawn first. */
    public boolean isBehindPlayer() {
        return direction == UP;
    }

    private double progress() {
        double t = (System.currentTimeMillis() - startTime) / (double) DURATION;
        t = Math.max(0, Math.min(1, t));
        // ease out: fast start, slowing down at the end of the swing
        return 1 - (1 - t) * (1 - t) * (1 - t);
    }

    /**
     * Draws the swing.
     *
     * @param g      graphics in game space
     * @param handX  screen x of the point the sword rotates around (the player's hand)
     * @param handY  screen y of that point
     */
    public void render(Graphics2D g, int handX, int handY) {
        if (isFinished())
            return;

        double p = progress();
        // swings clockwise on screen through the attack direction
        double startAngle = centerAngle - SWEEP / 2;
        double angle = startAngle + SWEEP * p;

        AffineTransform oldTransform = g.getTransform();
        Composite oldComposite = g.getComposite();
        Stroke oldStroke = g.getStroke();
        Object oldInterpolation = g.getRenderingHint(RenderingHints.KEY_INTERPOLATION);

        renderTrail(g, handX, handY, startAngle, angle, p);

        // keep the pixel art crisp while rotating
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setComposite(AlphaComposite.SrcOver);
        g.translate(handX, handY);
        g.rotate(angle - SPRITE_ANGLE);
        double scale = SPRITE_SIZE / 32.0;
        BufferedImage sword = Assets.sword;
        g.drawImage(sword, (int) (-GRIP_X * scale), (int) (-GRIP_Y * scale), SPRITE_SIZE, SPRITE_SIZE, null);

        g.setTransform(oldTransform);
        g.setComposite(oldComposite);
        g.setStroke(oldStroke);
        if (oldInterpolation != null)
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, oldInterpolation);
    }

    private void renderTrail(Graphics2D g, int cx, int cy, double startAngle, double angle, double p) {
        double sweep = angle - startAngle;
        if (sweep <= 0)
            return;
        // the trail fades out towards the end of the swing
        float alpha = (float) (0.6 * (1 - p) + 0.1);
        double r = BLADE_LENGTH * 0.85;
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setColor(TRAIL);
        g.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // Arc2D measures angles counter-clockwise with y pointing up, the game uses y pointing down
        g.draw(new Arc2D.Double(cx - r, cy - r, 2 * r, 2 * r,
                -Math.toDegrees(startAngle), -Math.toDegrees(sweep), Arc2D.OPEN));
    }
}
