package com.gamesbykevin.minesweeper.menu.option;

import com.gamesbykevin.minesweeper.menu.CustomMenu.LayerKey;
import com.gamesbykevin.framework.menu.Option;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class OptionsGoBack extends Option
{
    private static final String TITLE = "Go Back";
    
    public OptionsGoBack()
    {
        //when this option is selected it will go to another layer
        super(LayerKey.MainTitle);
        
        super.add(TITLE, null);
    }
}