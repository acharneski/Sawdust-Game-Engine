<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ page import="com.google.appengine.api.datastore.EntityNotFoundException"%>
<%@ page import="java.util.Date"%>
<%@ page import="com.sawdust.engine.service.Util" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>


<%@page import="java.text.DateFormat"%><jsp:useBean id="user" class="com.sawdust.server.jsp.JspUser"/>
<jsp:setProperty name="user" property="request" value="<%=request%>"/>
<jsp:setProperty name="user" property="response" value="<%=response%>"/>

<%
String query = request.getParameter("query");
String operation = request.getParameter("action");
String maxRows = request.getParameter("max");
if(null == query) query = "";
if(null == maxRows) maxRows = "10";
%>

<form action="dataAdhoc.jsp" method="get">
Query: <input type="text" name="query" value="<%=query%>"><br/>
Max Rows: <input type="text" name="max" value="<%=maxRows%>"><br/>
<input type="submit" name="action" value="DELETE">
<input type="submit" name="action" value="QUERY"><br/>
</form>

<jsp:useBean id="admin" class="com.sawdust.server.jsp.DataAdhocBean" />
<jsp:setProperty name="admin" property="request" value="<%=request%>" />
<jsp:setProperty name="admin" property="response" value="<%=response%>"/>
<%
if(null != query && null != operation) 
{
    try
    {
	    String result = admin.doQuery(query,operation,Integer.parseInt(maxRows));
	    %>Operation Completed: <%=result%><br/><%
    }
    catch(Throwable e)
    {
        %>Error: <%=Util.getFullString(e)%><br/><%
        
    }
}
%>
