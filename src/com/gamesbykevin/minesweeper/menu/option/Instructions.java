package com.gamesbykevin.minesweeper.menu.option;

import com.gamesbykevin.minesweeper.menu.CustomMenu;
import com.gamesbykevin.framework.menu.Option;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class Instructions extends Option
{
    private static final String TITLE = "Instructions";
    
    public Instructions()
    {
        //when this option is selected it will go to another layer
        super(CustomMenu.LayerKey.Instructions1);
        
        super.add(TITLE, null);
    }
}