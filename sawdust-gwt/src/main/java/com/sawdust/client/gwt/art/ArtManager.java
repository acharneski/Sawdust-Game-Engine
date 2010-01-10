package com.sawdust.client.gwt.art;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;
import com.sawdust.client.gwt.util.ImageCallback;
import com.sawdust.engine.view.cards.Card;
import com.sawdust.engine.view.cards.Ranks;
import com.sawdust.engine.view.cards.Suits;

public class ArtManager
{
    public static final ArtManager Instance = new ArtManager();
    public final HashMap<String, ImageBundle> ImageBundles = new HashMap<String, ImageBundle>();
    public final HashMap<String, HashMap<String, ImageCallback>> ImageRegistry = new HashMap<String, HashMap<String, ImageCallback>>();
    private LetterBundle letterBundle = null;

    private ArtManager()
    {
       ImageBundles.put("CARD1", (CardBundle) GWT.create(CardBundle.class));
       ImageBundles.put("GO1", (GoBundle) GWT.create(GoBundle.class));
       ImageBundles.put("WORD1", (LetterBundle) GWT.create(LetterBundle.class));
       
        registerCards();
        registerLetters();
        registerGo();
    }

    public AbstractImagePrototype getImage(final String lib, final String id)
    {
        if (ImageRegistry.containsKey(lib))
        {
           HashMap<String, ImageCallback> hashMap = ImageRegistry.get(lib);
           ImageBundle bundle = ImageBundles.get(lib);
           if (hashMap.containsKey(id))
           {
               return (hashMap.get(id)).getImage(bundle);
           }
        }
        System.err.println("ImageRegistry.containsKey(id) == false; id= " + id);
        return null;
    }

    private void ImageRegistry_put(Card card, ImageCallback imageCallback)
    {
       String lib = "CARD1";
       String str = card.CardId();
       ImageRegistry_put(lib, str, imageCallback);
    }

    private void  ImageRegistry_put(String lib, String str, ImageCallback imageCallback)
    {
       if(!this.ImageRegistry.containsKey(lib)) this.ImageRegistry.put(lib, new HashMap<String, ImageCallback>());
       this.ImageRegistry.get(lib).put(str, imageCallback);
    }

     private void ImageRegistry_put(String str, ImageCallback imageCallback)
    {
        String lib = "WORD1";
        if(str.startsWith("GO:")) lib = "GO1";
        ImageRegistry_put(lib, str, imageCallback);
    }

