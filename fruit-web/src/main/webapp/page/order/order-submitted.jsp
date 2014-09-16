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
<title>订单提交结果 - 粒粒果鲜</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
.item-img {
	width: 120px;
}
</style>
<script type="text/javascript">
	require([ 'page/order/order-submitted' ]);
</script>
</head>

<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" ng-controller="PageController as c" class="container-fluid">
			<div>
				<div class="alert alert-info fade in" role="dialog">
					<h4>
						<span>
							<i ng-class="{'al al-zhengque':(result.order.success===true),'al al-jinzhi':(result.order.success===false),'al al-spin al-jiazaizhong':(result.order.success==null)}"></i>
						</span> 
						<span ng-cloak> {{ (result.order.success)?"订单已生效!": result.order.message}} </span>
					</h4>
					<div ng-cloak>
						<div>
							<label>订单号：</label> <span ng-bind="result.order.result.orderNum"></span>
						</div>
						<div>
							<label>总价：</label> ￥{{result.order.result.totalPayAmount}}
						</div>
						<div><label>配送时间：</label> {{::result.order.result.deliveryDate | date:'M月d号'}}，{{::result.order.result.deliveryDate| date:'EEEE'}}</div>
					</div>
				</div>
			</div>
			<div>
				您可以：<a href="${contextPath}/page/order/center.html">查看订单状态</a>，<a href="${contextPath}/">继续购物</a>
			</div>
		</div>
		<jsp:include page="/WEB-INF/jsp/includes/fragment/footer.jsp" />
	</div>
</body>

</html>
</compress:html>
</cache:cache>
