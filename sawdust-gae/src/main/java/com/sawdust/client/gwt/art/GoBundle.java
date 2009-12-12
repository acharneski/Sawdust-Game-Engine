package com.sawdust.client.gwt.art;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;

public interface GoBundle extends ImageBundle
{

    @Resource("go1/black.png")
    AbstractImagePrototype getBlack();

    @Resource("go1/board.png")
    AbstractImagePrototype getBoard();

    @Resource("go1/highlight.png")
    AbstractImagePrototype getHighlight();

    @Resource("go1/white.png")
    AbstractImagePrototype getWhite();
}
