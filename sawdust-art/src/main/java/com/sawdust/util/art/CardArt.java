package com.sawdust.util.art;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.InterpolationBicubic;
import javax.media.jai.JAI;

import com.sawdust.engine.view.cards.Ranks;
import com.sawdust.engine.view.cards.Suits;

public class CardArt extends ArtToken
{
    final Ranks _rank;
    final Suits _suit;
    static final int width = 72;

    public CardArt(ArtLibraryGenerator<CardArt> parent, Ranks r, Suits s)
    {
        super(parent);
        _rank = r;
        _suit = s;
    }

    @Override
    BufferedImage createImage()
    {
        int height = (int) (width * (4.0 / 3.0));
        double sz = ((CardGenerator1)_parent).getSize(_rank);
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics graphics;
        graphics = image.getGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(-1, -1, width + 2, height + 2);
        graphics.setColor(Color.WHITE);
        int w = (int) (width * 0.035);
        graphics.fillRect(w, w, width - 2 * w, height - 2 * w);
        graphics.setClip(w, w, width - 2 * w, height - 2 * w);

        w *= 2;
        Color suitColor = (_suit.equals(Suits.Spades) || _suit.equals(Suits.Clubs)) ? Color.BLACK : Color.RED;
        String fontName = "Trebuchet MS";
        int fontStyle = Font.CENTER_BASELINE;
        String rankLabel = ((CardGenerator1)_parent).getRankLabel(_rank);

        graphics.setColor(suitColor);
        graphics.setFont(new Font(fontName, fontStyle, (int) (width * 0.7 * Math.pow(sz, 0.7))));

        Rectangle2D labelMetrics = graphics.getFontMetrics().getStringBounds(rankLabel, graphics);
        graphics.drawString(rankLabel, (int) (w), (int) (w - 0.75 * labelMetrics.getMinY()));
        Image suitIcon = ((CardGenerator1)_parent).getImage(_suit);
        ImageObserver observer = new ImageObserver()
        {
            public boolean imageUpdate(Image img, int infoflags, int x, int y, int pwidth, int pheight)
            {
                return false;
            }
        };

        image = JAI.create(
                "rotate",
                new ParameterBlock().addSource(image).add((float) (width / 2.0)).add((float) (height / 2.0)).add((float) Math.PI).add(
                        new InterpolationBicubic(4)), null).getAsBufferedImage();
        graphics = image.getGraphics();

        graphics.setColor(suitColor);
        graphics.setFont(new Font(fontName, fontStyle, (int) (width * 0.7 * Math.pow(sz, 0.7))));
        graphics.drawString(rankLabel, (int) (w), (int) (w - 0.75 * labelMetrics.getMinY()));
        graphics.drawImage(suitIcon, (int) (width * (0.5 - sz)), (int) ((height * 0.5) - (width * sz)), (int) (width * (0.5 + sz)),
                (int) ((height * 0.5) + (width * sz)), 0, 0, suitIcon.getWidth(observer), suitIcon.getHeight(observer), observer);

        return image;
    }

    @Override
    String getKey()
    {
        String suitLabel = _suit.toString();
        String rankLabel = _rank.toString();
        return rankLabel + suitLabel;
    }

}
