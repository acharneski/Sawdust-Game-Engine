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

import javax.media.jai.JAI;

public class LetterGenerator1
{
    static final StringBuilder initCode = new StringBuilder();
    static final StringBuilder declareCode = new StringBuilder();
    static final int size = 50;
    static final String letters[] = {
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
        "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };
    private static String inputPath;
    private static String basePath;
   private static boolean verbose;


    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
       inputPath = args[0];
       basePath = args[1];
        for(String letter : letters)
        {
            createSubLetter(letter, Color.WHITE, "1");
            createSubLetter(letter, Color.GREEN, "2");
        }
        
        
        verbose = false;
        if (verbose)
        {
           System.out.println(declareCode);
           System.out.println("\n\n");
           System.out.println(initCode);
        }
    }

    private static void createSubLetter(String letter, Color color, String suffix) throws FileNotFoundException
    {
        String artSet = "/letters1/";
        String imageName = letter + suffix;
        String imagePath = artSet + imageName + ".png";
        createImage(basePath + imagePath, size, letter, color);
        initCode.append(String.format(
                "\nImageRegistry.put(\"%s\", " + 
                "\n\tnew ImageCallback(){ public AbstractImagePrototype getImage(Object self){ " + 
                    "\n\t\treturn ((Bundle)self).get%s();" + 
                "\n\t}\n});",
                (suffix.equals("1"))?letter:imageName,
                imageName));
        declareCode.append(String.format(
                "\n@Resource(\"%s\")\n" + 
                "AbstractImagePrototype get%s();\n", 
                imagePath,
                imageName));
    }

    private static void createImage(String outputFile, int psize, String str, Color color) throws FileNotFoundException
    {
        BufferedImage image = new BufferedImage(psize,psize,BufferedImage.TYPE_INT_RGB);
        
        Graphics graphics = image.getGraphics();
//        graphics.setColor(Color.BLACK);
//        graphics.fillRect(-1, -1, 2*psize, 2*psize);
//        graphics.setColor(Color.LIGHT_GRAY);
//        int w = (int)(psize*0.05);
//        graphics.fillRect(w, w, psize - 2*w, psize - 2*w);

        graphics.setFont(new Font("Canarsie Slab JL", Font.BOLD, (int)(psize*0.90)));
        graphics.setColor(color);
        Rectangle2D labelMetrics = graphics.getFontMetrics().getStringBounds(str, graphics);
        graphics.drawString(str, 
                (int)(psize - labelMetrics.getWidth())/2, 
                (int)(0.8*labelMetrics.getHeight() + (psize - labelMetrics.getHeight())/2));
        
        // Encode the file as a BMP image
        new File(outputFile).getParentFile().mkdirs();
        FileOutputStream stream = new FileOutputStream(outputFile);
        String format = "PNG";
        JAI.create("encode", image, stream, format, null);
    }

}
