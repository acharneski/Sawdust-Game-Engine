<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>

<%@ page import="java.lang.String"%>
<%@ page import="java.util.List"%>
<%@ page import="com.sawdust.engine.model.GameType"%>
<%@ page import="com.sawdust.engine.controller.Util"%>
<%@ page import="com.sawdust.gae.logic.GameTypes"%>

<jsp:useBean id="user" class="com.sawdust.gae.jsp.JspUser" scope="request"/><jsp:setProperty name="user" property="request" value="<%=request%>"/><jsp:setProperty name="user" property="response" value="<%=response%>"/>
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
