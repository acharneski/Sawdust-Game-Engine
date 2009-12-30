<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page errorPage="/error.jsp"%>

<%@ page import="com.sawdust.server.jsp.JspLib"%>
<%@ page import="com.sawdust.engine.game.BaseGame"%>
<%@ page import="com.sawdust.engine.service.data.GameSession"%>
<%@ page import="com.sawdust.server.jsp.JspLib"%>
<%@ page import="com.sawdust.server.datastore.entities.TinySession"%>
<%@ page import="com.sawdust.engine.game.Game" %>

<%@ page import="com.sawdust.engine.service.Util"%>
<%@ page import="java.util.logging.Logger" %>

<%final Logger LOG = Logger.getLogger("session_breadcrumb_header");%>

<jsp:useBean id="requestData" class="com.sawdust.server.jsp.JspRequestInfoBean" scope="request"/>

<h1>
<a href="/game.jsp?game=<%=requestData.getGameTypeId()%>"><%=requestData.getGameType()%></a>&nbsp;&gt;&nbsp;<%=requestData.getGameSessionName()%>
</h1>