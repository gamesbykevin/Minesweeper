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
        //roam the hero can move freely
        Roam, 
        
        //text mode the hero spells words to advance
        Text
    }
    
    public Mode(final Audio audio)
    {
        super(TITLE);
        
        for (Types types : Types.values())
        {
            super.add(types.toString(), audio);
        }
        
        //default to roam
        super.setIndex(0);
    }
}