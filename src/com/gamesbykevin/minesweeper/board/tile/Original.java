package com.gamesbykevin.minesweeper.board.tile;

import java.awt.Rectangle;

import com.gamesbykevin.framework.base.Animation;

public final class Original extends Tile
{
    private static final int SPRITE_WIDTH = 16;
    private static final int SPRITE_HEIGHT = 16;
    
    public Original()
    {
        super.setDimensions(SPRITE_WIDTH, SPRITE_HEIGHT);
        
        this.addAnimation(State.Blank,          2, 53);
        this.addAnimation(State.BlankPress,     19, 53);
        this.addAnimation(State.Flag,           36, 53);
        this.addAnimation(State.Question,       53, 53);
        this.addAnimation(State.QuestionPress,  70, 53);
        this.addAnimation(State.MineReveal,     87, 53);
        this.addAnimation(State.MineSelection,  104, 53);
        this.addAnimation(State.MineFlag,       121, 53);
        this.addAnimation(State.One,            2, 70);
        this.addAnimation(State.Two,            19, 70);
        this.addAnimation(State.Three,          36, 70);
        this.addAnimation(State.Four,           53, 70);
        this.addAnimation(State.Five,           70, 70);
        this.addAnimation(State.Six,            87, 70);
        this.addAnimation(State.Seven,          104, 70);
        this.addAnimation(State.Eight,          121, 70);
        
        //set the current animation
        super.getSpriteSheet().setCurrent(State.Blank);
    }
    
    private void addAnimation(final State state, final int x, final int y)
    {
        Animation animation = new Animation();
        animation.add(new Rectangle(x, y, SPRITE_WIDTH, SPRITE_HEIGHT), 0);
        super.addAnimation(animation, state);
    }
}