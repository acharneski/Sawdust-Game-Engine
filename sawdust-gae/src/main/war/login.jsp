
<%@page import="java.net.HttpURLConnection"%><%@page import="java.net.URLEncoder"%><%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient" %>
<%@ page import="com.sawdust.server.jsp.JspLib"%>
<%@ page import="com.sawdust.server.logic.User"%>
<%@ page import="java.util.Map"%>
<%@ page errorPage="/error.jsp"%>

<%com.sawdust.engine.service.debug.RequestLocalLog.Instance.clear();%>

<%
User u = User.getUser(request, response, null);
u.setCookieLogin(response);
%>
<%--
<html>
<head>
</head>
<body>
<a href="<%=JspLib.getRedirectUrl(request)%>">Continue to Sawdust Games</a><hr/>
<jsp:include page="/jsp/dumpRequest.jsp"></jsp:include>
</body>
</html>
--%>
<%
JspLib.redirect(request, response);
%>
