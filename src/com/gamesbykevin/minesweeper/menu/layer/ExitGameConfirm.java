package com.gamesbykevin.minesweeper.menu.layer;

import com.gamesbykevin.framework.menu.Layer;
import com.gamesbykevin.framework.menu.Option;
import com.gamesbykevin.minesweeper.engine.Engine;
import com.gamesbykevin.minesweeper.menu.CustomMenu;
import com.gamesbykevin.minesweeper.menu.option.*;

public final class ExitGameConfirm extends Layer implements LayerRules
{
    public ExitGameConfirm(final Engine engine) throws Exception
    {
        //the layer will have the given transition and screen size
        super(Layer.Type.NONE, engine.getMain().getScreen());
        
        //this layer will have a title at the top
        super.setTitle("Confirm Exit");
        
        //we will not force this layer to show
        super.setForce(false);
        
        //we do not want to pause this layer once it completes
        super.setPause(true);
        
        //since there are options how big should the container be
        super.setOptionContainerRatio(RATIO);
        
        //setup options here
        super.add(CustomMenu.OptionKey.ExitGameConfirm, new ExitGameConfirmYes());
        super.add(CustomMenu.OptionKey.ExitGameDeny,    new ExitGameConfirmNo());
    }
}