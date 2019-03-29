package com.goldenbois.zombiesmash.state;

import java.awt.AlphaComposite;

import com.goldenbois.zombiesmash.saves.SaveManager;
import com.goldenbois.zombiesmash.worlds.World;
import com.goldenbois.zombiesmash.worlds.WorldGenerator;
import org.newdawn.slick.Graphics;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.goldenbois.zombiesmash.Handler;
import com.goldenbois.zombiesmash.HUDs.HUD;
import com.goldenbois.zombiesmash.gfx.Assets;
import com.goldenbois.zombiesmash.ui.UIImage;
import com.goldenbois.zombiesmash.ui.UIList;
import com.goldenbois.zombiesmash.ui.UIListElement;
import com.goldenbois.zombiesmash.ui.UIManager;
import org.newdawn.slick.Image;

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
            add(new UIListElement(10, 10, 610, 230, "Next Level", Assets.nextLevelSelectionOption, () -> {

                handler.setWorld(handler.getNextWorld());
                handler.getWorld().start();
                nextLevelSelection = false;
                isPaused = false;
            }));
            add(new UIListElement(10, 10, 610, 230, "Homeworld", Assets.nextLevelSelectionOption, () -> {
                handler.setWorld(handler.getHomeWorld());
                handler.getHomeWorld().start();
                nextLevelSelection = false;
                isPaused = false;
            }));
            add(new UIListElement(10, 10, 610, 230, "Quit", Assets.nextLevelSelectionOption, () -> {
                handler.getGame().close();
            }));

        }}, true, Assets.settingsBodyElement, false);
        myList.setPaddingX(20);
        myList.setPaddingY(20);
        myList.setSpacing(10);
        uiManager.addObject(myList);


        hud = new HUD(handler);


    }

    @Override
    public void tick() {
        uiManager.tick();
        if (!handler.getWorld().getEntityManager().getPlayer().isDead() && !isPaused) {
            handler.getWorld().tick();
            listenExit();
            listenSave();
            listenLoad();
            if (handler.getWorld().getEntityManager().getPlayer().collisionWithFinish(
                    (int) (handler.getWorld().getEntityManager().getPlayer().getX() / 128),
                    (int) (handler.getWorld().getEntityManager().getPlayer().getY() / 128))) {
                World nextWorld = this.worldGen.getNextWorld();
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

    }

    private void checkPlayerDie(Graphics g) {
        if (handler.getWorld().getEntityManager().getPlayer().isDead()) {
            if (!b) {
                timeUntilNextRender = System.currentTimeMillis() + 10;
                b = true;
            }
            paintComponent(Assets.deathScreen, g, v);
            g.drawString( "YOU DIED", 1920 / 2, 1080 / 2 + 200);
            if (System.currentTimeMillis() > timeUntilNextRender && v < 0.99) {
                v += 0.01;
                b = false;
            }
            handler.getWorld().getEntityManager().getPlayer().render(g);
        }
    }

    public void paintComponent(Image i, Graphics g, float v) {
        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, v);
        g.drawImage(i, 0, 0);
    }

    private void listenExit() {
        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_ESCAPE)) {

            handler.getGame().restart();
        }
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
