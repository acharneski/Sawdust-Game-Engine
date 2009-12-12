<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient" %>
<%@ page errorPage="/error.jsp"%>

<% com.sawdust.engine.service.debug.RequestLocalLog.Instance.clear(); %>

<wc:pageTemplate title="">
    <jsp:attribute name="rightColumn">
    </jsp:attribute>
	<jsp:body>
        <jsp:include page="/jsp/account_info.jsp"/>
        <br/>
        <jsp:include page="/jsp/bankLog.jsp"/>  
        <br/>
        <jsp:include page="/jsp/sessionListing.jsp"/>  
	</jsp:body>
</wc:pageTemplate>
