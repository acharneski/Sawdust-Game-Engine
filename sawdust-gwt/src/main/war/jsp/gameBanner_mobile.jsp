<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.sawdust.server.jsp.JspLib"%>
<%@ page import="com.sawdust.engine.game.GameType"%>
<%@ page import="com.sawdust.server.logic.GameTypes"%>

<jsp:useBean id="user" class="com.sawdust.server.jsp.JspUser"/>
<jsp:setProperty name="user" property="request" value="<%=request%>"/>

<jsp:useBean id="requestData" class="com.sawdust.server.jsp.JspRequestInfoBean" scope="request"/>

<div>
<%
    boolean isFirst = true;
	for(int i=0;i<GameTypes.values().length;i++) {
	   GameType game = (GameType) GameTypes.values()[i];
	    if(!isFirst) {%> - <%}
        if(requestData.get("game").equals(game.getName()))
        {
            %><span class="current-game"><a class="game" href="/game_mobile.jsp?<%=user.getSessonJunk(false,true)%>game=<%=game.getID()%>"><%=game.getName()%></a></span><%
        }
        else
        {
            %><a class="game" href="/game_mobile.jsp?<%=user.getSessonJunk(false,true)%>game=<%=game.getID()%>"><%=game.getName()%></a><%
        }
		isFirst = false;
	}
%>
</div>
