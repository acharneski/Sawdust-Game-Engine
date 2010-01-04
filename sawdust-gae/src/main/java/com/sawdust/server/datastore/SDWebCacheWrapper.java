/**
 * 
 */
package com.sawdust.server.datastore;

import java.io.Serializable;
import java.util.logging.Logger;

import com.sawdust.engine.service.HttpInterface;
import com.sawdust.engine.service.HttpResponse;
import com.sawdust.engine.service.Util;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.server.datastore.entities.SDWebCache;

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