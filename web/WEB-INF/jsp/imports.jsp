<%@ page import="com.nagornyi.env.EnvVariablesStorage" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/favicon.png">
<link href='https://fonts.googleapis.com/css?family=Roboto:300,400&subset=latin,cyrillic-ext' rel='stylesheet' type='text/css'>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css" type="text/css" media="screen">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/ng/popup.css" type="text/css" media="screen">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/ng/style.css" type="text/css" media="screen">
<meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" name="viewport">
<!--[if lt IE 8]>
<div style=' clear: both; text-align:center; position: relative;'>
<a href="http://windows.microsoft.com/en-US/internet-explorer/products/ie/home?ocid=ie6_countdown_bannercode">
<img src="http://storage.ie6countdown.com/assets/100/images/banners/warning_bar_0000_us.jpg" border="0" height="42" width="820" alt="You are using an outdated browser. For a faster, safer browsing experience, upgrade for free today." />
</a>
</div>
<![endif]-->
<!--[if lt IE 9]>
<script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
<![endif]-->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="https://www.google.com/recaptcha/api.js?hl=uk&render=explicit" async defer></script>
<jsp:include page="/WEB-INF/jsp/i18n.jsp" flush="true">
    <jsp:param name="bundleName" value="clientbundle"/>
</jsp:include>
<script src="${pageContext.request.contextPath}/js/EventBus.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/js/ng/ng.main.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/js/ng/ng.common.js" type="text/javascript"></script>
<meta name="google-signin-scope" content="profile email">
<meta name="google-signin-client_id" content="<%=EnvVariablesStorage.getValue("Oauth", "clientId")%>">
<script src="https://apis.google.com/js/platform.js" async defer></script>