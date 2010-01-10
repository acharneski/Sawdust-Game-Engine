<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page errorPage="/error.jsp"%>

<%@ page import="com.sawdust.gae.datastore.entities.GameSession"%>
<%@ page import="com.sawdust.gae.jsp.JspLib"%>
<%@ page import="java.lang.String"%>
<%@ page import="java.util.List"%>
<%@ page import="com.sawdust.engine.model.GameType"%>
<%@ page import="com.sawdust.engine.controller.Util"%>
<%@ page import="com.sawdust.gae.datastore.DataStore"%>
<%@ page import="com.sawdust.gae.datastore.entities.Promotion"%>
<%@page import="com.sawdust.engine.controller.exceptions.GameLogicException"%>

<html>

<jsp:useBean id="user" class="com.sawdust.gae.jsp.JspUser">
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