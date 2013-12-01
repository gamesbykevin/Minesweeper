package com.gamesbykevin.minesweeper.manager;

import com.gamesbykevin.framework.menu.Menu;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.minesweeper.engine.Engine;
import com.gamesbykevin.minesweeper.menu.CustomMenu.*;
import com.gamesbykevin.minesweeper.menu.option.*;
import com.gamesbykevin.minesweeper.resources.*;
import com.gamesbykevin.minesweeper.resources.GameImage.Keys;
import com.gamesbykevin.minesweeper.shared.IElement;

import java.awt.Graphics;
import java.util.Random;

/**
 * The parent class that contains all of the game elements
 * @author GOD
 */
public final class Manager implements Disposable, IElement
{
    //seed used to generate random numbers
    private final long seed = System.nanoTime();
    
    //random number generator object
    private final Random random = new Random(seed);
    
    /**
     * Constructor for Manager, this is the point where we load any menu option configurations
     * @param engine
     * @throws Exception 
     */
    public Manager(final Engine engine) throws Exception
    {
        //get the menu object
        final Menu menu = engine.getMenu();
        
        final int modeIndex = menu.getOptionSelectionIndex(LayerKey.Options, OptionKey.Mode);
        
        //dict = new Dictionary(engine.getResources().getGameText(GameText.Keys.Words));
        
        //create new instance
        //enemies = new EnemyManager(engine.getResources().getGameImage(Keys.EnemiesSpriteSheet));
        
        //the speed of the characters
        //final int speedIndex = menu.getOptionSelectionIndex(LayerKey.Options, OptionKey.Speed);
        
        //the image containing all the sprite images
        //final Image image = engine.getResources().getGameImage(GameImage.Keys.SpriteSheet);
        
        //the time delay per update
        //final long time = engine.getMain().getTime();
        
        System.out.println(seed);
    }
    
    /**
     * Get our object used to make random decisions
     * @return Random
     */
    public Random getRandom()
    {
        return this.random;
    }
    
    /**
     * Free up resources
     */
    @Override
    public void dispose()
    {
        
    }
    
    /**
     * Update all application elements
     * 
     * @param engine Our main game engine
     * @throws Exception 
     */
    @Override
    public void update(final Engine engine) throws Exception
    {
        
    }
    
    /**
     * Draw all of our application elements
     * @param graphics Graphics object used for drawing
     */
    @Override
    public void render(final Graphics graphics)
    {
        
    }
}