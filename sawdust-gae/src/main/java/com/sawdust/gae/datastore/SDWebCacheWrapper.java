/**
 * 
 */
package com.sawdust.gae.datastore;

import java.io.Serializable;
import java.util.logging.Logger;

import com.sawdust.engine.controller.HttpInterface;
import com.sawdust.engine.controller.HttpResponse;
import com.sawdust.engine.controller.Util;
import com.sawdust.engine.controller.exceptions.GameException;
import com.sawdust.gae.datastore.entities.SDWebCache;

final class SDWebCacheWrapper implements HttpInterface, Serializable
{
    private static final Logger LOG = Logger.getLogger(SDWebCacheWrapper.class.getName());

    SDWebCacheWrapper()
    {
        super();
    }
    
    @Override
    public HttpResponse getURL(String urlString)
    {
        SDWebCache url;
        String content = null;
        try
        {
            url = SDWebCache.getURL(urlString);
            if(null != url) content = url.getContent();
        }
        catch (GameException e)
        {
            LOG.fine(Util.getFullString(e));
        }
        if(null == content) 
        {
            return new HttpResponse("Error", 500);
        }
        return new HttpResponse(content, 200);
    }
}