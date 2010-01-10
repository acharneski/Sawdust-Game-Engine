<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page errorPage="/error.jsp"%>

<%@ page import="com.sawdust.gae.jsp.JspLib"%>
<%@ page import="com.sawdust.engine.model.basetypes.BaseGame"%>
<%@ page import="com.sawdust.engine.controller.entities.GameSession"%>
<%@ page import="com.sawdust.gae.jsp.JspLib"%>
<%@ page import="com.sawdust.gae.datastore.entities.TinySession"%>
<%@ page import="com.sawdust.engine.model.basetypes.GameState" %>
<%@ page import="com.sawdust.engine.controller.Util"%>
<%@ page import="java.util.logging.Logger" %>

<%final Logger LOG = Logger.getLogger("session_breadcrumb_header");%>

<jsp:useBean id="requestData" class="com.sawdust.gae.jsp.JspRequestInfoBean" scope="request"/>

<%
if(requestData.getGameType().isEmpty())
{
    %>
        <h1><a href="/">Sawdust Game Engine<sub>Beta</sub></a></h1>
        <jsp:include page="/jsp/gameBanner.jsp"></jsp:include>
    <%
}
else
{
    %>
        <h1><a href="/game.jsp?game=<%=requestData.getGameTypeId()%>"><%=requestData.getGameType()%></a><span class="breadcrumb-delimiter">&nbsp;&gt;&nbsp;</span><span class="subtitle"><%=requestData.getGameSessionName()%></span></h1>
    <%
}
%>
