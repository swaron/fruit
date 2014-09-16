<%@page session="false" trimDirectiveWhitespaces="true" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="compress" uri="http://htmlcompressor.googlecode.com/taglib/compressor"%>
<!doctype html>
<html lang="zh">
<head>
<title>粒粒果鲜-当期鲜果</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
</style>
<script type="text/javascript">
	
</script>
</head>
<body>
	<div id="wrapper">
		<div id="header">
			<!-- top bar -->
			<div class="box-warning">
				开发中......
				<!--[if lt IE 8]>
由于性能，html5支持等原因，更新到IE9+, 或者使用Chrome等现代浏览器访问本站效果较好。
<![endif]-->
			</div>
			<div class="top-bar">
				<!-- <div class="container-fluid topbar"></div> -->
				<nav class="navbar navbar-default" role="navigation">
					<div class="container-fluid">
						<ul class="nav navbar-nav">
							<li>
								<div class="logo-stack">
									<i class="logo-bottom al-stack-1x"></i> <i class="logo-top al-stack-1x"></i>
								</div> <a >粒粒果鲜</a>
							</li>
						</ul>
						<ul class="nav navbar-nav pull-right">
						</ul>
					</div>
				</nav>
			</div>
		</div>
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
	</div>
</body>
</html>
