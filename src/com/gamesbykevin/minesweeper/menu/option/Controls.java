package com.gamesbykevin.minesweeper.menu.option;

import com.gamesbykevin.minesweeper.menu.CustomMenu;
import com.gamesbykevin.framework.menu.Option;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class Controls extends Option
{
    private static final String TITLE = "Controls";
    
    public Controls()
    {
        //when this option is selected it will go to another layer
        super(CustomMenu.LayerKey.Controls1);
        
        super.add(TITLE, null);
    }
}