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
<title>购物车-粒粒果鲜</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
.summary-card {
	padding: 8px;
	background-color: rgb(235, 241, 238);
	box-shadow: 1px 2px 3px rgb(243, 238, 228);
}
table.shoplist {
	border-bottom: 1px solid rgb(221, 221, 221);
	border-left: 1px solid rgb(221, 221, 221);
	border-right: 1px solid rgb(221, 221, 221);
}

.shopitem {
	background-color: rgb(250, 250, 252);
}

tr.shopitem-on {
	background-color: rgb(255, 248, 225);
}

.shopitem .item-chk {
	width: 30px;
	min-width: 30px;
}

.shopitem .item-img {
	width: 96px;;
	min-width: 96px;
}

.cart-add input {
	width: 50px;
}

.table>tfoot>tr>td {
	vertical-align: inherit;
}

.price {
	color: rgb(255, 68, 2);
	font-weight: blod;
}
</style>
<script type="text/javascript">
	require([ 'page/cart' ]);
</script>
</head>
<body class="booking-histroy-page">
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" ng-controller="CartController as c" class="container-fluid">
			<div>粒粒果鲜 - 购物车</div>
			<hr />
			
			<table class="table shoplist" ng-repeat="(episodeId,itemList) in model.cart" ng-init="c.fetchEpisode(episodeId);">
				<tr ng-cloak>
					<th colspan="8"><span ng-bind="::_cache.episodes[episodeId].deliveryDate|date:'M月d号'"></span>，<span ng-bind="::_cache.episodes[episodeId].deliveryDate| date:'EEEE'"></span> <span ng-bind="::_cache.episodes[episodeId].deliveryAddress"></span> </th>
				</tr>
				<tr class="hide">
					<th class="c1"></th>
					<th style="min-width: 96px;">选择</th>
					<th>商品信息</th>
					<th>&nbsp;</th>
					<th style="min-width: 95px;">单价</th>
					<th style="min-width: 120px;">数量</th>
					<th>金额</th>
					<th>操作</th>
				</tr>
				<tr ng-cloak class="shopitem" ng-repeat="item in itemList" ng-class="{'shopitem-on':item.enabled}">
					<td class="item-chk" ng-init="c.fetchProduct(item.epid);"><input type="checkbox" ng-model="item.enabled"></td>
					<td class="item-img">
						<a ng-href="${contextPath}/page/episode/{{::_cache.products[item.epid].episodeProductId}}.html?title={{::_cache.products[item.epid].attrs['产品标题']}}">
							<img class="img-responsive" ng-src="{{_cache.products[item.epid].attrs['产品搜索缩略图'][0].url | thumb:'XS' }}">
						</a>
					</td>
					<td>
						<a ng-href="${contextPath}/page/episode/{{::_cache.products[item.epid].episodeProductId}}.html?title={{::_cache.products[item.epid].attrs['产品标题']}}">
						{{ ::_cache.products[item.epid].attrs['产品标题'] }}
						</a>
						<br/>  {{ ::_cache.products[item.epid].attrs['产品子标题'] }}
						<div ng-show="view.showEpisode">批次号：{{::_cache.products[item.epid].episodeId}}</div>
					</td>
					<td>&nbsp;</td>
					<td>{{ _cache.products[item.epid].price |currency}}/{{ _cache.products[item.epid].attrs['水果销售单位数量']}}{{ _cache.products[item.epid].attrs['水果销售单位名称']}}</td>
					<td>
						<div class="cart-add">
							<span class="decrease" href="" ng-click="item.count=(item.count-1)>0?(item.count-1):item.count"><i class="al al-jian"></i></span>
							<input type="number" min="1" max="99" step="1" ng-model="item.count">
							<span class="increase" href="" ng-click="item.count=(item.count+1)<=99?(item.count+1):item.count"> <i class="al al-jia"></i></span> 
						</div>
					</td>
					<td><span class="price"> {{ (item.count * _cache.products[item.epid].price) | currency }}</span></td>
					<td><a href="" ng-click="c.removeCartItem(item.epid)">
							<i class="al al-shanchu"></i>删除
						</a></td>
				</tr>
				<tfoot>
					<tr class="text-right">
						<td colspan="4">&nbsp;</td>
						<td colspan="3"><span ng-bind="c.getSubTotalCount(episodeId)"></span>  件商品, 小计 <span class="al-lg price" ng-bind="c.getSubTotalMoney(episodeId) | currency">
								</span></td>
						<td><button class="btn btn-primary"  ng-click="c.confirmOrder(episodeId)">结算</button> </td>
					</tr>
				</tfoot>
			</table>
			<%-- <div class="clearfix" ng-show="model.episodeCount != 1">
				<ul class="pull-right list-inline bordered radius summary-card" ng-cloak>
					<li><span ng-bind="c.getTotalCount()"></span>  件商品, 合计 <span class="al-lg price" ng-bind="c.getTotalMoney() | currency"></span></li>
					<li><button class="btn btn-primary" ng-click="c.confirmOrder()">结算</button></li>
				</ul>
			</div> --%>
			
		</div>
		<jsp:include page="/WEB-INF/jsp/includes/fragment/footer.jsp" />
	</div>
</body>
</html>
</compress:html>
</cache:cache>
