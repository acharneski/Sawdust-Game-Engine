<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page errorPage="/error.jsp"%>

<jsp:useBean id="user" class="com.sawdust.gae.jsp.JspUser" scope="request" />
<jsp:setProperty name="user" property="request" value="<%=request%>"/>

<%
	boolean isGameValid = true;
	if (null == request.getParameter("game"))
	{
		isGameValid = false;
	} else if (request.getParameter("game").isEmpty())
	{
		isGameValid = false;
	}
%>

<c:choose>
	<c:when test="<%=isGameValid%>">
		<wc:pageTemplate title="<%=request.getParameter("game")%>" header="">
			<jsp:body>
				<jsp:include page="/jsp/tutorial_body.jsp" />
			</jsp:body>
		</wc:pageTemplate>
	</c:when>
	<c:otherwise>
		<jsp:forward page="main.jsp"></jsp:forward>
	</c:otherwise>
</c:choose>
