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
<title>编辑属性</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
.tab-content {
	border-left: solid 1px rgb(231, 230, 230);
	border-right: solid 1px rgb(231, 230, 230);
	border-bottom: solid 1px rgb(231, 230, 230);
}
</style>
<script type="text/javascript">
	require([ 'admin/attribute/edit' ]);
</script>
</head>

<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" class="container-fluid">
			<div ng-controller="PropController as c">
				<div class="clearfix">
					<h4 class="pull-left">设置属性内容(<span ng-bind="model.attribute.attrId == null ?'新增':'编辑' "></span>)</h4>
					
					<div class="pull-right">
						<a type="button" href="${contextPath}/admin/attribute.html" class="btn btn-default">
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
					<li class="active"><a href="#tab1" role="tab" data-toggle="tab">基本属性</a></li>
				</ul>
				<div class="tab-content">
					<div class="tab-pane active" id="tab1">
						<form name="propForm" >
							<input type="hidden" name="attr_id"/>
							<table class="table table-borderless" style="max-width: 700px;">
								<tr>
									<td style="width: 120px;"><label for="attrCode">属性代码</label></td>
									<td><input type="text" class="form-control" name="attrCode" ng-model="model.attribute.attrCode" my-errors dbunique required="required"
										dbunique-msg="代码{1}已存在，请修改" minlength="2" minlength-msg="最小长度为{0}" maxlength="30"
										placeholder="尽量使用和前端标签一样的值" />
										<div class="help-block" ng-hide="propForm.attrCode.$invalid && propForm.attrCode.$dirty">必须具备唯一性且不包含空格。属性代码的长度最大值不能超过 30 个符号。</div>
										</td>
								</tr>
								<!-- <tr>
									<td><label>前端标签</label></td>
									<td><input type="text" class="form-control" name="frontendLabel" ng-model="model.attribute.frontendLabel" placeholder="系统主要用属性代码，很少用到这个标签，可为空。" /></td>
								</tr> -->
								<tr>
									<td><label>属性说明</label></td>
									<td><input type="text" class="form-control" name="note" ng-model="model.attribute.note" /></td>
								</tr>
								<tr>
									<td><label for="frontendInput">前端输入类型</label></td>
									<td><select name="frontendInput" title="店铺所有者的分类输出类型" class="form-control" required="required" ng-model="model.attribute.frontendInput">
											<option value="text">文本字段</option>
											<option value="textarea">文本区</option>
											<option value="statictext">静态文本</option>
											<option value="boolean">是/否</option>
											<option value="datetime">日期和时间</option>
											<option value="date">日期</option>
											<option value="select">单选下拉菜单</option>
											<option value="multiselect">多选下拉菜单</option>
											<option value="image">图片</option>
											<option value="multiimage">图片集</option>
											<option value="multifile">文件集</option>
											<option value="number">数字</option>
											<option value="price">价格</option>
											<option value="email">邮件</option>
									</select></td>
								</tr>
								<tr ng-cloak ng-if='model.attribute.frontendInput=="statictext"'>
									<td><label>属性值模型</label></td>
									<td><textarea class="form-control" name="attrModel" ng-model="model.attribute.attrModel" my-json-attr
											rows="4"></textarea>
										<div class="help-block">每行一个选项</div></td>
								</tr>
								<tr ng-cloak ng-if='model.attribute.frontendInput=="select" || model.attribute.frontendInput=="multiselect"'>
									<td><label>属性值模型</label></td>
									<td><textarea class="form-control" name="attrModel" ng-model="model.attribute.attrModel" my-json-attr
											rows="4"></textarea>
										<div class="help-block">每行一个选项</div></td>
								</tr>
								<tr>
									<td><label>前端默认值</label></td>
									<td><input type="text" class="form-control" name="frontendDefaultValue"
										ng-model="model.attribute.frontendDefaultValue" /></td>
								</tr>
							</table>
						</form>
					</div>
				</div>

				<div class="clearfix">
					<hr />
					<div class="pull-right">
						<a type="button" href="${contextPath}/admin/attribute.html" class="btn btn-default">
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
