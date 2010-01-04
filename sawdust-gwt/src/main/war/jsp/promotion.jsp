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
<%@ page import="com.sawdust.server.datastore.entities.Promotion"%>
<%@page import="com.sawdust.engine.service.debug.GameLogicException"%>
<%@page import="com.sawdust.server.datastore.DataStore"%><html>

<jsp:useBean id="user" class="com.sawdust.server.jsp.JspUser" scope="request" />
<jsp:setProperty name="user" property="request" value="<%=request%>"/>

<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <link type="text/css" rel="stylesheet" href="/gameClient/gwt/sawdust/sawdust.css">
    <title>Sawdust Game Engine - Coupon</title>
    </head><body>
	    
	<%
	String targetId = request.getPathInfo();
    if(targetId.startsWith("/")) targetId = targetId.substring(1);
    Promotion promo = Promotion.load(targetId);
    if(null != promo)
    {
        try
        {
            promo.addAccount(user.getAccount());
            DataStore.Save();
	        %>
	        <title><%=promo.getName()%> (Sawdust Game Engine Gift)</title>
	        </head><body>
	        Congratulations! You have been awarded <%=promo.getValue()%> credits.
	        <%
        }
        catch(GameLogicException e)
        {
            %>
            <title>ERROR - <%=promo.getName()%> (Sawdust Game Engine Gift)</title>
            </head><body>
            We're sorry. There seems to be a problem obtaining your gift: <%=e.getMessage()%>
            <%
        }
    }
    else
    {
        %>
        <title>ERROR - <%=promo.getName()%> (Sawdust Game Engine Gift)</title>
        </head><body>
        We're sorry. There seems to be a problem finding your gift.
        <%
    }
    %>
</body>
</html>