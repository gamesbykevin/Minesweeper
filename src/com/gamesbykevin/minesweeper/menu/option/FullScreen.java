package com.gamesbykevin.minesweeper.menu.option;

import com.gamesbykevin.minesweeper.menu.CustomMenu;
import com.gamesbykevin.framework.menu.Option;
import com.gamesbykevin.framework.resources.Audio;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class FullScreen extends Option
{
    private static final String TITLE = "Full Screen: ";
    
    public FullScreen(final Audio audio)
    {
        super(TITLE);
        
        for (CustomMenu.Toggle toggle : CustomMenu.Toggle.values())
        {
            super.add(toggle.toString(), audio);
        }
        
        //default to off
        super.setIndex(0);
    }
}