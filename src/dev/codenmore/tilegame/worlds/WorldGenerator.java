package dev.codenmore.tilegame.worlds;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.Modifiers.DamageMod;
import dev.codenmore.tilegame.Modifiers.HPMod;
import dev.codenmore.tilegame.Modifiers.Mod;
import dev.codenmore.tilegame.Modifiers.SpeedMod;
import dev.codenmore.tilegame.Settings;
import dev.codenmore.tilegame.entity.EntityManager;
import dev.codenmore.tilegame.entity.creatures.Enemies.Ogre;
import dev.codenmore.tilegame.entity.creatures.Enemies.Zombie;
import dev.codenmore.tilegame.entity.creatures.Player;
import dev.codenmore.tilegame.entity.statics.Chest;
import dev.codenmore.tilegame.items.Item;
import dev.codenmore.tilegame.entity.statics.Rock;
import dev.codenmore.tilegame.entity.statics.Tree;

import dev.codenmore.tilegame.worlds.util.MapImage;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Freddy on 22.02.2018.
 */
public class WorldGenerator {

    private Handler handler;
    private Player player;

    private ArrayList<Mod> difficultyMods;
    private ArrayList<World> worlds;

    public World getCurrentWorld() {
        return currentWorld;
    }

    private World currentWorld;
    private World homeWorld;

    /** Generated level n has the id 1000+n, the handmade first level use small ids and the homeworld 99999. */
    private static final int FIRST_GENERATED_ID = 1000;
    private static final int HOMEWORLD_ID = 99999;
    /** Seed of the current run, every generated level is derived from it. */
    private long runSeed;
    private int generatedLevels;

    private enum Difficulty {
        EASY, NORMAL, HARD;

        public ArrayList<Mod> getMods() {
            ArrayList<Mod> mods = new ArrayList<Mod>();
            if(this == EASY) {
                mods.add(new HPMod("Life Mod", 0.8));
                mods.add(new DamageMod("Damage Mod", 0.8));
                mods.add(new SpeedMod("Speed Mod", 0.8));

            }else if(this == NORMAL) {
            }else if(this == HARD) {
                mods.add(new HPMod("Life Mod", 1.3));
                mods.add(new DamageMod("Damage Mod", 1.3));
                mods.add(new SpeedMod("Speed Mod", 1.3));
            }
            return mods;
        }
    }

    public WorldGenerator(Handler handler, Player player)
    {
        this.handler = handler;
        this.player = player;

        difficultyMods = new ArrayList<Mod>();
        worlds = new ArrayList<World>();

        initializeWorlds();

    }

    private void initializeWorlds()
    {
        runSeed = new Random().nextLong();
        generatedLevels = 0;
        world1();
        homeworld();
    }

    private World world1(){
        World world1;
        if(Settings.getRenderDungeon()) {
            world1 = new World(1, handler,"/worlds/dungeon2.txt");
        }else {
            world1 = new World(1, handler,"/worlds/world2.txt");
        }

        EntityManager world1EnManager = new EntityManager(handler, player);
        // the first chest already holds a few things to show what chests are for
        Chest startChest = new Chest(handler, 400, 400);
        startChest.getInventory().add(Item.logItem.getId(), 3);
        startChest.getInventory().add(Item.seedItem.getId(), 2);
        world1EnManager.addEntity(startChest);

        world1EnManager.addEntity(new Tree(handler, 300, 450));
        world1EnManager.addEntity(new Tree(handler, 2000, 1300));
        world1EnManager.addEntity(new Tree(handler, 400, 2000));
        world1EnManager.addEntity(new Rock(handler, 3000, 2100));
        world1EnManager.addEntity(new Rock(handler, 1700, 900));
        world1EnManager.addEntity(new Rock(handler, 1400, 1900 ));
        world1EnManager.addEntity(new Rock(handler, 2450, 900 ));
        world1EnManager.addEntity(new Rock(handler, 2450, 850 ));

        world1EnManager.addEntity(new Zombie(handler, 1500, 1500 ));
        world1EnManager.addEntity(new Zombie(handler, 1500, 1750 ));
        world1EnManager.addEntity(new Zombie(handler, 1000, 750 ));
        world1EnManager.addEntity(new Zombie(handler, 200, 450 ));
        world1EnManager.addEntity(new Zombie(handler, 500, 170 ));
        world1EnManager.addEntity(new Zombie(handler, 1000, 1900 ));
        world1EnManager.addEntity(new Zombie(handler, 500, 1500 ));
        world1EnManager.addEntity(new Zombie(handler, 1570, 750 ));
        world1EnManager.addEntity(new Zombie(handler, 1300, 750 ));
        world1EnManager.addEntity(new Zombie(handler, 1200, 450 ));
        world1EnManager.addEntity(new Zombie(handler, 500, 1700 ));
        world1EnManager.addEntity(new Zombie(handler, 100, 1900 ));
        world1EnManager.addEntity(new Ogre(handler, 1200, 1900 ));
        world1EnManager.addEntity(new Ogre(handler, 1600, 1900 ));



        world1.setEntityManager(world1EnManager);
        
        worlds.add(world1);
        applyMods();
        currentWorld = world1;

        return world1;
    }

