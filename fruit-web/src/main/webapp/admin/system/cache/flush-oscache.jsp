<%@page session="false" trimDirectiveWhitespaces="true" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="compress" uri="http://htmlcompressor.googlecode.com/taglib/compressor"%>
<%@taglib prefix="cache" uri="http://www.opensymphony.com/oscache"%>
<%
	response.setHeader("Cache-Control", "no-cache"); //HTTP 1.1    
	response.setHeader("Pragma", "no-cache"); //HTTP 1.0    
	response.setDateHeader("Expires", 0); //prevents caching at the proxy server
%>
<cache:flush>
</cache:flush>
<!doctype html>
<html lang="zh">
<head>
<title>刷新 OSCache的JSP缓存</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
</style>
<script type="text/javascript">
	
</script>
</head>
<body>
	<div id="wrapper">
		<div class="alert alert-info">
			<h3>OsCache页面缓存刷新成功</h3>
		</div>
	</div>
</body>
</html>
