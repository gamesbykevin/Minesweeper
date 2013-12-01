package com.gamesbykevin.minesweeper.menu.option;

import com.gamesbykevin.minesweeper.menu.CustomMenu;
import com.gamesbykevin.framework.menu.Option;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class Resume extends Option
{
    private static final String TITLE = "Resume";
    
    public Resume()
    {
        //when this option is selected it will go to another layer
        super(CustomMenu.LayerKey.StartGame);
        
        super.add(TITLE, null);
    }
}