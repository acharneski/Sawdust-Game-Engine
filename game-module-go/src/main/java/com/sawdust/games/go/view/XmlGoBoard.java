package com.sawdust.games.go.view;

import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.jgap.gp.terminal.False;

import com.sawdust.games.go.controller.GoBoard;
import com.sawdust.games.go.model.GoPlayer;
import com.sawdust.games.go.model.GoScore;


@XmlRootElement
public class XmlGoBoard
{
    @XmlElement
    public
    XmlBoard board;

    @XmlRootElement
    public
    static final class Score implements Comparable<Score>
    {
        @XmlValue
        public
        String name;

        @XmlAttribute
        public
        int territory;

        @XmlAttribute
        public
        int prisoners;

        @XmlAttribute(required=false)
        public boolean winner = false;

        @SuppressWarnings("unused")
        private Score()
        {
            super();
        }

        public Score(String name, int prisoners, int territory, boolean w)
        {
            super();
            this.name = name;
            this.territory = territory;
            this.prisoners = prisoners;
            this.winner = w;
        }

        @Override
        public int compareTo(Score o)
        {
            return this.name.compareTo(o.name);
        }
    }
    
    @XmlElement
    public
    TreeSet<Score> player = new TreeSet<Score>();

    @XmlElement
    public XmlBoard lastboard;

    static final GoPlayer NON_INITIALIZED = null;

    public XmlGoBoard()
    {}
    
    public XmlGoBoard(final GoBoard b)
    {
        board = new XmlBoard(b.board);
        for(GoPlayer p : b.getPlayers())
        {
            GoScore scoreObj = b.getScore(p);
            Score score = new Score(p.getName(), scoreObj.prisoners,scoreObj.territory, p.equals(b.getWinner()));
            player.add(score);
        }
    }

}
