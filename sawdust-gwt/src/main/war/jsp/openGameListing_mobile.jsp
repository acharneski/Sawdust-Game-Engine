<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.sawdust.server.jsp.JspLib"%>
<%@ page import="com.sawdust.server.datastore.entities.GameSession"%>
<%@ page import="com.sawdust.server.datastore.entities.Account.InterfacePreference"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:useBean id="user" class="com.sawdust.server.jsp.JspUser" />
<jsp:setProperty name="user" property="request" value="<%=request%>" />

<%
    boolean showGameListing = true;
    String gameName = request.getParameter("game");
    if (null == gameName)
    {
        showGameListing = false;
    }
    else if (gameName.isEmpty())
    {
        showGameListing = false;
    }
    boolean anythingShown = false;
 	// TODO: Re-enable Java 5 support in JSP
 	java.util.Iterator i = JspLib.Instance.getGameSessions(user, gameName).iterator();
 	while(i.hasNext()) {
 	  GameSession gameSession = (GameSession) i.next();
    //for (GameSession gameSession : JspLib.Instance.getGameSessions(user, gameName)) {
        %><%=gameSession.getHtml(InterfacePreference.Mobile)%><%
        anythingShown = true;
    }
    if (!anythingShown)
    {
        %><strong>No Games Found</strong><%
    }
%>
