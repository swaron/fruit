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
<title>配送说明- 粒粒果鲜</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
h4 {
	padding-left: 1em;
}
</style>
<link rel="stylesheet" type="text/css" href="./help.css?v=${appVersion }">
</head>

<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/help-header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" class="container-fluid">
			<div class="clearfix">
				<jsp:include page="/WEB-INF/jsp/includes/fragment/help-side.jsp" />
				<div class="help-block">
					<h4>配送说明：粒粒果鲜分为两种模式，多人团购和一人团购。</h4>
					<ul>
						<li>
							<p>
								多人团购，粒粒果鲜将定时定点组织公司、社区进行常态团购水果，下单时间截止到配送当天的0:00，0元起免配送费； <br />第一期开通的配送点包含：
							</p>
							<table class="table table-bordered">
								<tr class="bg-primary">
									<th>配送点</th>
									<th>配送时间</th>
								</tr>
								<tr>
									<td>工行数据中心（上海自贸区）</td>
									<td>每周一、三、五，法定节假日除外</td>
								</tr>
								<tr>
									<td>农行数据中心</td>
									<td>每周一、三、五，法定节假日除外</td>
								</tr>
								<tr>
									<td>同济大学建筑设计研究院）</td>
									<td>每周二、四，法定节假日除外</td>
								</tr>
								<tr>
									<td>锦绣满堂（浦东花木）</td>
									<td>每周一至周五，法定节假日除外</td>
								</tr>
							</table>
						</li>
						<li>一人团购（包括送礼、公司采购等），只要金额超过1500元，支持上海外环线内、奉贤全区、闵行全区配送，免配送费。当天下单，次日到。下单时间截止到配送当天的0:00。</li>
					</ul>

				</div>
			</div>
		</div>
		<jsp:include page="/WEB-INF/jsp/includes/fragment/footer.jsp" />
	</div>
</body>
	</html>
	</compress:html>
</cache:cache>
