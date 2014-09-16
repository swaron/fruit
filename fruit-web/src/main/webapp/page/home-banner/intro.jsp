<%@page session="false" trimDirectiveWhitespaces="true" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@taglib prefix="compress" uri="http://htmlcompressor.googlecode.com/taglib/compressor"%>
<%@taglib prefix="cache" uri="http://www.opensymphony.com/oscache"%>
<s:eval var="profile" expression="@envProps['spring.profiles.active']" scope="request"></s:eval>

<cache:cache refresh="${profile=='dev' }">
	<compress:html compressJavaScript="true" compressCss="true" preserveLineBreaks="true" removeMultiSpaces="false" yuiCssLineBreak="120" yuiJsLineBreak="120">
	<!doctype html>
	<html lang="zh">
<head>
<title>首页水果秀 - 粒粒果鲜</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
.fruit-show-title .badge{
	position: absolute;
	left: 10px;
	font-size: 1.1em;
	font-weight: lighter;
}
.fruit-show-title .badge.name{
	bottom: 60px;
}
.fruit-show-title .badge.slogan{
	bottom: 35px;
}
</style>
<link rel="stylesheet" type="text/css" href="../help/help.css?v=${appVersion }">
<script type="text/javascript">
require([ 'page/home-banner/intro' ]);
</script>
</head>

<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/help-header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" class="container-fluid" ng-controller="PageController as c">
			<div class="clearfix">
				<jsp:include page="/WEB-INF/jsp/includes/fragment/help-side.jsp" />
				<div class="help-block">
					<h4>首页水果秀</h4>
					<p>
						<a href="${contextPath}/page/help/question-51.html">水果秀介绍链接</a>
					</p>
					<br>
					<h5>当前水果秀</h5>
					<table class="table table-bordered">
						<tr>
							<td style="width:80px;">配送点</td>
							<td ng-bind="::model.banner.addressName"></td>
						</tr>
						<tr>
							<td>模特名字</td>
							<td ng-bind="::model.banner.modelName"></td>
						</tr>
						<tr>
							<td>水果秀语</td>
							<td ng-bind="::model.banner.modelSlogan"></td>
						</tr>
						<tr>
							<td>模特名字链接网站</td>
							<td ng-bind="::model.banner.modelLink"></td>
						</tr>
						<tr>
							<td>模特介绍</td>
							<td>
								<pre ng-bind="::model.banner.modelIntroduction">
								</pre>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<div class="" style="position: relative;">
									<img class="img-responsive" ng-src="{{::model.banner.imageUrl[0].url }}">
									<div class="fruit-show-title" ng-cloak>
										<span ng-show="model.banner.modelName" class="badge name" ng-bind="::model.banner.modelName">  </span><br/>
										<span ng-show="model.banner.modelSlogan" class="badge slogan" ng-bind="::model.banner.modelSlogan"> </span>
									</div>
								</div>
								
							</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
		<jsp:include page="/WEB-INF/jsp/includes/fragment/footer.jsp" />
	</div>
</body>
	</html>
	</compress:html>
</cache:cache>
