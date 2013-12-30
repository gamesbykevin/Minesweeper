package com.gamesbykevin.minesweeper.board;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.minesweeper.board.tile.*;

import com.gamesbykevin.minesweeper.resources.Resources;
import com.gamesbykevin.minesweeper.resources.GameAudio.Keys;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Board extends Sprite implements Disposable
{
    //list of tiles on the board
    private List<Tile> tiles;
    
    //has the board been solved
    private boolean solved = false;
    
    //has a mine been selected
    private boolean lost = false;
    
    //store the total number of mines
    private final int mines;
    
    //we keep track of board size
    private final int columns, rows;
    
    //tmp list(s)
    private List<Cell> tmpCells;
    //private List<Tile> tmpTiles;
    
    /**
     * Create a new board of the specified dimensions and total number of mines
     * @param columns
     * @param rows
     * @param mines
     * @param random 
     */
    public Board(final int columns, final int rows, int mines)
    {
        //create new list of tiles
        this.tiles = new ArrayList<>();
        
        //create temporary list(s)
        tmpCells = new ArrayList<>();
        
        //track board size
        this.columns = columns;
        this.rows = rows;
        
        //if the number of mines is equal to or exceeds the number of tiles, correct the issue
        if (mines >= columns * rows)
            mines = (columns * rows) - 1;
        
        //store the final number
        this.mines = mines;
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        for (Tile tile : getTiles())
        {
            if (tile != null)
                tile.dispose();
            
            tile = null;
        }
        
        tiles.clear();
        tiles = null;
        
        tmpCells.clear();
        tmpCells = null;
    }
    
    /**
     * Create the board with the set dimensions/mines
     * @param random Object used for random decisions
     */
    public void reset(final Random random)
    {
        //reset win/lose flags
        this.solved = false;
        this.lost = false;
        
        //clear list of tiles
        this.tiles.clear();
        
        for (int row = 0; row < rows; row++)
        {
            for (int column = 0; column < columns; column++)
            {
                //create new tile
                Tile tile = new Original();
                
                //set location of tile
                tile.setCol(column);
                tile.setRow(row);
                
                //add tile to list
                tiles.add(tile);
            }
        }
        
        //place the mines on the board
        placeMines(random);
    }
    
    /**
     * Place the mines at random locations on the board
     * @param random Object used to make random decisions
     */
    private void placeMines(final Random random)
    {
        placeMines(random, null);
    }
    
    /**
     * Place the mines at random locations on the board
     * @param random Object used to make random decisions
     * @param ignore List of tiles we won't allow to be mines
     */
    private void placeMines(final Random random, List<Tile> tiles)
    {
        //here we will count how many mines we have added
        int count = 0;
        
        //reset list
        tmpCells.clear();
        
        //add all possible locations to list
        for (int row = 0; row < rows; row++)
        {
            for (int column = 0; column < columns; column++)
            {
                if (getTile(column, row).isMine())
                {
                    //keep track of how many mines may already exist
                    count++;
                }
                else
                {
                    //add location to the list
                    tmpCells.add(new Cell(column, row));
                }
            }
        }
        
        //remove any locations from our list if they are to be ignored
        if (tiles != null && !tiles.isEmpty())
        {
            for (Tile tile : tiles)
            {
                for (int index = 0; index < tmpCells.size(); index++)
                {
                    Cell cell = tmpCells.get(index);
                    
                    if (tile.equals(cell))
                    {
                        //remove cell from our list
                        tmpCells.remove(index);
                        
                        //start checking next element
                        break;
                    }
                }
            }
        }
        
        //continue until we have all our mines created
        while (count < mines)
        {
            //get a random index in our location list
            final int index = random.nextInt(tmpCells.size());
            
            //set the random tile to be a mine
            getTile(tmpCells.get(index)).setMine(true);
            
            //remove the location from the list
            tmpCells.remove(index);
            
            //increase our count so we know when we have added enough mines
            count++;
        }
    }
    
    public int getRowCount()
    {
        return this.rows;
    }
    
    public int getColumnCount()
    {
        return this.columns;
    }
    
    /**
     * Set the x, y coordinates of all the tiles.<br>
     * Note all tiles are expected to have the same width height.
     * @param startX starting x coordinate
     * @param startY starting y coordinate
     */
    public void setLocations(final int startX, final int startY)
    {
        for (Tile tile : getTiles())
        {
            tile.setX(startX + (tile.getCol() * tile.getWidth()));
            tile.setY(startY + (tile.getRow() * tile.getHeight()));
        }
    }
    
    /**
     * Get the tile at the specified location
     * @param cell
     * @return Tile, if not found null is returned
     */
    public Tile getTile(final Cell cell)
    {
        return getTile(cell.getCol(), cell.getRow());
    }
    
    /**
     * Get the tile at the specified location
     * @param column
     * @param row
     * @return Tile, if not found null is returned
     */
    public Tile getTile(final double column, final double row)
    {
        for (Tile tile : getTiles())
        {
            if ((int)tile.getCol() == column && (int)tile.getRow() == row)
                return tile;
        }
        
        return null;
    }
    
    /**
     * Determine if this is a brand new board with no tiles selected/flagged.
     * @return True if untouched, false otherwise
     */
    public boolean hasNewBoard()
    {
        //if the amount of available tiles equals the total size of tiles then the board is untouched
        return (getAvailableTiles().size() == getTiles().size());
    }
    
    /**
     * Get the list of all the tiles in the board
     * @return List of tiles
     */
    private List<Tile> getTiles()
    {
        return this.tiles;
    }
    
    /**
     * Get a List of tiles that have not been completed yet and are not flagged
     * @return List of tiles that have not been completed yet and are not flagged
     */
    public List<Tile> getAvailableTiles()
    {
        //possible random choices
        List<Tile> tmpTiles = new ArrayList<>();
        
        for (Tile tile : getTiles())
        {
            //if tile is not completed and it is not flagged then we have a valid choice
            if (!tile.isCompleted() && !tile.isFlagged())
                tmpTiles.add(tile);
        }
        
        //return our finished list
        return tmpTiles;
    }
    
    /**
     * Get list of tiles that are marked as completed.
     * @return List of completed tiles.
     */
    public List<Tile> getCompletedTiles()
    {
        //possible random choices
        List<Tile> tmpTiles = new ArrayList<>();
        
        for (Tile tile : getTiles())
        {
            //if this tile is completed and there are available neighbors this tile is good
            if (tile.isCompleted())
                tmpTiles.add(tile);
        }
        
        //return our finished list
        return tmpTiles;
    }
    
    /**
     * Get list of completed neighbor tiles
     * @param tile The tile that we want to check the neighbors
     * @return List of tiles that neighbor the parameter tile that are completed
     */
    public List<Tile> getCompletedTiles(final Tile tile)
    {
        //reset list
        List<Tile> tmpTiles = new ArrayList<>();
        
        for (Tile tmp : getAdjacentTiles(tile))
        {
            if (tmp.isCompleted())
                tmpTiles.add(tmp);
        }
        
        //return our result
        return tmpTiles;
    }
    
    /**
     * Get list of available neighbor tiles that are not flagged
     * @param tile The tile that we want to check the neighbors
     * @return List of tiles that are not marked as completed and are not flagged
     */
    public List<Tile> getAvailableNonFlaggedTiles(final Tile tile)
    {
        //reset list
        List<Tile> tmpTiles = new ArrayList<>();
        
        for (Tile tmp : getAdjacentTiles(tile))
        {
            if (!tmp.isCompleted() && !tmp.isFlagged())
                tmpTiles.add(tmp);
        }
        
        //return our result
        return tmpTiles;
    }
    
    /**
     * Get list of available neighbor tiles even if they are flagged
     * @param tile The tile that we want to check the neighbors
     * @return List of tiles that are not marked as completed
     */
    public List<Tile> getAvailableTiles(final Tile tile)
    {
        //reset list
        List<Tile> tmpTiles = new ArrayList<>();
        
        for (Tile tmp : getAdjacentTiles(tile))
        {
            if (!tmp.isCompleted())
                tmpTiles.add(tmp);
        }
        
        //return our result
        return tmpTiles;
    }
    
    /**
     * Get the list of neighboring tiles.<br>
     * Doesn't matter if the tile is complete or flagged etc...
     * @param tile
     * @return 
     */
    private List<Tile> getAdjacentTiles(final Tile tile)
    {
        //reset list
        List<Tile> tmpTiles = new ArrayList<>();
        
        for (int col = -1; col <= 1; col++)
        {
            for (int row = -1; row <= 1; row++)
            {
                if (col == 0 && row == 0)
                    continue;
                
                //get neighbor tile
                final Tile tmp = getTile(tile.getCol() + col, tile.getRow() + row);
                
                //if the tile exists add it
                if (tmp != null)
                    tmpTiles.add(tmp);
            }
        }
        
        return tmpTiles;
    }
    
    /**
     * Counts the number of mines around the current tile
     * @param tile
     * @return The total number of mines around the current tile
     */
    public int getAdjacentMineCount(final Tile tile)
    {
        int count = 0;
        
        for (Tile tmp : getAdjacentTiles(tile))
        {
            if (tmp.isMine())
                count++;
        }
        
        return count;
    }
    
    /**
     * Determine what happens when the right mouse button has been released
     * @param point
     * @throws Exception 
     */
    public void updateRightReleased(final Point point, final Resources resources) throws Exception
    {
        for (Tile tile : getTiles())
        {
            //tile can no longer be selected
            if (tile.isCompleted())
                continue;
            
            //are we within the tile
            if (tile.getRectangle().contains(point))
            {
                if (tile.isCompleted())
                {
                    //play sound effect
                    resources.playGameAudio(Keys.UnavailableSelection);
                    
                    //exit method because we can only select 1 tile at a time
                    return;
                }
                
                switch(tile.getState())
                {
                    case Blank:
                    case BlankPress:
                        tile.setState(Tile.State.Flag);
                        
                        //play sound effect
                        resources.playGameAudio(Keys.FlagTile);
                        break;
                        
                    case Flag:
                        tile.setState(Tile.State.Question);
                        break;
                        
                    case QuestionPress:
                    case Question:
                        tile.setState(Tile.State.Blank);
                        break;
                        
                    default:
                        throw new Exception("Unknown state found.");
                }
            }
        }
    }
    
    public void updateReleased(final Point point, final Resources resources, final Random random) throws Exception
    {
        for (Tile tile : getTiles())
        {
            if (tile.isCompleted() || tile.isFlagged())
                continue;
            
            //are we within the tile
            if (tile.getRectangle().contains(point))
            {
                //tile can no longer be selected or is flagged
                if (tile.isCompleted() || tile.isFlagged())
                {
                    //play sound effect
                    resources.playGameAudio(Keys.UnavailableSelection);
                    
                    //exit method because we can select only one tile
                    return;
                }
                
                //if the board is new we don't want our first selection to be a mine
                if (hasNewBoard())
                {
                    //we don't want the first selection to be a mine
                    tile.setMine(false);

                    //list of safe tiles where we do not want to place a mine
                    List<Tile> ignore = getAdjacentTiles(tile);
                    
                    //we will also make the first selection open up an area to get started
                    for (Tile tmp : ignore)
                    {
                        tmp.setMine(false);
                    }
                    
                    //also add our current tile to the ignore list so it will not be a mine
                    ignore.add(tile);
                    
                    /**
                     * Some of the neighbors we set to not be a mine may 
                     * have been so we need to find new locations for those mines.
                     */
                    placeMines(random, ignore);
                }
                
                //select our tile
                selectTile(tile);
                
                //if the player hit a mine the player loses
                if (tile.isMine())
                    break;
                
                if (tile.getNumberCount() > 0)
                {
                    //play sound effect
                    resources.playGameAudio(Keys.SelectTile);
                }
                else
                {
                    //play sound effect
                    resources.playGameAudio(Keys.Opening);
                    
                    //list of tiles to check
                    List<Tile> check = new ArrayList<>();

                    //add tile to list
                    check.add(tile);

                    //continue until we have selected all open tiles
                    while (!check.isEmpty())
                    {
                        //get a list of the tiles neighbors from the first tile in our list
                        final List<Tile> neighbors = getAdjacentTiles(check.get(0));

                        for (Tile tmp : neighbors)
                        {
                            //make sure the tile has not already been completed
                            if (!tmp.isCompleted())
                            {
                                //select the tile
                                selectTile(tmp);
                                
                                //if this tile also has no mines we will need to check its neighbors
                                if (tmp.getNumberCount() == 0)
                                    check.add(tmp);
                            }
                        }

                        //now that step is completed remove first tile from list
                        check.remove(0);
                    }
                }
                
                //after our selection check if the board has been solved
                checkSolved();
            }
        }
    }
    
    /**
     * Mark the tile as selected
     * @param tile 
     */
    private void selectTile(final Tile tile) throws Exception
    {
        if (tile.isMine())
        {
            //game is over, board has hit mine
            setLose();

            //mark all tiles as completed
            setCompleted();

            //markt the tile we selected so we know
            tile.setState(Tile.State.MineSelection);
        }
        else
        {
            //get adjacent count
            final int count = getAdjacentMineCount(tile);

            //mark as completed so we can no longer select again
            tile.setCompleted(count);
        }
    }
    
    /**
     * Has this board been solved
     * @return true if so, false otherwise
     */
    public boolean hasSolved()
    {
        return this.solved;
    }
    
    /**
     * Has this board hit a mine
     * @return true if so, false otherwise
     */
    public boolean hasLost()
    {
        return this.lost;
    }
    
    /**
     * Mark this board as win
     */
    public void setWin()
    {
        this.solved = true;
    }
    
    /**
     * Mark this board as lost
     */
    public void setLose()
    {
        this.lost = true;
    }
    
    /**
     * Check if we have solved the board
     */
    private void checkSolved()
    {
        //check every tile for completion that is not a mine
        for (Tile tile : getTiles())
        {
            //if tile is a mine
            if (tile.isMine())
                continue;
            
            //if the tile is not complete exit check
            if (!tile.isCompleted())
                return;
        }
        
        //mark all tiles as completed
        setCompleted();
        
        //if we have reached this point the board has been solved
        setWin();
    }
    
    /**
     * Mark all tiles on the board as complete and reveal mines
     */
    private void setCompleted()
    {
        //mark all tiles as completed now that the game is over
        for (Tile tmp : getTiles())
        {
            tmp.setCompleted();

            if (tmp.isMine())
            {
                if (tmp.isFlagged())
                {
                    tmp.setState(Tile.State.MineFlag);
                }
                else
                {
                    tmp.setState(Tile.State.MineReveal);
                }
            }
        }
    }
    
    /**
     * Determine how many mines still exist.<br>
     * This is done by taking the total mine count from the number of tiles that have been flagged.
     * @return Remaining mine count
     */
    public int getRemainingMineCount()
    {
        return (getMineCount() - getFlagCount());
    }
    
    /**
     * Get the total number of mines
     * @return Mine count
     */
    public int getMineCount()
    {
        return this.mines;
    }
    
    /**
     * Count the number of tiles that are flagged.
     * @return The total number of tiles that are flagged
     */
    public int getFlagCount()
    {
        int count = 0;
        
        for (Tile tile : getTiles())
        {
            if (tile.isFlagged())
                count++;
        }
        
        return count;
    }
    
    public void updatePressed(final Point point)
    {
        for (Tile tile : getTiles())
        {
            //tile can no longer be selected or is flagged
            if (tile.isCompleted() || tile.isFlagged())
                continue;
            
            if (tile.getRectangle().contains(point))
            {
                switch(tile.getState())
                {
                    case QuestionPress:
                    case Question:
                        tile.setState(Tile.State.QuestionPress);
                        break;
                        
                    default:
                        tile.setState(Tile.State.BlankPress);
                        break;
                }
            }
            else
            {
                switch(tile.getState())
                {
                    case QuestionPress:
                    case Question:
                        tile.setState(Tile.State.Question);
                        break;
                        
                    default:
                        tile.setState(Tile.State.Blank);
                        break;
                }
            }
        }
    }
    
    public void updateDragged(final Point point)
    {
        for (Tile tile : getTiles())
        {
            //tile can no longer be selected or is flagged
            if (tile.isCompleted() || tile.isFlagged())
                continue;
            
            if (tile.getRectangle().contains(point))
            {
                switch(tile.getState())
                {
                    case QuestionPress:
                    case Question:
                        tile.setState(Tile.State.QuestionPress);
                        break;
                        
                    default:
                        tile.setState(Tile.State.BlankPress);
                        break;
                }
            }
            else
            {
                switch(tile.getState())
                {
                    case QuestionPress:
                    case Question:
                        tile.setState(Tile.State.Question);
                        break;
                        
                    default:
                        tile.setState(Tile.State.Blank);
                        break;
                }
            }
        }
    }
    
    /**
     * Draw all of the tiles
     * @param graphics
     * @param image 
     */
    public void render(final Graphics2D graphics, final Image image)
    {
        for (Tile tile : getTiles())
        {
            tile.render(graphics, image);
        }
    }
}