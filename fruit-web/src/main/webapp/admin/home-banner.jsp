<%@page session="false" trimDirectiveWhitespaces="true" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="compress" uri="http://htmlcompressor.googlecode.com/taglib/compressor"%>
<%@taglib prefix="cache" uri="http://www.opensymphony.com/oscache"%> 
<s:eval var="profile" expression="@envProps['spring.profiles.active']" scope="request"></s:eval>

<cache:cache refresh="${profile=='dev' }">
<compress:html compressJavaScript="true" compressCss="true" preserveLineBreaks="true" removeMultiSpaces="false" yuiCssLineBreak="120" yuiJsLineBreak="120">
<!doctype html>
<html lang="zh">
<head>
<title>首页广告管理</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
</style>
<script type="text/javascript">
	require([ 'admin/home-banner' ]);
</script>
</head>

<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" class="container-fluid">
			<div ng-controller="PageController as c">
				<div>
					<ul class="list-unstyled button-bar">
						<li>
							<a class="btn btn-default" href="./home-banner/edit.html">新建</a>
						</li>
					</ul>
					<hr>
					<form name="query-form" class="table-form form-inline">
						<div class="clearfix">
							<div class="form-group"><label>全局显示：</label>
								<label><input type="radio" name="globalVisible"  ng-model="query.globalVisible" ng-value="null"> 全部 </label>
								<label><input type="radio" name="globalVisible"  ng-model="query.globalVisible" ng-value="true">  全局 </label>
								<label><input type="radio" name="globalVisible"  ng-model="query.globalVisible" ng-value="false"> 本地 </label>
							</div>
							<div class="form-group"><label>配送点：</label>
								<select name="deliveryAddressId" class="form-control" ng-model="query.deliveryAddressId" ng-options="addr.deliveryAddressId as addr.name for addr in result.addresses">
							    	<option value="">-- 任意 --</option>
							    </select>
							</div>
							<div class="form-group"><label>时间从：</label><input type="date" my-date class="form-control" name="startTime"  ng-model="query.startTime" ></div>
							<div class="form-group"><label>时间到：</label><input type="date" my-date class="form-control" name="endTime"  ng-model="query.endTime" ></div>
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
								<th style="min-width: 120px;">配送地址</th>
								<th style="min-width: 80px;">开始时间</th>
								<th style="min-width: 80px;">结束时间</th>
								<th style="min-width: 80px;">是否启用</th>
								<th style="min-width: 80px;">全局有效</th>
								<th style="min-width: 80px;">模特名字</th>
								<th style="min-width: 60px;">命令</th>
							</tr>
						</thead>
						<tr ng-repeat-start="item in result.banners" ng-cloak>
							<td>{{:: item.addressName }}</td>
							<td>{{:: item.startTime|date:'M月d号'}}</td>
							<td>{{:: item.endTime|date:'M月d号'}}</td>
							<td>{{:: item.enabled }}</td>
							<td>{{:: item.globalVisible}}</td>
							<td>{{:: item.modelName}}</td>
							<td>
							<a href="home-banner/edit.html?homeBannerId={{:: item.homeBannerId}}&copy=true"><i class="al al-fuzhi"></i>复制</a>
							<a href="home-banner/edit.html?homeBannerId={{:: item.homeBannerId}}"><i class="al al-edit"></i>编辑</a>
							 <a href="" ng-click="c.removeItem($index)"><i class="al al-shanchu"></i>删除</a></td>
						</tr>
						<tr ng-repeat-end ng-cloak>
							<td colspan="7"><img alt="" class="img-responsive" ng-src="{{::item.imageUrl[0].url }}"> </td>
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
