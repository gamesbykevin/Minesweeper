package com.gamesbykevin.minesweeper.player;

import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.minesweeper.board.Board;
import java.awt.Font;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Random;

public abstract class Player extends Sprite implements Disposable
{
    //the board where the mines will be
    private Board board;
    
    //timer object
    private Timer timer;
    
    //where the timer will be drawn
    private final Point timerLocation;
    
    //where mine count will be drawn
    private final Point infoLocation;
    
    //where to show success/fail result
    private final Point resultLocation;
    
    //everything will be drawn relative to this image
    private BufferedImage image;
    
    //source of icons
    private final Rectangle mouseSource;
    private final Rectangle successSource;
    private final Rectangle failSource;
    
    //the location of the mouse (used for cpu)
    private final Point mouseLocation;
    
    //the mouse finger pointer is x,y pixels off the upper right corner
    protected static final int MOUSE_OFFSET_X = 9;
    protected static final int MOUSE_OFFSET_Y = 0;
    
    //the starting position where the tiles will be drawn
    private static final int BOARD_START_X = 10;
    private static final int BOARD_START_Y = 45;
    
    //is this player human
    private final boolean human;
    
    //how many puzzles has this player solved
    private int wins = 0;
    
    //where status messages will be displayed for this user
    private Status status;
    
