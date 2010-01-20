package com.sawdust.gwt.art.go1;

import com.google.gwt.user.client.ui.ImageBundle;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.sawdust.gwt.art.GwtSawdustArt;

public enum enumArt implements GwtSawdustArt
{
    highlight
    {
        public AbstractImagePrototype getImage()
        {
            return singleton.instance().getHighlight();
        }

        public String getId()
        {
            return "GO:HIGHLIGHT";
        }

    },
    board
    {
        public AbstractImagePrototype getImage()
        {
            return singleton.instance().getBoard();
        }

        public String getId()
        {
            return "GO:BOARD";
        }

    },
    black
    {
        public AbstractImagePrototype getImage()
        {
            return singleton.instance().getBlack();
        }

        public String getId()
        {
            return "GO:BLACK";
        }

    },
    white
    {
        public AbstractImagePrototype getImage()
        {
            return singleton.instance().getWhite();
        }

        public String getId()
        {
            return "GO:WHITE";
        }

    }
}