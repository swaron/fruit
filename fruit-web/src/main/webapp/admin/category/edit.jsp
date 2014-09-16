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
<title>编辑类别</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
li.attr span{
	border: 1px solid #223312;
}
</style>
<script type="text/javascript">
	require([ 'admin/category/edit' ]);
</script>
</head>

<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" class="container-fluid">
			<div ng-controller="CategoryController as c">
				<div class="clearfix">
					<h4 class="pull-left">设置类别属性(<span ng-bind="model.category.categoryId == null ?'新增':'编辑' "></span>)</h4>
					<div class="pull-right">
						<a type="button" href="${contextPath}/admin/category.html" class="btn btn-default"> <i
							class="al al-chexiao"> </i>返回
						</a>
						<button type="button" class="btn btn-default" ng-disabled="form.$invalid" ng-click="c.saveAndReturn(form)">
							<i class="al" ng-class='{"al-baocun":!saving,"al-jiazaizhong al-spin":saving}'> </i>保存并返回
						</button>
					</div>
				</div>
				<hr />
				<ul class="nav nav-tabs" role="tablist">
					<li class="active"><a href="#tab1" role="tab" data-toggle="tab">基本属性</a></li>
					<li><a href="#tab2" role="tab" data-toggle="tab">产品属性</a></li>
				</ul>
				<div class="tab-content">
					<div class="tab-pane active" id="tab1">
						<form name="form">

							<table class="table table-borderless" style="max-width: 700px;">
								<tr>
									<td style="width: 120px;"><label for="categoryName">类别名称</label></td>
									<td><input type="text" class="form-control" name="categoryName" ng-model="model.category.name" my-errors required /></td>
								</tr>
								<tr>
									<td><label for="parent">父类别</label></td>
									<td><input type="text" class="form-control" name="parent" ng-model="view.parentDesc" list="dl-categorys" ng-change="c.changeParentCategory($event)" required />
									<datalist id="dl-categorys" ng-init="c.initCategorys()">
										<option ng-repeat="category in result.categorys | filter: form.parent.$viewValue" value="{{::category.categoryId}}.{{::category.name}}" />
										</datalist>
										</td>
								</tr>
								<tr>
									<td><label>显示排序</label></td>
									<td><input type="number" class="form-control" name="displayOrder" ng-model="model.category.displayOrder" placeholder="整形，小的拍前面" />
										</td>
								</tr>
							</table>
						</form>
					</div>
					<div class="tab-pane" id="tab2">
						<ul class="extra-prop-content list-unstyled" style="padding: 8px;">
							<li class="attr"><label>父类能继承的属性：</label><span ng-repeat="(key, val) in result.parent.attrs">{{key}}</span> </li>
							<li class="attr"><label>所有能继承的属性：</label> <a href="" ng-click="c.findTotalAttr(result.parent,$event)"><i class="al al-yulan"></i>点击获取</a>
								<span ng-repeat="(key, val) in result.parent.mergedAttrs">{{val.namePath}}:{{key}}</span>
							</li>
							<li><label>是否继承父类属性：<input type="checkbox" name="inheritAttr" ng-model="model.category.inheritAttr" />
							</label></li>
							<li class="attr"><label>新增的属性：</label><span ng-repeat="key in model.category.attrs | keys">{{key}} <a href="" ng-click="c.removeAttrFromCategory(key)"><i class="al al-cuowu" ></i></a> </span>   </li>
						</ul>
						<div class="search-attr">
							<form class="query-panel form-inline">
								<div class="clearfix">
									<div class="form-group">
										<label>属性名称：</label><input class="form-control" name="attrCode" ng-model="query.attrCode">
									</div>
									<div class="form-group" style="width: auto;">
										<button type="submit" class="btn btn-default" ng-click="c.searchAttr()">
											<i class="al al-sousuo"> </i>查询
										</button>
									</div>
								</div>
							</form>

							<table class="table table-bordered table-compact">
								<thead>
									<tr>
										<th>属性代码</th>
										<th>说明</th>
										<th>系统属性</th>
										<th>前端类型</th>
										<th style="min-width: 300px;">前端模型</th>
										<th>属性名称</th>
										<th>命令</th>
									</tr>
								</thead>
								<tr ng-repeat="item in result.attrs.records" ng-cloak>
									<td>{{:: item.attrCode }}</td>
									<td>{{:: item.note }}</td>
									<td>{{:: item.system | boolean }}</td>
									<td>{{:: item.frontendInput }}</td>
									<td><input class="form-control" ng-model="item.attrModel" ng-list /> </td>
									<td>{{:: item.frontendLabel }}</td>
									<td><a href="" ng-click="c.addAttrToCategory(item)">添加到当前类别</a> </td>
								</tr>
							</table>
						</div>
					</div>

				</div>

				<div class="clearfix">
					<hr />
					<div class="pull-right">
						<a type="button" href="${contextPath}/admin/category.html" class="btn btn-default"> <i
							class="al al-chexiao"> </i>返回
						</a>
						<button type="button" class="btn btn-default" ng-disabled="form.$invalid" ng-click="c.saveAndReturn(form)">
							<i class="al" ng-class='{"al-baocun":!saving,"al-jiazaizhong al-spin":saving}'> </i>保存并返回
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
