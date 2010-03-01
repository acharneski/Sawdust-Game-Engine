package com.sawdust.gwt.art.cards1;

import com.google.gwt.user.client.ui.ImageBundle;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.sawdust.gwt.art.GwtSawdustArt;

public enum enumArt implements GwtSawdustArt
{
    faceDown
    {
        public AbstractImagePrototype getImage()
        {
            return singleton.instance().getFaceDown();
        }

        public String getId()
        {
            return "VR";
        }

    }
}