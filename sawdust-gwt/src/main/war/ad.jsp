<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page errorPage="/error.jsp"%>

<%@ page import="com.sawdust.gae.jsp.JspLib"%>
<%@ page import="java.lang.String"%>
<%@ page import="java.util.List"%>
<%@ page import="com.sawdust.gae.datastore.entities.GameSession"%>
<%@ page import="com.sawdust.engine.model.GameType"%>
<%@ page import="com.sawdust.engine.controller.Util"%>
<%@ page import="com.sawdust.gae.logic.GameTypes"%>

<html style="overflow:hidden;">
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <link type="text/css" rel="stylesheet" href="/gameClient/gwt/sawdust/sawdust.css">
	    
	<%
	String targetId = request.getParameter("t");
	if(null == targetId || targetId.isEmpty())
	{
	    targetId = request.getPathInfo();
	    if(targetId.startsWith("/")) targetId = targetId.substring(1);
	}
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
	if(null != game)
	{
		int subStart = game.getShortDescription().indexOf(' ',5);
		int subEnd = game.getShortDescription().indexOf(' ',subStart+15);
		%>
		
		    <title><%=game.getName()%> - <%=game.getShortDescription().substring(subStart,subEnd)%></title>
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
			<script type="text/javascript" src="http://pagead2.googlesyndication.com/pagead/show_ads.js"></script>
			
		    <h1><%=game.getName()%></h1>
		    <h2><%=game.getShortDescription()%></h2>
		    <div><%=game.getDescription()%></div>
	    <%
    }
	else
	{
        %>
        
        <title>Sawdust Game Engine</title>
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
        <script type="text/javascript" src="http://pagead2.googlesyndication.com/pagead/show_ads.js"></script>
        
        We could not find the game and/or ad data requested: <%=targetId%>
    <%
	}
    %>
    <h2>Sawdust Game Engine</h2>
    <div>
    Sawdust Game Engine is an online gaming website focusing on card and board games. Our games are turn-based and tend to focus on strategy, chance, and skill. We are accessible via Facebook or directly via your Google account. 
    
    We offer free play, no download games which can be enjoyed with your existing Facebook friends on any HTML 5 browser such as Firefox, IE, and Chrome. We also have a mobile version which can be viewed on nearly any web-enabled phone, although for devices with full featured browsers such as the iPhone should use the main site.
    </div>
</body>
</html>