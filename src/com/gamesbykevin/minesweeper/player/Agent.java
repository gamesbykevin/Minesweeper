package com.gamesbykevin.minesweeper.player;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.util.Timer;


import com.gamesbykevin.minesweeper.board.tile.Tile;
import com.gamesbykevin.minesweeper.board.tile.Tile.State;

import java.awt.Point;
import java.util.List;
import java.util.Random;

public final class Agent extends Player
{
    //this will be the next location the mouse will go
    private final Point destination;
    
    //this timer will determine how long it takes to get to move from pixel to pixel
    private final Timer timer;
    
    //the tile the agent is going after
    private final Cell selection;
    
    //what will the agent do
    private State action;
    
    /**
     * Create computer opponent with specified dimensions and time delay between pixels
     * @param width
     * @param height
     * @param delay Time delay it will take to move from pixel to pixel
     */
    public Agent(final int width, final int height, final long delay)
    {
        super(width, height);
        
        //start at origin
        this.destination = new Point(0, 0);
        
        //timer that determines movement
        this.timer = new Timer(delay);
        
        //our tile the agent will interact with
        this.selection = new Cell();
    }
    
    private State getAction()
    {
        return this.action;
    }
    
    private void setAction(final State action)
    {
        this.action = action;
    }
    
    private Timer getMovementTimer()
    {
        return this.timer;
    }
    
    private Point getDestination()
    {
        return this.destination;
    }
    
    private Cell getSelection()
    {
        return this.selection;
    }
    
    /**
     * Update the computer logic to solve the puzzle
     * @param time Time to deduct per update
     * @param random Object used to make random decisions
     */
    public void update(final long time, final Random random) throws Exception
    {
        //if we won or lost don't continue any further
        if (hasGameOver())
            return;
        
        //update timer
        super.update(time);
        
        //is the mouse at the destination
        if (getMouseLocation().equals(getDestination()))
        {
            //if no action is set this is our first selection so choose random location
            if (getAction() == null)
            {
                //get list of possible choices
                final List<Tile> choices = getBoard().getAvailableTiles();
                
                //get random available
                final Tile tile = choices.get(random.nextInt(choices.size()));
                
                //set the selection and destination
                setNextStep(tile);
                
                //action will be to select location
                setAction(State.Blank);
                
                //move has been chosen so no need to continue
                return;
            }
            
            if (getAction() == State.Flag)
            {
                getBoard().updateRightReleased(getMouseLocation());
            }
            else
            {
                //any other action will count as selection a tile
                getBoard().updateReleased(getMouseLocation());
                
                //if we won or lost don't continue any further
                if (hasGameOver())
                    return;
            }

            //remove action
            setAction(null);

            //get list of completed tiles
            List<Tile> choices = getBoard().getCompletedTiles();

            //now check and see if any can be flagged
            for (Tile tile : choices)
            {
                //if tile is empty we don't need to check neighbors
                if (tile.getState() == State.Blank)
                    continue;

                //get the list of tiles that are available
                List<Tile> tiles = getBoard().getAvailableTiles(tile);
                    
                if (hasMatch(tiles.size(), tile))
                {
                    //check every available tile to see if one has not been flagged
                    for (Tile tmp : tiles)
                    {
                        //this tile is not flagged so we have our next move
                        if (!tmp.isFlagged())
                        {
                            //set the selection and destination
                            setNextStep(tmp);

                            //flag the location
                            setAction(State.Flag);

                            //exit now because we don't need to continue yet
                            return;
                        }
                    }
                }
            }

            //if we make it to this point, check to see if any tiles are safe to select
            for (Tile tile : choices)
            {
                //if tile is empty we don't need to check neighbors
                if (tile.getState() == State.Blank)
                    continue;

                //get the list of tiles that are available
                List<Tile> tiles = getBoard().getAvailableTiles(tile);

                //make sure we don't have a match so we can see if we can locate tiles that are safe
                if (!hasMatch(tiles.size(), tile))
                {
                    //count the flagged tiles
                    int count = 0;

                    //count the number of flagged tiles
                    for (Tile tmp : tiles)
                    {
                        //this tile is not flagged so we have our next move
                        if (tmp.isFlagged())
                            count++;
                    }

                    //get the total number of mines that neighbor this tile
                    final int mines = tile.getNumberCount();

                    //if the number of flagged equals the mine count
                    if (count == mines)
                    {
                        //check to see if there are any available non-flagged tiles and if so they will be our next selection
                        for (Tile tmp : tiles)
                        {
                            //if this tile is not flagged we have our next move
                            if (!tmp.isFlagged())
                            {
                                //set the selection and destination
                                setNextStep(tmp);

                                //the action will be to select the tile
                                setAction(State.Blank);

                                //exit now because we don't need to continue yet
                                return;
                            }
                        }
                    }
                }
            }
        }
        else
        {
            getMovementTimer().update(time);

            if (getMovementTimer().hasTimePassed())
            {
                if (getMouseLocation().x < getDestination().x)
                    getMouseLocation().x++;
                
                if (getMouseLocation().x > getDestination().x)
                    getMouseLocation().x--;
                
                if (getMouseLocation().y < getDestination().y)
                    getMouseLocation().y++;
                
                if (getMouseLocation().y > getDestination().y)
                    getMouseLocation().y--;
                
                getMovementTimer().reset();
            }
        }
    }
    
    /**
     * Check if the count matches the State of the tile
     * @param tileCount
     * @param tile
     * @return 
     */
    private boolean hasMatch(final int count, final Tile tile)
    {
        //check if the number on the tile matches the count of available
        final boolean match1 = (count == 1 && tile.getState() == State.One);
        final boolean match2 = (count == 2 && tile.getState() == State.Two);
        final boolean match3 = (count == 3 && tile.getState() == State.Three);
        final boolean match4 = (count == 4 && tile.getState() == State.Four);
        final boolean match5 = (count == 5 && tile.getState() == State.Five);
        final boolean match6 = (count == 6 && tile.getState() == State.Six);
        final boolean match7 = (count == 7 && tile.getState() == State.Seven);
        final boolean match8 = (count == 8 && tile.getState() == State.Eight);
        
        return (match1 || match2 || match3 || match4 || match5 || match6 || match7 || match8);
    }
    
    /**
     * Set the next selection and x,y destination
     * @param tile The tile where we want to go to.
     */
    private void setNextStep(final Tile tile)
    {
        //set random location
        getSelection().setCol(tile.getCol());
        getSelection().setRow(tile.getRow());

        //figure out x,y destination now that we know our selection
        final int x = getBoard().getTile(getSelection()).getPoint().x;
        final int y = getBoard().getTile(getSelection()).getCenter().y;
        getDestination().setLocation(x, y);
    }
}