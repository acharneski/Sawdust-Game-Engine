<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ page import="com.sawdust.gae.jsp.JspLib"%>
<%@ page import="com.sawdust.engine.model.GameType"%>
<%@ page import="com.sawdust.gae.logic.GameTypes"%>

<jsp:useBean id="user" class="com.sawdust.gae.jsp.JspUser"/>
<jsp:setProperty name="user" property="request" value="<%=request%>"/>

<jsp:useBean id="requestData" class="com.sawdust.gae.jsp.JspRequestInfoBean" scope="request"/>

<div class="tabs">
<%
    boolean isFirst = true;
    for(int i=0;i<GameTypes.values().length;i++) {
       GameType game = (GameType) GameTypes.values()[i];
        if(!isFirst) {%> <%}
        if(requestData.get("game").equals(game.getName()))
        {
            %><span class="tab-current"><a class="game" href="/game.jsp?<%=user.getSessonJunk(false,true)%>game=<%=game.getID()%>"><%=game.getName()%></a></span><%
        }
        else
        {
            %><span class="tab-other"><a class="game" href="/game.jsp?<%=user.getSessonJunk(false,true)%>game=<%=game.getID()%>"><%=game.getName()%></a></span><%
        }
        isFirst = false;
    }
%>
</div>
