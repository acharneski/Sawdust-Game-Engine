<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.sawdust.server.jsp.JspLib"%>
<%@ page import="com.sawdust.server.datastore.entities.GameSession"%>
<%@ page import="com.sawdust.engine.service.data.GameSession.SessionStatus"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:useBean id="user" class="com.sawdust.server.jsp.JspUser"/>
<jsp:setProperty name="user" property="request" value="<%=request%>"/>

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