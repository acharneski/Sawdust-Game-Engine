<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient" %>
<%@ page import="com.sawdust.server.jsp.JspLib"%>
<%@ page import="com.sawdust.server.logic.User"%>
<%@ page errorPage="/error.jsp"%>

<%
User.clearCookieLogin(response);
String redirectUrl = request.getParameter("redirect");
if(null == redirectUrl || redirectUrl.isEmpty())
{
	redirectUrl = "/";
}
response.sendRedirect(redirectUrl);
%>
