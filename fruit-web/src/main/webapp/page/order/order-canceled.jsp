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
<title>订单取消结果 - 粒粒果鲜</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
</style>
<script type="text/javascript">
	require([ 'page/order/order-canceled' ]);
</script>
</head>

<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" ng-controller="PageController as c" class="container-fluid">
			<div>
				<div class="alert alert-success fade in" role="dialog">
					<h4>
						<span ng-cloak> {{ model.cancelResultMessage }} </span>
					</h4>
					<div ng-cloak>
						<div>
							<label>订单号：</label> <span ng-bind="result.order.result.orderNum"></span>
						</div>
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
