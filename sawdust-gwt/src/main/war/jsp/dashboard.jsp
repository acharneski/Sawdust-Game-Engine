<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@page import="java.net.URLEncoder"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="com.sawdust.gae.jsp.JspLib"%>
<%@ page import="com.sawdust.gae.datastore.entities.Account"%>
<%@ page import="java.net.URLEncoder"%>

<jsp:useBean id="user" class="com.sawdust.gae.jsp.JspUser" scope="request"/><jsp:setProperty name="user" property="request" value="<%=request%>"/><jsp:setProperty name="user" property="response" value="<%=response%>"/>


<c:choose>
<c:when test="<%=null != user.getEmail()%>">
	<a href="/play.jsp<%=user.getSessonJunk(true,false)%>">You have <span id="openGames"><%=user.getAccount().getSessionKeys().size()%></span> open games</a><br/>
	<a href="/bankLog.jsp?id=<%=URLEncoder.encode(user.getAccount().getUserId())%>">You have <span id="bank"><%=user.getAccount().getBalance()%></span> credits</a>
</c:when>
<c:otherwise>
	<a href="<%=user.getLoginUrl()%>">Sign in!</a> 
</c:otherwise>
</c:choose>


