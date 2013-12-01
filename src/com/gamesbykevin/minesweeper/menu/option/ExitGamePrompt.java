/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gamesbykevin.minesweeper.menu.option;

import com.gamesbykevin.minesweeper.menu.CustomMenu;
import com.gamesbykevin.framework.menu.Option;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class ExitGamePrompt extends Option
{
    private static final String TITLE = "Exit Game";
    
    public ExitGamePrompt()
    {
        //when this option is selected it will go to another layer
        super(CustomMenu.LayerKey.ExitGameConfirm);
        
        super.add(TITLE, null);
    }
}