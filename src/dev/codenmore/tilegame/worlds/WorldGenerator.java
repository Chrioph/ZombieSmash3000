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
import dev.codenmore.tilegame.entity.statics.Rock;
import dev.codenmore.tilegame.entity.statics.Tree;

import java.util.ArrayList;

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
        world1();
        world2();
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

    public World getNextWorld()
    {
        boolean next = false;
        for(int i = 0; i<worlds.size(); i++) {
            if(worlds.get(i).getId() == currentWorld.getId()) {
                if(i<worlds.size()-1) {
                    currentWorld = worlds.get(i+1);
                    next = true;
                    break;
                }
            }
        }
        if(next) {
            return currentWorld;
        }else {
            return null;
        }
    }

    private World world2()
    {
        World world2 = new World(2, handler,"/worlds/world1.txt");
        EntityManager world2EnManager = new EntityManager(handler, player);

        //Add Entities here

        world2.setEntityManager(world2EnManager);

        worlds.add(world2);
        applyMods();

        return world2;
    }

    private World homeworld()
    {
        World homeworld = new World(99999, handler, "/worlds/homeworld.txt");
        EntityManager homeWorldEnManager = new EntityManager(handler, player);

        homeworld.setEntityManager(homeWorldEnManager);
        homeWorld = homeworld;
        worlds.add(homeworld);
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
