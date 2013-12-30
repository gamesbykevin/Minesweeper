package com.gamesbykevin.minesweeper.manager;

import com.gamesbykevin.framework.menu.Menu;

import com.gamesbykevin.minesweeper.engine.Engine;
import com.gamesbykevin.minesweeper.menu.CustomMenu.*;
import com.gamesbykevin.minesweeper.menu.option.*;
import com.gamesbykevin.minesweeper.resources.*;
import com.gamesbykevin.minesweeper.resources.GameImage.Keys;
import com.gamesbykevin.minesweeper.shared.IElement;

import com.gamesbykevin.minesweeper.player.*;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.Random;

/**
 * The parent class that contains all of the game elements
 * @author GOD
 */
public final class Manager implements IElement
{
    //seed used to generate random numbers
    private final long seed = System.nanoTime();
    
    //random number generator object
    private Random random = new Random(seed);
    //private Random random = new Random(30434406877967L);
    
    //our player object
    private Human human;
    
    //our artificial intelligence agent
    private Agent agent;
    
    //store the game type we are playing
    private Mode.Types mode;
    
    //the size of the orginal screen
    private Rectangle screen;
    
    //the background image
    private Image background;
    
    //has the game ended yet
    private boolean gameover = false;
    
    //the number of wins needed when playing race mode
    private static final int WIN_LIMIT = 100;
    
    private int TEMPORARY_LOSS_COUNT = 0;
    
    //status message that notifys the user how to access the menu
    private static final String DEFAULT_MENU_STATUS_MESSAGE = "Hit \"Esc\" for menu";
    
    private static final String HIT_MINE_DEFAULT_MESSAGE = "Hit mine. Board reset";
    
    /**
     * Constructor for Manager, this is the point where we load any menu option configurations
     * @param engine
     * @throws Exception 
     */
    public Manager(final Engine engine) throws Exception
    {
        //store the size of the screen
        screen = new Rectangle(engine.getMain().getScreen());
        
        //get the background image
        background = engine.getResources().getGameImage(Keys.Background);
        
        //get the menu object
        final Menu menu = engine.getMenu();
        
        //get the index 
        final int modeIndex = menu.getOptionSelectionIndex(LayerKey.Options, OptionKey.Mode);

        //get the game mode selected
        this.mode = Mode.Types.values()[modeIndex];
        
        //get the difficulty selection of the board
        final int boardDifficultyIndex = menu.getOptionSelectionIndex(LayerKey.Options, OptionKey.BoardDifficulty);
        
        //default board size
        int columns = 9;
        int rows = 9;
        int mines = 10;
        
        //determine board specs
        switch(BoardDifficulty.Selections.values()[boardDifficultyIndex])
        {
            case Beginner:
                columns = 9;
                rows = 9;
                mines = 10;
                break;
                
            case Intermediate:
                columns = 16;
                rows = 16;
                mines = 40;
                break;
                
            case Expert:
                columns = 22;
                rows = 22;
                mines = 99;
                break;
        }
        
        //dimensions of our puzzle
        int width, height;
        
        if (columns < 16 || rows < 16)
        {
            //size of the player window including board and misc area
            width = (16 * 17);
            height = (16 * 16) + 50;
        }
        else
        {
            //size of the player window including board and misc area
            width = (columns * 17);
            height = (rows * 16) + 50;
        }
        
        //make sure minimum dimensions are set
        if (width < 200)
            width = 200;
        if (height < 200)
            height = 200;
        
        //start y coordinate for player(s)
        final int startY = 5;
        
        //check the mode
        switch(mode)
        {
            case Versus:
            case Race:
                
                //get the difficulty selection
                final int opponentDifficultyIndex = menu.getOptionSelectionIndex(LayerKey.Options, OptionKey.OpponentDifficulty);
                
                agent = new Agent(width, height, OpponentDifficulty.Selections.values()[opponentDifficultyIndex]);
                agent.setLocation(screen.x + (screen.width / 2) + (screen.width / 4) - (width / 2), startY);
                agent.createBoard(columns, rows, mines, random);
                agent.setImage(engine.getResources().getGameImage(Keys.Original));
                
                break;
        }
        
        //create new player
        human = new Human(width, height);
        
        //set the location where the player image will be drawn
        if (agent != null)
        {
            //human will be on the left side
            human.setLocation(screen.x + (screen.width / 4) - (width / 2), startY);
        }
        else
        {
            //place human in the middle of the screen
            human.setLocation(screen.x + (screen.width / 2) - (width / 2), startY);
            
            //set the timer if timed mode
            if (mode == Mode.Types.Timed)
                human.setTimer(BoardDifficulty.Selections.values()[boardDifficultyIndex].getDelay());
        }
        
        //set the size of the board
        human.createBoard(columns, rows, mines, random);
        
        //set the sprite sheet image
        human.setImage(engine.getResources().getGameImage(Keys.Original));
        
        //play new game sound effect
        engine.getResources().playGameAudio(GameAudio.Keys.NewGame);
        
        System.out.println("Seed - " + seed);
    }
    
