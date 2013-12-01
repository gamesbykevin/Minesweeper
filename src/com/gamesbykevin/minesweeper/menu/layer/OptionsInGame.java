package com.gamesbykevin.minesweeper.menu.layer;

import com.gamesbykevin.framework.menu.Layer;
import com.gamesbykevin.framework.resources.Audio;
import com.gamesbykevin.minesweeper.engine.Engine;
import com.gamesbykevin.minesweeper.menu.CustomMenu;
import com.gamesbykevin.minesweeper.menu.option.*;
import com.gamesbykevin.minesweeper.resources.MenuAudio;

public final class OptionsInGame extends Layer implements LayerRules
{
    public OptionsInGame(final Engine engine) throws Exception
    {
        //the layer will have the given transition and screen size
        super(Layer.Type.NONE, engine.getMain().getScreen());
        
        //this layer will have a title at the top
        setTitle("Options");
        
        //should we force the user to view this layer
        setForce(false);
        
        //when the layer is complete should we transition to the next or pause
        setPause(true);
        
        //since there are options how big should the container be
        setOptionContainerRatio(RATIO);
        
        final Audio audio = engine.getResources().getMenuAudio(MenuAudio.Keys.OptionChange);
        
        //setup options here
        super.add(CustomMenu.OptionKey.Resume,      new Resume());
        super.add(CustomMenu.OptionKey.Sound,       new Sound(audio));
        super.add(CustomMenu.OptionKey.FullScreen,  new FullScreen(audio));
        super.add(CustomMenu.OptionKey.NewGame,     new NewGamePrompt());
        super.add(CustomMenu.OptionKey.ExitGame,    new ExitGamePrompt());
    }
}