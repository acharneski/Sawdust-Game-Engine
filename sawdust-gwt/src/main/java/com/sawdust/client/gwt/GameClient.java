package com.sawdust.client.gwt;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.sawdust.client.gwt.util.FacebookLogic;
import com.sawdust.client.gwt.widgets.GameClientWidget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GameClient implements EntryPoint
{

    ArrayList<GameClientWidget> w = new ArrayList<GameClientWidget>();

    /**
     * This is the entry point method.
     */
    public void onModuleLoad()
    {
        final RootPanel rootPanel = RootPanel.get("cardTable");
        if (rootPanel != null)
        {
            rootPanel.getElement().setInnerHTML("");
            w.add(new GameClientWidget(rootPanel));
        }
        //Window.alert("onModuleLoad");
        FacebookLogic.init();
    }

}
