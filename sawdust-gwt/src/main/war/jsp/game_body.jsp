<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>

<%@ page import="java.lang.String"%>
<%@ page import="java.util.List"%>
<%@ page import="com.sawdust.engine.game.GameType"%>
<%@ page import="com.sawdust.engine.service.Util"%>
<%@ page import="com.sawdust.server.logic.GameTypes"%>

<jsp:useBean id="user" class="com.sawdust.server.jsp.JspUser" />
<jsp:setProperty property="request" name="user" value="<%=request%>" />

<%@ page import="java.util.logging.Logger" %>
<%
    final Logger LOG = Logger.getLogger("game_body");
%>

<%
    GameType game = null;
    String gameName = request.getParameter("game");
	// TODO: Re-enable Java 5 support in JSP
    //for (GameType game : GameTypes.values()) {
	for(int i=0;i<GameTypes.values().length;i++) {
        game = (GameType) GameTypes.values()[i];
        if (game.getID().equals(gameName))
        {
            break;
        }
        else
        {
            game = null;
        }
	}
    String quickPlayUrl = "/quickPlay.jsp?"+ user.getSessonJunk(false,true) + "game="+game.getID();
%>


<h1><%=game.getName()%></h1>
<div id="gameCreator-quickLaunch"></div>
<%=game.getDescription()%>

<h2>Learn the game:</h2>
<% 
    List<GameType> tutorials = game.getTutorialSequence();
    int index=0;
    int cnt = 0;
    // TODO: Re-enable Java 5 support in JSP
    //for(GameType t : tutorials) {
    java.util.Iterator i = tutorials.iterator();
    while(i.hasNext()) {
      GameType t = (GameType) i.next();
       %>
       <wc:gameType gameObj="<%=t%>" tutorialNumber="<%=Integer.toString(cnt++)%>"></wc:gameType>
<%
    }
%>

<h2>Join an open game:</h2>
<%
    boolean t1 = null != user.getEmail();
    String t2 = user.getLoginUrl();
    try
	{
%>
<div class="sdge-game-listing">
    <jsp:include page="/jsp/openGameListing.jsp" />
</div>
<%
	}
    catch(Throwable e)
    {
        LOG.warning(Util.getFullString(e));
        %>Error loading existing games...<%
    }
%>

<h2>Create a new game:</h2>
<div id="gameCreator-main" width="100%" height="500px">Loading Game Creator...</div>
