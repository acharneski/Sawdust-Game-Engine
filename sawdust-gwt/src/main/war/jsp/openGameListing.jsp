<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sawdust.server.jsp.JspLib" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.sawdust.server.datastore.entities.GameSession" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="user" class="com.sawdust.server.jsp.JspUser" />
<jsp:setProperty name="user" property="request" value="<%=request%>" />

<%
    String showGameListing = null;
    String gameName = request.getParameter("game");
    if (null == gameName)
    {
        showGameListing = "Invalid Game";
    }
    else if (gameName.isEmpty())
    {
        showGameListing = "Game Name is required";
    }
    else if (null == user.getEmail() || null == user.getAccount())
    {
        showGameListing = "Sign in to browse open games!";
    }
%>
<c:choose>
	<c:when test="<%=null == showGameListing%>">
		<%
		    boolean anythingShown = false;
		    ArrayList<GameSession> games = JspLib.Instance.getGameSessions(user, gameName);
		    String txt = "";
		    if (null != games)
		    {
		     	// TODO: Re-enable Java 5 support in JSP
		     	java.util.Iterator i = games.iterator();
		     	while(i.hasNext()) {
		     	  GameSession gameSession = (GameSession) i.next();
	            //for (GameSession gameSession : games) {
	                if (null == gameSession) continue;
	                try
	                {
	                   txt += gameSession.getHtml(user.getAccount().getInterfacePreference());
	                   anythingShown = true;
	                }
	                catch(Throwable e)
	                {
	                    // Ignore
	                }
	            }
		    }
		    if (txt.isEmpty())
		    {
		        %><strong>No Games Found</strong><%
		    }
		    else
		    {
                %><strong><%=txt%></strong><%
		    }
		%>
	</c:when>
	<c:otherwise>
		<%=showGameListing%>
	</c:otherwise>
</c:choose>