package dev.codenmore.tilegame.worlds;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.Modifiers.DamageMod;
import dev.codenmore.tilegame.Modifiers.HPMod;
import dev.codenmore.tilegame.Modifiers.Mod;
import dev.codenmore.tilegame.Modifiers.SpeedMod;
import dev.codenmore.tilegame.entity.EntityManager;
import dev.codenmore.tilegame.entity.creatures.Enemies.Ogre;
import dev.codenmore.tilegame.entity.creatures.Enemies.Zombie;
import dev.codenmore.tilegame.entity.creatures.Player;
import dev.codenmore.tilegame.entity.statics.Rock;
import dev.codenmore.tilegame.entity.statics.Tree;
import dev.codenmore.tilegame.tiles.Tile;
import dev.codenmore.tilegame.worlds.util.Cell;
import dev.codenmore.tilegame.worlds.util.Map;
import dev.codenmore.tilegame.worlds.util.Room;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Freddy on 22.02.2018.
 */
public class WorldGenerator {

    private Handler handler;
    private Player player;

    private ArrayList<Mod> difficultyMods;
    private ArrayList<World> worlds;

    private World currentWorld;

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

    private enum Direction {
        UP(0), DOWN(1), LEFT(2), RIGHT(3);

        private int value;
        Direction(int value) {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }
    }

    public WorldGenerator(Handler handler, Player player)
    {
        this.handler = handler;
        this.player = player;

        difficultyMods = new ArrayList<Mod>();
        worlds = new ArrayList<World>();

        world1();
        world2();
    }

    private World world1(){
        World world1 = new World(1, handler,"/worlds/world2.txt");
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
        printGrid(world1.getTiles());
        applyMods();
        currentWorld = world1;

        return world1;
    }

    public World getFirstWorld()
    {
        return worlds.get(0);
    }

    public World generateWorldRooms(long seed)
    {
        int[][] tiles = new int[100][100];

        // generate start room, always 4x4
        Random rand = new Random(seed);

        // we want the room to be somewhere in bounds, those are the starting points for the room
        int spawnRoomX = rand.nextInt(27) + 1;
        int spawnRoomY = rand.nextInt(27) + 1;

        ArrayList<Room> rooms = new ArrayList<Room>();
        Room spawnRoom = new Room(spawnRoomX, spawnRoomY, 4, 4);
        rooms.add(spawnRoom);

        // we default every tile to a stone tile, then carve into the map
        for(int i = 0; i<tiles.length;i++) {
            for(int j = 0; j<tiles[i].length;j++) {
                tiles[i][j] = 2;
            }
        }

        for(int i = 0; i<4; i++) {
            tiles[spawnRoomX+i][spawnRoomY+i] = 0;
        }
        // Calculate player start coordinates
        int playerSpawnX = (spawnRoomX +1)* Tile.TILEWIDTH;
        int playerSpawnY = (spawnRoomY +1)* Tile.TILEHEIGHT;

        // generate rooms for the space that is left
        // technically, the start room reserves 5x5 space, because the outer line has to be painted
        // in this case, we have 30x30 space that we can distribute


        for(int i = 0; i<1000; i++) {
            int size = (rand.nextInt(10)+1) * 2 + 1;
            int rectangularity = rand.nextInt(1+size)*2;
            int width = size, height = size;
            boolean overlaps = false;
            if(rand.nextInt(10) < 5) {
                width += rectangularity;
            }else {
                height += rectangularity;
            }

            int x = rand.nextInt(100-width);
            int y = rand.nextInt(100-height);

            Room newRoom = new Room(x,y,width,height);
            for(Room room : rooms) {
                if(room.intersects(newRoom)) {
                    overlaps = true;
                    break;
                }
            }
            if(overlaps) {
                continue;
            }

            rooms.add(newRoom);

        }

        for(Room room : rooms) {
            for(int i = 0; i<room.width; i++) {
                for(int j = 0; j<room.height; j++) {
                    tiles[room.x+i][room.y+j] = 0;
                }
            }
        }

        World rWorld = new World(handler,tiles, playerSpawnX, playerSpawnY);
        EntityManager world1EnManager = new EntityManager(handler, player);

        rWorld.setEntityManager(world1EnManager);
        printGrid(tiles);
        Map myMap = new Map(tiles);
        myMap.renderMapImage();
        worlds.add(rWorld);
        applyMods();


        rWorld.start();
        return rWorld;


    }


