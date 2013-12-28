package com.gamesbykevin.minesweeper.menu.option;

import com.gamesbykevin.framework.menu.Option;
import com.gamesbykevin.framework.resources.Audio;
import com.gamesbykevin.framework.util.Timers;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class BoardDifficulty extends Option
{
    private static final String TITLE = "Board Difficulty: ";
    
    public enum Selections
    {
        //45 seconds
        Beginner(45000),
        
        //5 minutes
        Intermediate(300000),
        
        //10 minutes
        Expert(600000);
        
        private long delay;
        
        private Selections(final long delay)
        {
            this.delay = Timers.toNanoSeconds(delay);
        }
        
        /**
         * Gets the time limit if playing timed mode
         * @return Time delay in nanoseconds
         */
        public long getDelay()
        {
            return this.delay;
        }
    }
    
    public BoardDifficulty(final Audio audio)
    {
        super(TITLE);
        
        for (Selections selection : Selections.values())
        {
            super.add(selection.toString(), audio);
        }
        
        //default to beginner
        super.setIndex(0);
    }
}