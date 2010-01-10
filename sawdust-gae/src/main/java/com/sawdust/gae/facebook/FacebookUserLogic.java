/**
 * 
 */
package com.sawdust.gae.facebook;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.sawdust.engine.view.game.Message;
import com.sawdust.gae.logic.UserLogic;

public final class FacebookUserLogic extends UserLogic
{
    private static final Logger LOG = Logger.getLogger(FacebookUserLogic.class.getName());

    private final String userId;
    private final String apiSecretId;
    private final String sessionKey; 
    private final String apiKey;

    public FacebookUserLogic(final String sessionKey, final String apiKey, String userId, String apiSecretId)
    {
        LOG.info(String.format("FaceookUserLogic(final String sessionKey (%s), final String apiKey (%s), String userId (%s), String apiSecretId (%s))", sessionKey, apiKey, userId, apiSecretId));
        this.sessionKey = sessionKey;
        this.apiKey = apiKey;
        this.userId = userId;
        this.apiSecretId = apiSecretId;
    }

    @Override
    public String toString()
    {
        return "Facebook#"+userId;
    }

    @Override
    public void publishActivity(Message message)
    {
        FacebookUser.postUserActivity(sessionKey, apiKey, userId, apiSecretId, message);
    }

    public String getInfo()
    {
        return FacebookUser.getUserName(sessionKey, apiKey, userId, apiSecretId);
    }
}