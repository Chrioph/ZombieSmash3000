package dev.codenmore.tilegame.worlds;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.entity.Entity;
import dev.codenmore.tilegame.entity.EntityManager;
import dev.codenmore.tilegame.entity.creatures.Enemies.Ogre;
import dev.codenmore.tilegame.entity.creatures.Enemies.Zombie;
import dev.codenmore.tilegame.entity.creatures.Player;
import dev.codenmore.tilegame.entity.statics.Rock;
import dev.codenmore.tilegame.entity.statics.Tree;
import dev.codenmore.tilegame.tiles.Tile;
import dev.codenmore.tilegame.worlds.util.Room;

/**
 * Generates random dungeon levels: rectangular rooms connected by corridors.
 *
 * The rooms are placed randomly without overlapping, then connected by a minimum spanning
 * tree of corridors plus a few extra corridors so the level has loops. The player starts in
 * one room and the finish is placed in the room that is farthest away from it. Every room
 * except the start room gets enemies, bigger rooms also get rocks and trees.
 *
 * Generation only depends on the seed and the level number, the same input always gives
 * the same level.
 */
public class DungeonGenerator {

    private static final int GRASS = Tile.grassTile.getId();
    private static final int DIRT = Tile.dirtTile.getId();
    private static final int STONE = Tile.stoneTile.getId();
    private static final int FINISH = Tile.finishTile.getId();

    private static final int MIN_ROOM_SIZE = 5, MAX_ROOM_SIZE = 11;
    /** Free tiles kept between two rooms. */
    private static final int ROOM_GAP = 2;
    /**
     * Obstacles are only placed this many tiles away from a room's edge, so the ring along
     * the walls stays free and every corridor entrance can reach every other one.
     */
    private static final int DECOR_MARGIN = 2;

    private final Handler handler;
    private final Player player;
    private final long seed;
    private final int level;
    private final Random rand;

    private int width, height;
    private int[][] tiles;
    private final List<Room> rooms = new ArrayList<Room>();
    private Room spawnRoom, finishRoom;
    private Point finishTile;

    /**
     * @param level 1 for the first generated level, higher levels are bigger and harder
     */
    public DungeonGenerator(Handler handler, Player player, long seed, int level) {
        this.handler = handler;
        this.player = player;
        this.seed = seed;
        this.level = Math.max(1, level);
        this.rand = new Random(seed * 31 + this.level);
    }

    /**
     * Generates the level and returns it as a ready to use world with entities.
     *
     * @param id world id, has to be unique among all worlds
     */
    public World generate(int id) {
        generateTiles();
        Point spawn = tileCenter(spawnRoom.x + spawnRoom.width / 2, spawnRoom.y + spawnRoom.height / 2);
        World world = new World(id, handler, tiles, spawn.x, spawn.y);
        EntityManager entities = new EntityManager(handler, player);
        populate(entities);
        world.setEntityManager(entities);
        return world;
    }

    /** Only generates the tile layout, used by {@link #generate(int)} and for testing. */
    public int[][] generateTiles() {
        width = Math.min(40 + level * 4, 72);
        height = Math.min(30 + level * 3, 54);
        tiles = new int[width][height];
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                tiles[x][y] = STONE;

        placeRooms();
        for (Room room : rooms)
            carveRoom(room);
        connectRooms();
        placeFinish();
        return tiles;
    }

    // ------------------------------------------------------------------ layout

