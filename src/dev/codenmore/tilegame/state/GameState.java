package dev.codenmore.tilegame.state;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.HUDs.HUD;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.gfx.Text;
import dev.codenmore.tilegame.saves.SaveManager;
import dev.codenmore.tilegame.ui.ConfirmDialog;
import dev.codenmore.tilegame.ui.UIImage;
import dev.codenmore.tilegame.ui.UIList;
import dev.codenmore.tilegame.ui.UIListElement;
import dev.codenmore.tilegame.ui.UIManager;
import dev.codenmore.tilegame.worlds.World;
import dev.codenmore.tilegame.worlds.WorldGenerator;

public class GameState extends State {


    private boolean b = false;
    private long timeUntilNextRender;
    private float v = 0.01f;
    private long timeLastSave = 0;
    private long timeLastLoad = 0;
    private boolean isPaused = false;
    private boolean nextLevelSelection = false;

    private World world;
    private World homeWorld;
    private WorldGenerator worldGen;

    private SaveManager saveManager;

    private String saveFilePath = "save.xml";

    private HUD hud;
    private UIManager uiManager;
    private ConfirmDialog exitDialog;
    private ConfirmDialog quitDialog;


    public GameState(Handler handler) {
        super(handler);
        this.worldGen = handler.getGame().getWorldGenerator();
        homeWorld = worldGen.getHomeWorld();
        world = this.worldGen.getFirstWorld();
        world.start();
        handler.setWorld(world);
        handler.setHomeWorld(homeWorld);

        saveManager = new SaveManager(handler, saveFilePath, worldGen);

        uiManager = new UIManager(handler);
        handler.getMouseManager().setUIManager(uiManager);

        uiManager.addObject(new UIImage(635, 165, 650, 750, Assets.settingsBodyElement));
        UIList myList = new UIList(635, 165, 650, 750, "nextlevel", new ArrayList<UIListElement>() {{
            // the list stays registered for mouse input while hidden, so ignore clicks unless it is shown
            add(new UIListElement(10, 10, 610, 230, "Next Level", Assets.nextLevelSelectionOption, () -> {
                if (!isLevelSelectionClickable())
                    return;

                handler.setWorld(handler.getNextWorld());
                handler.getWorld().start();
                nextLevelSelection = false;
                isPaused = false;
            }));
            add(new UIListElement(10, 10, 610, 230, "Homeworld", Assets.nextLevelSelectionOption, () -> {
                if (!isLevelSelectionClickable())
                    return;
                handler.setWorld(handler.getHomeWorld());
                handler.getHomeWorld().start();
                nextLevelSelection = false;
                isPaused = false;
            }));
            add(new UIListElement(10, 10, 610, 230, "Quit", Assets.nextLevelSelectionOption, () -> {
                if (!isLevelSelectionClickable())
                    return;
                quitDialog.open();
            }));

        }}, true, Assets.settingsBodyElement, false);
        myList.setPaddingX(20);
        myList.setPaddingY(20);
        myList.setSpacing(10);
        uiManager.addObject(myList);


        hud = new HUD(handler);

        exitDialog = new ConfirmDialog(handler, "Back to main menu?",
                "Unsaved progress will be lost.", () -> handler.getGame().restart());
        quitDialog = new ConfirmDialog(handler, "Quit game?",
                "Unsaved progress will be lost.", () -> handler.getGame().close());


    }

