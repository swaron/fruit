<%@page session="false" trimDirectiveWhitespaces="true" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="compress" uri="http://htmlcompressor.googlecode.com/taglib/compressor"%>
<%@taglib prefix="cache" uri="http://www.opensymphony.com/oscache"%> 
<%    
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1    
response.setHeader("Pragma","no-cache"); //HTTP 1.0    
response.setDateHeader ("Expires", 0); //prevents caching at the proxy server    
%>

<!doctype html>
<html lang="zh">
<head>
<title>系统控制台</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
</style>
<script type="text/javascript">
	require([ 'admin/system/console' ]);
</script>
</head>

<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" class="container-fluid">
			<div ng-controller="PageController as c">
				<div class="panel panel-default">
					<div class="panel-heading">系统控制台，慎用</div>
					<div class="panel-body">
						<table class="table table-bordered">
							<tr>
								<th>动作</th>
								<th>命令</th>
							</tr>
							<tr>
								<td> 系统从<input class="form-control" type="datetime-local" name="start" ng-model="view.start" />
								到<input class="form-control" type="datetime-local" name="end" ng-model="view.end"> 时间段，重定向到页面<input class="form-control" type="text" name="url" ng-model="view.url"/> </td>
								<td><a href="" ng-click="c.submitRedirect()"><i class="al al-dengdaiwenjian"></i>执行重定向</a> <a href="" ng-click="c.cancelRedirect()"><i class="al al-wenjian"></i>取消重定向</a></td>
							</tr>
						</table>
					</div>
				</div>
			</div>

		</div>
		<jsp:include page="/WEB-INF/jsp/includes/fragment/footer.jsp" />
	</div>
</body>
</html>
