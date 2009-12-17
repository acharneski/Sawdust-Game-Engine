<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient" %>
<%@ page errorPage="/error.jsp"%>

<wc:pageTemplate title="">
<h1>Welcome!</h1>
</wc:pageTemplate>

<% 
throw new RuntimeException("testing");
%>