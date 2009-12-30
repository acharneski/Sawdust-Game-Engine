<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient" %>
<%@ page errorPage="/error.jsp"%>

<wc:pageTemplate title="">
    <jsp:attribute name="topColumn">
        <a href="http://apps.facebook.com/sawdust-games/">Social</a> and <a href="/mobile.jsp">Mobile</a> Gaming; Customizable.
    </jsp:attribute>
    <jsp:attribute name="rightColumn">
        <jsp:include page="/jsp/facebook_fan.jsp" />
    </jsp:attribute>
	<jsp:body>
                <jsp:include page="/jsp/welcome.jsp" />
	</jsp:body>
</wc:pageTemplate>
