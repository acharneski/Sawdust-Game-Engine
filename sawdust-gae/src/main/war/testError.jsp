<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient" %>
<%@ page errorPage="/error.jsp"%>
<%@page import="com.sawdust.engine.service.debug.RequestLocalLog"%>

<%com.sawdust.engine.service.debug.RequestLocalLog.Instance.clear();%>

<% 
RequestLocalLog.Instance.print("Testing the request-local log!\n");
RequestLocalLog.Instance.print("Again");
RequestLocalLog.Instance.print("And yet again!\n");
%>

<wc:pageTemplate title="">
<h1>Welcome!</h1>
</wc:pageTemplate>

<% 
throw new RuntimeException("testing");
%>