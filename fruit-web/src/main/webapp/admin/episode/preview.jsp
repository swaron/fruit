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
<title>当期鲜果预览-粒粒果鲜</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
.container-fluid {
	max-width: 1430px;
}
li.item {
	margin-bottom: 10px;
}

li.item ol.list-unstyled {
	margin-bottom: 0px;
}

li.item ol:HOVER {
	border: 1px solid rgb(217, 54, 146);
}

.p2 {
	font-weight: bold;
	font-size: 1.2em;
}

.l-desc {
	padding-top: 8px;
	color: rgb(228, 59, 59);
	height: 48px;
	line-height: 20px;
	/* width: 240px; */
	overflow: hidden;
}
.l-name {
	height: 23px;
	padding-bottom: 3px;
}

.l-price {
	padding: 5px 10px 2px 0px;
}


.l-price .r {
	font-size: 1em;
	/* color: rgb(231, 35, 35); */
	float: right;
	line-height: 1.8;
	/* font-weight:bold; */
}

.cart-add{
	position: relative;
}
.origin-country{
	position: absolute;
	bottom: 10px;
	right: 10px;
}
.origin-country .badge{
	background-color: rgba(119, 119, 119,0.9);
}

</style>
<script type="text/javascript">
	require([ 'admin/episode/preview' ]);
</script>
</head>
<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" ng-controller="EpisodeController as c" class="container-fluid">
			<div>
				当前团购：
				<span ng-if="result.episode.result" ng-cloak>
					<em ng-bind="::result.episode.result.deliveryDate|date:'M月d号'"></em>，<em ng-bind="::result.episode.result.deliveryDate| date:'EEEE'"></em> 地点：<em ng-bind="::result.episode.result.deliveryAddress"></em>
				</span>
				<span ng-if="!result.episode.result" ng-cloak> 当前没有团购，如有需要，可联系客服。 </span>
			</div>
			<hr />
			<ul class="row list-unstyled" ng-cloak>
				<li class="item col-xxs-12 col-xs-6 col-sm-4 col-md-3 avg-lg-5" ng-repeat="item in result.episode.records | orderBy: '-attrs[\'水果进口\']' ">
					<ol class="list-unstyled thumbnail ">
						<li class="l1" style="position: relative;">
						<a ng-href="${contextPath}/page/episode/{{::item.episodeProductId}}.html?title={{::item.attrs['产品标题']}}">
						<img class="img-responsive" alt="水果缩略图" ng-src="{{::item.attrs['产品搜索缩略图'][0].url | thumb:'M' }}"></a>
						<div class="origin-country">
								<span class="badge"> {{::item.attrs['水果产地']}} </span>
						</div>
						</li>
		
						<li class="l-desc"><span my-overflow-ellipsis="::item.attrs['产品子标题']" title="{{::item.attrs['产品子标题']}}">{{::item.attrs['产品子标题']}}</span></li>
						<!-- 第1行：描述，比如 大个车厘子，加拿大樱桃，热卖的泰国芒果，2行 -->
		
						<li class="l-price clearfix"><b class="pull-left price">{{::item.price * 0.85 |currency}}/{{::item.attrs['水果销售单位数量']}}{{::item.attrs['水果销售单位名称']}}</b>
							<span class="r">{{::item.attrs['水果净重说明']}}</span></li>
						<!-- 价格，单位 保证重量 -->
						<li class="l-name ellipsis" title="{{::item.attrs['产品标题']}}" >{{::item.attrs['产品标题']}}</li>
						<!-- 名称一行，比如佳沛猕猴桃 -->
						<li class="cart-add">
							<span class="decrease" href="" ng-click="buyCount=(buyCount<=1)?buyCount:buyCount-1"><i class="al al-jian"></i></span>
							<input type="number" min="1" max="99" step="1" ng-model="buyCount" ng-init="buyCount=1">
							<span class="increase" href="" ng-click="buyCount=(buyCount>=99)?buyCount:buyCount+1"> <i class="al al-jia"></i></span> 
							<button class="btn btn-danger" ng-click="c.addToCart(item,buyCount)">放入购物车</button>
						</li>
						<!-- 快捷添加到购物车 -->
				</ol>
				</li>
			</ul>
		</div>
		<jsp:include page="/WEB-INF/jsp/includes/fragment/footer.jsp" />
	</div>
</body>
</html>
</compress:html>
</cache:cache>
