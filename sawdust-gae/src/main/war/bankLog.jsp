<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient" %>
<%@ page errorPage="/error.jsp"%>

<% com.sawdust.engine.service.debug.RequestLocalLog.Instance.clear(); %>

<wc:pageTemplate title="">
	<jsp:body>
		<jsp:include page="/jsp/bankLog.jsp"/>  
	</jsp:body>
</wc:pageTemplate>
