package com.sawdust.util.art;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.media.jai.JAI;

public class LetterGenerator1 extends ArtLibraryGenerator<LetterArt>
{

    public static void main(String[] args) throws IOException
    {
        new LetterGenerator1(args[0], args[1]).createLibrary();
    }

    static final int size = 50;
    static final String letters[] =
    { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

    public LetterGenerator1(String string, String string2)
    {
        super(string, string2);
    }

    @Override
    LetterArt[] getTokenSet()
    {
        ArrayList<LetterArt> returnList = new ArrayList<LetterArt>(); 
        for (String letter : letters)
        {
            returnList.add(new LetterArt(this, letter+"1", Color.WHITE, size));
            returnList.add(new LetterArt(this, letter+"2", Color.GREEN, size));
        }
        return returnList.toArray(new LetterArt[]{});
    }

    @Override
    String getKey()
    {
        return "letters1";
    }

}
