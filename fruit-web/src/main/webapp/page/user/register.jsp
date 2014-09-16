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
<title>注册帐号</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
.sm-panel {
	width: 100%;
}

@media ( min-width : 768px) {
	.sm-panel {
		width: 530px;
	}
}

.text-center .btn {
	width: 50%;
}

.form-group label {
	width: 200px;
	text-align: left;
}

.alert>p, .alert>ul {
	margin-bottom: 10px;
}

.slide.ng-enter, .slide.ng-leave {
	-webkit-transition: 0.5s linear all;
	transition: 0.5s linear all;
}

.slide.ng-enter {
	opacity: 0;
}
.slide.ng-enter-active {
	opacity: 1;
}
</style>
<script type="text/javascript">
	require([ 'page/user/register' ]);
</script>
</head>
<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" class="container-fluid">
			<div ng-controller="PageController as c">
				<form name="regForm" class="center-block sm-panel">
					<div ng-hide="result.username" >
						<h4 class="text-center">注册帐号</h4>
						<div class="form-group">
							<!-- <label for="username">账号名：</label> -->
							<input type="text" class="form-control" name="username" my-autofill placeholder="Email/QQ/手机号"  ng-model="model.username" my-errors required title="输入用户名" ng-model-options="{ updateOn: 'blur' }">
							<div class="help-block">请使用Email，QQ号码或者手机号码注册。请勿用别人的号码，因为号码拥有者将来可以收回帐号。</div>
						</div>
						<div class="form-group">
							<!-- <label for="password">输入密码：</label> -->
							<input type="password" class="form-control" name="password" placeholder="输入密码" ng-model="model.password" my-errors required title="输入密码">
							<div class="help-block">
								1.重要性不同的网站，密码尽量不同。这里的密码不要和您的Email,QQ，网银相同。<br />2.密码尽量有点个人特色，别用123456这种大众密码。
							</div>
						</div>
						<div class="form-group">
							<!-- <label for="password">重复密码：</label> -->
							<input type="password" class="form-control" name="password2" placeholder="重复密码" ng-model="model.password2" my-errors required title="重复密码">
						</div>
						<!-- <div class="form-group">
						<input class="form-control" name="hint" placeholder="密码提示，可选" ng-model="model.hint" my-errors title="密码提示，可选">
					</div> -->
						<div class="">
							<div class="text-center">
								<button ng-click="c.register(regForm)" type="submit" class="btn btn-default">注册</button>
							</div>
						</div>
					</div>
					<div class="alert alert-success slide" ng-cloak ng-if="result.username">
						<h3>恭喜，您已注册。</h3>
						<h4>您现在可以用 {{result.username}} 作为用户名登录了。</h4>
						<ul>
							<li>我们认真<b>服务</b>用户，有任何问题或者建议请联系客服。
							</li>
							<li>我们保持<b>价格</b>优势，促进生产消费循环。
							</li>
							<li>我们挑选<b>时令</b>鲜果，新鲜程度紧次于农场直采。
							</li>
						</ul>
						<p>我们希望成为我们用户水果方面的可靠助手，优质我们用户的生活。</p>
						<div>
							<a ng-href="${contextPath}/">返回首页</a>
						</div>
					</div>
				</form>
			</div>
			<br />
		</div>
		<jsp:include page="/WEB-INF/jsp/includes/fragment/footer.jsp" />
	</div>
</body>
</html>
</compress:html>
</cache:cache>
