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
.form-control{
	display: inline-block;
}
.form-group label{
	width: 80px;
	text-align: right;
}
</style>
<script type="text/javascript">
	require([ 'page/order/confirm-order' ]);
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
				<div class="table-title">订单详情 <a href="${contextPath}/page/order/center.html">返回我的订单</a> </div>
				<table class="table shoplist bordered">
					<tr ng-cloak class="shopitem" ng-repeat="item in ::result.order.products">
						<td class="item-img"><img class="img-responsive" ng-src="{{item.attrs['产品搜索缩略图'][0].url | thumb:'XS'}}"></td>
						<td>{{item.attrs['产品子标题'] }}</td>
						<td>{{ item.price | currency}}/{{ item.attrs['水果销售单位数量']}}{{ item.attrs['水果销售单位名称']}}</td>
						<td>{{item.quantity}}</td>
						<td><span class="sr-only">无</span></td>
						<td>{{item.quantity * item.price |currency }}</td>
					</tr>
					<tr>
						<td colspan="6">
							<form name="deliveryForm">
								<div class="clearfix">
									<div class="text-right form-inline pull-right">
										<label ng-bind="::result.order.products.length" class="price"></label> 件商品, 合计 </i>
										<span class="al-lg price" ng-bind="c.getTotalMoney() | currency">
											<span>
									</div>
									<div class="form-inline pull-left">
										<div class="form-group">
											<label for="recipient">收件人 </label>
											<input name="recipient" class="form-control" ng-value="result.order.order.recipient" style="width: 100px;" readonly="readonly" />
										</div>
										<div class="form-group">
											<label for="tel">电话 </label>
											<input name="tel" class="form-control " ng-value="result.order.order.tel" style="width: 140px;" readonly="readonly" />
										</div>
										<div class="form-group">
											<label for="backupContact">备用电话 </label>
											<input name="backupContact" maxlength="11" class="form-control " ng-model="result.order.order.backupContact" style="width: 140px;" readonly/>
										</div>
										<div class="form-group">
											<labeL>留言</labeL>
											<input class="form-control" ng-model="result.order.order.deliverNotes" style="width: 300px;" readonly/>
										</div>
									</div>
								</div>
							</form>
						</td>
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
