package com.gamesbykevin.minesweeper.menu.layer;

import com.gamesbykevin.minesweeper.resources.MenuImage;
import com.gamesbykevin.framework.menu.Layer;
import com.gamesbykevin.minesweeper.engine.Engine;
import com.gamesbykevin.minesweeper.menu.CustomMenu;

public final class Instructions2 extends Layer implements LayerRules
{
    public Instructions2(final Engine engine)
    {
        //the layer will have the given transition and screen size
        super(Layer.Type.NONE, engine.getMain().getScreen());
        
        //set the background image of the Layer
        setImage(engine.getResources().getMenuImage(MenuImage.Keys.Instructions2));
        
        //what is the next layer
        setNextLayer(CustomMenu.LayerKey.MainTitle);
        
        //should we force the user to view this layer
        setForce(false);
        
        //when the layer is complete should we transition to the next or pause
        setPause(true);
        
        //is there a time limit for this layer
        setTimer(null);
        
        //no options here to setup
    }
}