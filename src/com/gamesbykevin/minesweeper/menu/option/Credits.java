package com.gamesbykevin.minesweeper.menu.option;

import com.gamesbykevin.minesweeper.menu.CustomMenu;
import com.gamesbykevin.framework.menu.Option;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class Credits extends Option
{
    private static final String TITLE = "Credits";
    
    public Credits()
    {
        //when this option is selected it will go to another layer
        super(CustomMenu.LayerKey.Credits);
        
        super.add(TITLE, null);
    }
}