    private void placeRooms() {
        int maxRooms = Math.min(7 + level, 16);
        // the start room is small and quiet
        spawnRoom = new Room(1 + rand.nextInt(width - 7), 1 + rand.nextInt(height - 7), 5, 5);
        rooms.add(spawnRoom);
        for (int attempt = 0; attempt < 500 && rooms.size() < maxRooms; attempt++) {
            int w = MIN_ROOM_SIZE + rand.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE + 1);
            int h = MIN_ROOM_SIZE + rand.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE + 1);
            // the outermost ring of the map always stays a wall
            int x = 1 + rand.nextInt(width - w - 1);
            int y = 1 + rand.nextInt(height - h - 1);
            Room room = new Room(x, y, w, h);
            boolean free = true;
            for (Room other : rooms) {
                if (room.overlaps(other, ROOM_GAP)) {
                    free = false;
                    break;
                }
            }
            if (free)
                rooms.add(room);
        }
    }

    private void carveRoom(Room room) {
        int floor = room != spawnRoom && rand.nextInt(4) == 0 ? DIRT : GRASS;
        for (int x = room.x; x < room.x + room.width; x++)
            for (int y = room.y; y < room.y + room.height; y++)
                tiles[x][y] = floor;
    }

    /** Minimum spanning tree over the room centres (Prim), plus a few extra corridors for loops. */
    private void connectRooms() {
        int n = rooms.size();
        boolean[] connected = new boolean[n];
        connected[0] = true;
        List<int[]> unused = new ArrayList<int[]>();
        for (int added = 1; added < n; added++) {
            int bestA = -1, bestB = -1, bestDist = Integer.MAX_VALUE;
            for (int a = 0; a < n; a++) {
                if (!connected[a])
                    continue;
                for (int b = 0; b < n; b++) {
                    if (connected[b])
                        continue;
                    int d = distance(rooms.get(a), rooms.get(b));
                    if (d < bestDist) {
                        bestDist = d;
                        bestA = a;
                        bestB = b;
                    }
                }
            }
            connected[bestB] = true;
            carveCorridor(rooms.get(bestA), rooms.get(bestB));
        }
        for (int a = 0; a < n; a++)
            for (int b = a + 1; b < n; b++)
                unused.add(new int[]{a, b});
        int loops = Math.min(1 + level / 2, 4);
        for (int i = 0; i < loops && !unused.isEmpty(); i++) {
            int[] pair = unused.remove(rand.nextInt(unused.size()));
            carveCorridor(rooms.get(pair[0]), rooms.get(pair[1]));
        }
    }

    private int distance(Room a, Room b) {
        return Math.abs(a.centerX() - b.centerX()) + Math.abs(a.centerY() - b.centerY());
    }

    /** L-shaped corridor between two room centres, only replaces walls. */
    private void carveCorridor(Room a, Room b) {
        int x1 = a.centerX(), y1 = a.centerY();
        int x2 = b.centerX(), y2 = b.centerY();
        if (rand.nextBoolean()) {
            carveLine(x1, y1, x2, y1);
            carveLine(x2, y1, x2, y2);
        } else {
            carveLine(x1, y1, x1, y2);
            carveLine(x1, y2, x2, y2);
        }
    }

    private void carveLine(int x1, int y1, int x2, int y2) {
        int dx = Integer.signum(x2 - x1), dy = Integer.signum(y2 - y1);
        int x = x1, y = y1;
        while (true) {
            if (tiles[x][y] == STONE)
                tiles[x][y] = DIRT;
            if (x == x2 && y == y2)
                break;
            x += dx;
            y += dy;
        }
    }

    /** The finish goes into the room that takes the longest walk from the start. */
    private void placeFinish() {
        int[][] dist = walkDistances(tiles, spawnRoom.centerX(), spawnRoom.centerY());
        int best = -1;
        for (Room room : rooms) {
            if (room == spawnRoom)
                continue;
            int d = dist[room.centerX()][room.centerY()];
            if (d > best) {
                best = d;
                finishRoom = room;
            }
        }
        if (finishRoom == null)
            finishRoom = spawnRoom;
        finishTile = new Point(finishRoom.centerX(), finishRoom.centerY());
        tiles[finishTile.x][finishTile.y] = FINISH;
    }

    /**
     * Breadth first search over walkable tiles.
     *
     * @return steps from the start to every tile, -1 for unreachable tiles
     */
    public static int[][] walkDistances(int[][] tiles, int startX, int startY) {
        int w = tiles.length, h = tiles[0].length;
        int[][] dist = new int[w][h];
        for (int[] row : dist)
            java.util.Arrays.fill(row, -1);
        ArrayDeque<int[]> queue = new ArrayDeque<int[]>();
        dist[startX][startY] = 0;
        queue.add(new int[]{startX, startY});
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!queue.isEmpty()) {
            int[] c = queue.poll();
            for (int[] d : dirs) {
                int nx = c[0] + d[0], ny = c[1] + d[1];
                if (nx < 0 || ny < 0 || nx >= w || ny >= h || dist[nx][ny] >= 0)
                    continue;
                if (Tile.tiles[tiles[nx][ny]].isSolid())
                    continue;
                dist[nx][ny] = dist[c[0]][c[1]] + 1;
                queue.add(new int[]{nx, ny});
            }
        }
        return dist;
    }

    // ------------------------------------------------------------------ entities

    private void populate(EntityManager entities) {
        boolean[][] used = new boolean[width][height];
        used[finishTile.x][finishTile.y] = true;

        for (Room room : rooms) {
            if (room == spawnRoom)
                continue;
            int area = room.width * room.height;

            // obstacles first, only in the inner part of bigger rooms
            if (room.width >= 7 && room.height >= 7) {
                int obstacles = 1 + rand.nextInt(Math.max(1, area / 30));
                for (int i = 0; i < obstacles; i++) {
                    Point p = randomTile(room, DECOR_MARGIN, used);
                    if (p == null)
                        continue;
                    if (rand.nextInt(3) == 0 && !used[p.x][p.y + 1]
                            && p.y + 1 <= room.y + room.height - 1 - DECOR_MARGIN) {
                        // trees are two tiles high and only block their lower tile
                        used[p.x][p.y + 1] = true;
                        entities.addEntity(new Tree(handler, p.x * Tile.TILEWIDTH, p.y * Tile.TILEHEIGHT));
                    } else {
                        entities.addEntity(new Rock(handler, p.x * Tile.TILEWIDTH, p.y * Tile.TILEHEIGHT));
                    }
                }
            }

            // enemies, more in bigger rooms and on higher levels
            int enemies = Math.max(1, Math.min(area / 30 + level / 3, 6));
            for (int i = 0; i < enemies; i++) {
                Point p = randomTile(room, 0, used);
                if (p == null)
                    break;
                boolean ogre = level >= 3 && rand.nextInt(100) < Math.min(10 + level * 4, 40);
                Entity enemy = ogre ? new Ogre(handler, 0, 0) : new Zombie(handler, 0, 0);
                centerOnTile(enemy, p.x, p.y);
                entities.addEntity(enemy);
            }
        }
    }

    /**
     * Picks a random free tile inside the room, at least {@code margin} tiles away from its
     * edge, and marks it as used.
     */
    private Point randomTile(Room room, int margin, boolean[][] used) {
        int innerW = room.width - 2 * margin, innerH = room.height - 2 * margin;
        if (innerW <= 0 || innerH <= 0)
            return null;
        for (int attempt = 0; attempt < 30; attempt++) {
            int x = room.x + margin + rand.nextInt(innerW);
            int y = room.y + margin + rand.nextInt(innerH);
            if (!used[x][y]) {
                used[x][y] = true;
                return new Point(x, y);
            }
        }
        return null;
    }

    /**
     * Moves the entity so its collision box is centred on the tile. Enemies have differently
     * sized and positioned collision boxes (an ogre's is much bigger than a zombie's).
     */
    private void centerOnTile(Entity e, int tx, int ty) {
        // collision bounds are in screen space, with the entity at (0, 0) they give the box offset
        Rectangle b = e.getCollisionBounds(-e.getX(), -e.getY());
        b.translate((int) handler.getGameCamera().getxOffset(), (int) handler.getGameCamera().getyOffset());
        e.setX(tx * Tile.TILEWIDTH + Tile.TILEWIDTH / 2f - (b.x + b.width / 2f));
        e.setY(ty * Tile.TILEHEIGHT + Tile.TILEHEIGHT / 2f - (b.y + b.height / 2f));
    }

    /** Pixel position that puts a 64x64 creature into the middle of the tile. */
    private static Point tileCenter(int tx, int ty) {
        return new Point(tx * Tile.TILEWIDTH + 32, ty * Tile.TILEHEIGHT + 32);
    }

    public long getSeed() {
        return seed;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public Room getSpawnRoom() {
        return spawnRoom;
    }

    public Point getFinishTile() {
        return finishTile;
    }
}
