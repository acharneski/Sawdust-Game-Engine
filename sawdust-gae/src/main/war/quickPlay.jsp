<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page errorPage="/error.jsp"%>

<%@ page import="java.lang.String"%>
<%@ page import="java.util.Enumeration"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.HashSet"%>
<%@ page import="com.sawdust.server.appengine.SawdustGameService_Google"%>
<%@ page import="com.sawdust.engine.game.GameType"%>
<%@ page import="com.sawdust.engine.common.config.GameConfig"%>
<%@ page import="com.sawdust.engine.common.config.GameModConfig"%>
<%@ page import="com.sawdust.server.logic.GameTypes"%>
<%@ page import="com.sawdust.engine.common.config.PropertyConfig"%>
<%@ page import="com.sawdust.engine.common.config.PropertyConfig.PropertyType"%>

<%
    com.sawdust.engine.service.debug.RequestLocalLog.Instance.clear();
%>

<jsp:useBean id="user" class="com.sawdust.server.jsp.JspUser" scope="request" />
<jsp:setProperty name="user" property="request" value="<%=request%>"/>

<%
    boolean isGameValid = true;
	if (null == request.getParameter("game"))
	{
		isGameValid = false;
	} else if (request.getParameter("game").isEmpty())
	{
		isGameValid = false;
	}
    String gameName = request.getParameter("game");
    GameType game = null;
 	// TODO: Re-enable Java 5 support in JSP
	for(int i=0;i<GameTypes.values().length;i++) {
	   GameType thisgame = (GameType) GameTypes.values()[i];
    //for (GameType thisgame : GameTypes.values()) {
        if (thisgame.getID().equals(gameName))
        {
		    game = thisgame;
		    break;
        }
    }

    String tutorialParam = request.getParameter("tut");
    if (null != tutorialParam && !tutorialParam.isEmpty())
    {
        int tutorialNumber = Integer.parseInt(tutorialParam);
        game = (GameType) game.getTutorialSequence().get(tutorialNumber);
    }
    
    boolean t1 = null != user.getEmail();
    String t2 = user.getLoginUrl();
    GameConfig gameConfig = game.getPrototypeConfig(user.getAccount());
    String id;
    HashSet<String> params = new HashSet<String>();
    Enumeration parameterNames = request.getParameterNames();
    while (parameterNames.hasMoreElements())
    {
        params.add((String) parameterNames.nextElement());
    }
  	// TODO: Re-enable Java 5 support in JSP
  	java.util.Iterator j = gameConfig.getProperties().values().iterator();
  	while(j.hasNext()) {
  	 PropertyConfig property = (PropertyConfig) j.next();
    //for (PropertyConfig property : gameConfig.getProperties().values()) {
        id = property.key;
        if (params.contains(id))
        {
    property.value = request.getParameter(id);
        }
        else
        {
    property.value = property.defaultValue;
        }
        if (PropertyType.Boolean == property.type)
        {
    property.value = property.value.equals("on") ? property.TRUE : property.FALSE;
        }
    }
   	// TODO: Re-enable Java 5 support in JSP
   	java.util.Iterator k = gameConfig.getModules().iterator();
   	while(k.hasNext()) {
   	   GameModConfig modConfig = (GameModConfig) k.next();
    //for (GameModConfig modConfig : gameConfig.getModules()) {
        String moduleID = modConfig.getName().replaceAll("\\W", "");
        id = moduleID + "_enabled";
        if (params.contains(id))
        {
    modConfig.setEnabled(request.getParameter(id).equals("on"));
        }
     	// TODO: Re-enable Java 5 support in JSP
     	java.util.Iterator l = modConfig.getProperties().values().iterator();
     	while(l.hasNext()) {
     	 PropertyConfig property = (PropertyConfig) l.next();
        // for (PropertyConfig property : modConfig.getProperties().values()) {
    id = moduleID + "_" + property.key;
    if (params.contains(id))
    {
        property.value = request.getParameter(id);
    }
    if (PropertyType.Boolean == property.type)
    {
        property.value = property.value.equals("on") ? property.TRUE : property.FALSE;
    }
        }
    }
    String uri = SawdustGameService_Google.createGame2(user.getAccessToken(), game, gameConfig, request, response).getRedirectUrl();
%><a href="<%=uri%>">Game Created</a> (If your browser does not redirect automatically, please click on the "Game Created" link.)<%
    response.sendRedirect(uri);
%>
