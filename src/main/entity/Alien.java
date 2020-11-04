package main.entity;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.SuperPusheen;
import main.Commons;
import main.gfx.Screen;
import main.level.Level;

/** Represents an alien as a sprite.
 *  Keeps the image of the sprite and the coordinates of the sprite.
    @author zetcode.com */
public class Alien extends Sprite {
    
    private final int ES = Commons.ENTITY_SIZE;
    private int counter;
    private int curDx;
    private int inity;
    private boolean activated, crushed;
    private int deathTime;
    
    public Alien(int x, int y, Level level) {                
        super(level);
        initAlien(x, y);
        xS = 10;
        yS = 2;
        inity = y;
    }    
    
    private void initAlien(int x, int y) {
        
        setX(x);
        setY(y);    
        ground = y+height;
        
        xSpeed = 1;
        ySpeed = 1;
        
        dx = -xSpeed;
        dy = ySpeed;
        
        counter = 0;
        curDx = dx;
        
        activated = crushed = false;
        deathTime = 0;
        
        var alienImg = "src/LittleGoomba.png";                   
       
        try {
            BufferedImage source = ImageIO.read(new File(alienImg));
//                BufferedImage source = ImageIO.read(Board.class.getResourceAsStream("/LittleGoomba.png"));
            setImage(source);
        } catch (IOException ex) {
            String msg = String.format("No such file found: %s", ex.getMessage());
            System.out.println(msg);
        }
    }    
        
    // Positions the alien in horizontal direction.
    @Override
    public void tick() {
        super.tick();     
                
        if (deathTime >= 100)
            die();
        if (crushed && !removed)
            deathTime++;
        else {
            if (counter % 2 == 0) 
                dx = curDx;
            else  {
                dx = 0;
            }
            counter++;

            int oldX = x;
            int oldY = y;

    //        boolean stopped = false;
    //        if (x <= level.getOffset()) { // left of the screen
    //            setVisible(false);
    //        } else {          
    //            stopped = !move(dx, dy);            
    //        }

            boolean stopped = !move(dx, dy);        
            int offset = level.getOffset();
            if (x <= 0)
                hurt(health);        
            else if (offset < x+width && x < offset + Commons.BOARD_WIDTH )
                setVisible(true);    
            else
                setVisible(false);        

            if (y >= Commons.BOARD_HEIGHT - height - ES) {  //bottom of the game            
    //            y = Commons.BOARD_HEIGHT - height - ES - 1;
                hurt(health);
            }
            // When falling, don't move in the x direction.
            int effDx = x - oldX;
            int effDy = y - oldY;
    //        if (effDy != 0 && effDx != 0) {
            if (effDy != 0) {
    //            x = oldX;
                dy++;
            }
            else
                dy = ySpeed;        

            if (dy > 0  && y < Commons.GROUND && willBeGrounded()) {
                int yt1 = y + dy + ES;
                int backoff = yt1 - (yt1 >> 4) * 16;
                if (backoff > 1)
                    dy -= backoff;
            }

    //        if (inity == Commons.Y160 - ES)
    //            System.out.println("y = " + y + ", dy = " + dy);

            if (stopped && dx != 0) {
                dx = - dx;
                curDx = dx;                    
            }
        }
    }  
    
    /** What happens when the player touches an entity.
     * @param sprite The sprite that this sprite is touched by. */    
    protected void touchedBy(Sprite sprite) {
        if (sprite instanceof Player) { // if the entity touches the player
            if (sprite.y + sprite.height <= y) {
                xS += 2;
                crushed = true;
                dx = 0;
            } else if (!crushed)
                sprite.hurt(1); // hurts the player, damage is based on lvl.
        }
        if (sprite instanceof Shot) { // if the shot touches this alien
            hurt(health); // hurts this alien.
            sprite.hurt(sprite.health); // hurt the shot
        }
        if (sprite instanceof Alien) {
            if (dx != 0) {
                dx = -dx;
                curDx = dx; 
                sprite.dx = -sprite.dx;
                ((Alien) sprite).curDx = sprite.dx;
            }            
        }
    }

    @Override
    public void render(Screen screen) {        
        // Becomes 1 every other square (8 pixels)
        int flip1 = (walkDist >> 2) & 1; // This will either be a 1 or a 0 depending on the walk distance (Used for walking effect by mirroring the sprite)
                       
        int sw = screen.getSheet().width;   // width of sprite sheet (256)
        int colNum = sw / Commons.PPS;    // Number of squares in a row (32)    
//        screen.render(x, y, xS + yS * colNum, flip1); // draws the top-left tile
        int PPS = Commons.PPS;
        
        if (crushed) {
            screen.render(x, y + PPS, xS + yS * colNum, 0); // render the top-left part of the sprite         
            screen.render(x + PPS, y + PPS, (xS + 1) + yS * colNum, 0);  // render the top-right part of the sprite
        } else {
            screen.render(x + PPS * flip1, y, xS + yS * colNum, flip1); // render the top-left part of the sprite         
            screen.render(x - PPS * flip1 + PPS, y, (xS + 1) + yS * colNum, flip1);  // render the top-right part of the sprite
            screen.render(x + PPS * flip1, y + PPS, xS + (yS + 1) * colNum, flip1); // render the bottom-left part of the sprite
            screen.render(x - PPS * flip1 + PPS, y + PPS, xS + 1 + (yS + 1) * colNum, flip1); // render the bottom-right part of the sprite        
        }
            
    }
    
    /** What happens when the alien dies */
    @Override
    public void die() {
        super.die(); // calls the die() method in Mob.java
        if (level.player != null && y <= Commons.GROUND && x > 0) { // if the player is not null
            level.player.score += 10; // gives the player 1000 points of score
//            level.player.gameWon(); // player wins the game
        }
    }
    
    public void activate() {
        activated = true;
    }
    
    public boolean isActivated() {
        return activated;
    }
    
    public boolean blocks(Sprite e) {
        if (crushed) return true;
        else return false;
    }   
}