<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient" %>
<%@ page errorPage="/error.jsp"%>

<wc:pageTemplate title="Administration Tools: Ad-Hoc Query Executor">
	<jsp:body>
		<jsp:include page="/jsp/dataAdhoc.jsp"/>  
	</jsp:body>
</wc:pageTemplate>
