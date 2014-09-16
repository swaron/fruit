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
<title>订单确认 - 粒粒果鲜</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
.item-img {
	width: 120px;
}

.price {
	color: rgb(255, 68, 2);
	font-weight: blod;
}

.summary-card {
	padding: 8px;
	background-color: rgb(235, 241, 238);
	box-shadow: 1px 2px 3px rgb(243, 238, 228);
}
ul.summary-card > li > label{
	text-align:right;
	width: 70px;
}
.form-control{
	display: inline-block;
}
.form-group label{
	width: 80px;
	text-align: right;
}
</style>
<script type="text/javascript">
	require([ 'page/order/confirm-order','jquery','bootstrap' ],function(order,$){
		$('[title][data-toggle="tooltip"]').tooltip();
		console.log('init');
	});
</script>
</head>
<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" ng-controller="ConfirmController as c" class="container-fluid">
			<ul class="list-inline">
				<li><label>配送日期</label> <span ng-bind="::result.order.order.deliveryDate|date:'M月d号'">，</span> <span ng-bind="::result.order.products[0].estimateDeliveryDate| date:'EEEE'"></span></li>
				<li><label>配送地址</label> <span ng-bind="::result.order.order.deliverAddress"></span></li>
				<hr class="sub" />
			</ul>
			<div>
				<div class="table-title">确认订单</div>
				<table class="table shoplist bordered">
					<tr ng-cloak class="shopitem" ng-repeat="item in ::result.order.products">
						<td class="item-img">
							<a ng-href="${contextPath}/page/episode/{{::item.episodeProductId}}.html?title={{::item.attrs['产品标题']}}">
							<img class="img-responsive" ng-src="{{item.attrs['产品搜索缩略图'][0].url | thumb:'XS' }}">
							</a>
						</td>
						<td>
							<a ng-href="${contextPath}/page/episode/{{::item.episodeProductId}}.html?title={{::item.attrs['产品标题']}}">
							{{ ::item.attrs['产品标题'] }}
							</a>
							<br/>  {{ ::item.attrs['产品子标题'] }}
						</td>
						<td style="min-width:95px;">{{ item.price | currency }}/{{item.attrs['水果销售单位数量']}}{{item.attrs['水果销售单位名称']}}</td>
						<td>{{item.quantity}} 份</td>
						<td><span class="sr-only">无</span></td>
						<td>{{(item.quantity * item.price) | currency }}</td>
					</tr>
					<tr>
						<td colspan="6">
							<form name="deliveryForm">
								<div class="clearfix">
									<div class="text-right form-inline pull-right">
										<label ng-bind="::result.order.products.length" class="price"></label> 件商品, 合计 
										<span class="al-lg price" ng-bind="c.getTotalMoney() | currency">
											<span>
									</div>
									<div class="form-inline pull-left">
										<div class="form-group">
											<label for="recipient">收件人 </label>
											<input name="recipient" class="form-control" ng-model="result.order.order.recipient" style="width: 100px;" required="required" />
										</div>
										<div class="form-group">
											<label for="tel">电话 </label>
											<input name="tel" maxlength="11" class="form-control " ng-model="result.order.order.tel" style="width: 140px;" required="required" />
										</div>
										<div class="form-group">
											<label for="backupContact">备用电话 </label>
											<input name="backupContact" maxlength="11" class="form-control " ng-model="result.order.order.backupContact" style="width: 140px;" />
										</div>
										<div class="form-group">
											<labeL>留言</labeL>
											<input class="form-control" ng-model="result.order.order.deliverNotes" style="width: 200px;" />
										</div>
									</div>
								</div>
							</form>
						</td>
					</tr>
				</table>
				<ul class="pull-right list-unstyled bordered radius summary-card" ng-cloak>
					<li><label>配送地址：</label> <span>{{::result.order.order.deliverAddress }} </span></li>
					<li><label>日期：</label>{{::result.order.order.deliveryDate | date:'M月d号'}} {{::result.order.order.deliveryDate| date:'EEEE'}} </li>
					<li><label>接收人：</label>{{result.order.order.recipient}} {{result.order.order.tel}}</li>
					<li ng-show="result.order.order.deliverNotes"><label>留言：</label>{{result.order.order.deliverNotes}}</li>
					<li class="text-right">
						<%-- <label>实付款：</label> </i><span class="price"> {{c.getTotalMoney() | currency}}</span> --%>
						<span style="display:inline-block;" title="{{deliveryForm.$invalid?'请输入收件人和联系电话':''}}" data-toggle="tooltip" data-placement="left auto">
						<button class="btn btn-primary" ng-disabled="deliveryForm.$invalid" ng-click="c.submitOrder()" >提交订单</button>
						</span>
					</li>
				</ul>
			</div>
		</div>
		<jsp:include page="/WEB-INF/jsp/includes/fragment/footer.jsp" />
	</div>
</body>
</html>
</compress:html>
</cache:cache>
