<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@page import="java.io.StringWriter"%>
<%@page import="java.io.OutputStream"%>
<%@page import="java.io.PrintWriter"%>

<%@page import="java.util.logging.Logger"%>
<%@page import="java.io.PrintStream"%>
<%@ page import="com.sawdust.gae.jsp.JspLib"%>
<%@ page import="com.sawdust.gae.datastore.DataStore"%>
<%@page import="com.sawdust.engine.controller.exceptions.GameLogicException"%>

<%@ page isErrorPage="true" %>

<%@ page import="com.sawdust.engine.controller.Util" %>
<%@ page import="java.util.logging.Logger" %>
<%
final Logger LOG = Logger.getLogger("gameType.tag");
LOG.warning("JSP Exception: " + Util.getFullString(exception));
%>

<html>
<title>
Error
</title>
<body>
There was an error trying to serve your request.  Please retry your request, and if that still doesn't work please come back in a little while!
</body>
</html>
