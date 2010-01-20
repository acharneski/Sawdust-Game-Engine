<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.Date"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>


<%@page import="java.text.DateFormat"%>

<jsp:useBean id="user" class="com.sawdust.gae.jsp.JspUser" scope="request"/><jsp:setProperty name="user" property="request" value="<%=request%>"/><jsp:setProperty name="user" property="response" value="<%=response%>"/>

<jsp:setProperty name="user" property="request" value="<%=request%>"/>
<jsp:setProperty name="user" property="response" value="<%=response%>"/>

<%
String className = request.getParameter("class");
if(null == className) throw new RuntimeException("Parameter Required: class");
String n = request.getParameter("since");
Date since = (null==n)?null:new Date(Long.parseLong(n));
%>

<jsp:useBean id="admin" class="com.sawdust.gae.jsp.DataCleanupBean" />
<jsp:setProperty name="admin" property="request" value="<%=request%>" />
<jsp:setProperty name="admin" property="response" value="<%=response%>"/>

Clearing <%=className%><br/>
Since <%=(null==since)?"null":DateFormat.getDateTimeInstance().format(since)%><br/>
<%=admin.clean(className, since)%><br/>
Cleaned records from 
    <%=(null==admin.getExtraResults().minTime)?"null":DateFormat.getDateTimeInstance().format(admin.getExtraResults().minTime)%>
to  <%=(null==admin.getExtraResults().maxTime)?"null":DateFormat.getDateTimeInstance().format(admin.getExtraResults().maxTime)%>
    <br/>
Cleaned <%=(admin.getExtraResults().nullTimeCount)%> null time records.<br/>
Updated <%=(admin.getExtraResults().inspected)%> records.<br/>
Deleted <%=(admin.getExtraResults().deleted)%> records.<br/>
Result: <%=(admin.getExtraResults().terminationCause)%>.<br/>

    