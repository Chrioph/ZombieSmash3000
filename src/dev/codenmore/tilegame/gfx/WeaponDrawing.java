package dev.codenmore.tilegame.gfx;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

/**
 * Draws the sword and bow sprites rotated around the point where they are held.
 * All angles are in radians in screen space: 0 = right, PI/2 = down.
 */
public final class WeaponDrawing {

    /** Grip of the sword inside the 32px sprite and the direction its blade points. */
    private static final double SWORD_GRIP_X = 9, SWORD_GRIP_Y = 24, SWORD_ANGLE = -Math.PI / 4;

    /** Grip of the bow inside the 32px sprite, the bow faces right. */
    private static final double BOW_GRIP_X = 19, BOW_GRIP_Y = 15.5;
    /** The string is the column x = 13 between the two limb tips. */
    private static final int BOW_STRING_X = 13, BOW_STRING_TOP = 4, BOW_STRING_BOTTOM = 27;

    private static BufferedImage bowWithoutString;
    private static Color bowStringColor;

    private WeaponDrawing() {
    }

    /**
     * @param angle direction the blade points to
     */
    public static void drawSword(Graphics2D g, double gripX, double gripY, double angle, int size) {
        drawRotated(g, Assets.sword, size, SWORD_GRIP_X, SWORD_GRIP_Y, angle - SWORD_ANGLE, gripX, gripY);
    }

    /**
     * Draws the bow with a string that can be pulled back.
     *
     * @param angle direction the bow (and the arrow) points to
     * @param pull  how far the middle of the string is pulled back, in sprite pixels (0 = resting,
     *              negative values bend it forward)
     */
    public static void drawBow(Graphics2D g, double gripX, double gripY, double angle, int size, double pull) {
        initBow();
        AffineTransform old = g.getTransform();
        Stroke oldStroke = g.getStroke();
        Object oldInterpolation = g.getRenderingHint(RenderingHints.KEY_INTERPOLATION);

        drawRotated(g, bowWithoutString, size, BOW_GRIP_X, BOW_GRIP_Y, angle, gripX, gripY);

        // draw the string in sprite coordinates so it rotates and scales with the bow
        g.translate(gripX, gripY);
        g.rotate(angle);
        g.scale(size / 32.0, size / 32.0);
        g.translate(-BOW_GRIP_X, -BOW_GRIP_Y);
        g.setColor(bowStringColor);
        g.setStroke(new BasicStroke(1f));
        double sx = BOW_STRING_X + 0.5;
        double nockX = sx - pull;
        double nockY = (BOW_STRING_TOP + BOW_STRING_BOTTOM + 1) / 2.0;
        g.draw(new Line2D.Double(sx, BOW_STRING_TOP - 0.5, nockX, nockY));
        g.draw(new Line2D.Double(nockX, nockY, sx, BOW_STRING_BOTTOM + 1.5));

        g.setTransform(old);
        g.setStroke(oldStroke);
        if (oldInterpolation != null)
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, oldInterpolation);
    }

    private static void drawRotated(Graphics2D g, BufferedImage img, int size, double spriteGripX, double spriteGripY,
                                    double rotation, double gripX, double gripY) {
        AffineTransform old = g.getTransform();
        Composite oldComposite = g.getComposite();
        Object oldInterpolation = g.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
        // keep the pixel art crisp while rotating
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setComposite(AlphaComposite.SrcOver);
        g.translate(gripX, gripY);
        g.rotate(rotation);
        double scale = size / 32.0;
        g.drawImage(img, (int) Math.round(-spriteGripX * scale), (int) Math.round(-spriteGripY * scale), size, size, null);
        g.setTransform(old);
        g.setComposite(oldComposite);
        if (oldInterpolation != null)
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, oldInterpolation);
    }

    /** Creates a copy of the bow sprite without its string, the string is drawn separately. */
    private static void initBow() {
        if (bowWithoutString != null && bowStringColor != null)
            return;
        BufferedImage src = Assets.bow;
        BufferedImage copy = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        copy.getGraphics().drawImage(src, 0, 0, null);
        bowStringColor = new Color(copy.getRGB(BOW_STRING_X, 15), true);
        for (int y = BOW_STRING_TOP; y <= BOW_STRING_BOTTOM; y++)
            copy.setRGB(BOW_STRING_X, y, 0);
        bowWithoutString = copy;
    }
}
