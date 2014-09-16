<%@page session="false" trimDirectiveWhitespaces="true"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="compress"
	uri="http://htmlcompressor.googlecode.com/taglib/compressor"%>

<style>
.mit-tip:HOVER {
	text-decoration: underline;
	cursor: pointer;
}
</style>
<footer id="footer" class="footer">
	<div class="container">
		<div class="row footer-top">
			<div class="col-xs-3 col-md-2">
				<h5>购物指南 </h5>
				<ul class="list-unstyled">
					<li><a href="${contextPath}/page/help/question-10.html">购物流程</a></li>
					<li><a href="${contextPath}/page/help/question-11.html">会员制</a></li>
				</ul>
			</div>
			<div class="col-xs-3 col-md-2">
				<h5>支付配送 </h5>
				<ul class="list-unstyled">
					<li><a href="${contextPath}/page/help/question-20.html">支付方式</a></li>
					<li><a href="${contextPath}/page/help/question-21.html">配送说明</a></li>
				</ul>
			</div>
			<div class="col-xs-3 col-md-2">
				<h5>售后服务</h5>
				<ul class="list-unstyled">
					<li><a href="${contextPath}/page/help/question-30.html">退换货</a></li>
					<li><a title="留言提建议和反馈意见" href="${contextPath}/page/feedback/proposal.html">投诉与建议</a></li>
				</ul>
			</div>
			<div class="col-xs-3 col-md-2">
				<h5>关于我们</h5>
				<ul class="list-unstyled">
					<li><a href="${contextPath}/page/help/question-40.html" >粒粒果鲜</a></li>
					<li><a href="${contextPath}/page/help/question-41.html" >联系方式</a></li>
					<li><a href="${contextPath}/page/help/question-42.html" >联系客服</a></li>
				</ul>
			</div>
			<div class="col-xs-3 col-md-2">
				<h5>其他</h5>
				<ul class="list-unstyled">
					<li><a href="${contextPath}/page/help/question-51.html" >首页水果秀</a></li>
				</ul>
			</div>
		</div>
		<hr class="sub">
		<div class="row footer-buttom">
			<div class="text-center">
				<div id="copyright" class="text-center" style="">
					<span class="mit-tip"
						title="本网站源码提供MIT授权(基本意思是你可以免费用于个人或者商业。当然，本站对使用的任何后果不负责)，源码地址待定。">版权所有
						©2014</span> 上海恒岩信息技术有限公司； 沪ICP备14038527号 <span class="hide">&nbsp; rev ${appVersion}</span>
				</div>
			</div>
			<div class="col-md-1 text-right"></div>
		</div>
	</div>
</footer>
