<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient" %>
<%@ page errorPage="/error.jsp"%>

<html>
<head></head>
<body>
<h1>Welcome!</h1>

<% 
if(true) throw new RuntimeException("testing");
%>

</body>
</html>
