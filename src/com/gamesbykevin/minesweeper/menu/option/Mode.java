package com.gamesbykevin.minesweeper.menu.option;

import com.gamesbykevin.framework.menu.Option;
import com.gamesbykevin.framework.resources.Audio;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class Mode extends Option
{
    private static final String TITLE = "Mode: ";
    
    public enum Types
    {
        //Play at your convenience
        Free, 
        
        //Solve the puzzle within the time limit
        Timed,
        
        // or Play against the computer
        Versus,
        
        //you and the computer play against each other and first to solve 5 puzzles without dying wins
        Race,
    }
    
    public Mode(final Audio audio)
    {
        super(TITLE);
        
        for (Types types : Types.values())
        {
            super.add(types.toString(), audio);
        }
        
        //default to free
        super.setIndex(0);
    }
}