<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>

<%@ page import="com.sawdust.engine.model.basetypes.BaseGame"%>
<%@ page import="com.sawdust.gae.jsp.JspLib"%>
<%@ page import="com.sawdust.gae.datastore.entities.GameSession"%>
<%@ page import="com.sawdust.gae.datastore.entities.TinySession"%>
<%@ page import="com.sawdust.engine.model.basetypes.GameState" %>
<%@ page import="com.sawdust.gae.datastore.entities.Account" %>

<jsp:useBean id="user" class="com.sawdust.gae.jsp.JspUser" scope="request"/><jsp:setProperty name="user" property="request" value="<%=request%>"/><jsp:setProperty name="user" property="response" value="<%=response%>"/>

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
		GameState game = s.getState();
		if(null != game)
		{
		    gameDesc = game.getConfig().getGameDescription();
		}
	}
%>


<%@page import="com.sawdust.gae.datastore.entities.GameSession"%>
<c:choose>
	<c:when test="<%=isSessionDefined%>">
        <script type="text/javascript" language="javascript" src="http://static.ak.connect.facebook.com/js/api_lib/v0.4/FeatureLoader.js.php"></script>  
		<iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
		<div id="cardTable" width="100%" height="500px" session="<%=tsession.getSessionId()%>">
		  Game Client Loading...
		</div>
        <div style="display:none;"><%=gameDesc%></div>
	</c:when>
	<c:otherwise>
		<h2 style="background:yellow;">We're sorry, but that game doesn't seem to exist anymore. However, you could always start your own game...</h2>
		<jsp:include page="/jsp/welcome.jsp"></jsp:include>
	</c:otherwise>
</c:choose>
