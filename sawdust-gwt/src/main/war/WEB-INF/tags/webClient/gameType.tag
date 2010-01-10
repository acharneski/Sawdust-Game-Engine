<%@ tag body-content="scriptless" %>
<%@ attribute name="game" required="false"%>
<%@ attribute name="tutorialNumber" required="false" %>
<%@ attribute name="gameObj" required="false" type="com.sawdust.engine.model.GameType"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ tag import="com.sawdust.engine.model.GameType"%>
<%@ tag import="java.util.List" %>
<%@ tag import="com.sawdust.gae.logic.GameTypes"%>
<%@ tag import="com.sawdust.engine.controller.Util" %>
<%@ tag import="java.util.logging.Logger" %>


<jsp:useBean id="user" class="com.sawdust.gae.jsp.JspUser"/>
<jsp:setProperty name="user" property="request" value="<%=request%>"/>

<%
final Logger LOG = Logger.getLogger("gameType.tag");
try {
%>

<%
if(null == game && null == gameObj) return;
GameType gameType = null;
if(null == gameObj)
{
    gameType = GameTypes.findById(game);
}
else
{
    gameType = gameObj;
}
String gameUrl = "/game.jsp?"+ user.getSessonJunk(false,true) + "game="+gameType.getID();
String tutorialUrl = "/tutorials.jsp?"+ user.getSessonJunk(false,true) + "game="+gameType.getID();
String quickPlayUrl = "/quickPlay.jsp?"+ user.getSessonJunk(false,true) + "game="+gameType.getID();
String links = gameType.getLinks();
if(null == links) links = "";
if(!links.isEmpty()) links = " - " + links;
if(null != tutorialNumber)
{
    quickPlayUrl += "&tut=" + tutorialNumber;
}
%>

<div class="sdge-game-type">
    <table border="0">
    <tr><td colspan="2">
        <h3><a href="<%=quickPlayUrl%>" target="_top"><%=gameType.getName()%></a></h3>
    </td></tr>
    <tr><td>
        <a href="<%=quickPlayUrl%>" target="_top"><img src="<%=gameType.getIcon()%>"></img></a>
    </td><td>
        <%=gameType.getShortDescription()%>
    </td></tr>
<%if(!gameType.isSubtype()){%>
    <tr><td colspan="2">
        <a href="<%=quickPlayUrl%>" target="_top">Play</a> - <a href="<%=tutorialUrl%>">Learn</a> - <a href="<%=gameUrl%>">Customize</a><%=links%>
    </td></tr>
<%} %>    
    </table>
</div>

<%
}
catch(Throwable e)
{
   LOG.warning("Error rendering tag: " + Util.getFullString(e));
}
%>