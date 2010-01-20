package com.sawdust.util.art;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.media.jai.JAI;

public abstract class ArtToken
{
    ArtLibraryGenerator<?> _parent;
    public ArtToken(ArtLibraryGenerator<?> parent)
    {
        super();
        _parent = parent;
    }

    abstract BufferedImage createImage();
    
    void writeImage(File out) throws FileNotFoundException
    {
        FileOutputStream stream = new FileOutputStream(out);
        String format = "PNG";
        JAI.create("encode", createImage(), stream, format, null);

    }

    abstract String getKey();

}
