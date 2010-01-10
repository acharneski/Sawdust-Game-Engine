package com.sawdust.server.facebook;

import java.net.URLEncoder;

public class UriComponent
{

    public enum Type
    {
        QUERY_PARAM
    }

    public static String contextualEncode(final String string, final Type query_param, final boolean b)
    {
        String encode = URLEncoder.encode(string);
        return encode;
    };

}
