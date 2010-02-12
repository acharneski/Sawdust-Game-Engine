<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page errorPage="/error.jsp"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient" %>

<%@ page import="com.sawdust.gae.jsp.JspLib"%>
<%@ page import="com.sawdust.engine.model.GameType"%>

<jsp:useBean id="user" class="com.sawdust.gae.jsp.JspUser" scope="request"/><jsp:setProperty name="user" property="request" value="<%=request%>"/><jsp:setProperty name="user" property="response" value="<%=response%>"/>
<h1>Welcome!</h1>
<h2>Please choose a game:</h2>

<table border="0">
<tr>
    <td><wc:gameType game="Go2"></wc:gameType></td>
    <td><wc:gameType game="Go"></wc:gameType></td>
</tr>
<tr>
    <td><wc:gameType game="WordHunt"></wc:gameType></td>
    <td><wc:gameType game="Euchre"></wc:gameType></td>
</tr>
<tr>
    <td><wc:gameType game="BlackJack"></wc:gameType></td>
    <td><wc:gameType game="Stop"></wc:gameType></td>
</tr>
<tr>
    <td><wc:gameType game="Poker"></wc:gameType></td>
    <td><div class="sdge-game-type"><h2 style="text-align: center;">More games coming soon!</h2></div></td>
</tr>
</table>
