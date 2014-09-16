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
<title>编辑新建首页横幅</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
.tab-content {
	border-left: solid 1px rgb(231, 230, 230);
	border-right: solid 1px rgb(231, 230, 230);
	border-bottom: solid 1px rgb(231, 230, 230);
}
</style>
<script type="text/javascript">
	require([ 'admin/home-banner/edit' ]);
</script>
</head>
<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" class="container-fluid">
			<div ng-controller="PageController as c">
				<div class="clearfix">
					<h4 class="pull-left">
						设置banner内容(
						<span ng-bind="model.banner.homeBannerId == null ?'新增':'编辑' "></span>
						)
					</h4>
					<div class="pull-right">
						<a type="button" href="${contextPath}/admin/home-banner.html" class="btn btn-default">
							<i class="al al-chexiao"> </i>返回
						</a>
						<button type="button" class="btn btn-default" ng-disabled="form1.$pristine" ng-click="c.saveAndReturn(form1)">
							<i class="al" ng-class='{"al-baocun":!saving,"al-jiazaizhong al-spin":saving}'> </i>保存并返回
						</button>
						<button type="button" class="btn btn-default" ng-disabled="form1.$pristine" ng-click="c.saveAndEdit(form1)">
							<i class="al" ng-class='{"al-baocun":!saving,"al-jiazaizhong al-spin":saving}'> </i>保存并继续编辑
						</button>
					</div>
				</div>
				<hr />
				<div>
					<form name="form1">
						<table class="table table-borderless" style="max-width: 700px;">
							<tr>
								<td><label> 配送点： </label></td>
								<td><select name="deliveryAddressName" class="form-control" ng-model="model.banner.deliveryAddressId" ng-options="addr.deliveryAddressId as addr.name for addr in result.addresses">
							    	<option value="">-- 任意 --</option>
							    </select></td>
							</tr>
							<tr>
								<td><label for="">开始日期</label></td>
								<td>
									<input type="date" my-date class="form-control" name="startTime"  ng-model="model.banner.startTime" >
								</td>
							</tr>
							<tr>
								<td><label for="">截止日期</label></td>
								<td>
									<input type="date" my-date class="form-control" name="endTime"  ng-model="model.banner.endTime" >
								</td>
							</tr>
							
							<tr>
								<td><label for="">作用范围</label></td>
								<td>
									<label><input type="radio" name="globalVisible"  ng-model="model.banner.globalVisible" ng-value="true">  全局 </label>
									<label><input type="radio" name="globalVisible"  ng-model="model.banner.globalVisible" ng-value="false"> 本地 </label>
								</td>
							</tr>
							<tr>
								<td><label for="">是否启用</label></td>
								<td>
									<label><input type="checkbox" name="enabled"  ng-model="model.banner.enabled" >  启用 </label>
								</td>
							</tr>
							<tr>
								<td><label>图片</label></td>
								<td><div my-attr-node attr-type="'image'" attr-code="'imageUrl'" attr-model="" attr-value="model.banner.imageUrl"></div>
								</td>
							</tr>
							<tr>
								<td><label for="">权重(权重大出现概率高)</label></td>
								<td>
									<input type="number" class="form-control" name="bannerWeight" ng-model="model.banner.bannerWeight" >
								</td>
							</tr>
							<tr>
								<td><label for="">模特帐号</label></td>
								<td><input type="text" class="form-control" name="modelAccountId" ng-model="model.banner.modelAccountId" /></td>
							</tr>
							<tr>
								<td><label for="">模特名字(显示在图片上)</label></td>
								<td><input type="text" class="form-control" name="modelName" ng-model="model.banner.modelName" /></td>
							</tr>
							<tr>
								<td><label for="">模特名字链接(点击名字跳转)</label></td>
								<td><input type="text" class="form-control" name="modelLink" ng-model="model.banner.modelLink" /></td>
							</tr>
							<tr>
								<td><label for="">模特水果秀语(有的话会出现在图片上) </label></td>
								<td><input type="text" class="form-control" name="modelSlogan" ng-model="model.banner.modelSlogan" /></td>
							</tr>
							<tr>
								<td><label for="">模特介绍 </label></td>
								<td><textarea class="form-control" name="modelIntroduction" ng-model="model.banner.modelIntroduction" placeholder="模特的身份介绍，由模特自己决定是否要写，写什么。" >
								</textarea></td>
							</tr>
							<tr>
								<td><label for="remark">备注</label></td>
								<td><input type="text" class="form-control" name="remark" ng-model="model.banner.remark" /></td>
							</tr>
						</table>
					</form>
				</div>
				<div class="clearfix">
					<hr />
					<div class="pull-right">
						<a type="button" href="${contextPath}/admin/home-banner.html" class="btn btn-default">
							<i class="al al-chexiao"> </i>返回
						</a>
						<button type="button" class="btn btn-default" ng-disabled="form1.$pristine" ng-click="c.saveAndReturn(form1)">
							<i class="al" ng-class='{"al-baocun":!saving,"al-jiazaizhong al-spin":saving}'> </i>保存并返回
						</button>
						<button type="button" class="btn btn-default" ng-disabled="form1.$pristine" ng-click="c.saveAndEdit(form1)">
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
