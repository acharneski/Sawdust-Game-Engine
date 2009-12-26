/**
 * 
 */
package com.sawdust.server.logic;

public class FacebookSite
{
    
    @Override
    public String toString()
    {
        return "Site [facebookSite=" + facebookSite + ", jspGatewaySite=" + jspGatewaySite + ", subVersion=" + subVersion + "]";
    }

    public final String apiSecretKey;
    public final String facebookSite;
    public final String jspGatewaySite;
    public final String subVersion;

    public FacebookSite(final String papiSecretKey, final String pjspGatewaySite, final String pfacebookSite, final String v)
    {
        super();
        apiSecretKey = papiSecretKey;
        jspGatewaySite = pjspGatewaySite;
        facebookSite = pfacebookSite;
        subVersion = v;
    }
}