package com.gamesbykevin.minesweeper.menu.option;

import com.gamesbykevin.framework.menu.Option;
import com.gamesbykevin.framework.resources.Audio;
import com.gamesbykevin.framework.util.Timers;

/**
 * The setup of this specific option
 * @author GOD
 */
public final class OpponentDifficulty extends Option
{
    private static final String TITLE = "Speed (Versus/Race Mode): ";
    
    public enum Selections
    {
        Slow(150),
        Medium(100),
        Fast(75),
        Impossible(1);
        
        private long delay;
        
        private Selections(final long delay)
        {
            this.delay = Timers.toNanoSeconds(delay);
        }
        
        /**
         * Get the time delay between each cpu movement
         * @return Time delay in nanoseconds
         */
        public long getDelay()
        {
            return this.delay;
        }
    }
    
    public OpponentDifficulty(final Audio audio)
    {
        super(TITLE);
        
        for (Selections selection : Selections.values())
        {
            super.add(selection.toString(), audio);
        }
        
        //default to medium
        super.setIndex(1);
    }
}