    public World generateWorldGrowingTree(long seed)
    {
        Random rand = new Random(seed);
        // Generate tileset for world

        //max size for world
        int[][] tiles = new int[32][32];
        // outline should be completely solid
        for(int i = 0; i<tiles.length;i++) {
            tiles[0][i] = 2;
            tiles[i][0] = 2;
            tiles[tiles.length-1][i] = 2;
            tiles[i][tiles.length-1] = 2;
        }

        int rand1 = rand.nextInt(10);
        int rand2 = rand.nextInt(10);
        int[][] visitedCells = new int[10][10];
        for(int i = 0; i<visitedCells.length;i++) {
            for(int j = 0; j<visitedCells[i].length; j++) {
                visitedCells[i][j] = 0;
            }
        }
        Cell cell;
        Cell nCell;
        int rand3;

        ArrayList<Cell> cells;
        cells = new ArrayList<Cell>();
        ArrayList<Direction> directions;
        directions = new ArrayList<Direction>();
        boolean nextFound;


        directions.add(Direction.UP);
        directions.add(Direction.DOWN);
        directions.add(Direction.RIGHT);
        directions.add(Direction.LEFT);

        cells.add(new Cell(rand1, rand2));
        visitedCells[rand1][rand2] = 1;
        System.out.println("Start Cell: " + rand1 + "/" + rand2);

        //
        tiles[getTripleCoords(rand1)][getTripleCoords(rand2)] = 0;
        while(!cells.isEmpty()) {
            nextFound = false;
            rand3 = rand.nextInt(cells.size());
            cell = cells.get(rand3);
            Collections.shuffle(directions, rand);
            for(int i = 0; i<directions.size(); i++) {
                if(directions.get(i).getValue() == 0) { // UP
                    if(cell.y-1 >= 0) {
                        if(visitedCells[cell.x][cell.y-1] == 0) {
                            // draw path in tiles
                            // when moving up we need to set tiles left and right
                            // last position is reserved for outer wall

                            tiles[getTripleCoords(cell.x)][getTripleCoords(cell.y-1)] = 0;
                            tiles[getTripleCoords(cell.x)][getTripleCoords(cell.y-1)+1] = 0;
                            tiles[getTripleCoords(cell.x)][getTripleCoords(cell.y-1)+2] = 0;


                            nCell = new Cell(cell.x, cell.y-1);
                            cells.add(nCell);

                            if(getTripleCoords(nCell.x) + 1 < tiles.length - 1) {
                                tiles[getTripleCoords(nCell.x) + 1][getTripleCoords(nCell.y)] = 2;
                                tiles[getTripleCoords(nCell.x) + 1][getTripleCoords(nCell.y)+1] = 2;
                                tiles[getTripleCoords(nCell.x) + 1][getTripleCoords(nCell.y)+2] = 2;
                            }else if(getTripleCoords(nCell.x) - 1 > 0) {
                                tiles[getTripleCoords(nCell.x) - 1][getTripleCoords(nCell.y)] = 2;
                                tiles[getTripleCoords(nCell.x) - 1][getTripleCoords(nCell.y)+1] = 2;
                                tiles[getTripleCoords(nCell.x) - 1][getTripleCoords(nCell.y)+2] = 2;
                            }

                            visitedCells[nCell.x][nCell.y] = 1;
                            System.out.println("New Cell (" + nCell.x + "/" + nCell.y +")");
                            nextFound = true;
                            break;
                        }
                    }

                }else if(directions.get(i).getValue() == 1) { // DOWN
                    if(cell.y+1 <= visitedCells.length-1) {
                        if(visitedCells[cell.x][cell.y+1] == 0) {
                            // draw path in tiles
                            // when moving up we need to set tiles left and right
                            // last position is reserved for outer wall

                            tiles[getTripleCoords(cell.x)][getTripleCoords(cell.y+1)] = 0;
                            tiles[getTripleCoords(cell.x)][getTripleCoords(cell.y+1)-1] = 0;
                            tiles[getTripleCoords(cell.x)][getTripleCoords(cell.y+1)-2] = 0;
                            nCell = new Cell(cell.x, cell.y+1);
                            cells.add(nCell);

                            if(getTripleCoords(nCell.x) + 1 < tiles.length - 1) {
                                tiles[getTripleCoords(nCell.x) + 1][getTripleCoords(nCell.y)] = 2;
                                tiles[getTripleCoords(nCell.x) + 1][getTripleCoords(nCell.y)-1] = 2;
                                tiles[getTripleCoords(nCell.x) + 1][getTripleCoords(nCell.y)-2] = 2;
                            }else if(getTripleCoords(nCell.x) - 1 > 0) {
                                tiles[getTripleCoords(nCell.x) - 1][getTripleCoords(nCell.y)] = 2;
                                tiles[getTripleCoords(nCell.x) - 1][getTripleCoords(nCell.y)-1] = 2;
                                tiles[getTripleCoords(nCell.x) - 1][getTripleCoords(nCell.y)-2] = 2;
                            }

                            visitedCells[nCell.x][nCell.y] = 1;
                            System.out.println("New Cell (" + nCell.x + "/" + nCell.y +")");
                            nextFound = true;
                            break;
                        }
                    }

                }else if(directions.get(i).getValue() == 2) { // LEFT
                    if(cell.x-1 >= 0) {
                        if(visitedCells[cell.x-1][cell.y] == 0) {
                            // draw path in tiles
                            // when moving up we need to set tiles left and right
                            // last position is reserved for outer wall

                            tiles[getTripleCoords(cell.x-1)][getTripleCoords(cell.y)] = 0;
                            tiles[getTripleCoords(cell.x-1)+1][getTripleCoords(cell.y)] = 0;
                            tiles[getTripleCoords(cell.x-1)+2][getTripleCoords(cell.y)] = 0;

                            nCell = new Cell(cell.x-1, cell.y);
                            cells.add(nCell);

                            if(getTripleCoords(nCell.y) + 1 < tiles.length - 1) {
                                tiles[getTripleCoords(nCell.x)][getTripleCoords(nCell.y)+1] = 2;
                                tiles[getTripleCoords(nCell.x)+1][getTripleCoords(nCell.y)+1] = 2;
                                tiles[getTripleCoords(nCell.x)+2][getTripleCoords(nCell.y)+1] = 2;
                            }else if(getTripleCoords(nCell.y) - 1 > 0) {
                                tiles[getTripleCoords(nCell.x)][getTripleCoords(nCell.y)-1] = 2;
                                tiles[getTripleCoords(nCell.x)+1][getTripleCoords(nCell.y)-1] = 2;
                                tiles[getTripleCoords(nCell.x)+2][getTripleCoords(nCell.y)-1] = 2;
                            }

                            visitedCells[nCell.x][nCell.y] = 1;
                            System.out.println("New Cell (" + nCell.x + "/" + nCell.y +")");
                            nextFound = true;
                            break;
                        }
                    }

                }else if(directions.get(i).getValue() == 3) { // RIGHT
                    if(cell.x+1 <= visitedCells.length-1) {
                        if(visitedCells[cell.x+1][cell.y] == 0) {
                            // draw path in tiles
                            // when moving up we need to set tiles left and right
                            // last position is reserved for outer wall

                            tiles[getTripleCoords(cell.x+1)][getTripleCoords(cell.y)] = 0;
                            tiles[getTripleCoords(cell.x+1)-1][getTripleCoords(cell.y)] = 0;
                            tiles[getTripleCoords(cell.x+1)-2][getTripleCoords(cell.y)] = 0;

                            nCell = new Cell(cell.x+1, cell.y);
                            cells.add(nCell);
                            visitedCells[nCell.x][nCell.y] = 1;
                            if(getTripleCoords(nCell.y) + 1 < tiles.length - 1) {
                                tiles[getTripleCoords(nCell.x)][getTripleCoords(nCell.y)+1] = 2;
                                tiles[getTripleCoords(nCell.x)-1][getTripleCoords(nCell.y)+1] = 2;
                                tiles[getTripleCoords(nCell.x)-2][getTripleCoords(nCell.y)+1] = 2;
                            }else if(getTripleCoords(nCell.y) - 1 > 0) {
                                tiles[getTripleCoords(nCell.x)][getTripleCoords(nCell.y)-1] = 2;
                                tiles[getTripleCoords(nCell.x)-1][getTripleCoords(nCell.y)-1] = 2;
                                tiles[getTripleCoords(nCell.x)-2][getTripleCoords(nCell.y)-1] = 2;
                            }
                            System.out.println("New Cell (" + nCell.x + "/" + nCell.y +")");
                            nextFound = true;
                            break;
                        }
                    }

                }
            }
            if(!nextFound) {
                // there was no available cell found
                cells.remove(cell);
            }



        }

        // END

        World rWorld = new World(handler,tiles, 300, 300);
        EntityManager world1EnManager = new EntityManager(handler, player);

        rWorld.setEntityManager(world1EnManager);
        printGrid(tiles);
        worlds.add(rWorld);
        applyMods();


        rWorld.start();
        return rWorld;

    }

    /**
     * Because of index starting at 0 we need an extra function to get the real 3 times position of coords
     *
     * Example:
     * We want to edit one array at the 12th position and another one on the 36th.
     * To access these positions, we would need for the first array an index of 11, for the second an index of 35
     * which means we can't just triple the first index to get our second one
     * Hence, we triple the first index and add 2 (3*11 = 33 + 2 = 35)
     * @param i
     * @return
     */
    private int getTripleCoords(int i)
    {
        return (i*3)+2;
    }

    private void printGrid(int[][] tiles)
    {
        for(int i = 0; i < tiles.length; i++)
        {
            for(int j = 0; j < tiles[i].length; j++)
            {
                System.out.printf("%5d ", tiles[i][j]);
            }
            System.out.println();
        }
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
