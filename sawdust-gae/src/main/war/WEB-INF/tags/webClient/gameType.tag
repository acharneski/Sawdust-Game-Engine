<%@ tag body-content="scriptless" %>
<%@ attribute name="game" required="true"%>

<%@ tag import="com.sawdust.engine.game.GameType"%>
<%@ tag import="java.util.List"%>
<%@ tag import="com.sawdust.server.logic.GameTypes"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:useBean id="user" class="com.sawdust.server.jsp.JspUser"/>
<jsp:setProperty name="user" property="request" value="<%=request%>"/>

<%@ tag import="com.sawdust.engine.service.Util" %>
<%@ tag import="java.util.logging.Logger" %>
<%
final Logger LOG = Logger.getLogger("gameType.tag");
try {
%>

<%
if(null == game) return;
GameType gameType = GameTypes.findById(game);
String gameUrl = "/game.jsp?"+ user.getSessonJunk(false,true) + "game="+gameType.getID();
String quickPlayUrl = "/quickPlay.jsp?"+ user.getSessonJunk(false,true) + "game="+gameType.getID();
String links = gameType.getLinks();
if(null == links) links = "";
if(!links.isEmpty()) links = " - " + links;
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
	    <ol>
	    <% 
	    List<GameType> tutorials = gameType.getTutorialSequence();
	    int index=0;
	 	// TODO: Re-enable Java 5 support in JSP
	 	java.util.Iterator i = tutorials.iterator();
	 	while(i.hasNext()) {
	 	  GameType t = (GameType) i.next();
	    //for(GameType t : tutorials) {
            %><li>
            <a href="<%=quickPlayUrl%>&tut=<%=index++%>" target="_top"><%=t.getName()%></a>
            </li><%
	    }%>
	    </ol>
	</td></tr>
    <tr><td colspan="2">
        <a href="<%=quickPlayUrl%>" target="_top">Play Now</a> - <a href="<%=gameUrl%>">Game Homepage</a><%=links%>
    </td></tr>
	</table>
</div>

<%
}
catch(Throwable e)
{
   LOG.warning("Error rendering tag: " + Util.getFullString(e));
}
%>