    @Override
    public void tick() {
        // the world is frozen while a confirmation dialog is open
        if (exitDialog.isOpen()) {
            exitDialog.tick();
            return;
        }
        if (quitDialog.isOpen()) {
            quitDialog.tick();
            return;
        }
        uiManager.tick();
        // Escape works everywhere in the game: while playing, after dying and in the level selection
        if (listenExit())
            return;
        if (!handler.getWorld().getEntityManager().getPlayer().isDead() && !isPaused) {
            handler.getWorld().tick();
            listenSave();
            listenLoad();
            if (handler.getWorld().getEntityManager().getPlayer().collisionWithFinish(
                    (int) (handler.getWorld().getEntityManager().getPlayer().getX() / 128),
                    (int) (handler.getWorld().getEntityManager().getPlayer().getY() / 128))) {
                // completing a level lets everything planted in the homeworld grow
                if (handler.getWorld() != homeWorld)
                    growPlants();
                // the exit of the homeworld continues with the level the player went home from
                World nextWorld = handler.getWorld() == homeWorld && handler.getNextWorld() != null
                        ? handler.getNextWorld() : this.worldGen.getNextWorld();
                if (nextWorld != null) {
                    handler.setNextWorld(nextWorld);
                    isPaused = true;
                    nextLevelSelection = true;
                }

            }
        }

        // if whole game is paused, dont tick world or exit
        if (isPaused) {
            if (nextLevelSelection) {

            }
        }
    }

    @Override
    public void render(Graphics g) {
        // TODO Auto-generated method stub
        handler.getWorld().render(g);
        if(!handler.getWorld().getEntityManager().getPlayer().isInputDisabled())
            hud.render(g);
        if(timeLastSave != 0 && (System.currentTimeMillis() - timeLastSave) < 3000) {
            g.setFont(Assets.font28);
            g.drawString("Saved!", 1920-130, 1080-20);
        }
        if(timeLastLoad != 0 && (System.currentTimeMillis() - timeLastLoad) < 3000) {
            g.setFont(Assets.font28);
            g.drawString("Loaded!", 1920-130, 1080-20);
        }
        if (nextLevelSelection) {
            uiManager.render(g);
        }
        checkPlayerDie(g);
        exitDialog.render(g);
        quitDialog.render(g);

    }

    private void checkPlayerDie(Graphics g) {
        if (handler.getWorld().getEntityManager().getPlayer().isDead()) {
            // paintComponent changes the alpha of g, restore it so later drawing is not translucent
            Composite oldComposite = ((Graphics2D) g).getComposite();
            if (!b) {
                timeUntilNextRender = System.currentTimeMillis() + 10;
                b = true;
            }
            paintComponent(Assets.deathScreen, g, v);
            Text.drawString(g, "YOU DIED", 1920 / 2, 1080 / 2 + 200, true, Color.RED, Assets.font100);
            Text.drawString(g, "Press ESC to return to the main menu", 1920 / 2, 1080 / 2 + 300, true, Color.WHITE, Assets.font28);
            if (System.currentTimeMillis() > timeUntilNextRender && v < 0.99) {
                v += 0.01;
                b = false;
            }
            handler.getWorld().getEntityManager().getPlayer().render(g);
            ((Graphics2D) g).setComposite(oldComposite);
        }
    }

    public void paintComponent(BufferedImage i, Graphics g, float v) {
        Graphics2D g2d = (Graphics2D) g;
        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, v);
        g2d.setComposite(composite);
        g2d.drawImage(i, 0, 0, 1920, 1080, null);
    }

    /**
     * Opens the "back to main menu" confirmation when Escape is pressed.
     *
     * @return true if the dialog was opened
     */
    private boolean listenExit() {
        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_ESCAPE)) {
            // Escape first closes an open chest, the crafting screen, the inventory or building
            if (handler.getWorld().getEntityManager().getPlayer().closeScreens())
                return false;
            exitDialog.open();
            return true;
        }
        return false;
    }

    private void growPlants() {
        for (java.util.List<dev.codenmore.tilegame.entity.Entity> list : java.util.Arrays.asList(
                homeWorld.getEntityManager().getEntities(), homeWorld.getEntityManager().getProjectiles())) {
            for (dev.codenmore.tilegame.entity.Entity e : list) {
                if (e instanceof dev.codenmore.tilegame.entity.statics.Plant)
                    ((dev.codenmore.tilegame.entity.statics.Plant) e).grow();
            }
        }
    }

    private boolean isLevelSelectionClickable() {
        return nextLevelSelection && !exitDialog.isOpen() && !quitDialog.isOpen();
    }

    private void listenSave() {
        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_F5)) {
            handler.getGame().save();
        }
    }

    private void listenLoad() {
        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_F6)) {
            handler.getGame().load();
        }
    }




}
