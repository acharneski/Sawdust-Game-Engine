<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>

<%@ page import="com.sawdust.server.jsp.JspLib"%>
<%@ page import="com.sawdust.server.datastore.entities.TinySession"%>

<%
    TinySession tsession = JspLib.Instance.getTinySession(request);
	boolean isSessionDefined = false;
	if(null != tsession && null != tsession.getSessionId())
	{
		isSessionDefined = true;
	}
%>

<c:choose>
	<c:when test="<%=isSessionDefined%>">
		Play this game with 
a <a href="http://sawdust-games.appspot.com/p/<%=tsession.getTinyId()%>" target="_top">large frame</a>, 
a <a href="http://sawdust-games.appspot.com/f/<%=tsession.getTinyId()%>" target="_top">tiny frame</a>, 
a <a href="http://sawdust-games.appspot.com/m/<%=tsession.getTinyId()%>" target="_top">mobile client</a>, 
or on <a href="http://apps.facebook.com/sawdust-games/f/<%=tsession.getTinyId()%>" target="_top">facebook</a>.
	</c:when>
	<c:otherwise>
	</c:otherwise>
</c:choose>
