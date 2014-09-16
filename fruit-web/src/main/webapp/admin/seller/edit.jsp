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
<title>编辑批发商信息</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
.tab-content {
	border-left: solid 1px rgb(231, 230, 230);
	border-right: solid 1px rgb(231, 230, 230);
	border-bottom: solid 1px rgb(231, 230, 230);
}
</style>
<script type="text/javascript">
	require([ 'admin/seller/edit' ]);
</script>
</head>

<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" class="container-fluid">
			<div ng-controller="PageController as c">
				<div class="clearfix">
					<h4 class="pull-left">设置内容(<span ng-bind="model.seller.sellerId == null ?'新增':'编辑' "></span>)</h4>
					
					<div class="pull-right">
						<a type="button" href="${contextPath}/admin/seller.html" class="btn btn-default">
							<i class="al al-chexiao"> </i>返回
						</a>
						<button type="button" class="btn btn-default" ng-disabled="propForm.$pristine" ng-click="c.saveAndReturn(propForm)">
							<i class="al" ng-class='{"al-baocun":!saving,"al-jiazaizhong al-spin":saving}'> </i>保存
						</button>
						<button type="button" class="btn btn-default" ng-disabled="propForm.$pristine" ng-click="c.saveAndEdit(propForm)">
							<i class="al" ng-class='{"al-baocun":!saving,"al-jiazaizhong al-spin":saving}'> </i>保存并继续编辑
						</button>
					</div>
				</div>
				<hr />
				<ul class="nav nav-tabs" role="tablist">
					<li class="active"><a href="#tab1" role="tab" data-toggle="tab">批发商信息</a></li>
				</ul>
				<div class="tab-content">
					<div class="tab-pane active" id="tab1">
						<form name="propForm" >
							<table class="table table-borderless" style="max-width: 700px;">
								<tr>
									<td><label>名称</label></td>
									<td><input type="text" class="form-control" name="name" ng-model="model.seller.name" /></td>
								</tr>
								<tr>
									<td><label>描述</label></td>
									<td><input type="text" class="form-control" name="comments" ng-model="model.seller.comments" /></td>
								</tr>
							</table>
						</form>
					</div>
				</div>

				<div class="clearfix">
					<hr />
					<div class="pull-right">
						<a type="button" href="${contextPath}/admin/seller.html" class="btn btn-default">
							<i class="al al-chexiao"> </i>返回
						</a>
						<button type="button" class="btn btn-default" ng-disabled="propForm.$pristine" ng-click="c.saveAndReturn(propForm)">
							<i class="al" ng-class='{"al-baocun":!saving,"al-jiazaizhong al-spin":saving}'> </i>保存
						</button>
						<button type="button" class="btn btn-default" ng-disabled="propForm.$pristine" ng-click="c.saveAndEdit(propForm)">
							<i class="al" ng-class='{"al-baocun":!saving,"al-jiazaizhong al-spin":saving}'> </i>保存并继续编辑
						</button>
					</div>
				</div>
			</div>
		</div>
		<jsp:include page="/WEB-INF/jsp/includes/fragment/footer.jsp" />
	</div>
</body>
</html>
</compress:html>
</cache:cache>
