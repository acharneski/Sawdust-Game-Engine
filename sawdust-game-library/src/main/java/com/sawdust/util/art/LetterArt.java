package com.sawdust.util.art;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

public class LetterArt extends ArtToken
{
    String letter; 
    Color color;
    int width;
    
    public LetterArt(ArtLibraryGenerator<?> parent, String letter, Color color, int width)
    {
        super(parent);
        this.letter = letter;
        this.color = color;
        this.width = width;
    }

    BufferedImage createImage()
    {
        BufferedImage image = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();

        graphics.setFont(new Font("Canarsie Slab JL", Font.BOLD, (int)(width*0.90)));
        graphics.setColor(color);
        String l = letter.substring(0,1);
        Rectangle2D labelMetrics = graphics.getFontMetrics().getStringBounds(l, graphics);
        graphics.drawString(l, 
                (int)(width - labelMetrics.getWidth())/2, 
                (int)(0.8*labelMetrics.getHeight() + (width - labelMetrics.getHeight())/2));
        return image;
    }

    @Override
    String getKey()
    {
        return letter;
    };
}
