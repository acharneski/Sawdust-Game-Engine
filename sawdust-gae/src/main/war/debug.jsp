<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient" %>
<%@ page import="com.sawdust.server.jsp.JspLib"%>
<%@ page errorPage="/error.jsp"%>

<% com.sawdust.engine.service.debug.RequestLocalLog.Instance.clear(); %>

<html>
<head>
<title>Request Debug Page</title>
</head>
<body>
<%=JspLib.Instance.dumpRequest(request)%>
</body>
</html>