package main.java.com.demo.entity;

import main.java.com.demo.Commons;
import main.java.com.demo.gfx.Color;
import main.java.com.demo.gfx.Font;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/** Represents an alien as a sprite.
 *  Keeps the image of the sprite and the coordinates of the sprite.
    @author zetcode.com */
public class Alien extends Sprite {
    
    private int counter;
    private int curDx;
    private boolean activated, crushed;
    private boolean isShot;
    private int deathTime;  // Counts ticks after death
    
    public Alien(int x, int y, Level level) {                
        super(level);
        initAlien(x, y);        
    }    
    
    private void initAlien(int x, int y) {
        
        xS = 4;
        yS = 2;
        width = height = ES;
        wS = width / PPS;
        hS = height / PPS;
        
        setX(x);
        setY(y);    
        ground = y+height;
        
        xSpeed = 1;
        
        dx = -xSpeed;
        dy = ySpeed;
        
        counter = 0;
        curDx = dx;
        
        activated = crushed = isShot =false;
        deathTime = 0;
        score = 100;
        scoreStr = "";
        scoreX = 0;
        scoreY = 0;
        
        unit = (int) (Math.log10(width)/Math.log10(2)); // the size of block to be used (4 for 16 px sprite and 3 for 8px sprite)
        aTile = Math.min(Math.pow(2, 4 - unit), 1); // 1 for unit 3, 1 for unit 4, 0.5 for unit 5 (big Pusheen)
    }    
        
    // Positions the alien in horizontal direction.
    @Override
    public void tick() {
        super.tick();     
                
        if (deathTime == 20) {    // Make invisible after 20 ticks.
            die();
            deathTime++;
        } else if ((crushed || isShot) && !removed)   // increase tick when attacked but not removed
            deathTime++;
        else {  // unaffected and moves            
            /* Update x position. */
            // Moves in x direction every other tick. This is to slow down alien.
            if (counter % 2 == 0) 
                dx = curDx;
            else  {
                dx = 0;
            }
            counter++;      
    
            /* Update y position. */
            int oldY = y;
            boolean stopped = !move(dx, dy);     // Updates x and y.
                        
            if (stopped && dx != 0) {   // Has met a wall
                dx = - dx;
                curDx = dx;                    
            }
            
            // Update visibility on the screen.
            int offset = level.getOffset();
            if (x <= 0)
                hurt(health);        
            else if (offset < x+width && x < offset + Commons.BOARD_WIDTH)
                setVisible(true);    
            else
                setVisible(false);        

            if (y > Commons.BOARD_HEIGHT)
                remove();            
            
            // When falling, add acceleration to y.
            int effDy = y - oldY;
            if (effDy != 0) // actual y displacement
                dy++;            
            else
                dy = ySpeed; // By default, there is gravity.
            
            // Adjust dy when facing a ground tile.
            if (dy > 0  && y + height < Commons.GROUND && willBeGrounded()) {
                int yt1 = y + dy + ES;
                int backoff = yt1 - (yt1 >> 4) * 16;
                if (backoff > 1)
                    dy -= backoff;
            }                  
        }
        
        // Update score location        
        if (scoreStr.isEmpty()){
            scoreX = x;
            scoreY = y;
            yFin = y;
        } else {    // has died and printing score on the screen during deathTime.
//            scoreX = scoreX + 0.5;
            scoreY = scoreY - 0.5;
            if (scoreY < yFin - ES)
                remove();
        }     
    }  
    
