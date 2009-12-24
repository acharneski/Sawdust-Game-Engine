package com.sawdust.server.datastore.entities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.sawdust.engine.service.Util;
import com.sawdust.engine.service.debug.GameException;
import com.sawdust.server.datastore.DataObj;
import com.sawdust.server.datastore.DataStore;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class SDWebCache extends DataObj
{
    private static final Logger LOG = Logger.getLogger(SDWebCache.class.getName());

    public static SDWebCache getURL(final String urlString) throws GameException
    {
        final PersistenceManager entityManager = DataStore.create();
        final Query newQuery = entityManager.newQuery(SDWebCache.class);
        newQuery.setFilter("url == _url");
        newQuery.declareParameters("String _url");
        SDWebCache queryResult = null;
        try
        {
            List execute = (List)newQuery.execute(urlString);
            if(0 < execute.size()) queryResult = (SDWebCache) execute.get(0);
        }
        catch (Throwable e)
        {
            LOG.warning(Util.getFullString(e));
        }
        if (null == queryResult)
        {
            try
            {
                final URL url = new URL(urlString.replaceAll("#.*", ""));
                HttpURLConnection connection = null;
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                final int responseCode = connection.getResponseCode();
                final InputStream contentStream = (java.io.ByteArrayInputStream) connection.getContent();
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(contentStream));
                final StringBuilder contentSb = new StringBuilder();
                for (int i = 0; i < 10000; i++)
                {
                    String readLine;
                    try
                    {
                        readLine = bufferedReader.readLine();
                    }
                    catch (final IOException e)
                    {
                        e.printStackTrace(System.out);
                        break;
                    }
                    if (null == readLine)
                    {
                        break;
                    }
                    contentSb.append(readLine);
                    // System.out.println(readLine);
                }
                final String string = contentSb.toString();
                if (!string.isEmpty() || (responseCode > 200))
                {
                    queryResult = new SDWebCache(urlString, responseCode, string);
                    LOG.info(String.format("Status code %d from http request %s", responseCode, urlString));
                }
                else if (responseCode > 200)
                {
                    queryResult = new SDWebCache(urlString, responseCode, String.format("ERROR%d", responseCode));
                    LOG.info(String.format("HTTP ERROR: Status code %d from http request %s", responseCode, urlString));
                }
                else
                {
                    queryResult = new SDWebCache(urlString, 900, "FUBAR");
                    LOG.warning(String.format("ERROR: Status code %d from http request %s", responseCode, urlString));
                }
            }
            catch (final MalformedURLException e)
            {
                queryResult = new SDWebCache(urlString, 901, "MalformedURLException");
                LOG.warning(String.format("ERROR: MalformedURLException: %s", Util.getFullString(e)));
            }
            catch (final IOException e)
            {
                queryResult = new SDWebCache(urlString, 901, "IOException");
                LOG.info(String.format("ERROR: IOException: %s", Util.getFullString(e)));
            }
        }
        return (queryResult.getStatus() == 200) ? queryResult : null;
    }

    public static SDWebCache load(final Key key)
    {
        try
        {
            final SDWebCache returnValue = DataStore.Get(SDWebCache.class, key);
            return returnValue;
        }
        catch (final Exception e)
        {
            e.printStackTrace(System.out);
            return null;
        }
    }

    public static SDWebCache load(final String sessionKeyString)
    {
        return SDWebCache.load(KeyFactory.stringToKey(sessionKeyString));
    }

    @Persistent
    private Text content = null;

    @Persistent
    private int status = 0;

    @Persistent
    public Date time = new Date();

    @Persistent
    public String url = "";

    protected SDWebCache()
    {
        super();
    }

    public SDWebCache(final String purl, final int pstatus, final String pcontent) throws GameException
    {
        super(KeyFactory.createKey(SDWebCache.class.getSimpleName(), String.format("%s\\%s", purl, DateFormat.getDateTimeInstance().format(new Date()))));
        url = purl;
        setStatus(pstatus);
        setContent(pcontent);
        if (this != DataStore.Add(this)) throw new AssertionError();
    }

    public String getContent()
    {
        return content.getValue();
    }

    /**
     * @return the _id
     */
    public String getId()
    {
        return KeyFactory.keyToString(getKey());
    }

    public void setContent(final String pcontent)
    {
        content = new Text(pcontent);
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public int getStatus()
    {
        return status;
    }
}
