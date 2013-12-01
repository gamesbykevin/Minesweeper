package com.gamesbykevin.minesweeper.menu.layer;

import com.gamesbykevin.minesweeper.resources.MenuImage;
import com.gamesbykevin.framework.menu.Layer;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.Timers;
import com.gamesbykevin.minesweeper.engine.Engine;
import com.gamesbykevin.minesweeper.menu.CustomMenu;

public final class Credits extends Layer implements LayerRules
{
    public Credits(final Engine engine)
    {
        //the layer will have the given transition and screen size
        super(Layer.Type.SCROLL_VERTICAL_NORTH, engine.getMain().getScreen());
        
        //set the background image of the Layer
        setImage(engine.getResources().getMenuImage(MenuImage.Keys.Credits));
        
        //should we force the user to view this layer
        setForce(false);
        
        //when the layer is complete should we transition to the next or pause
        setPause(false);
        
        //what is the next layer
        setNextLayer(CustomMenu.LayerKey.MainTitle);
        
        //what is the duration of the current layer
        setTimer(new Timer(Timers.toNanoSeconds(6000L)));
        
        //no options here to setup
    }
}