package com.gamesbykevin.minesweeper.board.tile;

import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.base.Animation;

import java.awt.Graphics;
import java.awt.Image;

public class Tile extends Sprite
{
    //is the tile a mine
    private boolean mine = false;
    
    //has the user already selected this tile whether it be safe or a mine
    private boolean complete;
    
    /**
     * All of the different states for the tile
     */
    public enum State
    {
        //idle empty tile
        Blank,
        
        //idle empty tile with the mouse pressed
        BlankPress, 
        
        //tile that is flagged
        Flag,
        
        //tile that has question mark over it
        Question,
        
        //tile that has question mark over it with mouse pressed
        QuestionPress,
        
        //after player loses this is the mine that wasn't selected
        MineReveal,
        
        //after player loses this is the mine that was selected
        MineSelection,
        
        //after player loses this is the mine that wasn't selected and was flagged
        MineFlag,
        
        //the count of the number of mines adjacent
        One, 
        Two, 
        Three, 
        Four, 
        Five, 
        Six, 
        Seven, 
        Eight,
    }    
    
    /**
     * Constructor for tile
     */
    protected Tile()
    {
        //call to parent constructor
        super();
        
        //create sprite sheet for our animations
        super.createSpriteSheet();
    }
    
    /**
     * Set the tile if it is a mine
     * @param mine 
     */
    public void setMine(final boolean mine)
    {
        this.mine = mine;
    }
    
    /**
     * Is this tile a mine
     * @return true if this is a mine, false otherwise
     */
    public boolean isMine()
    {
        return this.mine;
    }
    
    /**
     * Mark the tile as visited so it can no longer be selected.
     */
    public void setCompleted()
    {
        this.complete = true;
    }
    
    /**
     * Check the state of the tile to determine the number count of neighboring mines
     * @return total number of neighbors that are mines
     */
    public int getNumberCount()
    {
        switch(getState())
        {
            case One:
                return 1;
                
            case Two:
                return 2;
                
            case Three:
                return 3;
                
            case Four:
                return 4;
                
            case Five:
                return 5;
                
            case Six:
                return 6;
                
            case Seven:
                return 7;
                
            case Eight:
                return 8;
                
            //anything else we return 0
            default:
                return 0;
        }
    }
    
    /**
     * Mark the tile as visited so it can no longer be selected.<br>
     * Also mark the number of surrounding mines
     * @param count The number of mines surrounding the tile
     */
    public void setCompleted(final int count) throws Exception
    {
        //Mark the tile as visited so it can no longer be selected.
        setCompleted();
        
        switch(count)
        {
            case 0:
                setState(Tile.State.BlankPress);
                break;

            case 1:
                setState(Tile.State.One);
                break;

            case 2:
                setState(Tile.State.Two);
                break;

            case 3:
                setState(Tile.State.Three);
                break;

            case 4:
                setState(Tile.State.Four);
                break;

            case 5:
                setState(Tile.State.Five);
                break;

            case 6:
                setState(Tile.State.Six);
                break;

            case 7:
                setState(Tile.State.Seven);
                break;

            case 8:
                setState(Tile.State.Eight);
                break;

            default:
                throw new Exception("Unknown result found.");
        }
    }
    
    /**
     * Has the player already selected this tile
     * @return true if the player selected this tile, false otherwise
     */
    public boolean isCompleted()
    {
        return this.complete;
    }
    
    public boolean isFlagged()
    {
        return (getState() == State.Flag);
    }
    
    public void setSize(final int width, final int height)
    {
        //set the size of the tile
        super.setDimensions(width, height);
    }
    
    public void setState(final State state)
    {
        super.getSpriteSheet().setCurrent(state);
    }
    
    public State getState()
    {
        return (State)super.getSpriteSheet().getCurrent();
    }
    
    protected void addAnimation(final Animation animation, final Object key)
    {
        super.getSpriteSheet().add(animation, key);
    }
    
    public void render(final Graphics graphics, final Image image)
    {
        super.draw(graphics, image);
    }
}