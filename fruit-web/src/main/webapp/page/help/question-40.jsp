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
<title>自我介绍 - 粒粒果鲜</title>
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
			<div class="clearfix">
				<jsp:include page="/WEB-INF/jsp/includes/fragment/help-side.jsp" />
				<div class="help-block">
					<h4>好声音</h4>
					<dl>
						<dt>杨坤：请介绍下你自己？</dt>
						<dd>我的名字叫粒粒果鲜，来自上海闵行区浦江镇，是由几位IT民工和水果采购行家创建的。</dd>
					</dl>
					<dl>
						<dt>汪峰：你的梦想是什么？</dt>
						<dd>
							<p>粒粒果鲜的宗旨是做大家生活中的水果团购助手，并由行内人士控制好质量，我们的口号是：粒粒果鲜，帮你团购时令水果。</p>
							<p>水果的质量最主要是看新鲜度，粒粒果鲜的进货渠道与各大电商一样，都是一级批发市场。粒粒果鲜解决了损耗、减短供应链，以及省掉店租成本，由水果采购专家帮大家把价格降低。然后粒粒果鲜将当天采购的水果通过自建物流快速的送到您手上。</p>
							<p>粒粒果鲜目前的团购的水果都是时令水果，实际上，并不是所有反季节水果都是不好的，只是市场上有些反季节水果是用一些化学物质催熟、保鲜的，如果食用这些水果就会对身体不好。另外，我们从供应链的源头拿货，产品的质量也会得到有效控制，不可能出现以假乱真、以次充好的情况。然而每个人的口感喜好毕竟不一样，以及因为运输天气等因素，水果做到十全十美是不可能的。如果您有任何质量问题都可以联系我们的客服，我们一定把服务做到最好。</p>
						</dd>
					</dl>
					<dl>
						<dt>那英：你知道我去年带出个冠军的！</dt>
						<dd>
							<p>额…我还是讲讲粒粒果鲜的优势吧。粒粒果鲜主要的优势在团购、时令。</p>
							<p>因为精品店、水果实体店损耗大、店租高，所以正常情况下我们团购的成本低。现在电商大量涌现，最需要解决的是物流问题，而生鲜冷链非常复杂、成本很高，每种产品对物流的要求都是不一样的，比如鸡蛋、杨梅、冰冻虾对运输的要求简直天壤之别，所以现在做的比较好的一般是单品大份量的东西，比如5斤车厘子，5斤荔枝等，总体上来说全国性水果电商的前途并不明朗。</p>
							<p>有时候，我们的水果也可能比别家贵，产地、品种、外观等不一样都可能是原因，更可恶的是还有一些不法商家拿假货冒充品牌水果。另外，水果的行情千变万化，再加上有各种商家在进行单品促销、价格战、过期前甩货的情况。</p>
						</dd>
					</dl>
				</div>
			</div>
		</div>
		<jsp:include page="/WEB-INF/jsp/includes/fragment/footer.jsp" />
	</div>
</body>
	</html>
	</compress:html>
</cache:cache>
