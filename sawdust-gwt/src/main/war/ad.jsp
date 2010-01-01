<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.sawdust.server.jsp.JspLib"%>
<%@ page import="com.sawdust.server.datastore.entities.GameSession"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page errorPage="/error.jsp"%>

<%@ page import="java.lang.String"%>
<%@ page import="java.util.List"%>
<%@ page import="com.sawdust.engine.game.GameType"%>
<%@ page import="com.sawdust.engine.service.Util"%>
<%@ page import="com.sawdust.server.logic.GameTypes"%>

<%
String targetId = request.getParameter("t");
GameType game = null;
if(null != targetId && !targetId.isEmpty())
{
	// TODO: Re-enable Java 5 support in JSP
	//for (GameType game : GameTypes.values()) {
	for(int i=0;i<GameTypes.values().length;i++) {
	    game = (GameType) GameTypes.values()[i];
	    if (game.getID().equals(targetId))
	    {
	        break;
	    }
	    else
	    {
	        game = null;
	    }
	}
}
%>

<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <link type="text/css" rel="stylesheet" href="/gameClient/gwt/sawdust/sawdust.css">
    <title><%=game.getName()%></title>
</head>
<body>
    	<script type="text/javascript"><!--
	google_ad_client = "pub-8770918941318471";
	/* 120x600, created 8/26/09 */
	google_ad_slot = "1410090188";
	google_ad_width = 120;
	google_ad_height = 600;
	//-->
	</script>
	<script type="text/javascript" src="http://pagead2.googlesyndication.com/pagead/show_ads.js">
	</script>
    <%
    if(null != game)
    {
        %><h1><%=game.getName()%></h1><%
        %><h2><%=game.getShortDescription()%></h2><%
        %><%=game.getDescription()%><%
    }
    %>
</body>