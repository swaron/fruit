<%@page session="false" trimDirectiveWhitespaces="true" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="compress" uri="http://htmlcompressor.googlecode.com/taglib/compressor"%>
<%@taglib prefix="cache" uri="http://www.opensymphony.com/oscache"%> <s:eval var="profile" expression="@envProps['spring.profiles.active']" scope="request"></s:eval>

<cache:cache refresh="${profile=='dev' }">
<compress:html compressJavaScript="true" compressCss="true" preserveLineBreaks="true" removeMultiSpaces="false" yuiCssLineBreak="120" yuiJsLineBreak="120">
<!doctype html>
<html lang="zh">
<head>
<title>粒粒果鲜-当期鲜果</title>
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

.p2 {
	font-weight: bold;
	font-size: 1.2em;
}

.l-desc {
	padding-top: 8px;
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
.weight {
	position: absolute;
	top: 20px;
	right: 10px;
}
.weight.badge {
	color: rgb(100, 100, 100);
	background-color: rgba(243, 243, 243, 0.8);
}
@media (max-width: 479px){
	li.item .thumbnail:before {
	    content: " ";
	    display: table;
	}
	li.item .thumbnail:after {
	    content: " ";
	    display: table;
	    clear: both;
	}
	li.item .thumbnail .l1{
		float: left;
		width: 38.2%;
	}
	li.item .thumbnail .l2{
		width: 61.7%;
		float: left;
		padding-left: 4px;
	}
}
.fruit-show-title .badge{
	position: absolute;
/* 	color: rgb(119, 119, 119);
	background-color: rgb(255, 255, 255); */
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
<script type="text/javascript">
	require([ 'page/episode/current' ]);
</script>
</head>
<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" ng-controller="EpisodeController as c" class="container-fluid">
			<ul class="activity-slider list-unstyled" ng-cloak>
				<!-- <img ng-repeat="img in result.activityImages" class="img-responsive" style="width:100%;" ng-src="{{img.imageUrl[] | autofit:'#main .activity-slider' }}"> -->
				<li ng-repeat="img in result.activityImages" style="position: relative;">
					<a ng-href="{{::utils.url('/page/home-banner/intro.html',{'homeBannerId':img.homeBannerId} )}}">
						<img class="img-responsive" style="width:100%;" ng-src="{{img.imageUrl[0].url | autofit:'#main .activity-slider' }}">
					</a>
					<div class="fruit-show-title" ng-cloak>
						<a ng-show="img.modelName" class="badge name" ng-href="{{::img.modelLink}}"><span ng-bind="::img.modelName"></span>  </a><br/>
						<span ng-show="img.modelSlogan" class="badge slogan" ng-bind="::img.modelSlogan"> </span>
					</div>
				</li>
				
			</ul>
			<hr />
			<div>
				当前团购：
				<span ng-if="result.episode.result" ng-cloak>
					<em ng-bind="result.episode.result.deliveryDate|date:'M月d号'"></em>，<em ng-bind="result.episode.result.deliveryDate| date:'EEEE'"></em> 地点：<em ng-bind="result.episode.result.deliveryAddress"></em>
				</span>
				<span ng-if="result.episode.total === 0" ng-cloak> 当前没有可预订的团购，如有需要，可联系客服。 </span>
			</div>
			<hr class="sub" />
			<ul class="row list-unstyled" ng-cloak>
				<li class="item col-xxs-12 col-xs-6 col-sm-4 col-md-3 avg-lg-5" ng-repeat="item in result.episode.records | orderBy: ['-attrs[\'水果进口\']','attrs[\'产品标题\']'] ">
					<ol class="list-unstyled thumbnail ">
						<li class="l1" style="position: relative;">
							<a ng-href="${contextPath}/page/episode/{{::item.episodeProductId}}.html?title={{::item.attrs['产品标题']}}">
								<img class="img-responsive" alt="水果缩略图" ng-src="{{::item.attrs['产品搜索缩略图'][0].url | phonefit:'ul li.item:eq('+$index+') ol li.l1':'M' }}">
							</a>
							<div class="origin-country">
									<span class="badge"> {{::item.attrs['水果产地']}} </span>
							</div>
							<span class="weight badge">{{::item.attrs['水果净重说明']}}</span>
						</li>
						<li class="l2">
							<div class="l-desc"><span my-overflow-ellipsis="::item.attrs['产品子标题']" title="{{::item.attrs['产品子标题']}}">{{::item.attrs['产品子标题']}}</span></div>
							<!-- 第1行：描述，比如 大个车厘子，加拿大樱桃，热卖的泰国芒果，2行 -->
	
							<div class="l-price clearfix"><b class="pull-left price">{{::item.price|currency}}/{{::item.attrs['水果销售单位数量']}}{{::item.attrs['水果销售单位名称']}}</b>
								</div>
							<!-- 价格，单位 保证重量 -->
							<div class="l-name ellipsis text-primary" title="{{::item.attrs['产品标题']}}" >{{::item.attrs['产品标题']}}</div>
							<!-- 名称一行，比如佳沛猕猴桃 -->
							<div class="cart-add">
								<span class="decrease" href="" ng-click="buyCount=(buyCount<=1)?buyCount:buyCount-1"><i class="al al-jian"></i></span>
								<input type="number" min="1" max="99" step="1" ng-model="buyCount" ng-init="buyCount=1">
								<span class="increase" href="" ng-click="buyCount=(buyCount>=99)?buyCount:buyCount+1"> <i class="al al-jia"></i></span> 
								<button class="btn btn-primary" ng-click="c.addToCart(item,buyCount)">放入购物车</button>
							</div>
							<!-- 快捷添加到购物车 -->
						</li>
				</ol>
				</li>
			</ul>
			<ul class="list-unstyled" style="position: absolute;left: -1000px;"> <!-- 给搜索引擎专用的 -->
				<li> <a href="${contextPath}/page/episode/product-index.html" onclick="return false;" >产品列表</a> </li>
			</ul>
		</div>
		<jsp:include page="/WEB-INF/jsp/includes/fragment/footer.jsp" />
	</div>
</body>
</html>
</compress:html>
</cache:cache>
