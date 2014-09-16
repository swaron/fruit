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
<title>产品批次管理</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
</style>
<script type="text/javascript">
	require([ 'admin/episode' ]);
</script>
</head>

<body class="booking-histroy-page">
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" class="container-fluid" ng-controller="EpisodeController as c">
			<div class="pull-right help-block">
				<button type="button" class="btn btn-default" ng-click="c.addItem()">
					<i class="al al-xinjian"> </i>新增批次
				</button>
			</div>
			<div>
				<table class="table table-bordered">
					<thead>
						<tr>
							<th>预售日期</th>
							<th>配送地点</th>
							<th>名称</th>
							<th>预订开始时间</th>
							<th>预订结束时间</th>
							<th>包含产品数目</th>
							<th>是否有效</th>
							<th>命令</th>
						</tr>
					</thead>
					<tr ng-repeat="item in result.episodes.records" ng-cloak>
						<td>{{:: item.deliveryDate | date:'yyyy-MM-dd' }}</td>
						<td>{{:: item.deliveryAddress}}</td>
						<td>{{:: item.name }}</td>
						<td>{{:: item.bookingTimeStart | date:'yyyy-MM-dd HH:mm' }}</td>
						<td>{{:: item.bookingTimeEnd | date:'yyyy-MM-dd HH:mm' }}</td>
						<td>未实现</td>
						<td>{{:: item.activated }}</td>
						<td>
						<a href="episode/preview.html?episodeId={{:: item.episodeId}}" target="preview"><i class="al al-yulan"></i>预览</a>
						<a href="${contextPath}/admin/order/center.html?episodeId={{:: item.episodeId}}" target="_blank"><i class="al al-yulan"></i>查看订单</a>
						<a href="" ng-click="c.editItem(item)"><i class="al al-edit"></i>编辑信息</a>
						<a href="episode/edit.html?episodeId={{:: item.episodeId}}"><i class="al al-edit"></i>编辑产品</a>
						<a href="" ng-click="c.copyItem(item)"><i class="al al-fuzhi"></i>复制批次</a>
						<a href="" ng-click="c.removeItem($index)"><i class="al al-shanchu"></i>删除</a></td>
					</tr>
				</table>
			</div>
			<div class="modal fade" id="edit-item-win" tabindex="-1" role="dialog" aria-labelledby="edit-item-win-label"
				aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="edit-item-win-label"><span ng-bind="model.current.episodeId == null ?'新增':'编辑' "></span>
							<span ng-bind="view.popup.note"></span>&nbsp;
							</h4>
							
						</div>
						<div class="modal-body ">
							<form action="" name="addForm">
								<div class="alert alert-warning alert-dismissible ng-hide" role="alert">
									<button type="button" class="close" data-dismiss="alert">
										<span aria-hidden="true">&times;</span><span class="sr-only">关闭</span>
									</button>
									<strong>!</strong> 验证失败，请检查。
								</div>
								<table class="table table-condensed table-borderless">
									<tr class="">
										<td><label for="deliveryAddr">配送地点</label></td>
										<td>
										<select class="form-control" name="deliveryAddr" required="required" ng-model="model.current.deliveryAddress" ng-options="item.name as item.name for item in result.addrs">
										</select>
									</tr>
									<tr class="">
										<td><label for="deliveryDate">供应日期</label></td>
										<td><input type="date" my-date class="form-control" name="deliveryDate"
											ng-model="model.current.deliveryDate" required="" placeholder="格式：1985-10-10" /></td>
									</tr>
									<tr>
										<td><label for="name">名称</label></td>
										<td><input type="text" class="form-control" name="name" ng-model="model.current.name" /></td>
									</tr>
									<tr>
										<td><label for="bookingTimeStart">预约开始时间</label></td>
										<td><input type="datetime-local" my-date class="form-control" name="bookingTimeStart" required
											ng-model="model.current.bookingTimeStart" placeholder="格式：1985-10-10T12:02" /></td>
									</tr>
									<tr>
										<td><label for="bookingTimeEnd">预约结束时间</label></td>
										<td><input type="datetime-local" my-date class="form-control" name="bookingTimeEnd" required
											ng-model="model.current.bookingTimeEnd" placeholder="格式：1985-10-10T12:02" /></td>
									</tr>
									<tr>
										<td><label>是否有效</label></td>
										<td><input type="checkbox" name="activated" ng-model="model.current.activated" /></td>
									</tr>
								</table>
							</form>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
							<button type="button" class="btn btn-primary" ng-click="c.save(addForm)"
								ng-disabled="addForm.$invalid || addForm.$pristine || addForm.saving">
								<i class="al" ng-class='{"al-baocun":!saving,"al-jiazaizhong al-spin":saving}'></i>保存
							</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
		</div>
		<jsp:include page="/WEB-INF/jsp/includes/fragment/footer.jsp" />
	</div>
</body>
</html>
</compress:html>
</cache:cache>