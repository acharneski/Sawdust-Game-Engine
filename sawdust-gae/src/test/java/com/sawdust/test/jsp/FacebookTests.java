package com.sawdust.test.jsp;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;
import com.sawdust.engine.controller.entities.Account;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.engine.model.GameType;
import com.sawdust.engine.model.LoadedDeck;
import com.sawdust.engine.model.players.Participant;
import com.sawdust.engine.model.players.Player;
import com.sawdust.engine.view.AccessToken;
import com.sawdust.engine.view.cards.Ranks;
import com.sawdust.engine.view.cards.Suits;
import com.sawdust.engine.view.config.GameConfig;
import com.sawdust.engine.view.game.Message;
import com.sawdust.gae.datastore.DataStore;
import com.sawdust.gae.datastore.Games;
import com.sawdust.gae.datastore.entities.GameSession;
import com.sawdust.gae.facebook.FacebookUserLogic;
import com.sawdust.gae.jsp.DataAdhocBean;
import com.sawdust.gae.logic.GameTypes;
import com.sawdust.gae.logic.SessionToken;
import com.sawdust.gae.logic.User;
import com.sawdust.gae.logic.User.UserTypes;
import com.sawdust.games.blackjack.BlackjackGame;
import com.sawdust.games.blackjack.BlackjackGameType;
import com.sawdust.test.Util;
import com.sawdust.test.appengine.MockEnvironment;

public class FacebookTests extends TestCase
{
    private static final String sessionKey = "2.TcXFza3o9UEae_By1ahldQ__.86400.1262656800-100000080240659";
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
    public void testTextbookPost() throws Exception
    {
        Message message = new Message("Visit us at http://sawdust-games.appspot.com/");
        message.fbAttachment = "{'name':'Google','href':'http://www.google.com/','description':'Google Home Page'}";
        logic.publishActivity(message);
    }
    
    @Test(timeout = 10000)
    public void testActivityPost() throws Exception
    {
        Message message = new Message("Visit us at http://sawdust-games.appspot.com/");
        String mediaData = String.format("[{'type':'image','src':'%s','href':'%s'}]",
                "http://sawdust-games.appspot.com/media/poker.png",
                "http://apps.facebook.com/sawdust-games/quickPlay.jsp?game=Go");
        String attachmentData = String.format("{'name':'Join My Game','media':%s,'href':'%s','description':'%s'}", 
                mediaData,
                "http://apps.facebook.com/sawdust-games/",
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
