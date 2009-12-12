<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page errorPage="/error.jsp"%>

<%com.sawdust.engine.service.debug.RequestLocalLog.Instance.clear();%>

<jsp:useBean id="user" class="com.sawdust.server.jsp.JspUser" scope="request" />
<jsp:setProperty name="user" property="request" value="<%=request%>"/>
<jsp:setProperty name="user" property="response" value="<%=response%>"/>

<c:choose>
	<c:when test="<%=null != user.getEmail()%>">
		<wc:pageTemplate title="My Games">
			<jsp:include page="/jsp/sessionListing.jsp" />
		</wc:pageTemplate>
	</c:when>
	<c:otherwise>
		<wc:pageTemplate title="My Games">
			You must sign in to play!
		</wc:pageTemplate>
	</c:otherwise>
</c:choose>
