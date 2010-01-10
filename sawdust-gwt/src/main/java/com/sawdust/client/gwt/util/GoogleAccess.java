package com.sawdust.client.gwt.util;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.sawdust.engine.view.AccessToken;

public class GoogleAccess
{
    public static AccessToken getAccessToken(final RootPanel rootPanel)
    {
        final AccessToken returnValue = new AccessToken();
        // String userOverride = rootPanel.getElement().getAttribute("user");
        final String userOverride = Window.Location.getParameter("user");
        if ((null != userOverride) && !userOverride.isEmpty())
        {
            returnValue.setUserId(userOverride);
        }

        final String sessionOverride = rootPanel.getElement().getAttribute("session");
        // String sessionOverride = Window.Location.getParameter("session");
        if ((null != sessionOverride) && !sessionOverride.isEmpty())
        {
            returnValue.setSessionId(sessionOverride);
        }
        return returnValue;
    }

}
