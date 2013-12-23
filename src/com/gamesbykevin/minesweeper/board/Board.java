package com.gamesbykevin.minesweeper.board;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.base.Sprite;

import com.gamesbykevin.minesweeper.board.tile.*;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Board extends Sprite
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
    
    /**
     * Create a new board of the specified dimensions and total number of mines
     * @param columns
     * @param rows
     * @param mines
     * @param random 
     */
    public Board(final int columns, final int rows, int mines, final Random random, final int startX, final int startY)
    {
        //create new list of tiles
        this.tiles = new ArrayList<>();
        
        //track board size
        this.columns = columns;
        this.rows = rows;
        
        //if the number of mines is equal to or exceeds the number of tiles, correct the issue
        if (mines >= columns * rows)
            mines = (columns * rows) - 1;
        
        //store the final number
        this.mines = mines;
        
        //each spot will be added to this list to determine possible mine locations
        List<Cell> cells = new ArrayList<>();
        
        for (int row = 0; row < rows; row++)
        {
            for (int column = 0; column < columns; column++)
            {
                final Cell cell = new Cell(column, row);
                
                //add location to the list
                cells.add(cell);
                
                //create new tile
                Tile tile = new Original();
                
                //set location of tile
                tile.setCol(cell);
                tile.setRow(cell);
                
                //add tile to list
                tiles.add(tile);
            }
        }
        
        //here we will count how many mines we have added
        int count = 0;
        
        //continue until we have all our mines created
        while (count < mines)
        {
            //get a random index in our location list
            final int index = random.nextInt(cells.size());
            
            //set the random tile to be a mine
            getTile(cells.get(index)).setMine(true);
            
            //remove the location from the list
            cells.remove(index);
            
            //increase our count so we know when we have added enough mines
            count++;
        }
        
        //set locations of tiles
        setLocations(startX, startY);
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
    private void setLocations(final int startX, final int startY)
    {
        for (Tile tile : tiles)
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
        for (Tile tile : tiles)
        {
            if ((int)tile.getCol() == column && (int)tile.getRow() == row)
                return tile;
        }
        
        return null;
    }
    
    /**
     * Get a random tile from the existing that have not been completed yet
     * @param random Object used to make random decision
     * @return Random tile that can be selected
     */
    public List<Tile> getAvailableTiles()
    {
        //possible random choices
        List<Tile> choices = new ArrayList<>();
        
        for (Tile tile : tiles)
        {
            //if tile is not completed and it is not flagged then we have a valid choice
            if (!tile.isCompleted() && !tile.isFlagged())
                choices.add(tile);
        }
        
        //return our finished list
        return choices;
    }
    
    /**
     * Get list of tiles that are marked as completed.
     * @return List of completed tiles.
     */
    public List<Tile> getCompletedTiles()
    {
        //possible random choices
        List<Tile> choices = new ArrayList<>();
        
        for (Tile tile : tiles)
        {
            //if this tile is completed and there are available neighbors this tile is good
            if (tile.isCompleted())
                choices.add(tile);
        }
        
        //return our finished list
        return choices;
    }
    
    /**
     * Get list of available neighbor tiles
     * @param tile The that we want to check the neighbors
     * @return List of tiles that are not marked as completed
     */
    public List<Tile> getAvailableTiles(final Tile tile)
    {
        List<Tile> choices = new ArrayList<>();
        
        for (Tile tmp : getAdjacentTiles(tile))
        {
            if (!tmp.isCompleted())
                choices.add(tmp);
        }
        
        //return our result
        return choices;
    }
    
    /**
     * Get the list of neighboring tiles.<br>Doesn't matter if the tile is complete or flagged etc...
     * @param tile
     * @return 
     */
    private List<Tile> getAdjacentTiles(final Tile tile)
    {
        final List<Tile> neighbors = new ArrayList<>();
        
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
                    neighbors.add(tmp);
            }
        }
        
        return neighbors;
    }
    
    /**
     * Counts the number of mines around the current tile
     * @param tile
     * @return The total number of mines around the current tile
     */
    public int getAdjacentMineCount(final Tile tile)
    {
        int count = 0;
        
        final List<Tile> neighbors = getAdjacentTiles(tile);
        
        for (Tile tmp : neighbors)
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
    public void updateRightReleased(final Point point) throws Exception
    {
        for (Tile tile : tiles)
        {
            //tile can no longer be selected
            if (tile.isCompleted())
                continue;
            
            //are we within the tile
            if (tile.getRectangle().contains(point))
            {
                switch(tile.getState())
                {
                    case Blank:
                    case BlankPress:
                        tile.setState(Tile.State.Flag);
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
    
    public void updateReleased(final Point point) throws Exception
    {
        for (Tile tile : tiles)
        {
            //tile can no longer be selected or is flagged
            if (tile.isCompleted() || tile.isFlagged())
                continue;
            
            //are we within the tile
            if (tile.getRectangle().contains(point))
            {
                //did the player select a mine
                if (tile.isMine())
                {
                    //game is over, board has hit mine
                    setLose();
                    
                    //mark all tiles as completed
                    setCompleted();
                    
                    //markt the tile we selected so we know
                    tile.setState(Tile.State.MineSelection);
                    
                    break;
                }
                else
                {
                    //get adjacent count
                    final int count = getAdjacentMineCount(tile);
                    
                    //mark as completed so we can no longer select again
                    tile.setCompleted(count);
                    
                    //if this is a blank tile we need to open the tiles up around it
                    if (count == 0)
                    {
                        //list of tiles to check
                        List<Tile> check = new ArrayList<>();
                        
                        //add tile to list
                        check.add(tile);
                        
                        while (!check.isEmpty())
                        {
                            //get the first tile to check in our list
                            Tile tmp = check.get(0);
                            
                            final List<Tile> neighbors = getAdjacentTiles(tmp);
                            
                            for (Tile tmp1 : neighbors)
                            {
                                //make sure the tile has not already been completed
                                if (!tmp1.isCompleted())
                                {
                                    //get the total mine count
                                    final int tmpCount = getAdjacentMineCount(tmp1);

                                    //if there are no mines add to our list to check also
                                    if (tmpCount == 0)
                                        check.add(tmp1);

                                    //mark this tile as complete
                                    tmp1.setCompleted(tmpCount);
                                }
                            }
                            
                            //remove tile from list of things to check
                            check.remove(0);
                        }
                    }
                    
                    //now that we have selected our place check if the board has been solved
                    checkSolved();
                }
            }
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
        for (Tile tile : tiles)
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
        for (Tile tmp : tiles)
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
     * Get the total number of mines
     * @return Mine count
     */
    public int getMineCount()
    {
        return this.mines;
    }
    
    public int getFlagCount()
    {
        int count = 0;
        
        for (Tile tile : tiles)
        {
            if (tile.isFlagged())
                count++;
        }
        
        return count;
    }
    
    public void updatePressed(final Point point)
    {
        for (Tile tile : tiles)
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
        for (Tile tile : tiles)
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
        for (Tile tile : tiles)
        {
            tile.render(graphics, image);
        }
    }
}