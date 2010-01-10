package com.sawdust.util.art;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.media.jai.JAI;

import com.sawdust.engine.view.cards.Card;
import com.sawdust.engine.view.cards.Ranks;
import com.sawdust.engine.view.cards.Suits;
import com.sun.media.jai.codec.FileSeekableStream;

public class CardGenerator1 extends ArtLibraryGenerator<CardArt>
{
    String resourcePath = "";
    String outputPath = "";

    public CardGenerator1(String inputPath, String basePath)
    {
        super(inputPath, basePath);
        resourcePath = inputPath;
        outputPath = basePath;
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        new CardGenerator1(args[0], args[1]).createLibrary();
    }

    double getSize(Ranks r)
    {
        if (r.getOrder() > Ranks.Ten.getOrder())
        {
            return 0.45 + ((r.getOrder() - 10) * 0.035);
        }
        else
        {
            return 0.3 + ((r.getOrder() - 2) * 0.007);
        }
    }

    String getRankLabel(Ranks r)
    {
        if (r == Ranks.Ten) return "10";
        return r.toString();
    }

    public Image getImage(Suits s)
    {
        File outFile;
        if (s.equals(Suits.Clubs))
        {
            outFile = new File(resourcePath + "/input/club.png");
        }
        else if (s.equals(Suits.Spades))
        {
            outFile = new File(resourcePath + "/input/spade.png");
        }
        else if (s.equals(Suits.Diamonds))
        {
            outFile = new File(resourcePath + "/input/diamond.png");
        }
        else if (s.equals(Suits.Hearts))
        {
            outFile = new File(resourcePath + "/input/heart.png");
        }
        else
        {
            throw new RuntimeException();
        }
        try
        {
            return JAI.create("stream", new FileSeekableStream(outFile)).getAsBufferedImage();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    String getKey()
    {
        return "cards1";
    }

    @Override
    CardArt[] getTokenSet()
    {
        ArrayList<CardArt> returnList = new ArrayList<CardArt>();
        for (final Suits s : Suits.values())
        {
            if (s == Suits.Null) continue;
            for (final Ranks r : Ranks.values())
            {
                if (r == Ranks.Null) continue;
                Card card = new Card(r, s, 0);
                returnList.add(new CardArt(this, r, s));
            }
        }
        return returnList.toArray(new CardArt[] {});
    }

}
