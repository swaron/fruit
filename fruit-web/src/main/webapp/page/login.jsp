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
<title>帐号登陆</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
form.center-block{
	width:300px;
}
</style>
<script type="text/javascript">
	require([ 'page/login' ]);
</script>
</head>
<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" class="container-fluid">
			<div ng-controller="LoginController as c">
				<form name="loginForm" class="center-block" ng-submit="c.submit(loginForm)">
					<h4 class="text-center">帐号登陆</h4>
					<div class="form-group">
						<input type="text" class="form-control" name="username" my-autofill placeholder="Email/QQ/手机号" autocomplete="on"
							ng-model="_login.model.username" my-errors>
					</div>
					<div class="form-group">
						<input type="password" class="form-control" name="password" placeholder="密码" my-autofill ng-model="_login.model.password" my-errors>
					</div>

					<div class="form-group clearfix checkbox">
						<div class="pull-right">
							<a ng-href="{{::utils.url('/page/user/want-password.html?from=login')}}" target="_blank" title="请查看注册时留的密码提示，或者联系客服。">忘记密码了?</a>
						</div>
						<div class="">
							<label title="若你帐号有余额，为了您的财产安全，请不要在网吧或公用电脑上使用此功能！"> <input title="记住密码，自动登录" type="checkbox"
								ng-model="_login.model.autoLogin" name="autoLogin" ng-init="_login.model.autoLogin=true"> <span>自动登录</span>
							</label>
						</div>
					</div>

					<div class="clearfix">
						<button id="submit-btn" type="submit" class="btn btn-primary">登陆</button>
						<div class="pull-right">
							<a ng-href="{{::utils.url('/page/user/register.html?from=login')}}" target="_blank" class="btn btn-default">注册</a>
						</div>
					</div>
				</form>
			</div>
			<br/>
		</div>
		<jsp:include page="/WEB-INF/jsp/includes/fragment/footer.jsp" />
	</div>
</body>
</html>
</compress:html>
</cache:cache>

