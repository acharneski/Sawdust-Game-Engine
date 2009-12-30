package com.sawdust.test.jsp;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;
import com.sawdust.engine.common.AccessToken;
import com.sawdust.engine.common.cards.Ranks;
import com.sawdust.engine.common.cards.Suits;
import com.sawdust.engine.common.config.GameConfig;
import com.sawdust.engine.common.game.Message;
import com.sawdust.engine.game.GameType;
import com.sawdust.engine.game.LoadedDeck;
import com.sawdust.engine.game.blackjack.BlackjackGame;
import com.sawdust.engine.game.blackjack.BlackjackGameType;
import com.sawdust.engine.game.players.Participant;
import com.sawdust.engine.game.players.Player;
import com.sawdust.engine.service.data.Account;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.test.Util;
import com.sawdust.test.appengine.MockEnvironment;
import com.sawdust.server.datastore.DataStore;
import com.sawdust.server.datastore.Games;
import com.sawdust.server.datastore.entities.GameSession;
import com.sawdust.server.facebook.FacebookUserLogic;
import com.sawdust.server.jsp.DataAdhocBean;
import com.sawdust.server.logic.GameTypes;
import com.sawdust.server.logic.SessionToken;
import com.sawdust.server.logic.User;
import com.sawdust.server.logic.User.UserTypes;

public class FacebookTests extends TestCase
{
    private static final String sessionKey = "2.tZAXZasyRH_sUnVMw9lQCw__.86400.1262257200-100000080240659";
    private static final String userId = "100000080240659";

    private static final String apiKey = "5edf837a505a788ed8fabdb3a2d42143";
    private static final String apiSecretId = "48ac90f59799edc11e332278b0f88488";

    static final FacebookUserLogic logic = new FacebookUserLogic(
            sessionKey, 
            apiKey, 
            userId, 
            apiSecretId
            ); 
    
    
    @Test(timeout = 10000)
    public void testActivityPost() throws Exception
    {
        Message message = new Message("Visit us at http://sawdust-games.appspot.com/");
        String mediaData = String.format("[{'type':'image','src':'%s','href':'%s'}]",
                "http://sawdust-games.appspot.com/media/go.png",
                "http://apps.facebook.com/sawdust-games/quickPlay.jsp?game=Go");
        
        //String attachmentData = "{}";
        //String attachmentData = "{'name':'Google','href':'http://www.google.com/','description':'Google Home Page'}";
        //String attachmentData = String.format("{'name':'Join My Game','href':'%s','media':%s,'description':'%s'}", 
        String attachmentData = String.format("{\"name\":\"Join My Game\",\"href\":\"%s\",\"media\":%s,\"description\":\"%s\"}", 
                "http://apps.facebook.com/sawdust-games/",
                mediaData,
                "This is a unit test. Is contains lots of text and should be somewhat long.");
        message.fbAttachment = attachmentData;
        logic.publishActivity(message);
    }

    @Test(timeout = 10000)
    public void testGetInfo() throws Exception
    {
        logic.getInfo();
    }

}
