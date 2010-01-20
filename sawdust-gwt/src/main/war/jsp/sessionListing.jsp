<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ page import="com.sawdust.gae.jsp.JspLib"%>
<%@ page import="com.sawdust.gae.datastore.entities.GameSession"%>
<%@ page import="com.sawdust.engine.controller.entities.GameSession.SessionStatus"%>

<jsp:useBean id="user" class="com.sawdust.gae.jsp.JspUser" scope="request"/><jsp:setProperty name="user" property="request" value="<%=request%>"/><jsp:setProperty name="user" property="response" value="<%=response%>"/>
<div class="sdge-game-listing">
<h2>Open Game Sessions:</h2>
<%
	// TODO: Re-enable Java 5 support in JSP
	java.util.Iterator i = JspLib.Instance.getUserSessions(user).iterator();
	while(i.hasNext()) {
	   GameSession gameSession = (GameSession) i.next();
    //for (GameSession gameSession : JspLib.Instance.getUserSessions(user)) {
        %><%=gameSession.getHtml(user.getAccount().getInterfacePreference())%><br /><%
	}
%>

</div>