<%@page session="false" trimDirectiveWhitespaces="true" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="compress" uri="http://htmlcompressor.googlecode.com/taglib/compressor"%>
<%@taglib prefix="cache" uri="http://www.opensymphony.com/oscache"%> <s:eval var="profile" expression="@envProps['spring.profiles.active']" scope="request"></s:eval>
<%
	response.setHeader("Cache-Control", "max-age=" + 3600*24*2 + ", must-revalidate"); //HTTP 1.1    
	response.setDateHeader("Expires", System.currentTimeMillis() + 3600*24*2 * 1000L); //prevents caching at the proxy server
%>

<!doctype html>
<html lang="zh">
<head>
<title>产品列表-粒粒果鲜</title>
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
.cart-add .origin-country{
	position: absolute;
	right: 0px;
	top: 4px;
}

</style>
</head>
<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" ng-controller="EpisodeController as c" class="container-fluid">
			<hr />
			<ul class="row list-unstyled">
				<c:forEach items="${records}" var="item">
				<li class="item col-xs-6 col-sm-4 col-md-3 avg-lg-5">
					<ol class="list-unstyled thumbnail ">
						<li class="l1" style="position: relative;">
						<a href='${contextPath}/page/episode/${item.episodeProductId}.html?title=${attrs[item.productId]["产品标题"]}'>
						<img class="img-responsive" alt="水果缩略图" src='${attrs[item.productId]["产品搜索缩略图"][0].url}&w=360&h=360'></a></li>

						<li class="l-desc"><span title='${attrs[item.productId]["产品子标题"]}'>${attrs[item.productId]["产品子标题"]}</span></li>
						<!-- 第1行：描述，比如 大个车厘子，加拿大樱桃，热卖的泰国芒果，2行 -->

						<li class="l-price clearfix"><b class="pull-left price">${attrs[item.productId]["水果销售单位数量"]}${attrs[item.productId]["水果销售单位名称"]}</b>
							<span class="r">${attrs[item.productId]["水果净重说明"]}</span></li>
						<!-- 价格，单位 保证重量 -->
						<li class="l-name ellipsis" title="${attrs[item.productId]['产品标题']}" >${attrs[item.productId]["产品标题"]}</li>
						<!-- 名称一行，比如佳沛猕猴桃 -->
				</ol>
				</li>
				</c:forEach>
			</ul>
			<script type="text/javascript">
				setTimeout(function(){
					window.location.href = "${contextPath}/";
				},50000); 
			</script>
		</div>
		<jsp:include page="/WEB-INF/jsp/includes/fragment/footer.jsp" />
	</div>
</body>
</html>
