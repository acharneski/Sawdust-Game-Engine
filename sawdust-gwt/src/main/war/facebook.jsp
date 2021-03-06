<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="wc" tagdir="/WEB-INF/tags/webClient" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page import="com.sawdust.gae.jsp.JspLib"%>
<%@ page import="com.sawdust.gae.jsp.JspUser"%>
<%@ page import="com.sawdust.gae.facebook.FacebookUser"%>
<%@ page import="com.sawdust.gae.facebook.FacebookSite"%>
<%@ page import="com.sawdust.gae.logic.GameTypes"%>
<%@ page import="java.net.URLEncoder"%>

<%@ page errorPage="/error.jsp"%>

<%
    boolean isGameUrl = false;
if(request.getPathInfo().startsWith("/play"))
{
	isGameUrl = true;
}
if(request.getPathInfo().startsWith("/f/"))
{
	isGameUrl = true;
}
%>

<jsp:useBean id="user" class="com.sawdust.gae.jsp.JspUser" />
<jsp:setProperty name="user" property="request" value="<%=request%>"/>
<jsp:setProperty name="user" property="response" value="<%=response%>"/>


<%
  String perm = FacebookUser.GetFbParam(request, "fb_sig_ext_perms");
  if(null == perm) perm = "";
%>
<c:choose>

<c:when test="<%=perm.contains("publish_stream")%>">
    <%-- User has authorized this application --%>
    <fb:js-string var="chatInvite">  
        <fb:chat-invite 
            msg="Let's play a game: http://apps.facebook.com/sawdust-games<%=JspLib.getRedirectUrl(request)%>" 
            condensed="false"
        />
    </fb:js-string> 
    
    <fb:dashboard>
    <c:if test="<%=isGameUrl%>">
        <fb:create-button href="#" onclick="(new Dialog()).showMessage('Invite Player', chatInvite);">Invite Players</fb:create-button> 
    </c:if> 
    </fb:dashboard>
</c:when>

<c:when test="<%="1".equals(FacebookUser.GetFbParam(request, "fb_sig_added"))%>">
    <%-- User has authorized this application --%>
    <fb:js-string var="chatInvite">  
        <fb:chat-invite 
            msg="Let's play a game: http://apps.facebook.com/sawdust-games<%=JspLib.getRedirectUrl(request)%>" 
            condensed="false"
        />
    </fb:js-string> 
    
    <%-- User has authorized this application, but not to publish to the stream 
    <fb:prompt-permission perms="publish_stream">We'd like permission to tell your freinds about your victories at Sawdust Games.</fb:prompt-permission>
    --%>

    <fb:dashboard>
    <c:if test="<%=isGameUrl%>">
        <fb:create-button href="#" onclick="(new Dialog()).showMessage('Invite Player', chatInvite);">Invite Players</fb:create-button> 
    </c:if> 
    </fb:dashboard>

</c:when>

<c:when test="<%=null == FacebookUser.GetFbParam(request, "fb_sig_logged_out_facebook")%>">
	<%-- User is logged in, but has not authorized the application --%>
	<fb:redirect url="http://www.facebook.com/login.php?v=1.0&api_key=<%=FacebookUser.GetFbParam(request, "fb_sig_api_key")%>&next=<%=URLEncoder.encode("http://apps.facebook.com/sawdust-games"+JspLib.getRedirectUrl(request))%>&canvas="/>
</c:when>

<c:otherwise>
	<%-- User is not logged in --%>
</c:otherwise>

</c:choose>

<%
    FacebookSite facebookSite = FacebookUser.verifyFacebookSignature(request);
%>
<fb:iframe src="<%=JspLib.getIFrameUrl(request, facebookSite)%>" width="760px" height="1000px" style="overflow: scroll;">
<p>Your browser does not support iframes.</p>
</fb:iframe>
