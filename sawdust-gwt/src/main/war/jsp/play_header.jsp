<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>

<%@ page import="com.sawdust.engine.game.BaseGame"%>
<%@ page import="com.sawdust.server.jsp.JspLib"%>
<%@ page import="com.sawdust.server.datastore.entities.TinySession"%>
<%@ page import="com.sawdust.engine.service.data.GameSession"%>
<%@ page import="com.sawdust.engine.game.Game" %>

<jsp:useBean id="user" class="com.sawdust.server.jsp.JspUser" />
<jsp:setProperty name="user" property="request" value="<%=request%>" />

<%
    GameSession s = JspLib.Instance.loadSessionFromTinyUrlRequest(request,user);
    String desc = "";
    if (null != s)
    {
        Game game = s.getLatestState();
        if (null != game) desc = game.getKeywords();
        //desc = game.getConfig().getGameDescription().replaceAll("[^\\w]"," ");
    }
%>

<meta name="keywords" content="<%=desc%>" />




