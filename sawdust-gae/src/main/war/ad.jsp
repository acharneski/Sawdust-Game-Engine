<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.sawdust.server.jsp.JspLib"%>
<%@ page import="com.sawdust.server.datastore.entities.GameSession"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page errorPage="/error.jsp"%>

<%com.sawdust.engine.service.debug.RequestLocalLog.Instance.clear();%>

<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <link type="text/css" rel="stylesheet" href="/gameClient/gwt/sawdust/sawdust.css">
    <title><%=request.getParameter("k")%></title>
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
</body>