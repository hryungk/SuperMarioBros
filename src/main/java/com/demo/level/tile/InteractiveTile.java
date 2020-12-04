/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.com.demo.level.tile;

import main.java.com.demo.Commons;
import main.java.com.demo.entity.HiddenSprite;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/**
 *
 * @author HRK
 */
public abstract class InteractiveTile extends Tile {
    
    protected final int ES = Commons.ENTITY_SIZE;    
    protected boolean isHitBottom;    // True when the player hits the bottom of this brick.
    protected int x, initY, y, xt, yt;    
    protected boolean removed;
    protected double ds, dsInit;    
    protected boolean firstTime, hitOnce;
    protected HiddenSprite hs; // a hidden sprite under the tile, if any.
    protected int numHS; // Number of hidden sprites under this tile
    
    public InteractiveTile(int id, int xt, int yt) {
        super(id);
        this.xt = xt;
        this.yt = yt;
        init();
    }
    
    private void init() {
       
        removed = false;        
        isHitBottom = false;
        ds = 0;
        dsInit = Commons.ITV0;
        
        x = xt * ES;
        y = yt * ES;
        initY = y;// permanent y-position 
        
        
        firstTime = hitOnce = false;
    }
   
    @Override
    public void tick(int xt, int yt, Level level) {
//        if (isHitBottom && bNum != 3) { // When first hit by the player
//            bNum = 3;
//            ds = dsInit;
//        }            
//        if (isHitBottom) {
            if ((y == initY && ds == dsInit) || y < initY) {                 
                ds = ds + 0.5;
            }
            else
                ds = 0;        
//        }

        if ((int) (y + ds) <= initY) {
            y = (int) (y + ds);
        }
        else
            y = initY;
    }
    
    @Override
    public void render(Screen screen, Level level, int xt, int yt) {
//        initY = yt * ES; 
//        if (isHitBottom && (int) (y + ds) <= initY) {
//        if ((int) (y + ds) <= initY) {
//            y = (int) (y + ds);
//        }
//        else
//            y = initY;
    }
    
    /** What happens when you hit the tile (ex: punching a tree) */
    public void hurt() {
        
        isHitBottom = true;
        ds = dsInit;     
        if (!hitOnce) {
            firstTime = true;
            hitOnce = true;
        }
    }
    
    public void setHit(boolean hit) {
        this.isHitBottom = hit;
    }
    
    public boolean isHitBottom() {
        return isHitBottom;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }    
    
    public void setHiddenSprite(HiddenSprite hs) {
        this.hs = hs;
//        if (firstTime)
//            numHS = 1;
    }
}