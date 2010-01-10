package com.sawdust.client.gwt.util;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwittit.client.facebook.ConnectState;
import com.gwittit.client.facebook.FacebookApi;
import com.gwittit.client.facebook.FacebookConnect;
import com.gwittit.client.facebook.entities.ActionLink;
import com.gwittit.client.facebook.entities.Attachment;
import com.sawdust.engine.view.game.Message;

public class FacebookLogic
{
    public static String API_KEY = "5edf837a505a788ed8fabdb3a2d42143";
    private static boolean _isInit = false;
    private static FacebookApi apiClient = null;
    private static boolean loggedIn = false; 

    public static void postActivity(Message m)
    {
        init();
        if(!loggedIn)
        {
            //Window.alert("Invalid Facebook Session");
            return;
        }
        Attachment attachment = Attachment.fromJson(m.fbAttachment);
        List<ActionLink> actionLinks = null;
        String targetId = null;
        String userMessagePrompt = null;
        Boolean autoPublish = false;
        String actorId = null;
        boolean showDialog = true;
        AsyncCallback<JavaScriptObject> callback = new AsyncCallback<JavaScriptObject>()
        {
            
            @Override
            public void onSuccess(JavaScriptObject result)
            {
            }
            
            @Override
            public void onFailure(Throwable caught)
            {
                //Window.alert("Failed posting activity");
            }
        };
        apiClient.streamPublish(m.getText(), attachment, actionLinks, targetId, userMessagePrompt, autoPublish, actorId, showDialog, callback);
    }

    public static void init()
    {
     if(!_isInit )
     {
         //Window.alert("Init FacebookLogic");
         _isInit = true;
         apiClient = GWT.create ( FacebookApi.class );
         FacebookConnect.init ( API_KEY );
         //FacebookConnect.init ( API_KEY, "http://sawdust-games.appspot.com/xd_receiver.htm");
         FacebookConnect.waitUntilStatusReady (new AsyncCallback<ConnectState>()
         {
            @Override
            public void onSuccess(ConnectState result)
            {
                if(result == ConnectState.connected)
                {
                    loggedIn = true;
                    //Window.alert("Facebook activity postings standing by!");
                }
                else
                {
                    //Window.alert("Could not authenticate with facebook services");
                }
            }
            
            @Override
            public void onFailure(Throwable caught)
            {
                //Window.alert("Could not initialize facebook module");
            }
        });
         
     }
    }

}
