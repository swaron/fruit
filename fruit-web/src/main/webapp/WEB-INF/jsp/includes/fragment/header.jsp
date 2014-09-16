<%@page session="false" trimDirectiveWhitespaces="true" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@taglib prefix="compress" uri="http://htmlcompressor.googlecode.com/taglib/compressor"%>
<div id="header">
	<!-- top bar -->
<!--[if lt IE 8]>
	<div class="box-warning">
	很抱歉，由于使用了新技术的原因，IE7及以下无法使用本系统。请暂时使用IE9+/Chrome/Firefox/Safari等浏览器访问。如有建议，烦请点击底部"投诉与建议"留言。
	</div>
<![endif]-->
	<div class="box-warning hide">
		网站处于试运行阶段，线下团购已经开始，选择水果之后，请联系客服，客服会线下会安排送货。
	</div>
	<div class="top-bar">
		<!-- <div class="container-fluid topbar"></div> -->
		<nav class="navbar navbar-default" role="navigation">
			<div class="container-fluid">
				<ul class="nav navbar-nav pull-right">
				<c:if test="${profile eq 'dev'}">
					<li><span class="">
							<input id="debug-btn" type="checkbox" value="">
							debug
						</span></li>
					<script type="text/javascript">
						require([ "app/cookie" ], function(cookies) {
							var el = angular.element(document.getElementById('debug-btn'));
							el.attr('checked', cookies('debug'));
							el.on('click', function(event) {
								if (event.target.checked) {
									cookies('debug', 'true', {
										expires : 7,
										path : '/'
									});
									console.log('click on debug; checked:true');
								} else {
									console.log('click on debug; checked:false');
									cookies('debug', 'false', {
										expires : -1,
										path : '/'
									});
								}
							});
						});
					</script>
				</c:if>
					<li><a style="max-width: 250px; display: block;" class="ellipsis" href="javascript:void(0)" ng-click="_user.selectAddress()">
							配送到：
							<span ng-bind="_repo.model.address.name"></span>
						</a></li>
					<li><a href="${contextPath}/page/cart.html" class="cart">
							<i class="al al-gouwuchekong"></i>&nbsp;购物车
							<span class="badge bg-primary" ng-bind="_cart.getCount()"></span>
						</a>
						<a ng-hide="::_user.isAuthenticated" href="${contextPath}/page/login.html">登陆</a> 
						<a ng-hide="::_user.isAuthenticated" href="${contextPath}/page/user/register.html">注册</a>
						<a ng-if="::_user.isAuthenticated" href="${contextPath}/page/order/center.html">订单中心</a> 
						<a ng-if="::_user.isAuthenticated" ng-bind="::_repo.model.user.username"></a>
						<a ng-if="::_user.isAuthenticated" href="javascript:void(0)" ng-click="_user.logout()">退出</a></li>
				</ul>
			</div>
		</nav>
	</div>
	<ul class="brand-bar list-unstyled container-fluid">
		<li>
			<a class="logo-stack brand-img pull-left" href="${contextPath}/">
				<i class="logo-bottom al-stack-1x">
				</i> <i class="logo-top al-stack-1x"></i>
			</a>
		</li>
		<li><a class="brand-txt " href="${contextPath}/">粒粒果鲜</a></li>
		<li class="pull-right">
			<a href="${contextPath}/page/help/question-42.html" ><i class="al al-duihua"></i> 联系客服</a>
			<a href="${contextPath}/page/feedback/proposal.html">反馈问题</a>
		</li>
	</ul>
</div>
