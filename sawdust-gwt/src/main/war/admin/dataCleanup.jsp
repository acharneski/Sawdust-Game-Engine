<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient" %>
<%@ page errorPage="/error.jsp"%>

<wc:pageTemplate title="Administration Tools: Data Cleanup">
	<jsp:body>
		<jsp:include page="/jsp/dataCleanup.jsp"/>  
	</jsp:body>
</wc:pageTemplate>
