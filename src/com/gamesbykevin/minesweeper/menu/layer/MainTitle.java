package com.gamesbykevin.minesweeper.menu.layer;

import com.gamesbykevin.minesweeper.resources.MenuImage;
import com.gamesbykevin.framework.menu.Layer;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.minesweeper.engine.Engine;
import com.gamesbykevin.minesweeper.menu.CustomMenu;
import com.gamesbykevin.minesweeper.menu.option.Controls;
import com.gamesbykevin.minesweeper.menu.option.Credits;
import com.gamesbykevin.minesweeper.menu.option.Instructions;
import com.gamesbykevin.minesweeper.menu.option.Options;
import com.gamesbykevin.minesweeper.menu.option.StartGame;
import com.gamesbykevin.minesweeper.shared.Shared;

public final class MainTitle extends Layer implements LayerRules
{
    public MainTitle(final Engine engine) throws Exception
    {
        //the layer will have the given transition and screen size
        super(Layer.Type.NONE, engine.getMain().getScreen());
        
        //this layer will have a title at the top
        setTitle(Shared.GAME_NAME);
        
        //set the background image of the Layer
        setImage(engine.getResources().getMenuImage(MenuImage.Keys.TitleBackground));
        
        //we will not force this layer to show
        setForce(false);
        
        //we want to pause this layer once it completes
        setPause(true);
        
        //this layer will be active for x seconds
        //setTimer(new Timer(Timers.toNanoSeconds(2500L)));
        
        //since there are options how big should the container be
        setOptionContainerRatio(RATIO);
        
        //add options
        super.add(CustomMenu.OptionKey.StartGame,       new StartGame());
        super.add(CustomMenu.OptionKey.Options,         new Options());
        super.add(CustomMenu.OptionKey.Controls,        new Controls());
        super.add(CustomMenu.OptionKey.Instructions,    new Instructions());
        super.add(CustomMenu.OptionKey.Credits,         new Credits());
    }
}