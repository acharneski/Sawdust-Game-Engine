<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ page import="com.sawdust.server.jsp.JspLib"%>
<%@ page import="com.sawdust.engine.game.GameType"%>
<%@ page import="com.sawdust.server.logic.GameTypes"%>

<jsp:useBean id="user" class="com.sawdust.server.jsp.JspUser"/>
<jsp:setProperty name="user" property="request" value="<%=request%>"/>

<div>
<%
    boolean isFirst = true;
// TODO: Re-enable Java 5 support in JSP
for(int i=0;i<GameTypes.values().length;i++) {
   GameType game = (GameType) GameTypes.values()[i];
//for (GameType game : GameTypes.values()) {
    if(!isFirst) {
%> - <%}
	%><a class="game" href="/game.jsp?<%=user.getSessonJunk(false,true)%>game=<%=game.getID()%>"><%=game.getName()%></a><%
	isFirst = false;
}
%>
</div>
