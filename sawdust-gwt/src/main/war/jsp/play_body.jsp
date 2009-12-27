<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>

<%@ page import="com.sawdust.engine.game.BaseGame"%>
<%@ page import="com.sawdust.server.jsp.JspLib"%>
<%@ page import="com.sawdust.server.datastore.entities.GameSession"%>
<%@ page import="com.sawdust.server.datastore.entities.TinySession"%>
<%@ page import="com.sawdust.engine.game.Game" %>
<%@ page import="com.sawdust.server.datastore.entities.Account" %>

<jsp:useBean id="user" class="com.sawdust.server.jsp.JspUser"/>
<jsp:setProperty name="user" property="request" value="<%=request%>"/>

<%
	TinySession tsession = JspLib.Instance.getTinySession(request);
	boolean isSessionDefined = false;
	if(null != tsession && null != tsession.getSessionId())
	{
		isSessionDefined = true;
	}
	String gameDesc = "";
	Account account = user.getAccount();
	GameSession s = isSessionDefined?GameSession.load(tsession.getSessionId(), (null==account)?null:account.getPlayer()):null;
	if(null != s)
	{
		Game game = s.getLatestState();
		if(null != game)
		{
		    gameDesc = game.getConfig().getGameDescription();
		}
	}
%>


<%@page import="com.sawdust.server.datastore.entities.GameSession"%><c:choose>
	<c:when test="<%=isSessionDefined%>">
		<iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
		<div id="cardTable" width="100%" height="500px" session="<%=tsession.getSessionId()%>">
		  Game Client Loading...
		</div>
          <div style="display:none;"><%=gameDesc%></div>
	</c:when>
	<c:otherwise>
		ERROR: No session id found
	</c:otherwise>
</c:choose>
