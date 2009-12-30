package com.sawdust.engine.common.game;

public class SolidColorGameCanvas extends GameCanvas
{

    public SolidColorGameCanvas(String color, String text)
    {
        this();
        this.color = color;
        this.textColor = text;
    }

    SolidColorGameCanvas()
    {
        super();
    }

    public String color;
    public String textColor;

}
