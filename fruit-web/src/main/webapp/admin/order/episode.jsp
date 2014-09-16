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
<title>订单中心批次选择</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
</style>
<script type="text/javascript">
	require([ 'admin/order/episode' ]);
</script>
</head>
<body class="booking-histroy-page">
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" class="container-fluid" ng-controller="PageController as c">
			<div>
				<form name="query-form" class="table-form form-inline">
					<div class="clearfix">
						<div class="form-group">
							<label>批次日期：</label>
							<input type="date" my-date class="form-control" name="deliveryDate" ng-model="query.deliveryDate">
						</div>
						<div class="form-group">
							<label>配送地点：</label>
							<input type="text" class="form-control" name="deliveryAddress" ng-model="query.deliveryAddress">
						</div>
						<div class="pull-right" style="width: auto;">
							<button type="submit" class="btn btn-default" ng-click="c.load()">
								<i class="al al-xinjian"> </i>查询
							</button>
						</div>
					</div>
				</form>
				<table class="table table-bordered">
					<thead>
						<tr>
							<th>ID</th>
							<th>配送地点</th>
							<th>预售日期</th>
							<th>名称</th>
							<th>预订开始时间</th>
							<th>预订结束时间</th>
							<th>是否有效</th>
							<th>命令</th>
						</tr>
					</thead>
					<tr ng-repeat="item in result.records" ng-cloak>
						<td>{{:: item.episodeId }}</td>
						<td>{{:: item.deliveryAddress}}</td>
						<td>{{:: item.deliveryDate | date:'yyyy-MM-dd' }}</td>
						<td>{{:: item.name }}</td>
						<td>{{:: item.bookingTimeStart | date:'yyyy-MM-dd HH:mm' }}</td>
						<td>{{:: item.bookingTimeEnd | date:'yyyy-MM-dd HH:mm' }}</td>
						<td>{{:: item.activated }}</td>
						<td><a href="${contextPath}/admin/order/center.html?episodeId={{:: item.episodeId}}" target="order-center">
								<i class="al al-dingdan"></i>查看订单
							</a></td>
					</tr>
				</table>
			</div>
		</div>
		<jsp:include page="/WEB-INF/jsp/includes/fragment/footer.jsp" />
	</div>
</body>
	</html>
	</compress:html>
</cache:cache>