    public World getFirstWorld()
    {
        return worlds.get(0);
    }

    /**
     * Returns the level after the current one. Level 1 is handmade, from level 2 on every level
     * is a randomly generated dungeon that gets bigger and harder, so there is always a next
     * level. The homeworld is not a level, it is reached from the level selection.
     */
    public World getNextWorld()
    {
        int index = worlds.indexOf(currentWorld);
        for(int i = index + 1; i < worlds.size(); i++) {
            if(worlds.get(i) != homeWorld) {
                currentWorld = worlds.get(i);
                return currentWorld;
            }
        }
        currentWorld = generateLevel();
        return currentWorld;
    }

    /**
     * Generates the next random dungeon level and adds it to the worlds.
     */
    private World generateLevel()
    {
        generatedLevels++;
        DungeonGenerator generator = new DungeonGenerator(handler, player, runSeed, generatedLevels);
        World world = generator.generate(FIRST_GENERATED_ID + generatedLevels);
        world.getEntityManager().applyMods(difficultyMods);
        worlds.add(world);
        if(Settings.getDebug()) {
            // overview of the generated level for debugging
            MapImage.write(world.getTiles(), new File("map.png"));
        }
        return world;
    }

    private World homeworld()
    {
        World homeworld = new World(HOMEWORLD_ID, handler, "/worlds/homeworld.txt");
        EntityManager homeWorldEnManager = new EntityManager(handler, player);

        homeworld.setEntityManager(homeWorldEnManager);
        homeWorld = homeworld;
        worlds.add(homeworld);
        homeworld.setPlaceable(true);
        return homeworld;
    }

    /**
     * Set current world by id
     * @param id
     */
    public void setWorld(int id)
    {
        for(World w : worlds) {
            if(w.getId() == id) {
                currentWorld = w;
                break;
            }
        }
    }

    /**
     * Number of the level as shown to the player: 1 for the handmade first level, 2 for the
     * first generated level and so on, 0 for the homeworld.
     */
    public static int getLevelNumber(World world)
    {
        if(world == null || world.getId() == HOMEWORLD_ID)
            return 0;
        if(world.getId() > FIRST_GENERATED_ID)
            return world.getId() - FIRST_GENERATED_ID + 1;
        return world.getId();
    }

    public World getHomeWorld()
    {
        return homeWorld;
    }

    /**
     * If there is a difficulty with the given name, resets difficulty modifications with modifications from chosen difficulty
     * @param diff
     */
    public void setDifficulty(String diff){
        for(Difficulty difficulty : Difficulty.values()) {
            if(diff.equalsIgnoreCase(difficulty.toString())) {
                // empty difficulty mod list
                difficultyMods.clear();
                difficultyMods = difficulty.getMods();
                break;
            }
        }
        applyMods();
    }

    public void resetWorlds()
    {
        worlds.clear();
        initializeWorlds();
    }



    /**
     * Reapplies mods of difficulty (and maybe other mods, who knows) to all entity objects
     */
    private void applyMods()
    {
        for(World world : worlds) {
            world.getEntityManager().applyMods(difficultyMods);
        }

    }
}