    protected Player(final int width, final int height, final boolean human)
    {
        //set the dimensions of our overall image
        super.setDimensions(width, height);
        
        //is this player human
        this.human = human;
        
        //create new transparent image with width height
        this.image = new BufferedImage((int)getWidth(), (int)getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        //location of the timer
        this.timerLocation = new Point(10, 20);

        //location of the mine count
        this.infoLocation = new Point(10, 40);
        
        //where to show result
        this.resultLocation = new Point(130, 05);
        
        //the mouse location
        this.mouseLocation = new Point();
        
        //locations of icons
        this.mouseSource = new Rectangle(0, 0, 23, 34);
        this.successSource = new Rectangle(40, 0, 35, 28);
        this.failSource = new Rectangle(90, 0, 35, 35);
        
        //create our timer object
        this.timer = new Timer();
        
        //create new status object
        this.status = new Status();
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        board.dispose();
        board = null;
        
        timer = null;
    
        image.flush();
        image = null;
    
        status.dispose();
        status = null;
    }
    
    /**
     * Increase the number of wins by 1
     */
    public void increaseWins()
    {
        this.wins++;
    }
    
    /**
     * Get the number of wins this player has
     * @return The total number of wins
     */
    public int getWins()
    {
        return this.wins;
    }
    
    /**
     * Set the timer here because we are playing timed mode
     * @param delay The time to count down
     */
    public void setTimer(final long delay)
    {
        //set the reset value
        getTimer().setReset(delay);
        
        //then reset that time
        getTimer().reset();
    }
    
    /**
     * Get the timer
     * @return The timer that is keeping track of time remaining/passed
     */
    public Timer getTimer()
    {
        return this.timer;
    }
    
    /**
     * Create a new board with the specified information
     * @param columns 
     * @param rows 
     * @param mines 
     * @param random 
     */
    public void createBoard(final int columns, final int rows, int mines, final Random random)
    {
        //create board object and set default size/mines
        this.board = new Board(columns, rows, mines);
        
        //create the board
        reset(random);
    }
    
    /**
     * Add status message
     * @param message 
     */
    public void addStatusMessage(final String message)
    {
        getStatus().add(message);
    }
    
    private Status getStatus()
    {
        return this.status;
    }
    
    protected Point getMouseLocation()
    {
        return this.mouseLocation;
    }
    
    public void reset(final Random random)
    {
        //generate new board
        getBoard().reset(random);
        
        //set locations of tiles
        getBoard().setLocations(BOARD_START_X, BOARD_START_Y);
    }
    
    /**
     * Has the player solved the board
     * @return True if the board has been solved, false otherwise
     */
    public boolean hasWin()
    {
        return getBoard().hasSolved();
    }
    
    /**
     * Has the player hit a mine.
     * @return True if so, false otherwise
     */
    public boolean hasLose()
    {
        return getBoard().hasLost();
    }
    
    public void markLose()
    {
        getBoard().setLose();
    }
    
    public void markWin()
    {
        getBoard().setWin();
    }
    
    /**
     * Get the game board
     * @return Board in play
     */
    protected Board getBoard()
    {
        return this.board;
    }
    
    protected void resetMouseLocation(final Point mouseOrigin)
    {
        //set the mouse location
        getMouseLocation().setLocation(mouseOrigin);
        
        //offset location depending on the location of the mouse
        getMouseLocation().translate((int)-super.getX(), (int)-super.getY());
    }
    
    /**
     * Has the game ended for this player
     * @return true is the has player lost or won, false if still playing
     */
    public boolean hasGameOver()
    {
        return (hasWin() || hasLose());
    }
    
    /**
     * Update the timer as long as the game hasn't ended
     * @param time 
     */
    protected void update(final long time)
    {
        //if we won or lost don't continue any further
        if (hasGameOver())
            return;
        
        //update the timer
        getTimer().update(time);
        
        //check if the timer is set to a limit and if the time ran out
        checkTimer();
    }
    
    /**
     * Check the timer to verify if time has ran out.
     */
    private void checkTimer()
    {
        //if the timer is limited
        if (getTimer().getReset() != 0)
        {
            //has the time passed
            if (getTimer().hasTimePassed())
            {
                //if time has passed set time left to 0
                getTimer().setRemaining(0);
                
                //time has ran out the player has lost
                getBoard().setLose();
            }
        }
    }
    
    /**
     * Set the location of the player and the status screen
     * @param x
     * @param y 
     */
    @Override
    public void setLocation(final int x, final int y)
    {
        super.setLocation(x, y);
        
        //set the location of the status screen relative to the player
        status.setLocation(super.getX(), super.getY() + super.getHeight() + 5);
        
        //set the width to match the player
        status.setWidth(super.getWidth());
        
        //set a fixed height
        status.setHeight(65);
    }
    
    /**
     * Draw the board and other info
     * @param graphics 
     */
    public void render(final Graphics graphics)
    {
        //if image does not exist we can't continue
        if (image == null || super.getImage() == null)
            return;
        
        //make sure status object exists
        if (getStatus() != null)
        {
            //draw status messages
            getStatus().render(graphics);
        }
        
        //get graphics object for image
        Graphics2D g2d = this.image.createGraphics();
        
        //set parent font
        g2d.setFont(graphics.getFont().deriveFont(Font.BOLD, 18f));
        
        //clear the image so a new one can be drawn
        g2d.clearRect(0, 0, image.getWidth(), image.getHeight());
        
        if (getTimer() != null)
        {
            //if there is a reset time present we are counting down the time
            if (getTimer().getReset() != 0)
            {
                g2d.drawString("Timer: " + timer.getDescRemaining(Timers.FORMAT_6), this.timerLocation.x, this.timerLocation.y);
            }
            else
            {
                g2d.drawString("Timer: " + timer.getDescPassed(Timers.FORMAT_6), this.timerLocation.x, this.timerLocation.y);
            }
        }
        
        final int mineCount = getBoard().getMineCount() - getBoard().getFlagCount();
        
        if (human)
            g2d.drawString("Human Mines: " + mineCount, this.infoLocation.x, this.infoLocation.y);
        else
            g2d.drawString("CPU Mines: " + mineCount, this.infoLocation.x, this.infoLocation.y);
        
        //draw fail icon
        if (getBoard().hasLost())
            drawIcon(g2d, resultLocation, failSource);
        
        //draw win icon
        if (getBoard().hasSolved())
            drawIcon(g2d, resultLocation, successSource);
        
        //draw board to image
        getBoard().render(g2d, super.getImage());
        
        //draw mouse so we can see
        if (this.mouseLocation != null)
            drawMouse(g2d, this.mouseLocation);
        
        //write image to graphics object
        super.draw(graphics, this.image);
        
        //release image resources
        this.image.flush();
    }
    
    protected void drawMouse(final Graphics graphics, final Point d)
    {
        //only draw the mouse if the game is over
        if (!hasGameOver())
        {
            drawIcon((Graphics2D)graphics, d, mouseSource);
        }
    }
    
    /**
     * Draw icon from source s at destination d
     * @param g2d Graphics object
     * @param d Destination
     * @param s Source
     */
    private void drawIcon(final Graphics2D g2d, final Point d, final Rectangle s)
    {
        final int dx1 = d.x;
        final int dy1 = d.y;
        final int dx2 = d.x + s.width;
        final int dy2 = d.y + s.height;
        
        final int sx1 = s.x;
        final int sy1 = s.y;
        final int sx2 = s.x + s.width;
        final int sy2 = s.y + s.height;
        
        g2d.drawImage(super.getImage(), dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
    }
}