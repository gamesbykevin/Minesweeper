package com.gamesbykevin.minesweeper.player;

import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Status extends Sprite implements Disposable
{
    //list of messages
    private List<String> messages;
    
    //how many messages to allow in out list
    private static final int MESSAGE_LIMIT = 4;
    
    //initial startup message
    private static final String INITIAL_MESSAGE = "Game Begin.";
    
    public Status()
    {
        super();
        
        //add initial message
        add();
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        messages.clear();
        messages = null;
    }
    
    /**
     * Remove all messages from list
     */
    protected void clear()
    {
        messages.clear();
    }
    
    private void add()
    {
        //add initial message
        add(INITIAL_MESSAGE);
    }
    
    /**
     * Add status message
     * @param message Message to add
     */
    public void add(final String message)
    {
        if (messages == null)
        {
            //create new message list
            messages = new ArrayList<>();
        }
        
        //add message to list and add time after as well
        messages.add(message + ".   " + new SimpleDateFormat("HH:mm:ss.SSS").format(Calendar.getInstance().getTime()));
        
        //if we are over our limit we will keep removing the last message until we are within the limit set (just like a stack)
        while(messages.size() > MESSAGE_LIMIT)
        {
            //remove the first message in the list
            messages.remove(0);
        }
    }
    
    public void render(final Graphics graphics)
    {
        //store temporary font object
        final Font tmp = graphics.getFont();
        
        //set font size
        graphics.setFont(graphics.getFont().deriveFont(Font.PLAIN, 14));
        
        //get font height
        final int height = graphics.getFontMetrics().getHeight();
        
        graphics.setColor(Color.BLACK);
        graphics.fillRect((int)super.getX(), (int)super.getY(), (int)super.getWidth(), (int)super.getHeight());
        
        graphics.setColor(Color.WHITE);
        
        int y = (int)(super.getY() + 15);
        
        for (String message : messages)
        {
            graphics.drawString(message, (int)(super.getX() + 10), y);
            
            y += height;
        }
        
        //restore the previous font
        graphics.setFont(tmp);
    }
}