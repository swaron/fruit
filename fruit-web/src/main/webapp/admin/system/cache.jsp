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
<!doctype html>
<html lang="zh">
<head>
<title>系统缓存设置</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
</style>
<script type="text/javascript">
	require([ 'admin/system/cache' ]);
</script>
</head>
<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" class="container-fluid">
			<div ng-controller="PageController as c">
				<div class="panel panel-default">
					<div class="panel-heading">系统版本设置, 手动修改JS或者字典表之后需要清空系统缓存，并且修改版本号</div>
					<s:eval expression="@'me.suisui.web.support.AppSetting'.getInitAppVersion()" var="initAppVersion"></s:eval>
					<s:eval expression="T(me.suisui.framework.util.AppCache).cacheNames" var="guavaCaches"></s:eval>
					<div class="panel-body">
						<ul class="list-unstyled">
							<li>初始版本: ${initAppVersion}, 当前版本： <span id="current-version">${appVersion}</span>
							</li>
							<li>将当前版本修改为： <input type="text" name="newVersion" ng-model="view.newVersion" />
								<button type="button" ng-click="c.updateAppVersion()">修改</button>
							</li>
						</ul>
						<hr>
						<div>
							当前的guava缓存单位:
							<span ng-bind="result.cacheLength"></span>
							。
							<span id="guava-cache-count"></span>
							<button type="button" ng-click="c.clearAllCache()">清除所有guava缓存</button>
						</div>
						<table class="table table-bordered">
							<tr>
								<th>缓存名称</th>
								<th>缓存信息</th>
								<th>命令</th>
							</tr>
							<tr ng-cloak ng-repeat="(key,val) in result.caches">
								<td>{{::key}}</td>
								<td>{{::val}}</td>
								<td><a href="" ng-click="c.clearCache(key)">清除此缓存</a></td>
							</tr>
						</table>
						<hr>
						<div>
							<h5>OSCache对应的jsp页面缓存（每一小时自动刷新一次）</h5>
							<FORM  METHOD=POST ACTION="${contextPath }/admin/system/cache/flush-oscache.html" TARGET="oscache">
								<button type="button" ng-click="c.updateStartupTime()">更新JSP</button>
								<button type="submit" ng-click="flushOscache=true">更新JSP并刷新OSCache</button>
							</FORM>
							<IFRAME ng-show="flushOscache" NAME="oscache" STYLE="width: 100%;border:none;"></IFRAME>
						</div>
					</div>
				</div>
			</div>
		</div>
		<jsp:include page="/WEB-INF/jsp/includes/fragment/footer.jsp" />
	</div>
</body>
</html>
