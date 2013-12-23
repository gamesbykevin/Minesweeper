package com.gamesbykevin.minesweeper.manager;

import com.gamesbykevin.framework.menu.Menu;
import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.minesweeper.engine.Engine;
import com.gamesbykevin.minesweeper.menu.CustomMenu.*;
import com.gamesbykevin.minesweeper.menu.option.*;
import com.gamesbykevin.minesweeper.resources.*;
import com.gamesbykevin.minesweeper.resources.GameImage.Keys;
import com.gamesbykevin.minesweeper.shared.IElement;

import com.gamesbykevin.minesweeper.player.*;

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
    
    //our player object
    private final Human human;
    
    //our artificial intelligence agent
    private final Agent agent;
    
    /**
     * Constructor for Manager, this is the point where we load any menu option configurations
     * @param engine
     * @throws Exception 
     */
    public Manager(final Engine engine) throws Exception
    {
        //get the menu object
        final Menu menu = engine.getMenu();
        
        //final int modeIndex = menu.getOptionSelectionIndex(LayerKey.Options, OptionKey.Mode);

        /*
        9 X 9, 10 mines
        16 X 16, 40 mines
        22 X 22, 99 mines        
        */
        
        final int columns = 9;
        final int rows = 9;
        final int mines = 10;

        //size of the player window including board and misc area
        int width = (columns * 17);
        int height = (rows * 16) + 50;
        
        //make sure minimum dimensions are set
        if (width < 200)
            width = 200;
        if (height < 200)
            height = 200;
        
        //create new player
        human = new Human(width, height);
        
        //set the location where the player image will be drawn
        human.setLocation(0, 0);
        
        //set the size of the board
        human.createBoard(columns, rows, mines, random);
        
        //set the sprite sheet image
        human.setImage(engine.getResources().getGameImage(Keys.Original));
        
        //time to wait between each pixel movement for our opponent(s)
        final long delay = Timers.toNanoSeconds(50L);
        
        agent = new Agent(width, height, delay);
        agent.setLocation(width, 0);
        agent.createBoard(columns, rows, mines, random);
        agent.setImage(engine.getResources().getGameImage(Keys.Original));
        
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
        if (human != null)
        {
            human.update(engine.getMouse(), engine.getMain().getTime());
        }
        
        if (agent != null)
        {
            agent.update(engine.getMain().getTime(), random);
            
            //if the human game ended determine the winner
            if (human.hasGameOver())
            {
                if (human.getBoard().hasSolved())
                    agent.getBoard().setLose();
                if (human.getBoard().hasLost())
                    agent.getBoard().setWin();
            }
            else
            {
                //if the agent game ended determine the winner
                if (agent.hasGameOver())
                {
                    if (agent.getBoard().hasSolved())
                        human.getBoard().setLose();
                    if (agent.getBoard().hasLost())
                        human.getBoard().setWin();
                }
            }
        }
    }
    
    /**
     * Draw all of our application elements
     * @param graphics Graphics object used for drawing
     */
    @Override
    public void render(final Graphics graphics)
    {
        if (human != null)
        {
            human.render(graphics);
        }
        
        if (agent != null)
        {
            agent.render(graphics);
        }
    }
}