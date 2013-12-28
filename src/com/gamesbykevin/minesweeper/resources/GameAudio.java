package com.gamesbykevin.minesweeper.resources;

import static com.gamesbykevin.minesweeper.resources.Resources.RESOURCE_DIR;

import com.gamesbykevin.framework.resources.*;

/**
 * All audio for game
 * @author GOD
 */
public class GameAudio extends AudioManager
{
    //location of resources
    private static final String DIRECTORY = "audio/game/sound/{0}.wav";
    
    //description for progress bar
    private static final String DESCRIPTION = "Loading Game Audio Resources";
    
    public enum Keys
    {
        //sound when a new game starts
        NewGame,
        
        //when selecting a tile and it is a blank one which opens up an area
        Opening,
        
        //when you win
        Win,
        
        //when you lost
        Lose,
        
        //when selecting tile
        SelectTile,
        
        //when selecting a tile that is completed or flagged
        UnavailableSelection,
        
        //when flagging a tile
        FlagTile,
    }
    
    public GameAudio() throws Exception
    {
        super(RESOURCE_DIR + DIRECTORY, Keys.values());
        
        //the description that will be displayed for the progress bar
        super.setDescription(DESCRIPTION);
    }
}