package dev.codenmore.tilegame.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import dev.codenmore.tilegame.gfx.Text;

/**
 * Button with a text label, styled like the image buttons of the main menu.
 */
public class UITextButton extends UIObject {

    private static final Color FILL = new Color(160, 160, 160);
    private static final Color FILL_HOVER = new Color(138, 138, 138);
    private static final Color BORDER = new Color(196, 196, 196);

    private final String text;
    private final Font font;
    private final ClickListener clicker;

    public UITextButton(float x, float y, int width, int height, String text, Font font, ClickListener clicker) {
        super(x, y, width, height);
        this.text = text;
        this.font = font;
        this.clicker = clicker;
    }

    @Override
    public void tick() {
    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Stroke oldStroke = g2d.getStroke();
        g.setColor(hovering ? FILL_HOVER : FILL);
        g.fillRect((int) x, (int) y, width, height);
        g2d.setStroke(new BasicStroke(8));
        g.setColor(BORDER);
        g.drawRect((int) x + 4, (int) y + 4, width - 8, height - 8);
        g2d.setStroke(oldStroke);
        Text.drawString(g, text, (int) x + width / 2, (int) y + height / 2, true, Color.WHITE, font);
    }

    @Override
    public void onClick() {
        clicker.onClick();
    }
}
