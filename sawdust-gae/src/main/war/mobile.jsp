<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>
<%@ page errorPage="/error.jsp"%>

<%
    com.sawdust.engine.service.debug.RequestLocalLog.Instance.clear();
%>

<wc:pageTemplate title="" supressLeft="true" isMobile="true">
	<jsp:body>
                <jsp:include page="/jsp/welcome.jsp" />
	</jsp:body>
</wc:pageTemplate>
