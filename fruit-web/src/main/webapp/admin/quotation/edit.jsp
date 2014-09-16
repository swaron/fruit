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
<title>编辑报价表</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
.tab-content {
	border-left: solid 1px rgb(231, 230, 230);
	border-right: solid 1px rgb(231, 230, 230);
	border-bottom: solid 1px rgb(231, 230, 230);
}
</style>
<script type="text/javascript">
	require([ 'admin/quotation/edit' ]);
</script>
</head>

<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" class="container-fluid">
			<div ng-controller="PageController as c">
				<div class="clearfix">
					<h4 class="pull-left">设置报价内容(<span ng-bind="model.quotation.quotationId == null ?'新增':'编辑' "></span>)</h4>
					<div class="pull-right">
						<a type="button" href="${contextPath}/admin/quotation.html" class="btn btn-default">
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
				<ul class="nav nav-tabs" role="tablist">
					<li class="active"><a href="#tab1" role="tab" data-toggle="tab">基本属性</a></li>
				</ul>
				<div class="tab-content">
					<div class="tab-pane active" id="tab1">
						<form name="form1" >
							<table class="table table-borderless" style="max-width: 700px;">
								<tr>
									<td><label for="level1">一级类别</label></td>
									<td>
										<select class="form-control" name="level1" ng-model="view.level1" ng-init="view.level1=2" readonly >
											<option value="2">水果</option>
										</select>
									</td>
								</tr>
								<tr>
									<td><label for="level2">二级类别(水果大类)</label></td>
									<td>
										<select class="form-control" name="level2" ng-model="view.level2" ng-options="c.categoryId as c.name for c in result.categorys | parentId:view.level1"></select>
									</td>
								</tr>
								<tr>
									<td><label for="level3">三级类别(品牌品种)</label></td>
									<td>
										<select class="form-control" name="level3" ng-model="view.level3" ng-options="c.categoryId as c.name for c in result.categorys | parentId:view.level2"></select>
									</td>
								</tr>
								<tr>
									<td><label for="level4">四级类别(大小规格)</label></td>
									<td>
										<select class="form-control" name="level4" ng-model="view.level4" ng-options="c as c.name for c in result.categorys | parentId:view.level3"></select>
										<input type="text" class="form-control" name="categoryName" ng-model="model.quotation.categoryName" readonly />
									</td>
								</tr>
								<tr>
									<td><label for="seller">销售方</label></td>
									<td>
									<select class="form-control" name="sellerId" ng-model="view.seller" ng-options="seller as seller.name for seller in result.sellers | orderBy:name"></select>
									</td>
								</tr>
								<tr>
									<td><label for="price">单价</label></td>
									<td><input type="number" step="0.01" class="form-control" name="price" ng-model="model.quotation.price" required/></td>
								</tr>
								<tr>
									<td><label for="quantity">单位数量</label></td>
									<td><input type="number" class="form-control" name="quantity" ng-model="model.quotation.quantity" required/></td>
								</tr>
								<tr>
									<td><label for="unit">单位名称</label></td>
									<td>
									<div my-attr-node attr-type="'select'" attr-code="'unit'" attr-model='["斤","克","个","只","条","箱","包"]'
											attr-value="model.quotation.unit"></div>
									</td>
								</tr>
								<tr>
									<td><label for="happenTime">报价日期</label></td>
									<td><input type="date" my-date class="form-control" name="happenTime"
											ng-model="model.quotation.happenTime" placeholder="格式：1985-10-10" required/></td>
								</tr>
								<tr>
									<td><label>图片</label></td>
									<td><div my-attr-node attr-type="'image'" attr-code="'productPhotos'" attr-model=""
											attr-value="model.quotation.productPhotos"></div></td>
								</tr>
								<tr>
									<td><label for="remark">备注</label></td>
									<td><input type="text"  class="form-control" name="remark"
											ng-model="model.quotation.remark"/></td>
								</tr>
								
								<!-- <tr>
									<td><label for="basePrice">是否基本价格<br>(最小销售单位的价格)</label></td>
									<td><input type="checkbox" name="basePrice" class="form-control" ng-model="model.quotation.basePrice" /></td>
								</tr>
								<tr>
									<td><label for="normalizedPrice">公斤单价</label></td>
									<td><input type="number" step="0.01" class="form-control" name="normalizedPrice" ng-model="model.quotation.normalizedPrice" /></td>
								</tr> -->
							</table>
						</form>
					</div>
				</div>

				<div class="clearfix">
					<hr />
					<div class="pull-right">
						<a type="button" href="${contextPath}/admin/quotation.html" class="btn btn-default">
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
