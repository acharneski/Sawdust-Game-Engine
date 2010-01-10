<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>

<%@ page import="com.sawdust.gae.jsp.JspLib"%>
<%@ page import="com.sawdust.engine.model.basetypes.BaseGame"%>
<%@ page import="com.sawdust.engine.model.basetypes.GameState" %>
<%@ page import="com.sawdust.gae.datastore.entities.TinySession"%>
<%@ page import="com.sawdust.gae.datastore.entities.GameSession"%>

<jsp:useBean id="user" class="com.sawdust.gae.jsp.JspUser"/>

<%
    GameSession s = (GameSession) JspLib.Instance.loadSessionFromTinyUrlRequest(request,user);
    String desc = "";
    if (null != s)
    {
        GameState game = s.getState();
        if (null != game) desc = game.getKeywords();
        //desc = game.getConfig().getGameDescription().replaceAll("[^\\w]"," ");
    }
%>

<meta name="keywords" content="<%=desc%>" />




