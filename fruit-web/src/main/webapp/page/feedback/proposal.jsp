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
<title>建议中心 - 粒粒果鲜</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">

ul.msg-list{
	margin-top:20px;
}
ul.msg-list li{
	padding: 8px;
	margin-bottom:4px;
}
</style>
<link rel="stylesheet" type="text/css" href="../help/help.css?v=${appVersion }">
<script type="text/javascript">
	require([ 'page/feedback/proposal' ]);
</script>
</head>
<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" ng-controller="PageController as c" class="container-fluid">
			<jsp:include page="/WEB-INF/jsp/includes/fragment/help-side.jsp" />
			<div class="help-block">
				<h4 class="">您的投诉与建议将帮助粒粒果鲜提供更好的产品与服务，感谢您的留言。</h4>
				<form name="msgForm" class="table-form clearfix bordered">
						<div class="form-group">
							<label>留言人：</label>
							<input placeholder="选填，会显示在留言列表里" class="form-control" ng-model="model.username" ng-init="model.username = _repo.model.user.username" />
						</div>
						<div class="form-group">
							<label for="content">留言内容：</label>
							<textarea name="content" class="form-control" required="required" ng-model="model.content" ></textarea>
						</div>
						<button type="submit" class="btn btn-default" ng-click="c.submit(msgForm)">
							<i class="al al-zhengque"> </i>提交
						</button>
				</form>
				<ul class="list-unstyled msg-list" ng-cloak>
					<li class="bordered" ng-repeat="item in result.msg.records">
						<div class="msg-aside msg-user-info">
							<b class="msg-nick">{{::item.username}}</b> 
							<span class="msg-time small text-muted">{{::item.updatedTime | date:'MM-dd HH:mm'}}</span>
						</div>
						<div class="msg-warp">
							{{::item.content}}
						</div>
					</li>
				</ul>
			</div>
		</div>
		<jsp:include page="/WEB-INF/jsp/includes/fragment/footer.jsp" />
	</div>
</body>
</html>
</compress:html>
</cache:cache>
