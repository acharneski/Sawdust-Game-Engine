<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient" %>
<%@ page errorPage="/error.jsp"%>

<%@ page import="com.sawdust.gae.jsp.JspLib"%>
<%@ page import="com.sawdust.gae.logic.User"%>

<%
User.clearCookieLogin(response);
String redirectUrl = request.getParameter("redirect");
if(null == redirectUrl || redirectUrl.isEmpty())
{
	redirectUrl = "/";
}
response.sendRedirect(redirectUrl);
%>
