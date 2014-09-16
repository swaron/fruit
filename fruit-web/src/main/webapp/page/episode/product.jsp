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
<title><s:message htmlEscape="true" text="${param['title']}"></s:message> - 粒粒果鲜</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
ul.property {
	padding-top: 8px;
}

ul.property li label {
	width: 80px;
	text-align: left;
}

ul.property .cart-add {
	display: inline-block;
	vertical-align: middle;
}

.cart-add input {
	width: 50px;
}

.cart-add-bar {
	padding-left: 68px;
	margin-top: 20px;
}

.breadcrumb-wrapper {
	background-color: rgb(245, 245, 245);
}

.col-fix{
	margin-bottom: 15px;
}
.p-content{
	font-size: 14px;
	line-height: 1.6;
	color: #333;
	/* background-color: #fff; */
	padding: 4px;
	max-width: 780px;
	margin: 0 auto;
}
.thumb-container{
	width: 100%;
	max-width: 738px;
}
@media (min-width: 768px) {
	.product .col-sm-flex{
		margin-left: 392px;
	}
	.thumb-container{
		width: 392px;
	}
	.p-content{
		padding: 20px;
	}
}
.p-content .w650{
	max-width: 650px;
	margin: 0 auto;
}
.p-content .w750{
	max-width: 750px;
	margin: 0 auto;
}
.p-content .w850{
	max-width: 850px;
	margin: 0 auto;
}
.p-content .w920{
	max-width: 920px;
	margin: 0 auto;
	/* default is 920 */
}
</style>
<link rel="stylesheet" type="text/css" href="${asset}/css/markdown.min.css?v=${appVersion}" >
<script type="text/javascript">
	require([ 'page/episode/product' ]);
</script>
</head>
<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" ng-controller="PageController as c">
			<div class="container-fluid"></div>
			<hr />
			<div class="breadcrumb-wrapper">
				<ol class="breadcrumb container-fluid">
					<li><a href="${contextPath}/page/episode/current.html">首页</a></li>
					<li><a href="${contextPath}/page/episode/current.html">个人团购</a></li>
					<li class="active" ng-bind="model.product.attrs['产品标题']"></li>
				</ol>
			</div>
			<div class="container-fluid">
				<div class="row" data-desc="上半部">
					<div class="product col-md-10 col-sm-12" data-desc="商品图片和参数">
						<!--  嵌套栅格列 -->
						<div class="row">
							<!-- 左边图片，宽度100%;最大360，最小240px -->
							<div class="col-fix pull-left thumb-container" >
								<!-- 360 + 15*2 + 2px -->
								<div class="p_up_img" my-img-gallery-zoom="model.images">
									<div class="p_mainimg" style="border: 1px solid rgb(248, 166, 11); width:100%; height: auto;">
										<!-- 提前给像素，防止闪动 -->
										<a ng-href="{{::model.product.attrs['产品主图片集'][0].url }}">
											<img id="p_img_left_holder" class="img-responsive" alt="" ng-src="{{model.mimage.md}}">
										</a>
									</div>
									<div hy-thumb-slider="::model.product.attrs['产品主图片集']" thumb-md="model.mimage.md" thumb-lg="model.mimage.lg"></div>
								</div>
							</div>
							<!-- 右边参数，宽度自适应 -->
							<div class="col-sm-flex col-xs-12">
								<div class="p_up_detail">
									<div class="name">
										<h4 ng-bind="::model.product.attrs['产品标题']"></h4>
										<strong class="text-primary" ng-bind="::model.product.attrs['产品子标题']"></strong>
									</div>
									<ul class="property list-unstyled">
										<li><label>价&nbsp;格</label> <b class="price" ng-bind="model.product.price|currency"> </b></li>
										<li ng-cloak><label>数量&nbsp;</label> {{::model.product.attrs['水果销售单位数量']}}{{::model.product.attrs['水果销售单位名称']}} </li>
										<li ng-show="::model.product.attrs['水果品种']" ng-cloak><label>品&nbsp;种</label> <span ng-bind="::model.product.attrs['水果品种']"> </span></li>
										<li ng-show="::model.product.attrs['水果品牌']" ng-cloak><label>品&nbsp;牌</label> <span ng-bind="::model.product.attrs['水果品牌']"> </span></li>
										<li><label> 原 产 地</label> <span ng-bind="::model.product.attrs['水果产地']"></span></li>
										<%-- <li><label> 库存情况：</label> <span>有库存</span></li> --%>
										<li><label>配送时间</label> <span ng-cloak> {{::model.episode.deliveryDate|date:'M月d号'}} {{::model.episode.deliveryDate| date:'EEEE'}} </span></li>
										<li><label>配送地点</label> <span ng-bind="::model.episode.deliveryAddress"> </span></li>
										<li><label>购买数量</label>
											<div class="cart-add">
												<span class="decrease" href="" ng-click="buyCount=(buyCount<=1)?buyCount:buyCount-1"><i class="al al-jian"></i></span>
												<input type="number" min="1" max="99" step="1" ng-model="buyCount" ng-init="buyCount=1">
												<span class="increase" href="" ng-click="buyCount=(buyCount>=99)?buyCount:buyCount+1"> <i class="al al-jia"></i></span> 
											</div></li>
									</ul>
									<ul class="list-inline cart-add-bar">
										<li><button class="btn btn-primary" ng-click="c.addToCart(model.product,buyCount)">放入购物车</button></li>
										<li class="social-show hide">
											<!-- 分享功能，未完成 --> <span class="split sharespan">|</span> <span class="sharefont sharespan">分享：</span> <span class="share sharespan">
												<a href="javascript:void(0);" onclick="share.weibo()" class="weibo"></a>
												<a href="javascript:void(0);" onclick="share.kaixin001()" class="kaixin"></a>
												<a href="javascript:void(0);" onclick="share.qqweibo()" class="qq"></a>
												<a href="javascript:void(0);" onclick="share.renren()" class="renren"></a>
												<a href="javascript:void(0);" onclick="share.douban()" class="douban"></a>
											</span>
										</li>
									</ul>
									<div ng-show="::model.product.attrs['产品温馨提示']" ng-cloak><span> 温馨提示：</span> <p class="help-block" ng-bind="::model.product.attrs['产品温馨提示']"> </p></div>
								</div>
							</div>
						</div>
					</div>
					<div class="col-md-2 hidden-xs hidden-sm" data-desc="同类商品，最近查看商品，热销商品"></div>
				</div>
				<div bs-tabs> <!-- 下半部：产品详情， 值得买吗， 问题处理记录 --> <div bs-tab title="产品详情">
				<div class="p-content" ng-bind-html="::model.product.attrs['产品详细页面HTML内容'] | contentPreProcress "></div>
				</div> <!-- <div bs-tab-tpl title="值得买吗" template-url="./views/tab2.html"></div>
				<div bs-tab-tpl title="问题处理记录" template-url="./views/tab3.html"></div> --> </bs-tabs>
			</div>
		</div>
		<jsp:include page="/WEB-INF/jsp/includes/fragment/footer.jsp" />
	</div>
</body>
</html>
</compress:html>
</cache:cache>