    private void registerCards()
    {
       ImageRegistry_put("CARD1", "VR", new ImageCallback()
       {
           public AbstractImagePrototype getImage(final Object self)
           {
               return ((CardBundle) self).getRedDown();
           }
       });

       ImageRegistry_put(new Card(Ranks.Ace, Suits.Clubs, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getAceClubs();
            }
        });
        ImageRegistry_put(new Card(Ranks.Eight, Suits.Clubs, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getEightClubs();
            }
        });
        ImageRegistry_put(new Card(Ranks.Five, Suits.Clubs, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getFiveClubs();
            }
        });
        ImageRegistry_put(new Card(Ranks.Four, Suits.Clubs, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getFourClubs();
            }
        });
        ImageRegistry_put(new Card(Ranks.Jack, Suits.Clubs, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getJackClubs();
            }
        });
        ImageRegistry_put(new Card(Ranks.King, Suits.Clubs, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getKingClubs();
            }
        });
        ImageRegistry_put(new Card(Ranks.Nine, Suits.Clubs, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getNineClubs();
            }
        });
        ImageRegistry_put(new Card(Ranks.Queen, Suits.Clubs, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getQueenClubs();
            }
        });
        ImageRegistry_put(new Card(Ranks.Seven, Suits.Clubs, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getSevenClubs();
            }
        });
        ImageRegistry_put(new Card(Ranks.Six, Suits.Clubs, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getSixClubs();
            }
        });
        ImageRegistry_put(new Card(Ranks.Ten, Suits.Clubs, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getTenClubs();
            }
        });
        ImageRegistry_put(new Card(Ranks.Three, Suits.Clubs, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getThreeClubs();
            }
        });
        ImageRegistry_put(new Card(Ranks.Two, Suits.Clubs, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getTwoClubs();
            }
        });
        ImageRegistry_put(new Card(Ranks.Ace, Suits.Diamonds, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getAceDiamonds();
            }
        });
        ImageRegistry_put(new Card(Ranks.Eight, Suits.Diamonds, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getEightDiamonds();
            }
        });
        ImageRegistry_put(new Card(Ranks.Five, Suits.Diamonds, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getFiveDiamonds();
            }
        });
        ImageRegistry_put(new Card(Ranks.Four, Suits.Diamonds, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getFourDiamonds();
            }
        });
        ImageRegistry_put(new Card(Ranks.Jack, Suits.Diamonds, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getJackDiamonds();
            }
        });
        ImageRegistry_put(new Card(Ranks.King, Suits.Diamonds, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getKingDiamonds();
            }
        });
        ImageRegistry_put(new Card(Ranks.Nine, Suits.Diamonds, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getNineDiamonds();
            }
        });
        ImageRegistry_put(new Card(Ranks.Queen, Suits.Diamonds, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getQueenDiamonds();
            }
        });
        ImageRegistry_put(new Card(Ranks.Seven, Suits.Diamonds, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getSevenDiamonds();
            }
        });
        ImageRegistry_put(new Card(Ranks.Six, Suits.Diamonds, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getSixDiamonds();
            }
        });
        ImageRegistry_put(new Card(Ranks.Ten, Suits.Diamonds, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getTenDiamonds();
            }
        });
        ImageRegistry_put(new Card(Ranks.Three, Suits.Diamonds, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getThreeDiamonds();
            }
        });
        ImageRegistry_put(new Card(Ranks.Two, Suits.Diamonds, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getTwoDiamonds();
            }
        });
        ImageRegistry_put(new Card(Ranks.Ace, Suits.Hearts, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getAceHearts();
            }
        });
        ImageRegistry_put(new Card(Ranks.Eight, Suits.Hearts, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getEightHearts();
            }
        });
        ImageRegistry_put(new Card(Ranks.Five, Suits.Hearts, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getFiveHearts();
            }
        });
        ImageRegistry_put(new Card(Ranks.Four, Suits.Hearts, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getFourHearts();
            }
        });
        ImageRegistry_put(new Card(Ranks.Jack, Suits.Hearts, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getJackHearts();
            }
        });
        ImageRegistry_put(new Card(Ranks.King, Suits.Hearts, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getKingHearts();
            }
        });
        ImageRegistry_put(new Card(Ranks.Nine, Suits.Hearts, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getNineHearts();
            }
        });
        ImageRegistry_put(new Card(Ranks.Queen, Suits.Hearts, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getQueenHearts();
            }
        });
        ImageRegistry_put(new Card(Ranks.Seven, Suits.Hearts, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getSevenHearts();
            }
        });
        ImageRegistry_put(new Card(Ranks.Six, Suits.Hearts, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getSixHearts();
            }
        });
        ImageRegistry_put(new Card(Ranks.Ten, Suits.Hearts, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getTenHearts();
            }
        });
        ImageRegistry_put(new Card(Ranks.Three, Suits.Hearts, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getThreeHearts();
            }
        });
        ImageRegistry_put(new Card(Ranks.Two, Suits.Hearts, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getTwoHearts();
            }
        });
        ImageRegistry_put(new Card(Ranks.Ace, Suits.Spades, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getAceSpades();
            }
        });
        ImageRegistry_put(new Card(Ranks.Eight, Suits.Spades, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getEightSpades();
            }
        });
        ImageRegistry_put(new Card(Ranks.Five, Suits.Spades, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getFiveSpades();
            }
        });
        ImageRegistry_put(new Card(Ranks.Four, Suits.Spades, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getFourSpades();
            }
        });
        ImageRegistry_put(new Card(Ranks.Jack, Suits.Spades, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getJackSpades();
            }
        });
        ImageRegistry_put(new Card(Ranks.King, Suits.Spades, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getKingSpades();
            }
        });
        ImageRegistry_put(new Card(Ranks.Nine, Suits.Spades, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getNineSpades();
            }
        });
        ImageRegistry_put(new Card(Ranks.Queen, Suits.Spades, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getQueenSpades();
            }
        });
        ImageRegistry_put(new Card(Ranks.Seven, Suits.Spades, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getSevenSpades();
            }
        });
        ImageRegistry_put(new Card(Ranks.Six, Suits.Spades, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getSixSpades();
            }
        });
        ImageRegistry_put(new Card(Ranks.Ten, Suits.Spades, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getTenSpades();
            }
        });
        ImageRegistry_put(new Card(Ranks.Three, Suits.Spades, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getThreeSpades();
            }
        });
        ImageRegistry_put(new Card(Ranks.Two, Suits.Spades, 0), new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((CardBundle) self).getTwoSpades();
            }
        });
    }


   private void registerGo()
    {
        ImageRegistry_put("GO:BOARD", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((GoBundle) self).getBoard();
            }
        });
        ImageRegistry_put("GO:WHITE", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((GoBundle) self).getWhite();
            }
        });
        ImageRegistry_put("GO:BLACK", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((GoBundle) self).getBlack();
            }
        });
        ImageRegistry_put("GO:HIGHLIGHT", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((GoBundle) self).getHighlight();
            }
        });
    }

   private void registerLetters()
    {
        ImageRegistry_put("A", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getA1();
            }
        });
        ImageRegistry_put("A2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getA2();
            }
        });
        ImageRegistry_put("B", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getB1();
            }
        });
        ImageRegistry_put("B2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getB2();
            }
        });
        ImageRegistry_put("C", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getC1();
            }
        });
        ImageRegistry_put("C2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getC2();
            }
        });
        ImageRegistry_put("D", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getD1();
            }
        });
        ImageRegistry_put("D2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getD2();
            }
        });
        ImageRegistry_put("E", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getE1();
            }
        });
        ImageRegistry_put("E2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getE2();
            }
        });
        ImageRegistry_put("F", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getF1();
            }
        });
        ImageRegistry_put("F2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getF2();
            }
        });
        ImageRegistry_put("G", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getG1();
            }
        });
        ImageRegistry_put("G2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getG2();
            }
        });
        ImageRegistry_put("H", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getH1();
            }
        });
        ImageRegistry_put("H2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getH2();
            }
        });
        ImageRegistry_put("I", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getI1();
            }
        });
        ImageRegistry_put("I2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getI2();
            }
        });
        ImageRegistry_put("J", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getJ1();
            }
        });
        ImageRegistry_put("J2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getJ2();
            }
        });
        ImageRegistry_put("K", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getK1();
            }
        });
        ImageRegistry_put("K2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getK2();
            }
        });
        ImageRegistry_put("L", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getL1();
            }
        });
        ImageRegistry_put("L2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getL2();
            }
        });
        ImageRegistry_put("M", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getM1();
            }
        });
        ImageRegistry_put("M2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getM2();
            }
        });
        ImageRegistry_put("N", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getN1();
            }
        });
        ImageRegistry_put("N2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getN2();
            }
        });
        ImageRegistry_put("O", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getO1();
            }
        });
        ImageRegistry_put("O2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getO2();
            }
        });
        ImageRegistry_put("P", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getP1();
            }
        });
        ImageRegistry_put("P2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getP2();
            }
        });
        ImageRegistry_put("Q", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getQ1();
            }
        });
        ImageRegistry_put("Q2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getQ2();
            }
        });
        ImageRegistry_put("R", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getR1();
            }
        });
        ImageRegistry_put("R2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getR2();
            }
        });
        ImageRegistry_put("S", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getS1();
            }
        });
        ImageRegistry_put("S2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getS2();
            }
        });
        ImageRegistry_put("T", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getT1();
            }
        });
        ImageRegistry_put("T2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getT2();
            }
        });
        ImageRegistry_put("U", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getU1();
            }
        });
        ImageRegistry_put("U2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getU2();
            }
        });
        ImageRegistry_put("V", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getV1();
            }
        });
        ImageRegistry_put("V2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getV2();
            }
        });
        ImageRegistry_put("W", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getW1();
            }
        });
        ImageRegistry_put("W2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getW2();
            }
        });
        ImageRegistry_put("X", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getX1();
            }
        });
        ImageRegistry_put("X2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getX2();
            }
        });
        ImageRegistry_put("Y", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getY1();
            }
        });
        ImageRegistry_put("Y2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getY2();
            }
        });
        ImageRegistry_put("Z", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getZ1();
            }
        });
        ImageRegistry_put("Z2", new ImageCallback()
        {
            public AbstractImagePrototype getImage(final Object self)
            {
                return ((LetterBundle) self).getZ2();
            }
        });
    }
}
