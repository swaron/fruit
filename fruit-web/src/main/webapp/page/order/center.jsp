<%@page session="false" trimDirectiveWhitespaces="true" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="compress" uri="http://htmlcompressor.googlecode.com/taglib/compressor"%>
<%@taglib prefix="cache" uri="http://www.opensymphony.com/oscache"%> 
<s:eval var="profile" expression="@envProps['spring.profiles.active']" scope="request"></s:eval>

<cache:cache refresh="${profile=='dev' }">
<compress:html compressJavaScript="true" compressCss="true" preserveLineBreaks="true" removeMultiSpaces="false" yuiCssLineBreak="120" yuiJsLineBreak="120">
<!doctype html>
<html lang="zh">
<head>
<title>我的订单 - 粒粒果鲜</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
img.thumb{
	width: 60px;
	height: 60px;
}
</style>
<script type="text/javascript">
	require([ 'page/order/center' ]);
</script>
</head>
<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" ng-controller="PageController as c" class="container-fluid">
			<div>
				<form class="table-form clearfix form-inline">
					<div class="pull-right">
						<div class="form-group">
							<select class="form-control" ng-model="view.period">
								<option selected="selected">最近三周</option>
								<option>最近三月</option>
								<option>最近三年</option>
								<option>全部时间</option>
							</select>
						</div>
						<div class="form-group">
							<input class="form-control" placeholder="水果名称，订单编号" ng-model="view.query">
						</div>
						<button type="submit" class="btn btn-default" ng-click="c.load()">
							<i class="al al-sousuo"> </i>查询
						</button>
					</div>
				</form>
				<table class="table table-bordered">
					<thead>
						<tr>
							<th>订单内容</th>
							<th class="hidden-xs">收件人</th>
							<th>应付金额</th>
							<th class="hidden-xs">创建时间</th>
							<th>订单状态<br/>(到付状态更新可能不及时) </th>
							<th>命令</th>
						</tr>
					</thead>
					<tr  ng-repeat-start="item in result.orders.records" ng-cloak>
						<td colspan="1">订单编号: {{:: item.orderNum }}
						</td>
						<td colspan="5">
							<div ng-show="::item.claimRequest">理赔问题：{{::item.claimRequest}}</div>
							<div ng-show="::item.claimResponse">理赔结果：{{::item.claimResponse}}</div>
						</td>
					</tr>
					<tr ng-repeat-end ng-cloak>
						<td><ul class="list-inline"><li ng-repeat="prod in ::item.products"><img title="{{::prod.attrs['产品标题'] }}" class="img-responsive thumb" ng-src="{{::prod.attrs['产品搜索缩略图'][0].url | thumb:'XXS'}}">  </li> </ul> </td>
						<td class="hidden-xs">{{:: item.recipient }}</td>
						<td>{{:: item.totalPayAmount }}</td>
						<td class="hidden-xs">{{:: item.orderCreatedTime | date:'medium'}}</td>
						<td>{{:: item.orderStatus | dbcode:'goods_order':'order_status' }}</td>
						<td><a href="${contextPath}/page/order/view-order.html?orderId={{item.orderId}}">
								<i class="al al-xinjian"></i>查看订单
							</a> <a href="" ng-click="c.cancelOrder(item,$event)">
								<i class="al al-jinzhi"></i>取消订单
							</a><a href="" ng-click="c.claimRequest(item,$event)">
								<i class="al al-duihua"></i>理赔订单
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
