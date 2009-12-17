<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>
<%@ page import="com.sawdust.server.jsp.JspLib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page errorPage="/error.jsp"%>

<%@ page import="com.sawdust.engine.game.BaseGame"%>
<%@ page import="com.sawdust.engine.service.data.GameSession"%>
<%@ page import="com.sawdust.server.jsp.JspLib"%>
<%@ page import="com.sawdust.server.datastore.entities.TinySession"%>
<%@ page import="com.sawdust.engine.game.Game" %>

<%@ page import="com.sawdust.engine.service.Util"%>
<%@ page import="java.util.logging.Logger" %>
<%
final Logger LOG = Logger.getLogger("tinySession");
%>

<jsp:useBean id="user" class="com.sawdust.server.jsp.JspUser"/>
<jsp:setProperty name="user" property="request" value="<%=request%>"/>

<%
String title = "Play";
String keywords = "Sawdust Game Engine - Go, Poker, Blackjack, and more games!";
GameSession gSession = null;
try
{
    gSession = JspLib.Instance.loadSessionFromTinyUrlRequest(request, user);
    if (null != gSession)
    {
        title = gSession.getName();
        Game g = gSession.getLatestState();
        if (null != g) keywords = g.getKeywords();
    }
}
catch(Throwable t)
{
    LOG.warning("Exception: " + Util.getFullString(t));
    throw new ServletException(t);
}
%>

<c:choose>
	<c:when test="<%=request.getServletPath().equals("/p")%>">
		<%-- There is a game to play --%>
		<wc:pageTemplate title="<%=title%>" keywords="<%=keywords%>">
			<jsp:attribute name="headerInclude">
                <jsp:include page="/jsp/play_header.jsp" />
			<script type="text/javascript" language="javascript" src="/gameClient/gameClient.nocache.js"></script>
			</jsp:attribute>
			<jsp:attribute name="leftColumn">
				<jsp:include page="/jsp/gameListing.jsp" />
		 	</jsp:attribute>
			<jsp:body>
				<jsp:include page="/jsp/play_body.jsp" />
				<jsp:include page="/jsp/play_links.jsp" />
			</jsp:body>
		</wc:pageTemplate>
	</c:when>
	<c:when test="<%=request.getServletPath().equals("/f")%>">
		<%-- There is a game to play --%>
		<wc:pageTemplate title="<%=title%>" css="/gameClient/gwt/sawdust/sawdust.css" keywords="<%=keywords%>" supressLeft="true">
			<jsp:attribute name="headerInclude">
                <script type="text/javascript" language="javascript" src="/gameClient/gameClient.nocache.js"></script>
            </jsp:attribute>
			<jsp:attribute name="topColumn">
                <jsp:include page="/jsp/gameBanner.jsp" />
            </jsp:attribute>
			<jsp:attribute name="leftColumn">
            </jsp:attribute>
			<jsp:body>
                <jsp:include page="/jsp/play_body.jsp" />
                <jsp:include page="/jsp/play_links.jsp" />
            </jsp:body>
		</wc:pageTemplate>
	</c:when>
	<c:when test="<%=request.getServletPath().equals("/m")%>">
		<%-- There is a game to play --%>
		<wc:pageTemplate title="<%=title%>" css="/gameClient/gwt/sawdust/sawdust.css" keywords="<%=keywords%>" supressLeft="true" isMobile="true">
			<jsp:attribute name="headerInclude">
                <script type="text/javascript" language="javascript" src="/gameClient/gameClient.nocache.js"></script>
            </jsp:attribute>
			<jsp:body>
                <jsp:include page="/jsp/play_body_mobile.jsp" />
                <jsp:include page="/jsp/play_links.jsp" />
            </jsp:body>
		</wc:pageTemplate>
	</c:when>
	<c:otherwise>
		<%-- There is a game to play --%>
		<wc:pageTemplate title="<%=title%>">
			<jsp:attribute name="headerInclude">
		    	<script type="text/javascript" language="javascript" src="/gameClient/gameClient.nocache.js"></script>
			</jsp:attribute>
			<jsp:attribute name="leftColumn">
				<jsp:include page="/jsp/gameListing.jsp" />
		 	</jsp:attribute>
			<jsp:body>
				I am not sure how to serve your request.
			</jsp:body>
		</wc:pageTemplate>
	</c:otherwise>
</c:choose>
