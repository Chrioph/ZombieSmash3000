package dev.codenmore.tilegame.ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.KeyEvent;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.Settings;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.gfx.Text;
import dev.codenmore.tilegame.input.KeyManager;
import dev.codenmore.tilegame.input.MouseManager;

/**
 * Modal yes/no dialog that is drawn on top of the current state.
 *
 * While the dialog is open the owning state should only call {@link #tick()} and skip its own logic.
 * Controls: Left/Right or A/D to select, Enter/Space to confirm the selection,
 * Y/J for yes, N/Escape for no, or click a button with the mouse.
 * "No" is selected by default so pressing Escape twice or Enter never quits by accident.
 */
public class ConfirmDialog {

    private static final int WIDTH = 900;
    private static final int HEIGHT = 380;
    private static final int X = (1920 - WIDTH) / 2;
    private static final int Y = (1080 - HEIGHT) / 2;
    private static final int BUTTON_WIDTH = 260;
    private static final int BUTTON_HEIGHT = 90;
    private static final int BUTTON_Y = Y + HEIGHT - BUTTON_HEIGHT - 40;

    private static final Color OVERLAY = new Color(0, 0, 0, 160);
    private static final Color PANEL = new Color(16, 22, 34);
    private static final Color BORDER = new Color(0, 0, 0);
    private static final Color BUTTON = new Color(60, 66, 80);
    private static final Color BUTTON_SELECTED = new Color(170, 30, 30);

    private final Handler handler;
    private final String title;
    private final String message;
    private final Runnable onConfirm;

    private final Rectangle yesButton = new Rectangle(1920 / 2 - BUTTON_WIDTH - 30, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);
    private final Rectangle noButton = new Rectangle(1920 / 2 + 30, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);

    // volatile: open() may be called from a UI click handler on the AWT event thread
    private volatile boolean open = false;
    private boolean yesSelected = false;
    private boolean mouseWasPressed = false;
    private UIManager detachedUIManager;

    public ConfirmDialog(Handler handler, String title, String message, Runnable onConfirm) {
        this.handler = handler;
        this.title = title;
        this.message = message;
        this.onConfirm = onConfirm;
    }

    public boolean isOpen() {
        return open;
    }

    public void open() {
        if (open)
            return;
        open = true;
        yesSelected = false;
        MouseManager mouse = handler.getMouseManager();
        // ignore a click that is still held down when the dialog opens
        mouseWasPressed = mouse.isLeftPressed();
        // detach the state's UI so clicks don't reach buttons hidden behind the dialog
        detachedUIManager = mouse.getUIManager();
        mouse.setUIManager(null);
    }

    public void close() {
        if (!open)
            return;
        open = false;
        handler.getMouseManager().setUIManager(detachedUIManager);
        detachedUIManager = null;
    }

    public void tick() {
        if (!open)
            return;

        KeyManager keys = handler.getKeyManager();
        if (keys.keyJustPressed(KeyEvent.VK_LEFT) || keys.keyJustPressed(KeyEvent.VK_A))
            yesSelected = true;
        if (keys.keyJustPressed(KeyEvent.VK_RIGHT) || keys.keyJustPressed(KeyEvent.VK_D))
            yesSelected = false;

        if (keys.keyJustPressed(KeyEvent.VK_ESCAPE) || keys.keyJustPressed(KeyEvent.VK_N)) {
            close();
            return;
        }
        if (keys.keyJustPressed(KeyEvent.VK_Y) || keys.keyJustPressed(KeyEvent.VK_J)) {
            confirm();
            return;
        }
        if (keys.keyJustPressed(KeyEvent.VK_ENTER) || keys.keyJustPressed(KeyEvent.VK_SPACE)) {
            if (yesSelected)
                confirm();
            else
                close();
            return;
        }

        tickMouse();
    }

    private void tickMouse() {
        MouseManager mouse = handler.getMouseManager();
        // mouse coordinates are in window pixels, the dialog is laid out in the 1920x1080 game space
        int mx = (int) (mouse.getMouseX() / Settings.getScaleX());
        int my = (int) (mouse.getMouseY() / Settings.getScaleY());

        if (yesButton.contains(mx, my))
            yesSelected = true;
        else if (noButton.contains(mx, my))
            yesSelected = false;

        boolean pressed = mouse.isLeftPressed();
        boolean clicked = mouseWasPressed && !pressed;
        mouseWasPressed = pressed;
        if (!clicked)
            return;

        if (yesButton.contains(mx, my))
            confirm();
        else if (noButton.contains(mx, my))
            close();
    }

    private void confirm() {
        close();
        onConfirm.run();
    }

    public void render(Graphics g) {
        if (!open)
            return;

        Graphics2D g2d = (Graphics2D) g;
        Stroke oldStroke = g2d.getStroke();
        Composite oldComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.SrcOver);

        g.setColor(OVERLAY);
        g.fillRect(0, 0, 1920, 1080);

        g.setColor(PANEL);
        g.fillRoundRect(X, Y, WIDTH, HEIGHT, 40, 40);
        g2d.setStroke(new BasicStroke(8));
        g.setColor(BORDER);
        g.drawRoundRect(X, Y, WIDTH, HEIGHT, 40, 40);

        Text.drawString(g, title, 1920 / 2, Y + 80, true, Color.WHITE, Assets.font56);
        Text.drawString(g, message, 1920 / 2, Y + 160, true, Color.LIGHT_GRAY, Assets.font28);

        renderButton(g2d, yesButton, "Yes", yesSelected);
        renderButton(g2d, noButton, "No", !yesSelected);

        g2d.setStroke(oldStroke);
        g2d.setComposite(oldComposite);
    }

    private void renderButton(Graphics2D g, Rectangle r, String label, boolean selected) {
        g.setColor(selected ? BUTTON_SELECTED : BUTTON);
        g.fillRoundRect(r.x, r.y, r.width, r.height, 20, 20);
        g.setStroke(new BasicStroke(selected ? 6 : 4));
        g.setColor(selected ? Color.WHITE : BORDER);
        g.drawRoundRect(r.x, r.y, r.width, r.height, 20, 20);
        Text.drawString(g, label, r.x + r.width / 2, r.y + r.height / 2, true, Color.WHITE, Assets.font40);
    }
}
