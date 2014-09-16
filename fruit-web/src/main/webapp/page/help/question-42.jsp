<%@page session="false" trimDirectiveWhitespaces="true" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@taglib prefix="compress" uri="http://htmlcompressor.googlecode.com/taglib/compressor"%>
<%@taglib prefix="cache" uri="http://www.opensymphony.com/oscache"%>
<s:eval var="profile" expression="@envProps['spring.profiles.active']" scope="request"></s:eval>

<cache:cache refresh="${profile=='dev' }">
	<compress:html compressJavaScript="true" compressCss="true" preserveLineBreaks="true" removeMultiSpaces="false" yuiCssLineBreak="120" yuiJsLineBreak="120">
	<!doctype html>
	<html lang="zh">
<head>
<title>联系客服 - 粒粒果鲜</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
.weinxin-qr{
	width: 215px;
}
</style>
<link rel="stylesheet" type="text/css" href="./help.css?v=${appVersion }">
<script type="text/javascript">
</script>
</head>

<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/help-header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" class="container-fluid">
			<div class="clearfix">
				<jsp:include page="/WEB-INF/jsp/includes/fragment/help-side.jsp" />
				<div class="help-block">
					<h4>你可以通过以下任意一种方式联系客服人员：（工作时间：8:00 — 22:00）</h4>
					<ol>
						<li>(推荐) 添加微信号粒粒果鲜，然后通过微信给粒粒果鲜发送消息，在线客服人员会及时回复。<br/>
							<img class="weinxin-qr" alt="" src="${contextPath}/static/image/weixin/qrcode_liliguoxian_1.png"/>
						</li>
						<li>打电话或者短信: 18721920210/18513523832</li>
						<li>发送邮件到 18513523832&#64;wo&#46;cn</li>
					</ol>
				</div>
			</div>
		</div>
		<jsp:include page="/WEB-INF/jsp/includes/fragment/footer.jsp" />
	</div>
</body>
	</html>
	</compress:html>
</cache:cache>
