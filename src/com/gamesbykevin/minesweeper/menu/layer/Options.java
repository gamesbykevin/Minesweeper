package com.gamesbykevin.minesweeper.menu.layer;

import com.gamesbykevin.minesweeper.resources.MenuAudio;
import com.gamesbykevin.minesweeper.resources.MenuImage;
import com.gamesbykevin.framework.menu.*;
import com.gamesbykevin.framework.resources.Audio;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.minesweeper.engine.Engine;
import com.gamesbykevin.minesweeper.manager.Manager.*;
import com.gamesbykevin.minesweeper.menu.CustomMenu.*;
import com.gamesbykevin.minesweeper.menu.option.*;

public final class Options extends Layer implements LayerRules
{
    public Options(final Engine engine) throws Exception
    {
        //the layer will have the given transition and screen size
        super(Layer.Type.NONE, engine.getMain().getScreen());
        
        //this layer will have a title at the top
        setTitle("Options");
        
        //set the background image of the Layer
        setImage(engine.getResources().getMenuImage(MenuImage.Keys.TitleBackground));
        
        //what is the duration of the current layer
        setTimer(new Timer(Timers.toNanoSeconds(5000L)));
        
        //should we force the user to view this layer
        setForce(false);
        
        //when the layer is complete should we transition to the next or pause
        setPause(true);
        
        //since there are options how big should the container be
        setOptionContainerRatio(RATIO);
        
        Audio audio = engine.getResources().getMenuAudio(MenuAudio.Keys.OptionChange);
        
        //add options
        super.add(OptionKey.Mode,           new Mode(audio));
        super.add(OptionKey.Sound,          new Sound(audio));
        super.add(OptionKey.FullScreen,     new FullScreen(audio));
        super.add(OptionKey.GoBack,         new OptionsGoBack());
    }
}