    /** What happens when the player touches an entity.
     * @param sprite The sprite that this sprite is touched by. */    
    protected void touchedBy(Sprite sprite) {
        if (sprite instanceof Player) { // if the entity touches the player
            boolean isOverTop = sprite.y + sprite.height <= y;
            boolean willCrossTop = sprite.y + sprite.height + sprite.dy >= y;
            boolean isXInRange = sprite.x + sprite.width > x && x + width > sprite.x;
            if (isOverTop && willCrossTop && isXInRange && !crushed) { // player jumps over the enemy
                xS += 2;
                crushed = true;
                dx = 0;
                ((Player)sprite).score += score; // gives the player 100 points of score
                scoreStr = Integer.toString(score);                
                ((Player) sprite).setCrushedAlien(true);
            } else if (!crushed && !isShot) {
                if (((Player) sprite).isImortal()) {                    
                    setShot();
                    dx = 0;
                    ((Player)sprite).score += score; // gives the player 1000 points of score
                    scoreStr = Integer.toString(score);
                }
                else
                    sprite.hurt(1); // hurts the player, damage is based on lvl.
            }
        }
//        if (sprite instanceof Shot) { // if the shot touches this alien
////            hurt(health); // hurts this alien.
//            sprite.hurt(sprite.health); // hurt the shot
//            if (!shot) {                
//                shot = true;
//                dx = 0;
//                level.player.score += Commons.SPE; // gives the player 1000 points of score
//                score = Integer.toString(Commons.SPE);
//            }
//        }
        if (sprite instanceof Alien) {
            if (!((Alien) sprite).isCrushed()) {
                if (dx != 0) {
                    dx = -dx;
                    curDx = dx; 
                    sprite.dx = -sprite.dx;
                    ((Alien) sprite).curDx = sprite.dx;
                }          
            }
        }
    }

    @Override
    public void render(Screen screen) {        
        // Becomes 1 every other square (8 pixels)
        int flip1 = (walkDist >> 2) & 1; // This will either be a 1 or a 0 depending on the walk distance (Used for walking effect by mirroring the sprite)
                       
        int sw = screen.getSheet().width;   // width of sprite sheet (256)
        int colNum = sw / PPS;    // Number of squares in a row (32)    
//        screen.render(x, y, xS + yS * colNum, flip1); // draws the top-left tile
        
        if (isVisible()) {
            if (crushed) {
                screen.render(x, y + PPS, xS + yS * colNum, 0); // render the top-left part of the sprite         
                screen.render(x + PPS, y + PPS, (xS + 1) + yS * colNum, 0);  // render the top-right part of the sprite
            } else if (isShot) {
                flip1 = 0;
                screen.render(x + PPS * flip1, y + PPS, xS + yS * colNum, 2); // render the top-left part of the sprite         
                screen.render(x - PPS * flip1 + PPS, y + PPS, (xS + 1) + yS * colNum, 2);  // render the top-right part of the sprite
                screen.render(x + PPS * flip1, y, xS + (yS + 1) * colNum, 2); // render the bottom-left part of the sprite
                screen.render(x - PPS * flip1 + PPS, y, xS + 1 + (yS + 1) * colNum, 2); // render the bottom-right part of the sprite        
            } else {
                screen.render(x + PPS * flip1, y, xS + yS * colNum, flip1); // render the top-left part of the sprite         
                screen.render(x - PPS * flip1 + PPS, y, (xS + 1) + yS * colNum, flip1);  // render the top-right part of the sprite
                screen.render(x + PPS * flip1, y + PPS, xS + (yS + 1) * colNum, flip1); // render the bottom-left part of the sprite
                screen.render(x - PPS * flip1 + PPS, y + PPS, xS + 1 + (yS + 1) * colNum, flip1); // render the bottom-right part of the sprite        
            }
        }

        // Render score location once died
        if (!scoreStr.isEmpty()){
            Font.draw(scoreStr, screen, (int)scoreX, (int)scoreY, Color.WHITE);
//            System.out.println("scoreY: " + (int)scoreY);
        }        
    }    
    
    public void activate() {
        activated = true;
    }
    
    public boolean isActivated() {
        return activated;
    }
    
    public boolean blocks(Sprite e) {
//        if (crushed) return true;
//        else 
            return false;
    }   
    
    public boolean isCrushed() {
        return crushed;
    }
    
    public boolean isShot() {
        return isShot;
    }
    
    public void setShot() {
        isShot = true;
    }
}