    /**
     * Has the game ended? Used to prevent continuous updates
     * @return True if game is over regardless of win/lose
     */
    private boolean hasGameOver()
    {
        return this.gameover;
    }
    
    private void flagGameOver()
    {
        this.gameover = true;
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
        random = null;
        
        if (human != null)
            human.dispose();
        
        human = null;
        
        if (agent != null)
            agent.dispose();
        
        agent = null;
        
        mode = null;
        
        screen = null;
        
        if (background != null)
            background.flush();
        
        background = null;
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
        if (hasGameOver())
            return;
        
        if (human != null)
            human.update(engine);
        
        if (agent != null)
            agent.update(engine);
        
        //check if the game has ended depending on the game mode
        checkMode(engine.getResources());
    }
    
    /**
     * Check if the human/computer win/lose depending on the game mode.<br>
     * We will also determine the display status message
     */
    private void checkMode(final Resources resources)
    {
        //if there is a computer agent preset we are either playing versus or race
        if (agent != null)
        {
            switch (mode)
            {
                case Versus:
                    
                    //if the human has a game over check for win/lose
                    if (human.hasGameOver())
                    {
                        if (human.hasWin())
                        {
                            //play sound effect
                            resources.playGameAudio(GameAudio.Keys.Win);
                            
                            //add status message
                            human.addStatusMessage("You win");
                            human.addStatusMessage(DEFAULT_MENU_STATUS_MESSAGE);
                            
                            //mark flag so correct icon is displayed
                            agent.markLose();

                            //mark game over
                            flagGameOver();
                        }

                        if (human.hasLose())
                        {
                            //play sound effect
                            resources.playGameAudio(GameAudio.Keys.Lose);
                            
                            //add status message
                            agent.addStatusMessage("CPU wins");
                            human.addStatusMessage(DEFAULT_MENU_STATUS_MESSAGE);
                            
                            //mark flag so correct icon is displayed
                            agent.markWin();

                            //mark game over
                            flagGameOver();
                        }
                    }
                    else
                    {
                        //if the agent has a game over check for win/lose
                        if (agent.hasGameOver())
                        {
                            if (agent.hasWin())
                            {
                                //play sound effect
                                resources.playGameAudio(GameAudio.Keys.Lose);
                                
                                //add status message
                                agent.addStatusMessage("CPU wins");
                                human.addStatusMessage(DEFAULT_MENU_STATUS_MESSAGE);
                                
                                //mark flag so correct icon is displayed
                                human.markLose();

                                //mark game over
                                flagGameOver();
                            }

                            if (agent.hasLose())
                            {
                                //play sound effect
                                resources.playGameAudio(GameAudio.Keys.Win);
                                
                                //add status message
                                agent.addStatusMessage("Hit Mine");
                                human.addStatusMessage("You win");
                                human.addStatusMessage(DEFAULT_MENU_STATUS_MESSAGE);
                                
                                //mark flag so correct icon is displayed
                                human.markWin();

                                //mark game over
                                flagGameOver();
                            }
                        }
                    }
                    break;

                case Race:

                    //check if the human has won/lost
                    if (human.hasGameOver())
                    {
                        if (human.hasWin())
                        {
                            //play sound effect
                            resources.playGameAudio(GameAudio.Keys.Win);
                            
                            //increase the win count and create a new board
                            human.increaseWins();
                            
                            //add status message
                            human.addStatusMessage("Solved puzzle. Wins: " + human.getWins());
                            
                            //if the human has reached the number of wins then the agent has lost the game
                            if (human.getWins() >= WIN_LIMIT)
                            {
                                //add status message
                                agent.addStatusMessage("You won. Game Over");
                                human.addStatusMessage("Winner");
                                human.addStatusMessage(DEFAULT_MENU_STATUS_MESSAGE);
                                
                                //mark flag so correct icon is displayed
                                agent.markLose();

                                //mark game over
                                flagGameOver();
                            }
                            else
                            {
                                //else generate a new board
                                human.reset(random);
                            }
                        }
                        else
                        {
                            //add status message
                            human.addStatusMessage(HIT_MINE_DEFAULT_MESSAGE);
                            
                            //reset board
                            human.reset(random);
                        }
                    }
                    else
                    {
                        //check if the agent has won/lost
                        if (agent.hasGameOver())
                        {
                            if (agent.hasWin())
                            {
                                //increase the win count and create a new board
                                agent.increaseWins();
                                
                                //add status message
                                agent.addStatusMessage("Solved. Wins: " + agent.getWins());
                                
                                System.out.println("Win: " + agent.getWins() + ", Loss: " + this.TEMPORARY_LOSS_COUNT);
                                
                                //if the agent has reached the number of wins then the human has lost the game
                                if (agent.getWins() >= WIN_LIMIT)
                                {
                                    //add status message
                                    agent.addStatusMessage("CPU won. Game Over");
                                    human.addStatusMessage("Loser");
                                    human.addStatusMessage(DEFAULT_MENU_STATUS_MESSAGE);
                                    
                                    //mark flag so correct icon is displayed
                                    human.markLose();

                                    //mark game over
                                    flagGameOver();
                                    
                                    //play sound effect
                                    resources.playGameAudio(GameAudio.Keys.Lose);
                                }
                                else
                                {
                                    //play sound effect
                                    resources.playGameAudio(GameAudio.Keys.Win);
                                
                                    //generate a new board
                                    agent.reset(random);
                                }
                            }
                            else
                            {
                                TEMPORARY_LOSS_COUNT++;
                                
                                System.out.println("Win: " + agent.getWins() + ", Loss: " + this.TEMPORARY_LOSS_COUNT);
                                
                                //add status message
                                agent.addStatusMessage(HIT_MINE_DEFAULT_MESSAGE);

                                //reset board
                                agent.reset(random);
                                
                                
                            }
                        }
                    }
                    break;
            }
        }
        else
        {
            //if there is no opponent we are playing single player human
            if (human.hasGameOver())
            {
                //mark game over
                flagGameOver();
                
                if (human.hasWin())
                {
                    //play sound effect
                    resources.playGameAudio(GameAudio.Keys.Win);
                    
                    //add status message
                    human.addStatusMessage("Winner");
                    human.addStatusMessage(DEFAULT_MENU_STATUS_MESSAGE);
                }
                else
                {
                    //play sound effect
                    resources.playGameAudio(GameAudio.Keys.Lose);
                    
                    //if the time is counting down and time has passed
                    if (human.getTimer().getReset() != 0 && human.getTimer().hasTimePassed())
                    {
                        //add status message
                        human.addStatusMessage("You lose, times up");
                    }
                    else
                    {
                        //add status message
                        human.addStatusMessage("You lose");
                    }
                    
                    //add status message
                    human.addStatusMessage(DEFAULT_MENU_STATUS_MESSAGE);
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
        //draw background if exists
        if (background != null)
        {
            graphics.drawImage(background, screen.x, screen.y, screen.width, screen.height, null);
        }
        
        //draw opponent if exists
        if (agent != null)
        {
            //draws object board/info and status screen
            agent.render(graphics);
        }
        
        //draw human if exists
        if (human != null)
        {
            //draws object board/info and status screen
            human.render(graphics);
        }
    }
}