<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ page import="com.sawdust.gae.datastore.DataStore"%>

<jsp:useBean id="user" class="com.sawdust.gae.jsp.JspUser" scope="request"/><jsp:setProperty name="user" property="request" value="<%=request%>"/><jsp:setProperty name="user" property="response" value="<%=response%>"/>

<%
if(user.setAccountData(request))
{
    DataStore.Save();
    %><strong>Account Data Saved</strong><%
}
%>

<form method="post">
<table>
    <tr>
        <td>ID:</td>
        <td><input type="text" name="id" value="<%=user.getAccount().getUserId()%>"/></td>
    </tr>
    <tr>
        <td>Display Name:</td>
        <td><input type="text" name="name" value="<%=user.getAccount().getName()%>"/></td>
    </tr>
    <tr>
        <td colspan = 2><input type="submit" value="Save Changes"/></td>
    </tr>
</table>
</form>