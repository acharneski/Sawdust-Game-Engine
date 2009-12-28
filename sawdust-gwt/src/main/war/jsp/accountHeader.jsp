<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.sawdust.server.jsp.JspLib"%>
<%@ page import="com.sawdust.server.datastore.entities.Account"%>
<%@ page import="java.net.URLEncoder"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@page import="java.net.URLEncoder"%>
<jsp:useBean id="user" class="com.sawdust.server.jsp.JspUser"/>
<jsp:setProperty name="user" property="request" value="<%=request%>"/>
<jsp:setProperty name="user" property="response" value="<%=response%>"/>

<c:choose>
<c:when test="<%=false == user.isGuest()%>">
    Hello, <a href="/accountInfo.jsp"><%=user.getEmail()%></a>! <br />
	<c:if test="<%=null != user.getLogoutUrl()%>">
		<a href="<%=user.getLogoutUrl()%>">sign out</a> <br />
	</c:if>
	<a href="/play.jsp<%=user.getSessonJunk(true,false)%>">You have <span id="openGames"><%=user.getAccount().getSessionKeys().size()%></span> open games</a><br/>
	<a href="/bankLog.jsp?id=<%=URLEncoder.encode(user.getAccount().getUserId())%>">You have <span id="bank"><%=user.getAccount().getBalance()%></span> credits</a>
</c:when>
<c:when test="<%=null != user.getEmail()%>">
    Welcome, honored guest! <br />
	<a href="<%=user.getLoginUrl()%>">Sign in with your Google account!</a> <br/>
	<a href="/play.jsp<%=user.getSessonJunk(true,false)%>">You have <span id="openGames"><%=user.getAccount().getSessionKeys().size()%></span> open games</a><br/>
	<a href="/bankLog.jsp?id=<%=URLEncoder.encode(user.getAccount().getUserId())%>">You have <span id="bank"><%=user.getAccount().getBalance()%></span> credits</a>
</c:when>
<c:otherwise>
	<a href="<%=user.getLoginUrl()%>">Sign in!</a> 
</c:otherwise>
</c:choose>


