package com.gamesbykevin.minesweeper.player;

import com.gamesbykevin.framework.base.Sprite;

import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.minesweeper.board.Board;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Random;

public class Player extends Sprite
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
    private static final int BOARD_START_X = 0;
    private static final int BOARD_START_Y = 50;
    
    protected Player(final int width, final int height)
    {
        //set the dimensions of our overall image
        super.setDimensions(width, height);
        
        //create new transparent image with width height
        this.image = new BufferedImage((int)getWidth(), (int)getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        //location of the timer
        this.timerLocation = new Point(10, 20);

        //location of the mine count
        this.infoLocation = new Point(10, 40);
        
        //where to show result
        this.resultLocation = new Point(130, 10);
        
        //the mouse location
        this.mouseLocation = new Point();
        
        //locations of icons
        this.mouseSource = new Rectangle(0, 0, 23, 34);
        this.successSource = new Rectangle(40, 0, 35, 28);
        this.failSource = new Rectangle(90, 0, 35, 35);
        
        //create our timer object
        this.timer = new Timer();
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
        //create board
        this.board = new Board(columns, rows, mines, random, BOARD_START_X, BOARD_START_Y);
    }
    
    protected Point getMouseLocation()
    {
        return this.mouseLocation;
    }
    
    public Board getBoard()
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
     * @return true is the player lost or won, false if still playing
     */
    public boolean hasGameOver()
    {
        return (getBoard().hasLost() || getBoard().hasSolved());
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
        
        this.timer.update(time);
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
        
        //get graphics object for image
        Graphics2D g2d = this.image.createGraphics();
        
        //set parent font
        g2d.setFont(graphics.getFont());
        
        //clear the image so a new one can be drawn
        g2d.clearRect(0, 0, image.getWidth(), image.getHeight());
        
        if (this.timer != null)
            g2d.drawString("Timer: " + timer.getDescPassed(Timers.FORMAT_6), this.timerLocation.x, this.timerLocation.y);
        
        final int mineCount = getBoard().getMineCount() - getBoard().getFlagCount();
        
        g2d.drawString("Mines: " + mineCount, this.infoLocation.x, this.infoLocation.y);
        
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
            drawIcon(g2d, this.mouseLocation, mouseSource);
        
        //write image to graphics object
        super.draw(graphics, this.image);
        
        //release image resources
        this.image.flush();
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