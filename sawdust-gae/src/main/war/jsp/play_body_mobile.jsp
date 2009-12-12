<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>

<%@ page import="com.sawdust.engine.game.BaseGame"%>
<%@ page import="com.sawdust.server.jsp.JspLib"%>
<%@ page import="com.sawdust.server.datastore.entities.TinySession"%>
<%@ page import="com.sawdust.engine.common.game.Message.MessageType"%>
<%@ page import="com.sawdust.engine.common.game.Message"%>
<%@ page import="com.sawdust.engine.common.game.ClientCommand"%>
<%@ page import="java.util.*"%>
<%@page import="com.sawdust.engine.service.debug.GameException"%>
<%@page import="com.sawdust.server.datastore.entities.GameSession"%>
<%@page import="java.net.URLEncoder"%>
<%@ page import="com.sawdust.engine.game.Game" %>

<jsp:useBean id="user" class="com.sawdust.server.jsp.JspUser"/>
<jsp:setProperty name="user" property="request" value="<%=request%>"/>

<%
    TinySession tsession = JspLib.Instance.getTinySession(request);
    boolean isSessionDefined = false;
    if (null != tsession && null != tsession.getSessionId())
    {
        isSessionDefined = true;
    }
    String gameDesc = "";
    GameSession s = GameSession.load(tsession.getSessionId(), user.getAccount().getPlayer());
    if (null != s)
    {
        Game game = s.getLatestState();
        if (null != game)
        {
    gameDesc = game.getConfig().getGameDescription();
        }
    }
%>



<H1>MOBILE EDITION</H1>

<c:choose>
	<c:when test="<%=isSessionDefined%>">

		<jsp:useBean id="game" class="com.sawdust.server.jsp.JspGame" />
		<jsp:setProperty name="game" property="request" value="<%=request%>" />
		<jsp:setProperty name="game" property="sessionId" value="<%=tsession.getSessionId()%>" />
		<%
		    String commandParameter = request.getParameter("manual");
				if(null == commandParameter || commandParameter.isEmpty()) commandParameter = request.getParameter("command");
				if(null != commandParameter && !commandParameter.isEmpty()) 
				{
					try
					{
					    game.doCommand(commandParameter);
					}
					catch(GameException e)
					{
		%><i><%=e.getMessage()%></i><br/><%
			}
		}
		%>
        <%
     	// TODO: Re-enable Java 5 support in JSP
     	java.util.Iterator i = game.getMajorCommands().iterator();
     	while(i.hasNext()) {
     	 String cmd = (String) i.next();
        //for (String cmd : game.getMajorCommands()) {
            %><a href="<%=request.getRequestURI()%>?command=<%=URLEncoder.encode(cmd)%>"><%=cmd%></a><br/><%
        }
        %>
		<form><strong>Command: </strong> 
			<select name="command">
		        <%
			       	// TODO: Re-enable Java 5 support in JSP
			       	java.util.Iterator j = game.getCommands().iterator();
			       	while(j.hasNext()) {
			       	  ClientCommand cmd = (ClientCommand) j.next();
		            //for (ClientCommand cmd : game.getCommands()) {
		        %>
		        <option value="<%=cmd.getCommand()%>"><%=cmd.getCommand()%></option>
		        <%
		            }
		        %>
			</select>
			<br>
	        <input type="text" name="manual">
			<input type="submit" value="Go">
		</form>
		
		<%
	    boolean wasCompact = false;
        StringBuilder sb = new StringBuilder();
       	// TODO: Re-enable Java 5 support in JSP
       	java.util.Iterator k = game.getMessageList().iterator();
       	while(k.hasNext()) {
       	  Message msg = (Message) k.next();
        //for (Message msg : game.getMessageList()) {
		%>
			<c:choose>
				<c:when test="<%=msg.getType() == MessageType.Compact%>">
					<%
					    sb.insert(0, "<span>" + msg.getText() + "</span>");
					%>
				</c:when>
				<c:otherwise>
					<c:if test="<%=sb.length() > 0%>">
                        <%=sb.toString()%>
						<%
						    sb.delete(0, sb.length());
						%>
						<br />
					</c:if>
					<c:if test="<%=!msg.getText().isEmpty()%>">
						<%=msg.getText()%><br />
					</c:if>
				</c:otherwise>
			</c:choose>
		<%
		    }
		%>
        <%=sb.toString()%>


	</c:when>
	<c:otherwise>
		ERROR: No session id found
	</c:otherwise>
</c:choose>
