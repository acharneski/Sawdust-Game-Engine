<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>

<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Date"%>
<%@ page import="com.sawdust.server.datastore.entities.MoneyTransaction"%>
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
<jsp:useBean id="account" class="com.sawdust.server.jsp.JspTransactionLog" />
<jsp:setProperty name="account" property="request" value="<%=request%>" />
<jsp:setProperty name="account" property="accountId" value="<%=id%>" />

<div class="sdge-bank-statement">
<h2>Bank Statement</h2>
<%
    MoneyAccount a = account.getAccount();
    Account person = Account.LoadIfExists(id);
    %><h3><%=a.getDisplayName()%></h3><%
    %><h3>ID: <%=id.substring(0,(id.length()<20)?id.length():20)%></h3><%
    if(person != null)
    {
        %><h3>Personal Account: <%
        if(false && !person.getUserId().equals(user.getUser().getUserID()))
        {
            if(user.isAdmin())
            {
                %>Admin Access Granted</h3><%
            }
            else
            {
                %>Access Denied</h3><%
                throw new SecurityException("Personal transaction records are not shared.");
            }
        }
        else
        {
            %>Access Granted</h3><%
        }
    }
    else
    {
        %><h3>Game Session Account</h3><%
    }
%>

<jsp:useBean id="statement" class="com.sawdust.server.jsp.JspBankStatement" />
<jsp:setProperty name="statement" property="request" value="<%=request%>" />
<jsp:setProperty name="statement" property="account" value="<%=a%>" />

    <table class="sdge-bank-statement">
    <tr>
        <th></th>
        <th>Previous</th>
        <th>Amount</th>
        <th>Final</th>
    </tr>
<% 
	// TODO: Re-enable Java 5 support in JSP
	java.util.Iterator i = statement.getTransactions().iterator();
	while(i.hasNext()) {
	   MoneyTransaction t = (MoneyTransaction) i.next();
    //for (MoneyTransaction t : statement.getTransactions()) {
        if(null != t.recipientId && t.recipientId.equals(a.accountId))
        {
            %><tr>
                <td>
                    <h4><%=t.description.trim()%></h4>
                    <strong>Time: </strong><i><%=DateFormat.getDateTimeInstance().format(t.time)%></i><br/>
                    <%if(null!=t.senderId){%>
                     <strong>ID: </strong>
                     <a href="bankLog.jsp?id=<%=URLEncoder.encode(t.senderId)%>">
                      <%=t.senderId.substring(0,(t.senderId.length()<20)?t.senderId.length():20)%>
                     </a>
                    <%}%>
                </td>
	            <td style="text-align:right;"><%=t.recipientStartBalance%></td>
	            <td style="text-align:right;"><%=t.transactionAmount%></td>
	            <td style="text-align:right;"><%=t.recipientEndBalance%></td>
	        </tr><%
        }
        else if(null != t.senderId && t.senderId.equals(a.accountId))
        {
            %><tr>
                <td>
                    <h4><%=t.description%></h4>
                    <strong>Time: </strong><i><%=DateFormat.getDateTimeInstance().format(t.time)%></i><br/>
                    <%if(null!=t.recipientId){%>
                     <strong>ID: </strong>
                     <a href="bankLog.jsp?id=<%=URLEncoder.encode(t.recipientId)%>">
                      <%=t.recipientId.substring(0,(t.recipientId.length()<20)?t.recipientId.length():20)%>
                     </a>
                    <%}%>
                </td>
	            <td style="text-align:right;"><%=t.senderStartBalance%></td>
	            <td style="text-align:right;">-<%=t.transactionAmount%></td>
	            <td style="text-align:right;"><%=t.senderEndBalance%></td>
	        </tr><%
        }
    }

%>
</table>
<a href="bankLog.jsp?until=<%=statement.getMinTime().getTime()-1%>">&lt&lt Previous</a> - <a href="bankLog.jsp">Top</a> - <a href="bankLog.jsp?since=<%=statement.getMaxTime().getTime()%>">Next &gt&gt</a>
</div>