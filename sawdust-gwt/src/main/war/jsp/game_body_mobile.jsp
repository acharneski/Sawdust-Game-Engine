<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ page import="java.lang.String"%>
<%@ page import="java.util.Enumeration"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.HashSet"%>
<%@ page import="com.sawdust.gae.SawdustGameService_Google"%>
<%@ page import="com.sawdust.engine.model.GameType"%>
<%@ page import="com.sawdust.engine.view.config.GameConfig"%>
<%@ page import="com.sawdust.engine.view.config.GameModConfig"%>
<%@ page import="com.sawdust.gae.logic.GameTypes"%>
<%@ page import="com.sawdust.engine.view.config.PropertyConfig"%>
<%@ page import="com.sawdust.engine.view.config.PropertyConfig.PropertyType"%>

<jsp:useBean id="user" class="com.sawdust.gae.jsp.JspUser" />
<jsp:setProperty property="request" name="user" value="<%=request%>" />

<%
    String gameName = request.getParameter("game");
    GameType game = null;
 	// TODO: Re-enable Java 5 support in JSP
	for(int i=0;i<GameTypes.values().length;i++) {
	   GameType thisgame = (GameType) GameTypes.values()[i];
        if (thisgame.getID().equals(gameName))
        {
		    game = thisgame;
		    break;
        }
    }
    boolean t1 = null != user.getEmail();
    String t2 = user.getLoginUrl();
    GameConfig gameConfig = game.getPrototypeConfig(user.getAccount());
%>

<h1><%=game.getName()%></h1>
        
<%=game.getDescription()%>

<h2>Join an existing game:</h2>
<div class="sdge-game-listing">
    <jsp:include page="/jsp/openGameListing_mobile.jsp" />
</div>

<h2>Or make your own:</h2>

<%
%><form method="post"><table><%
String id;
boolean isSpecified = true;
HashSet<String> params = new HashSet<String>();
Enumeration parameterNames = request.getParameterNames();
while(parameterNames.hasMoreElements())
{
    params.add((String) parameterNames.nextElement());
}
// TODO: Re-enable Java 5 support in JSP
java.util.Iterator j = gameConfig.getProperties().values().iterator();
while(j.hasNext()) {
   PropertyConfig property = (PropertyConfig) j.next();
//for (PropertyConfig property : gameConfig.getProperties().values()) {
    id = property.key;
    if(params.contains(id))
    {
        property.value = request.getParameter(id);
    }
    else
    {
        %><!-- Not Specified: <%=id%> --><%
        if(PropertyType.Boolean != property.type) isSpecified = false;
        property.value = property.defaultValue;
    }
    %><tr><%
    %><td><%=property.key + ":&nbsp "%></td><%
    %><td><% 
    if (PropertyType.Text == property.type)
    {
        %><input type="text" name="<%=id%>" value="<%=property.value%>"/><%
    }
    else if (PropertyType.Number == property.type)
    {
        %><input type="text" name="<%=id%>" value="<%=property.value%>"/><%
    }
    else if (PropertyType.Boolean == property.type)
    {
        %><input type="checkbox" name="<%=id%>" checked="<%=property.value.equals(property.TRUE)%>"/><%
        property.value = property.value.equals("on")?property.TRUE:property.FALSE;
    }
    else
    {
        throw new RuntimeException("Upsupported Type: " + property.type);
    }
    %></td><%
    %></tr><%
}
// TODO: Re-enable Java 5 support in JSP
   java.util.Iterator k = gameConfig.getModules().iterator();
   while(k.hasNext()) {
      GameModConfig modConfig = (GameModConfig) k.next();
//for (GameModConfig modConfig : gameConfig.getModules()) {
    String moduleID = modConfig.getName().replaceAll("\\W","");
    %><tr><%
    %><td><%=modConfig.getName() + ":&nbsp "%></td><%
    %><td><%
    id = moduleID + "_enabled";
    if(params.contains(id))
    {
        modConfig.setEnabled(request.getParameter(id).equals("on"));
    }
	%><input type="checkbox" name="<%=id%>" checked="<%=modConfig.isEnabled()%>"/><br/><%
    %><table><%
	 // TODO: Re-enable Java 5 support in JSP
    java.util.Iterator l = modConfig.getProperties().values().iterator();
    while(l.hasNext()) {
       PropertyConfig property = (PropertyConfig) l.next();
	//for (PropertyConfig property : modConfig.getProperties().values()) {
	    id = moduleID + "_" + property.key;
	    if(params.contains(id))
	    {
	        property.value = request.getParameter(id);
	    }
	    %><tr><%
	    %><td><%=property.key + ":&nbsp "%></td><%
	    %><td><% 
	    if (PropertyType.Text == property.type)
	    {
	        %><input type="text" name="<%=id%>" value="<%=property.value%>" /><%
	    }
	    else if (PropertyType.Number == property.type)
	    {
	        %><input type="text" name="<%=id%>" value="<%=property.value%>" /><%
	    }
	    else if (PropertyType.Boolean == property.type)
	    {
	        %><input type="checkbox" name="<%=id%>"  checked="<%=property.value.equals(property.TRUE)%>"/><%
	        property.value = property.value.equals("on")?property.TRUE:property.FALSE;
	    }
	    else
	    {
	        throw new RuntimeException("Upsupported Type: " + property.type);
	    }
	    %></td><%
	    %></tr><%
	}
	%></table></td><%
    %></tr><%
}
    %><tr><td colspan="2"><input type="submit" value="Create Game"/></td></tr><%

    %><tr><td colspan="2"><%
    if(isSpecified)
    {
        String uri = SawdustGameService_Google.createGame2(
                user.getAccessToken(),
                game, 
                gameConfig,
                request,
                response).getRedirectUrl().replaceAll("/[pf]/","/m/");
        %><a href="<%=uri%>">Game Created</a><%
        response.sendRedirect(uri);
    }
    %></td></tr><%
%></table></form><%
%>
