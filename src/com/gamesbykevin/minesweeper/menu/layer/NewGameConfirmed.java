package com.gamesbykevin.minesweeper.menu.layer;

import com.gamesbykevin.framework.menu.Layer;
import com.gamesbykevin.minesweeper.engine.Engine;

public final class NewGameConfirmed extends Layer implements LayerRules
{
    public NewGameConfirmed(final Engine engine)
    {
        //the layer will have the given transition and screen size
        super(Layer.Type.NONE, engine.getMain().getScreen());
        
        //no options here to setup
    }
}