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

        @XmlAttribute
        int prisoners;

        @SuppressWarnings("unused")
        private Score()
        {
            super();
        }

        public Score(String name, int prisoners, int territory)
        {
            super();
            this.name = name;
            this.territory = territory;
            this.prisoners = prisoners;
        }

        @Override
        public int compareTo(Score o)
        {
            return this.name.compareTo(o.name);
        }
    }
    
    @XmlElement
    TreeSet<Score> player = new TreeSet<Score>();

    static final GoPlayer NON_INITIALIZED = null;

    public XmlGoBoard()
    {}
    
    public XmlGoBoard(final GoBoard b)
    {
        board = new XmlBoard(b.board);
        for(GoPlayer p : b.getPlayers())
        {
            GoScore scoreObj = b.getScore(p);
            player.add(new Score(p.getName(), scoreObj.prisoners,scoreObj.territory));
        }
    }

}
