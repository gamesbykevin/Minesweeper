package com.gamesbykevin.minesweeper.player;

import com.gamesbykevin.framework.input.Mouse;
import java.awt.Point;

public final class Human extends Player
{
    public Human(final int width, final int height)
    {
        super(width, height);
    }
    
    /**
     * Update the board based on input from the human user's mouse
     * @param mouse Our mouse object
     * @param time Time to deduct from timer per update
     */
    public void update(final Mouse mouse, final long time) throws Exception
    {
        //update timer
        super.update(time);
        
        //reset mouse x, y
        resetMouseLocation(mouse.getLocation());
        
        //if we won or lost don't continue any further
        if (hasGameOver())
            return;
        
        final boolean released = mouse.isMouseReleased();
        final boolean pressed = mouse.isMousePressed();
        final boolean dragged = mouse.isMouseDragged();
        
        //the pointer of the mouse cursor image is not in the upper left so we need to offset
        getMouseLocation().translate(MOUSE_OFFSET_X, MOUSE_OFFSET_Y);
        
        if (mouse.hitRightButton() && released)
        {
            //update the board accordingly
            getBoard().updateRightReleased(getMouseLocation());
        }
        
        if (dragged)
        {
            //update the board accordingly
            getBoard().updateDragged(getMouseLocation());
        }
        
        if (released)
        {
            //update the board accordingly
            getBoard().updateReleased(getMouseLocation());
        }
        
        if (pressed)
        {
            //update the board accordingly
            getBoard().updatePressed(getMouseLocation());
        }
        
        //reset mouse events
        mouse.reset();
        
        //reset mouse x, y
        resetMouseLocation(mouse.getLocation());
    }
}