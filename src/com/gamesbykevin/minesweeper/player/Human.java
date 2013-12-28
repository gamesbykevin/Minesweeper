package com.gamesbykevin.minesweeper.player;

import com.gamesbykevin.framework.input.Mouse;
import com.gamesbykevin.minesweeper.engine.Engine;

import java.awt.Graphics;
import java.awt.Point;

public final class Human extends Player implements IPlayer
{
    //the human mouse should be rendered anywhere
    private Point universalLocation;
    
    public Human(final int width, final int height)
    {
        super(width, height, true);
        
        this.universalLocation = new Point();
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        universalLocation = null;
    }
    
    /**
     * Update the computer logic to solve the puzzle
     * @param engine Our game engine object containing resources
     * @throws Exception 
     */
    @Override
    public void update(final Engine engine) throws Exception
    {
        //update timer
        super.update(engine.getMain().getTime());
        
        Mouse mouse = engine.getMouse();
        
        final Point origin = mouse.getLocation();
        
        //set universal mouse location
        this.universalLocation.setLocation(origin);
        
        //reset mouse x, y
        resetMouseLocation(origin);
        
        //if we won or lost don't continue any further
        if (hasGameOver())
            return;
        
        //the pointer of the mouse cursor image is not in the upper left so we need to offset
        getMouseLocation().translate(MOUSE_OFFSET_X, MOUSE_OFFSET_Y);
        
        if (mouse.hitRightButton() && mouse.isMouseReleased())
        {
            //update the board accordingly
            getBoard().updateRightReleased(getMouseLocation(), engine.getResources());
            
            //reset mouse events
            mouse.reset();
        }
        
        if (mouse.isMouseDragged())
        {
            //update the board accordingly
            getBoard().updateDragged(getMouseLocation());
            
            //reset mouse events
            mouse.reset();
        }
        
        if (mouse.isMouseReleased())
        {
            //update the board accordingly
            getBoard().updateReleased(getMouseLocation(), engine.getResources());
            
            //reset mouse events
            mouse.reset();
        }
        
        if (mouse.isMousePressed())
        {
            //update the board accordingly
            getBoard().updatePressed(getMouseLocation());
            
            //reset mouse events
            mouse.reset();
        }
        
        //reset mouse x, y
        resetMouseLocation(origin);
    }
    
    /**
     * Here we need to render the universal location of the mouse and not just the one for the player's canvas
     * @param graphics 
     */
    @Override
    public void render(final Graphics graphics)
    {
        super.render(graphics);
        
        //draw the mouse outside of the canvas
        super.drawMouse(graphics, universalLocation);
    }
}