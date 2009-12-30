<%@ tag body-content="scriptless" %>
<%@ attribute name="title" required="true"%>
<%@ attribute name="header"%>
<%@ attribute name="gatewayLink"%>
<%@ attribute name="keywords"%>
<%@ attribute name="css"%>
<%@ attribute name="supressLeft"%>
<%@ attribute name="isMobile" %>
<%@ attribute name="headerOverride" fragment="true" %>
<%@ attribute name="rightColumn" fragment="true"%>
<%@ attribute name="topColumn" fragment="true"%>
<%@ attribute name="headerInclude" fragment="true"%>

<%@ tag import="com.sawdust.engine.service.Util" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ tag import="java.util.logging.Logger" %>

<%
final Logger LOG = Logger.getLogger("pageTemplate");
%>

<%
	if (null == title)
	{
	    title = "Sawdust Game Engine";
	}
	if (null == header)
	{
		header = title;
	}
    if(null == gatewayLink)
    {
	    if(null != isMobile)
	    {
	        gatewayLink = "/mobile.jsp";
	    }
	    else
	    {
	        gatewayLink = "/";
	    }
    }
	String cssFile = "/gameClient/gwt/sawdust/sawdust.css";
	if (null != css)
	{
		cssFile = css;
	}
	if(null != keywords)
	{
	    StringBuilder sb = new StringBuilder();
	 	// TODO: Re-enable Java 5 support in JSP

	 	String values[] = keywords.split("\\s+");
		for(int i=0;i<values.length;i++) {
		   String w = (String) values[i];
	    //for(String w : keywords.split("\\s+")) {
	        if(w.length() > 3 && 0 > sb.indexOf(w))
	        {
	            if(sb.length() > 0) sb.append("+");
	            sb.append(w);
	        }
	    }
	    keywords = sb.toString();
	}
%>

<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <link type="text/css" rel="stylesheet" href="/site.css">
    <link type="text/css" rel="stylesheet" href="<%=cssFile%>">
	<title><%
		if(title.isEmpty())
		{
			%>Sawdust Game Engine<%
		}
		else
		{
			%><%=title%> (Sawdust Game Engine)<%
		}
	%></title>
	<c:if test="<%=null != headerInclude%>">
	    <%
	    try
	    {
	        %>
	    		<jsp:invoke fragment="headerInclude" />
	        <%
	    }
	    catch(Throwable e)
	    {
	        LOG.warning("Exception rendering header inclusion: " + Util.getFullString(e));
	        %> <!-- ERROR: headerInclude2 --> <%
	    }
	    %>
	</c:if>
    <!-- Sawdust Game Engine v1.0.1 -->
</head>
<body>
	<div class="sdge-site-header">
		<div class="sdge-site-accountinfo">
			<jsp:include page="/jsp/accountHeader.jsp" />
		</div>
		<c:choose>
			<c:when test="<%=null != headerOverride%>">
                <jsp:invoke fragment="headerOverride" />
			</c:when>
	
			<c:otherwise>
				<h1 align="left"><a href="<%=gatewayLink%>">
				<%
				if(null == isMobile)
				{
				        if(null == header || header.isEmpty())
				        {
				            %>Sawdust Game Engine<sub>Beta</sub><%
				        }
				        else
				        {
				            %>SGE<sub>Beta</sub>&nbsp;&lt;&nbsp;<%=header%><%
				        }
				}
				else
				{
				        if(header.isEmpty())
				        {
				            %>Sawdust Game Engine<sub>Mobile Beta</sub><%
				        }
				        else
				        {
		                    %>SGE<sub>Beta</sub>&nbsp;&lt;&nbsp;<%=header%><%
				        }
				}
				%>
				</a></h1>
		        <div>
			        <c:choose>
			            <c:when test="<%=null != topColumn%>">
						    <%
						    try
						    {
						        %>
				                    <jsp:invoke fragment="topColumn" />
						        <%
						    }
						    catch(Throwable e)
						    {
						        LOG.warning("Exception rendering top column: " + Util.getFullString(e));
						        %>Now with more BETA!<%
						    }
						    %>
			            </c:when>
		                <c:when test="<%=null != isMobile%>">
		                    <jsp:include page="/jsp/gameBanner_mobile.jsp" />
		                </c:when>
			            <c:otherwise>
			                <jsp:include page="/jsp/gameBanner.jsp" />
			            </c:otherwise>
			        </c:choose>
		        </div>
			</c:otherwise>
		</c:choose>
	</div>

	<div class="sdge-site-body">
	<%
	try
	{
	    %>
			<jsp:doBody />
	    <%
	}
	catch(Throwable e)
	{
	    LOG.warning("Exception rendering body: " + Util.getFullString(e));
	    %>We're sorry, there was an error serving your request. Please try again, or come back later. Thank you for your patience!<%
	}
	%>
		<hr/>
        <c:choose>
            <c:when test="<%=null != isMobile%>">
                <div style="float:right;valign=top;"><jsp:include page="/jsp/ad_mobile.jsp" /></div>
            </c:when>
            <c:otherwise>
            </c:otherwise>
        </c:choose>
		<div class="sdge-site-footer">
			Do you like our site? We would love to hear from you. 
			Please email us at <a href="mailto:sawdustgames@gmail.com" target="_blank">sawdustgames@gmail.com</a> 
			or visit us on <a href="http://www.facebook.com/apps/application.php?id=202795340537">facebook</a>.
		</div>
	</div>

	<div class="sdge-site-adbar">
		<c:choose>
            <c:when test="<%=(null != isMobile)%>">
                <!-- Normal ads excluded: Mobile page -->
            </c:when>
			<c:when test="<%=null == rightColumn%>">
			<c:choose>
			    <c:when test="<%=null != keywords%>">
                    <iframe src="/ad.jsp?k=<%=keywords%>" width = "120px" height = "600px"></iframe>
			    </c:when>
			    <c:otherwise>
                    <jsp:include page="/jsp/ad.jsp" />
			    </c:otherwise>
			</c:choose>
			</c:when>
			<c:otherwise>
			    <%
			    try
			    {
			        %>
						<jsp:invoke fragment="rightColumn" />
			        <%
			    }
			    catch(Throwable e)
			    {
			        LOG.warning("Exception rendering right column: " + Util.getFullString(e));
			        %>There was a problem rendering your ads. Please don't cry. They might work next time.<%
			    }
			    %>
			</c:otherwise>
		</c:choose>
	</div>
<jsp:include page="/jsp/analytics.jsp" />
</body>
</html>
