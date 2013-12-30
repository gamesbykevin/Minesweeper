package com.gamesbykevin.minesweeper.player;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.util.Timer;

import com.gamesbykevin.minesweeper.board.tile.Tile;
import com.gamesbykevin.minesweeper.board.tile.Tile.State;
import com.gamesbykevin.minesweeper.engine.Engine;
import com.gamesbykevin.minesweeper.menu.option.OpponentDifficulty.Selections;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Agent extends Player implements IPlayer
{
    //this timer will determine how long it takes to get to move from pixel to pixel
    private Timer timer;
    
    //how many x,y pixels to move at once everytime the cpu is allowed to move
    private static final int MOVE_X_PIXELS = 3;
    private static final int MOVE_Y_PIXELS = 3;
    
    //list of locations/actions for the computer to take
    private Steps steps;
    
    /**
     * Create computer opponent with specified dimensions and time delay between pixels
     * @param width
     * @param height
     * @param difficulty This will determine the time delay in movement
     */
    public Agent(final int width, final int height, final Selections difficulty)
    {
        super(width, height, false);
        
        //timer that determines movement
        this.timer = new Timer(difficulty.getDelay());
        
        //create new list of steps
        this.steps = new Steps();
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        timer = null;
        
        steps.dispose();
        steps = null;
    }
    
    private Timer getMovementTimer()
    {
        return this.timer;
    }
    
    /**
     * Move the mouse x pixel(s) towards the destination
     */
    private void move()
    {
        //if no steps exist we can't move the mouse
        if (!steps.hasSteps())
            return;
        
        //get the x,y distance from our destination
        int xDistance = getMouseLocation().x - steps.getDestination().x;
        int yDistance = getMouseLocation().y - steps.getDestination().y;
        
        //make distance a positive number
        if (xDistance < 0)
            xDistance = -xDistance;
        if (yDistance < 0)
            yDistance = -yDistance;
        
        //if we are closer than the movement speed we made it
        if (xDistance < MOVE_X_PIXELS)
            getMouseLocation().x = steps.getDestination().x;
        if (yDistance < MOVE_Y_PIXELS)
            getMouseLocation().y = steps.getDestination().y;
        
        if (getMouseLocation().x < steps.getDestination().x)
            getMouseLocation().x += MOVE_X_PIXELS;

        if (getMouseLocation().x > steps.getDestination().x)
            getMouseLocation().x -= MOVE_X_PIXELS;

        if (getMouseLocation().y < steps.getDestination().y)
            getMouseLocation().y += MOVE_Y_PIXELS;

        if (getMouseLocation().y > steps.getDestination().y)
            getMouseLocation().y -= MOVE_Y_PIXELS;
    }
    
    /**
     * Update the computer logic to solve the puzzle
     * @param engine Our game engine object containing resources
     * @throws Exception 
     */
    @Override
    public void update(final Engine engine) throws Exception
    {
        //if we won or lost don't continue any further
        if (hasGameOver())
            return;
        
        //update game timer
        super.update(engine.getMain().getTime());
        
        //if we don't have any steps to follow
        if (!steps.hasSteps())
        {
            //select random tile
            locateRandom(engine.getManager().getRandom());
            
            //we have a step now so no need to continue
            return;
        }
        
        //since we have steps we will check if the mouse has reached its destination
        if (!getMouseLocation().equals(steps.getDestination()))
        {
            //update our movement timer
            getMovementTimer().update(engine.getMain().getTime());

            //if time has passed we can move the mouse another pixel
            if (getMovementTimer().hasTimePassed())
            {
                //move the mouse towards the destination
                move();
                
                //reset our timer to not move again until the time passes
                getMovementTimer().reset();
            }
            
            //we can't continue any further at the moment
            return;
        }
        
        //now that we are at our destination it is time to follow action
        if (steps.getAction() == State.Flag)
        {
            //flag the specified tile
            getBoard().updateRightReleased(getMouseLocation(), engine.getResources());
        }
        else
        {
            //currently if we aren't flagging a tile then we will select it
            getBoard().updateReleased(getMouseLocation(), engine.getResources(), engine.getManager().getRandom());
            
            //now that selection was made if the game is now over don't continue
            if (hasGameOver())
                return;
        }
        
        //at this point the step is completed, remove it from our list
        steps.remove();
        
        //if there are still steps to complete don't continue to search for more
        if (steps.hasSteps())
            return;
        
        //lets see if we can locate our next move
        determineNextSteps(engine.getManager().getRandom());
    }
    
    /**
     * Here is the high level logic on how we will determine what the next steps will be
     */
    private void determineNextSteps(final Random random)
    {
        /**
         * Logic to solving the board
         * 1. Check if any completed tiles can be flagged as mines
         * 2. Check for any tiles that are safe to select.
         * 3. If we reach this step, locate which tile has the lowest % of being a mine
         * 4. If all else fails we will have to select a random tile
         */
        
        //get list of completed tiles
        List<Tile> choices = getBoard().getCompletedTiles();
        
        //check if we can flag any tiles
        locateFlagged(choices);
        
        //check if any tiles are safe to select
        locateSafeTiles(choices);
        
        //if we still don't have any steps after the above checks, the following will last resort
        if (!steps.hasSteps())
        {
            //check how many mines remain and through process of elimination locate any more safe locations
            //locateRemainingMines(choices);
            
            //if we still haven't found any steps
            if (!steps.hasSteps())
            {
                //locate an available tile with the lowest probability of selecting a mine
                checkProbability(random);
            }
        }
    }
    
    /**
     * Through the process of elimination we will check all completed tiles.<br>
     * We will check those tiles to determine how many border tiles still exist that could be a mine.<br>
     * We will count all of the mines that border the completed tiles.<br>
     * If the count equals the # of remaining mines on the board that aren't flagged<br>
     * then any tile that does not border a completed tile is safe to choose.<br>
     * @param choices List of completed tiles
     */
    private void locateRemainingMines(final List<Tile> choices)
    {
        //first get the count of remaining mines that are hidden
        final int remainingMines = getBoard().getRemainingMineCount();
        
        //how many existing mines can we verify still exist from the completed tiles
        int remainingCount = 0;
        
        //list containing dangerous tiles
        List<Tile> dangerous = new ArrayList<>();
        
        //now we need to find out how to determine if all the remaining mines border completed tiles
        for (Tile tile : choices)
        {
            if (tile.getNumberCount() == 0)
                continue;
            
            //count how many mines are still hidden
            int existing = getExistingMinesCount(tile);
            
            //if this tile does not have any, skip to the next tile
            if (existing == 0)
                continue;
            
            //if we are at this point then mines still exist so get a list of those possible tiles
            List<Tile> possible = getBoard().getAvailableNonFlaggedTiles(tile);
            
            //if the list is empty continue to the next tile
            if (possible.isEmpty())
                continue;
            
            //does a tile in the possible list match a tile in the dangerous list
            boolean foundMatch = false;
            
            //count how many tiles that we find match
            int countMatch = 0;
            
            //check if any of the tiles exist already in our dangerous list
            for (Tile tmp1 : possible)
            {
                if (!dangerous.isEmpty())
                {
                    final int size = dangerous.size();
                    
                    for (int index = 0; index < size; index++)
                    {
                        Tile tmp2 = dangerous.get(index);
                        
                        if (tmp1.equals(tmp2))
                        {
                            foundMatch = true;
                            countMatch++;
                        }
                        else
                        {
                            //if it doesn't exist add to our list
                            dangerous.add(tmp1);
                        }
                    }
                }
                else
                {
                    dangerous.add(tmp1);
                }
            }
            
            //if no match was found add the existing to our total count
            if (!foundMatch)
            {
                //if none of the possible tiles exist in the dangerous list, lets add the existing mine count to our total count
                remainingCount += existing;
            }
            else
            {
                //if the match count was less than the existing count we can still add the difference
                if (countMatch < existing)
                {
                    remainingCount += (existing - countMatch);
                }
            }
        }
        
        if (remainingCount < remainingMines)
        {
            System.out.println("Count is smalller. remainingCount=" + remainingCount + ", remainingMines=" + remainingMines);
            return;
        }
        
        if (remainingCount > remainingMines)
        {
            System.out.println("Count is larger than remaining which shouldn't be.");
            return;
        }
        
        System.out.println("Count is equal.");
        
        //this is our list of possible selections
        List<Tile> tiles = getBoard().getAvailableTiles();
        
        //at this point we only want the available tiles that do not border a completed tile
        for (int index = 0; index < tiles.size(); index++)
        {
            //get the current Tile
            Tile tile = tiles.get(index);
            
            //if there are completed tiles bordering this tile remove it from the choices list
            if (!getBoard().getCompletedTiles(tile).isEmpty())
            {
                tiles.remove(index);
                index--;
                continue;
            }
        }
        
        //if there are remaining choices then these are safe selections
        if (!tiles.isEmpty())
        {
            //add the remaining choices to our steps
            for (Tile tile : tiles)
            {
                steps.add(tile, State.Blank);
            }
        }
    }
    
    /**
     * Here we will check all of the available tiles.<br>
     * For each of the available tiles we will get a list of neighbors.<br>
     * Then for each neighbor we will calculate that neighbors probability of selecting the available tile.<br>
     * After checking all the neighbors we will calculate the total overall probability<br>
     * The lowest probability will be our selection.
     */
    private void checkProbability(final Random random)
    {
        //get a list of all the available tiles we have
        final List<Tile> choices = getBoard().getAvailableTiles();
        
        float probability = 1.0f;
        
        //there may be more than 1 solution
        List<Integer> solutions = new ArrayList<>();
        
        for (int index = 0; index < choices.size(); index++)
        {
            Tile tile = choices.get(index);
            
            //get all of the completed tiles surrounding our available tile
            List<Tile> tiles = getBoard().getCompletedTiles(tile);
            
            int totalExistingMines = 0;
            int totalExistingTiles = 0;
            
            //calculate the probability for each neighboring tile
            for (Tile tmp : tiles)
            {
                //determine how many mines still exist after deducting the number of flagged
                int existingMines = getExistingMinesCount(tmp);

                //add to our total
                totalExistingMines += existingMines;
                
                //determine the number of existing tiles after deducting the number of flagged
                int existingTiles = getExistingTilesCount(tmp);
                
                //add to our total
                totalExistingTiles += existingTiles;
            }
            
            //after checking all of the neighbors, calculate the overall probability
            float tmpProbability = (float)totalExistingMines / (float)totalExistingTiles;
            
            //if the new probability is lower than the previous we have a better solution
            if (tmpProbability <= probability)
            {
                //if this probability is better remove all other solutions from list
                if (tmpProbability < probability)
                    solutions.clear();
                
                //set the lowest probability
                probability = tmpProbability;
                
                //add solution to list
                solutions.add(index);
            }
        }
        
        //do we have a solution
        if (!solutions.isEmpty())
        {
            //add the location/state to our steps
            steps.add(choices.get(solutions.get(random.nextInt(solutions.size()))), State.Blank);
        }
    }
    
    /**
     * Get the amount of existing mines after taking into account the flagged ones
     * @param tiles List of tiles we need to check
     * @param tile The tile in the middle of the tiles collection
     * @return Count
     */
    private int getExistingMinesCount(final Tile tile)
    {
        return (tile.getNumberCount() - getFlaggedCount(tile));
    }
    
    /**
     * Count how many tiles are available for selection after taking into account the flagged ones
     * @param tiles List of tiles we need to check
     * @return Count
     */
    private int getExistingTilesCount(final Tile tile)
    {
        //get list of neighbor tiles
        List<Tile> tiles = getBoard().getAvailableTiles(tile);
        
        return (tiles.size() - getFlaggedCount(tile));
    }
    
    /**
     * Check if any of the tiles are safe to select
     * @param choices List of tiles to search
     */
    private void locateSafeTiles(final List<Tile> choices)
    {
        //if we make it to this point, check to see if any tiles are safe to select
        for (Tile tile : choices)
        {
            //if tile is empty we don't need to check neighbors
            if (tile.getNumberCount() == 0)
                continue;

            //get the list of tiles that are available, just like previous
            List<Tile> tiles = getBoard().getAvailableTiles(tile);

            //make sure we don't have a match so we can see if we can locate tiles that are safe
            if (!hasMatch(tiles.size(), tile))
            {
                //count the flagged tiles
                int flagged = getFlaggedCount(tile);

                //get the total number of mines that neighbor this tile
                final int mines = tile.getNumberCount();

                //if the # of flagged equals the # of mines any remaining tiles are safe to select
                if (flagged == mines && flagged < tiles.size())
                {
                    //if there are any available tiles they are safe to select so add them to the list
                    for (Tile tmp : tiles)
                    {
                        //if this tile is not flagged we have a valid move
                        if (!tmp.isFlagged())
                        {
                            //add the location/state to our steps
                            steps.add(tmp, State.Blank);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Select a random tile that hasn't been selected yet
     * @param random Object used to make random decisions
     */
    private void locateRandom(final Random random)
    {
        //get list of possible choices
        final List<Tile> choices = getBoard().getAvailableTiles();
        
        //add the location/state to our steps
        steps.add(choices.get(random.nextInt(choices.size())), State.Blank);
    }
    
    /**
     * Count how many tiles in the collection are flagged
     * @param tiles The collection of tiles we are checking
     * @return 
     */
    private int getFlaggedCount(Tile tile)
    {
        int count = 0;
        
        List<Tile> tiles = getBoard().getAvailableTiles(tile);
        
        for (Tile tmp : tiles)
        {
            if (tmp.isFlagged())
                count++;
        }
        
        return count;
    }
    
    /**
     * Check if any of the tiles can be flagged
     * @param choices List of tiles to search
     */
    private void locateFlagged(final List<Tile> choices)
    {
        //now check and see if any can be flagged
        for (Tile tile : choices)
        {
            //if tile is empty we don't need to check neighbors
            if (tile.getNumberCount() == 0)
                continue;

            //get the list of tiles that are available
            List<Tile> tiles = getBoard().getAvailableTiles(tile);

            //if there are no available tiles skip to next iteration
            if (tiles.size() < 1)
                continue;
            
            //do the number of available tiles equal the number on the tile, if so then we know they are all mines and they can be flagged
            if (hasMatch(tiles.size(), tile))
            {
                //check every available tile to see if one has not been flagged
                for (Tile tmp : tiles)
                {
                    //this tile is not flagged so we have a valid move
                    if (!tmp.isFlagged())
                    {
                        //add the location/state to our steps
                        steps.add(tmp, State.Flag);
                    }
                }
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
        //does the count match the number count on the tile
        return (count == tile.getNumberCount());
    }
    
    /**
     * Call parent reset and clear the instruction list
     * @param random Object used to make random decisions
     */
    @Override
    public void reset(final Random random)
    {
        super.reset(random);
        
        steps.reset();
    }
    
    /**
     * This class will keep a list of steps for the computer to follow
     */
    private class Steps
    {
        //which tiles are part of the steps
        private List<Cell> locations;
        
        //what action to take
        private List<State> actions;
        
        //the x,y coordinates where we want to move the mouse to
        private List<Point> destinations;
        
        private Steps()
        {
            locations = new ArrayList<>();
            actions = new ArrayList<>();
            destinations = new ArrayList<>();
        }
        
        public void dispose()
        {
            locations.clear();
            locations = null;
            
            actions.clear();
            actions = null;
            
            destinations.clear();
            destinations = null;
        }
        
        /**
         * Add step to list.<br>
         * This will consist of the tile location (column, row)<br> and the action to be taken.<br>
         * If the location is already in the List it will not be added again
         * @param cell The location (column, row) of the tile.
         * @param action What do we want to do.
         */
        private void add(final Cell cell, final State action)
        {
            //make sure the location isn't already in the list
            for (Cell tmp : locations)
            {
                if (tmp.equals(cell))
                    return;
            }
            
            locations.add(cell);
            actions.add(action);
            
            //figure out x,y destination since we know our tile
            final int x = getBoard().getTile(cell).getPoint().x;
            final int y = getBoard().getTile(cell).getCenter().y;
            
            destinations.add(new Point(x, y));
        }
        
        /**
         * Remove all locations/actions from our list
         */
        public void reset()
        {
            locations.clear();
            actions.clear();
            destinations.clear();
        }
        
        public boolean hasSteps()
        {
            return (!locations.isEmpty());
        }
        
        /**
         * Get the current location
         * @return The tile that we want to select
         */
        public Cell getLocation()
        {
            return locations.get(0);
        }
        
        /**
         * Get the current action
         * @return What action do we want to take? select tile, flag tile etc....
         */
        public State getAction()
        {
            return actions.get(0);
        }
        
        /**
         * Get the x,y coordinate where we want our mouse to navigate to
         * @return Point
         */
        public Point getDestination()
        {
            return destinations.get(0);
        }
        
        /**
         * Remove the step from our list
         */
        public void remove()
        {
            locations.remove(0);
            actions.remove(0);
            destinations.remove(0);
        }
    }
}