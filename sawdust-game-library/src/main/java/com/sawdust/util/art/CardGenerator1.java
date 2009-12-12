package com.sawdust.util.art;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.media.jai.InterpolationBicubic;
import javax.media.jai.JAI;

import com.sawdust.engine.common.cards.Card;
import com.sawdust.engine.common.cards.Ranks;
import com.sawdust.engine.common.cards.Suits;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.PNGEncodeParam;

public class CardGenerator1
{
   private static final StringBuilder initCode     = new StringBuilder();
   private static final StringBuilder declareCode  = new StringBuilder();
   private static final int           size         = 72;
   static String                      resourcePath = "";
   static String                      outputPath   = "";
   private static boolean verbose;
   
   /**
    * @param args
    * @throws IOException
    */
   public static void main(String[] args) throws IOException
   {
      resourcePath = args[0];
      outputPath = args[1];
      for (final Suits s : Suits.values())
      {
         if (s == Suits.Null) continue;
         for (final Ranks r : Ranks.values())
         {
            if (r == Ranks.Null) continue;
            String imageName = r.toString() + s.toString();
            String imagePath = "/cards1/" + imageName + ".png";
            Card card = new Card(r, s, 0);
            createImage(outputPath + imagePath, size, s, r);
            String cardString = String.format("%s%s", card.getRank().name(), card.getSuit().fullString());
            initCode.append(String.format("\nImageRegistry.put(new Card(Ranks.%s, Suits.%s, 0), "
                  + "\n\tnew ImageCallback(){ public AbstractImagePrototype getImage(Object self){ "
                  + "\n\t\treturn ((Bundle)self).get%s();" + "\n\t}\n});", r.name(), s.fullString(), cardString));
            declareCode.append(String.format("\n@Resource(\"%s\")\n" + "AbstractImagePrototype get%s();\n", imagePath, cardString));
         }
      }
      
      verbose = false;
      if (verbose)
      {
         System.out.println(declareCode);
         System.out.println("\n\n");
         System.out.println(initCode);
      }
   }
   
   private static void createImage(String outputFile, int width, Suits s, Ranks r) throws IOException
   {
      int height = (int) (width * (4.0 / 3.0));
      double sz = getSize(r);
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
      Color suitColor = (s.equals(Suits.Spades) || s.equals(Suits.Clubs)) ? Color.BLACK : Color.RED;
      String fontName = "Trebuchet MS";
      int fontStyle = Font.CENTER_BASELINE;
      String rankLabel = getRankLabel(r);
      
      graphics.setColor(suitColor);
      graphics.setFont(new Font(fontName, fontStyle, (int) (width * 0.7 * Math.pow(sz, 0.7))));
      
      Rectangle2D labelMetrics = graphics.getFontMetrics().getStringBounds(rankLabel, graphics);
      graphics.drawString(rankLabel, (int) (w), (int) (w - 0.75 * labelMetrics.getMinY()));
      // graphics.drawString(rankLabel, (int)((width-w) -
      // (labelMetrics.getMaxX())), (int)(height-w));
      
      Image suitIcon = getImage(s);
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
      
      // graphics.drawString(rankLabel, (int)((width-w) -
      // (labelMetrics.getMaxX())), (int)(height-w));
      graphics.drawString(rankLabel, (int) (w), (int) (w - 0.75 * labelMetrics.getMinY()));
      
      graphics.drawImage(suitIcon, (int) (width * (0.5 - sz)), (int) ((height * 0.5) - (width * sz)), (int) (width * (0.5 + sz)),
            (int) ((height * 0.5) + (width * sz)), 0, 0, suitIcon.getWidth(observer), suitIcon.getHeight(observer), observer);
      
      // image = JAI.create("scale", new ParameterBlock()
      // .addSource(image)
      // .add(0.1F)
      // .add(0.1F)
      // .add(0.0F)
      // .add(0.0F)
      // .add(new InterpolationBicubic(4))
      // , null).getRendering().getAsBufferedImage();
      
      new File(outputFile).getParentFile().mkdirs();
      FileOutputStream stream = new FileOutputStream(outputFile);
      String format = "PNG";
      PNGEncodeParam encodeParam = new PNGEncodeParam.RGB();
      encodeParam.setBitDepth(8);
      ImageEncoder createImageEncoder = ImageCodec.createImageEncoder(format, stream, encodeParam);
      createImageEncoder.encode(image);
      // JAI.create("encode", image, stream, format, null);
   }
   
   private static double getSize(Ranks r)
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
   
   private static String getRankLabel(Ranks r)
   {
      if (r == Ranks.Ten) return "10";
      return r.toString();
   }
   
   private static Image getImage(Suits s) throws IOException
   {
      if (s.equals(Suits.Clubs))
      {
         return JAI.create("stream", new FileSeekableStream(new File(resourcePath + "/input/club.png"))).getAsBufferedImage();
      }
      else if (s.equals(Suits.Spades))
      {
         return JAI.create("stream", new FileSeekableStream(new File(resourcePath + "/input/spade.png"))).getAsBufferedImage();
      }
      else if (s.equals(Suits.Diamonds))
      {
         return JAI.create("stream", new FileSeekableStream(new File(resourcePath + "/input/diamond.png"))).getAsBufferedImage();
      }
      else if (s.equals(Suits.Hearts))
      {
         return JAI.create("stream", new FileSeekableStream(new File(resourcePath + "/input/heart.png"))).getAsBufferedImage();
      }
      else
      {
         throw new RuntimeException();
      }
   }
   
}
