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
	很抱歉，由于使用了新技术的原因，IE7及以下无法使用本系统。请暂时使用IE9+/Chrome/Firefox/Safari等浏览器访问。如有建议，烦请点击底部"提意见"留言。
	</div>
<![endif]-->
	<ul class="brand-bar list-unstyled container-fluid">
		<li>
			<a class="logo-stack brand-img pull-left" href="${contextPath}/">
				<i class="logo-bottom al-stack-1x">
				</i> <i class="logo-top al-stack-1x"></i>
			</a>
		</li>
		<li><a class="brand-txt text-primary" href="${contextPath}/">粒粒果鲜</a></li>
	</ul>
	<hr/>
</div>
