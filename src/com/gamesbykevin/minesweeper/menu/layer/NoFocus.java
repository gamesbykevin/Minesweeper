package com.gamesbykevin.minesweeper.menu.layer;

import com.gamesbykevin.minesweeper.resources.MenuImage;
import com.gamesbykevin.framework.menu.Layer;
import com.gamesbykevin.minesweeper.engine.Engine;

public final class NoFocus extends Layer implements LayerRules
{
    public NoFocus(final Engine engine)
    {
        //the layer will have the given transition and screen size
        super(Layer.Type.NONE, engine.getMain().getScreen());
        
        //set the background image of the Layer
        setImage(engine.getResources().getMenuImage(MenuImage.Keys.AppletFocus));
        
        //should we force the user to view this layer
        setForce(false);
        
        //when the layer is complete should we transition to the next or pause
        setPause(true);
        
        //no options here to setup
    }
}