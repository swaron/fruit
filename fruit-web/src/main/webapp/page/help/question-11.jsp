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
<title>会员制 - 粒粒果鲜</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
</style>
<link rel="stylesheet" type="text/css" href="./help.css?v=${appVersion }">
</head>

<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/help-header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" class="container-fluid">
			<jsp:include page="/WEB-INF/jsp/includes/fragment/help-side.jsp" />
			<div class="help-block ">
				<h4>会员制</h4>
				<p>     会员分4级，具体为: 水晶会员,黄金会员,白金会员,钻石会员 </p>
				<ol>
					<li>
						<dl>
							<dt>普通会员</dt>
							<dd class="help-block2">条件：在注册粒粒果鲜官网的账户后，您自动成为粒粒果鲜的水晶会员</dd>
						</dl>
					</li>
					<li>
						<dl>
							<dt>黄金会员</dt>
							<dd class="help-block2">条件：一年内（自然年）消费金额超过1000元（含）的会员。</dd>
						</dl>
					</li>
					<li>
						<dl>
							<dt>白金会员</dt>
							<dd class="help-block2">条件：一年内（自然年）消费金额超过2500元（含）的会员。</dd>
						</dl>
					</li>
					<li>
						<dl>
							<dt>钻石会员</dt>
							<dd class="help-block2">条件：一年内（自然年）累计消费金额达到 4500 元（含）的会员。</dd>
						</dl>
					</li>
				</ol>
				<h4>粒粒果鲜将为各级别会员提供精美的礼品。</h4>
				<table class="table table-bordered">
					<tr class="bg-primary">
						<th>会员特权</th>
						<th>普通会员</th>
						<th>黄金会员</th>
						<th>白金会员</th>
						<th>钻石会员</th>
					</tr>
					<tr>
						<td>运费优惠</td>
						<td>0元起免运费</td>
						<td>0元起免运费</td>
						<td>0元起免运费</td>
						<td>0元起免运费</td>
					</tr>
					<tr>
						<td>三七节礼品</td>
						<td>/</td>
						<td>可享</td>
						<td>可享</td>
						<td>可享,更加精美</td>
					</tr>
					<tr>
						<td>生日礼品</td>
						<td>/</td>
						<td>/</td>
						<td>可享</td>
						<td>可享,更加精美</td>
					</tr>
				</table>
			</div>
		</div>
		<jsp:include page="/WEB-INF/jsp/includes/fragment/footer.jsp" />
	</div>
</body>
	</html>
	</compress:html>
</cache:cache>
