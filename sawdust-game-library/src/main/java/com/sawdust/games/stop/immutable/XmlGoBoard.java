package com.sawdust.games.stop.immutable;

import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement
public class XmlGoBoard
{
    @XmlElement
    XmlBoard board;

    @XmlRootElement
    static final class Score implements Comparable<Score>
    {
        @XmlValue
        String name;

        @XmlAttribute
        int territory;

        public Score()
        {
            super();
            this.name = name;
            this.territory = territory;
        }

        public Score(String name, int score)
        {
            this();
            this.name = name;
            this.territory = score;
        }

        @Override
        public int compareTo(Score o)
        {
            return this.name.compareTo(o.name);
        }
    }
    
    @XmlElement
    TreeSet<Score> score = new TreeSet<Score>();

    static final Player NON_INITIALIZED = null;

    public XmlGoBoard()
    {}
    
    public XmlGoBoard(final GoBoard b)
    {
        board = new XmlBoard(b.board);
        score.add(new Score("Player 1", 4));
        score.add(new Score("Player 2", 6));
    }

}
