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
<title>首页水果秀 - 粒粒果鲜</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
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
					<h4>首页水果秀</h4>
					<p>
						晒晒你的美颜吧，太久不见阳光会发霉的。即日起只要能晒上粒粒果鲜首页，就能获得水果一份哦。<br />
					</p>
					参与方式：请把相关资料发到客服邮箱 18513523832&#64;wo&#46;cn; 或者联系微信“粒粒果鲜”。说明参加首页水果秀，内容如下
					<ol class="">
						<li>至少一张照片，有条件的话纯色背景为佳。也可以直接拍和水果有关的照片。</li>
						<li>希望在水果秀上出现的名字，可以是英文名字，或者昵称，或者中文姓名。</li>
						<li>所在的配送地址。不同配送地址的首页水果秀可能会不一样。</li>
						<li>粒粒果鲜网站帐号，用于收水果赠品。</li>
						<li>对水果秀的附加要求或建议。</li>
					</ol>
					<div><label>说明：</label>
					<ol class="">
						<li>登上首页之后，至少同配送点的其他人可以看到包含你照片的水果秀。</li>
						<li>因为有的时候有多个候选者，我们无法保证每份作品都能上首页，敬请谅解。</li>
						<li>正常情况，水果秀模特一个月换一次，连任的模特可以连续获得水果礼品。</li>
					</ol>
					</div>
					<br>
					<h5>作品内容示例</h5>
					<ol>
						<li>
							<ul class="list-unstyled">
								<li><label>模特名字(会出现在图片上)</label>：iman</li>
								<li><label>广告语(通常为空，会出现在图片上)</label>：健康好生活，你值得拥有。</li>
								<li><label>模特介绍(不出现在图片上，会出现在详细页面)：</label><pre>模特自己写的合法的任意内容。</pre> </li>
								<li>
									<img class="img-responsive" alt="" src="http://www.liliguoxian.com/upload.do?id=d980be790152a6a5f3a340ada734ac0612bd1&w=1310">
								</li>
							</ul>
						</li>
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
