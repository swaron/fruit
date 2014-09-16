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
<title>订单后台 - 粒粒果鲜</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
.p-name{
}
img.p-img {
	margin-right:4px;
	width: 90px;
	height: 90px;
}
li.order{
	padding: 4px;
}
li.order dt > div{
	float: left;
	padding-right: 30px;
}
li.order dt{
	font-weight: normal;
}
</style>
<script type="text/javascript">
	require([ 'admin/order/center' ]);
</script>
</head>
<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" ng-controller="PageController as c" class="container-fluid">
			<div>
				<h4 ng-cloak>配送时间：<span class="text-muted">{{::result.result.deliveryDate| date:'M月d号' }} {{::result.result.deliveryDate| date:'EEEE' }}</span> 配送地址：<span class="text-muted">{{::result.result.deliveryAddress}}</span> </h4>
				<div class="text-right"><a href="" ng-click="c.updateAllStatus()"><i class="al al-edit"></i> 所有 等待付款 改为 已完成</a> </div>
				<hr>
				<ul class="list-unstyled">
					<li ng-repeat="item in result.records" class="bordered order" ng-cloak>
						<dl>
							<dt class="clearfix">
								<div class="">
									<label>订单编号：</label>
									<span>{{::item.orderNum}}</span>
								</div>
								<div class="">
									<label>应付金额：</label>
									<span>{{::item.totalPayAmount | currency }}</span>
								</div>
								<div class="">
									<label>订单状态：</label>
									<span ng-class="{'text-danger': item.orderStatus !=1 }">{{item.orderStatus | dbcode:'goods_order':'order_status'}}</span>
								</div>
								<div class="">
									<label>收件人：</label>
									<span>{{::item.recipient}}，{{::item.tel}}</span>
									<span class="text-muted">&nbsp;{{::item.backupContact}}</span>
								</div>
								<div ng-if="item.deliverNotes" style="width: auto;">
									<label>留言：</label>
									<span>{{::item.deliverNotes}}</span>
								</div>
								<div ng-if="item.claimRequest" style="width: auto;">
									<label>理赔申请：</label>
									<span>{{::item.claimRequest}}</span>
								</div>
								<div ng-if="item.claimResponse" style="width: auto;">
									<label>理赔结果：</label>
									<span>{{::item.claimResponse}}</span>
								</div>
								<div class="pull-right">
									<a href="" ng-click="c.completeOrder(item)"> <i class="al al-edit"></i>完成订单</a> 
									<a href="" ng-click="c.cancelOrder(item)"> <i class="al al-cuowu"></i>取消订单</a>
									<a href="" ng-click="c.claimResponse(item)"> <i class="al al-cuowu"></i>处理理赔</a>
								</div>
							</dt>
							<dd>
								<ol class="order-detail list-inline">
									<li ng-repeat="p in ::item.opEpGp">
										<div class="clearfix">
											<img class="p-img pull-left" ng-src="{{::p.attrs['产品搜索缩略图'][0].url | thumb:'XS' }}">
											<div class="pull-left">
												<span class="price" ng-bind="::p.price | currency"></span><br>
												<b ng-bind="::p.quantity"></b> 份 * {{::p.attrs['水果销售单位数量']}}{{::p.attrs['水果销售单位名称']}}
											</div>
										</div>
										<div>
											<span class="p-name">{{::p.name }}</span>
										</div>
									</li>
								</ol>
							</dd>
						</dl>
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
