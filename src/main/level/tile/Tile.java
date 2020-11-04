package main.level.tile;

import java.util.Random;
import main.entity.Entity;
import main.entity.Player;
import main.entity.Sprite;
import main.gfx.Screen;
import main.level.Level;

public abstract class Tile {
    public static int tickCount = 0; //A global tickCount used in the Lava & water tiles.
    protected Random random = new Random(); // Random is used for random numbers (duh!).

    public static Tile[] tiles = new Tile[256]; // An array of tiles
    public static Tile sky = new SkyTile(0);
    public static Tile ground = new GroundTile(1); 
    public static Tile pipe = new PipeTile(2); // creates a grass tile with the Id of 0, (I don't need to explain the other simple ones)
    public static Tile block = new BlockTile(3); 
    public static Tile brick = new BrickTile(4); 
    public static Tile Qbrick = new QuestionBrickTile(5); 
    
    public final byte ID; // Id of the tile
    private boolean steppedOn;
    private Sprite steppingSprite;
    protected int xS, yS; // Location on the sprite sheet [square, 8 pixels]
    protected int wS, hS; // width and height of tile [squares]
    
    protected boolean removed;
    
    public Tile(int id) {
            ID = (byte) id; // creates the id
            if (tiles[id] != null) throw new RuntimeException("Duplicate tile ids!"); // You cannot have over-lapping ids
            tiles[id] = this; // Assigns the id
            steppedOn = false;
            removed = false;
            wS = hS = 2;    // 2-square-long in each side. 
    }
    
    /** What happens when you hit an item on a tile (ex: Pickaxe on rock) */
    public boolean interact(int xt, int yt, Player player, int attackDir) {
            return false;
    }
     /** Returns if the player can walk on it, overrides in sub-classes  */
    public boolean mayPass(int x, int y, Entity e) {
        return false;
    }    
    /** What happens when a Sprite steps on the tile. */
    public void steppedOn(Sprite sprite) {
        steppedOn = true;
        steppingSprite = sprite;
    }
    
    /** Render method, used in sub-classes
     * @param screen current screen
     * @param level current level
     * @param x x tile position of the current level
     * @param y x tile position of the current level */
    public abstract void render(Screen screen, Level level, int x, int y);
    
    /** Update method
     * @param level current level
     * @param xt x position of the current level
     * @param yt x position of the current level */
    public abstract void tick(int xt, int yt, Level levelß);

    /** What happens when you hit the tile (ex: punching a tree) */
    public abstract void hurt(int x, int y, Sprite source, int dmg, int attackDir);

    /** What happens when you run into the tile (ex: run into a cactus) */
    public abstract void bumpedInto(int xt, int yt, Entity entity);
}