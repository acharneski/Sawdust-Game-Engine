/**
 * 
 */
package com.sawdust.gae.facebook;

import java.io.Serializable;

public class FacebookSite implements Serializable
{
    protected FacebookSite() {
        
    }
    
    @Override
    public String toString()
    {
        return "Site [facebookSite=" + facebookSite + ", jspGatewaySite=" + jspGatewaySite + ", subVersion=" + subVersion + "]";
    }

    public String apiSecretKey;
    public String facebookSite;
    public String jspGatewaySite;
    public String subVersion;

    public FacebookSite(final String papiSecretKey, final String pjspGatewaySite, final String pfacebookSite, final String v)
    {
        super();
        apiSecretKey = papiSecretKey;
        jspGatewaySite = pjspGatewaySite;
        facebookSite = pfacebookSite;
        subVersion = v;
    }
}