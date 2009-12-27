<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>

<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Date"%>
<%@ page import="com.sawdust.server.datastore.entities.ActivityEventRecord"%>
<%@ page import="com.sawdust.server.datastore.entities.MoneyAccount"%>

<%@page import="java.text.DateFormat"%>

<jsp:useBean id="user" class="com.sawdust.server.jsp.JspUser"/>
<jsp:setProperty name="user" property="request" value="<%=request%>"/>
<jsp:setProperty name="user" property="response" value="<%=response%>"/>

<%
String id = request.getParameter("id");
if(null == id) id = user.getAccount().getUserId(); 
%>

<%@page import="com.sawdust.server.datastore.entities.Account"%>
<%@page import="com.sawdust.server.datastore.entities.ActivityEventRecord"%>
<jsp:useBean id="account" class="com.sawdust.server.jsp.JspTransactionLog" />
<jsp:setProperty name="account" property="request" value="<%=request%>" />
<jsp:setProperty name="account" property="accountId" value="<%=id%>" />

<div class="sdge-bank-statement">
<h2>Activity Log</h2>
<%
    MoneyAccount a = account.getAccount();
    Account person = Account.LoadIfExists(id);
    %><h3><%=a.getDisplayName()%></h3><%
    %><h3>ID: <%=id.substring(0,(id.length()<20)?id.length():20)%></h3><%
    %><h3>Personal Account: <%
    if(!person.getUserId().equals(user.getUser().getUserID()))
    {
        if(user.isAdmin())
        {
            %>Admin Access Granted</h3><%
        }
        else
        {
            %>Access Denied</h3><%
            throw new SecurityException("Personal activity records are not shared.");
        }
    }
    else
    {
        %>Access Granted</h3><%
    }
%>

<jsp:useBean id="statement" class="com.sawdust.server.jsp.JspActivityLog" />
<jsp:setProperty name="statement" property="request" value="<%=request%>" />
<jsp:setProperty name="statement" property="account" value="<%=a%>" />

    <table class="sdge-bank-statement">
    <tr>
        <th>Event</th>
        <th>Type</th>
        <th>Time</th>
    </tr>
<% 
	// TODO: Re-enable Java 5 support in JSP
    //for (MoneyTransaction t : statement.getTransactions()) {
	java.util.Iterator i = statement.getTransactions().iterator();
	while(i.hasNext()) {
	    ActivityEventRecord t = (ActivityEventRecord) i.next();
%>
<tr>
   <td><%=(null==t.getData())?"":t.getData().event%></td>
   <td style="text-align:right;"><%=t.eventType%></td>
   <td style="text-align:right;"><%=DateFormat.getDateTimeInstance().format(t.time)%></td>
</tr>
<%
    }

%>
</table>
<a href="activityLog.jsp?until=<%=statement.getMinTime().getTime()-1%>">&lt&lt Previous</a> - <a href="activityLog.jsp">Top</a> - <a href="activityLog.jsp?since=<%=statement.getMaxTime().getTime()%>">Next &gt&gt</a>
</div>