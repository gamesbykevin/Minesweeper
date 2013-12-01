package com.gamesbykevin.minesweeper.resources;

import static com.gamesbykevin.minesweeper.resources.Resources.RESOURCE_DIR;
import com.gamesbykevin.framework.resources.*;

/**
 * All game images
 * @author GOD
 */
public class GameImage extends ImageManager
{
    //location of resources
    private static final String DIRECTORY = "images/game/{0}.png";
    
    //description for progress bar
    private static final String DESCRIPTION = "Loading Game Image Resources";
    
    public enum Keys
    {
        
    }
    
    public GameImage() throws Exception
    {
        super(RESOURCE_DIR + DIRECTORY, Keys.values());
        
        //the description that will be displayed for the progress bar
        super.setDescription(DESCRIPTION);
    }
}