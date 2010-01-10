<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page errorPage="/error.jsp"%>

<jsp:useBean id="user" class="com.sawdust.gae.jsp.JspUser"/>
<jsp:setProperty name="user" property="request" value="<%=request%>"/>

<jsp:useBean id="requestData" class="com.sawdust.gae.jsp.JspRequestInfoBean" scope="request"/>

<%
	boolean isGameValid = true;
	if (null == request.getParameter("game"))
	{
		isGameValid = false;
	} else if (request.getParameter("game").isEmpty())
	{
		isGameValid = false;
	}
	else
	{
	    requestData.put("game", request.getParameter("game"));
	}
%>

<c:choose>
	<c:when test="<%=isGameValid%>">
		<wc:pageTemplate title="<%=request.getParameter("game")%>" header="">
			<jsp:attribute name="headerInclude">
				<script type="text/javascript" language="javascript" src="gameCreator/gameCreator.nocache.js"></script>
			</jsp:attribute>
			<jsp:body>
				<jsp:include page="/jsp/game_body.jsp" />
			</jsp:body>
		</wc:pageTemplate>
	</c:when>
	<c:otherwise>
		<jsp:forward page="main.jsp"></jsp:forward>
	</c:otherwise>
</c:choose>
