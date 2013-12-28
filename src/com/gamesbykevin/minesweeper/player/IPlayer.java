package com.gamesbykevin.minesweeper.player;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.minesweeper.engine.Engine;

public interface IPlayer extends Disposable
{
    public void update(final Engine engine) throws Exception;
}
