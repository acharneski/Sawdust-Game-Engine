<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.sawdust.server.jsp.JspLib"%>
<%@ page import="com.sawdust.server.logic.User"%>
<%@ page import="com.sawdust.server.logic.FacebookUser"%>
<%@ page import="java.net.URLEncoder"%>

<%@page import="com.sawdust.engine.service.debug.RequestLocalLog"%>

<div>

<hr/><br>
<h2>Request Parameters:</h2>
<%
java.util.Enumeration e = request.getParameterNames();
java.util.ArrayList<String> sigs = new java.util.ArrayList<String>();
while (e.hasMoreElements())
{
    String key = (String) e.nextElement();
    String values[] = request.getParameterValues(key);
  	// TODO: Re-enable Java 5 support in JSP
	for(int i=0;i<values.length;i++) {
	   String value = (String) values[i];
    //for(String value : values) {
        %><%=String.format("%s = %s<br>",key, value)%><%
    }
}
%>

<hr/><br>
<h2>Request Cookies:</h2>
<pre>
<%
javax.servlet.http.Cookie[] cookies = request.getCookies();
if (null != cookies)
{
  	// TODO: Re-enable Java 5 support in JSP
  	for(int i=0;i<cookies.length;i++) {
  	 javax.servlet.http.Cookie c = (javax.servlet.http.Cookie) cookies[i];
    //for (javax.servlet.http.Cookie c : cookies) {
        %><%=c.getName()%> - <%=c.getValue()%>
<%
    }
}
%>
</pre>

<hr/><br>
<h2>Facebook Parameters:</h2>
<pre>
<%=FacebookUser.getParamsForTrace(request)%>
</pre>

<hr/><br>
<h2>Request Log:</h2>
<pre>
<%=RequestLocalLog.Instance.getRequestLog()%>
</pre>

</div>