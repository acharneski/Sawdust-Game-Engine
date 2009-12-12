<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ page import="java.lang.String"%>
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
    String gameName = request.getParameter("game");
	// TODO: Re-enable Java 5 support in JSP
	for(int i=0;i<GameTypes.values().length;i++) {
	   GameType game = (GameType) GameTypes.values()[i];
    //for (GameType game : GameTypes.values()) {
        if (!game.getID().equals(gameName)) continue;
%>


<h1><%=game.getName()%></h1>

<div id="gameCreator-quickLaunch"></div>

<%=game.getDescription()%>

<%
    }
    boolean t1 = null != user.getEmail();
    String t2 = user.getLoginUrl();

    try
	{
%>

<h2>Join an existing game:</h2>
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
<h2>Or make your own:</h2>
<div id="gameCreator-main" width="100%" height="500px">Loading Game Creator...</div>
