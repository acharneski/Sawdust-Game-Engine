package com.sawdust.gwt.art.go1;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;

public interface xface extends ImageBundle
{
    @Resource("go1/highlight.png")
    AbstractImagePrototype getHighlight();

    @Resource("go1/board.png")
    AbstractImagePrototype getBoard();

    @Resource("go1/black.png")
    AbstractImagePrototype getBlack();

    @Resource("go1/white.png")
    AbstractImagePrototype getWhite();
}