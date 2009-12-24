/**
 * 
 */
package com.sawdust.server.appengine;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.sawdust.engine.game.HttpInterface;
import com.sawdust.engine.game.HttpResponse;
import com.sawdust.engine.game.LanguageProvider;


public final class EnglishLanguageProvider implements LanguageProvider
{
    private static final Logger LOG = Logger.getLogger(EnglishLanguageProvider.class.getName());

    public EnglishLanguageProvider()
    {
        super();
    }
    
    @Override
    public String getUrl(String urlString, HttpInterface theInternet)
    {
        final HttpResponse word2 = theInternet.getURL(urlString);
        if (null == word2) return null;
        return word2.getContent();
    }

    @Override
    public boolean verifyWord(String word, HttpInterface theInternet)
    {
        final String datasource = "http://en.wiktionary.org/wiki/";
        final String urlString = datasource + URLEncoder.encode(word.toLowerCase());
        HttpResponse url = theInternet.getURL(urlString);
        if (null == url) 
        {
            LOG.info(String.format("Word %s invalid: Null response",word, url.getStatusCode()));
            return false;
        }
        if (null == url.getContent()) 
        {
            LOG.info(String.format("Word %s invalid: Null content",word, url.getStatusCode()));
            return false;
        }
        if (400 <= url.getStatusCode()) 
        {
            LOG.info(String.format("Word %s invalid: HTTP status %d",word, url.getStatusCode()));
            return false;
        }
        if (!url.getContent().contains("/wiki/Category:English"))
        {
            LOG.info(String.format("Word %s invalid: Non-english",word, url.getStatusCode()));
            return false;
        }
        return true;
    }

    static final ArrayList<String> __wordLetters = new ArrayList<String>();
    {
        String[] strings = new String[]{
                "a", "b", "c", "d", "e", "f", "g",
                "h", "i", "j", "k", "l", "m", "n",
                "o", "p", "q", "r", "s", "t", "u",
                "v", "w", "x", "y", "z"
        };
        for(String s : strings)
        {
            __wordLetters.add(_normalizeString(s));
        }
    }
    
    private static final ArrayList<String> __spaceLetters = new ArrayList<String>();
    {
        String[] strings = new String[]{
                " ", ".", ",", "!", "-", ";", "?"
        };
        for(String s : strings)
        {
            __spaceLetters.add(_normalizeString(s));
        }
    }
    
    @Override
    public ArrayList<String> getDelimiterCharacterSet()
    {
        return new ArrayList<String>(__spaceLetters);
    }

    @Override
    public ArrayList<String> getWordCharacterSet()
    {
        return new ArrayList<String>(__wordLetters);
    }

    @Override
    public String normalizeString(String b)
    {
        return _normalizeString(b);
    }

    private static String _normalizeString(String b)
    {
        return b.toUpperCase();
    }
}