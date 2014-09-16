<%@page session="false" trimDirectiveWhitespaces="true" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="compress" uri="http://htmlcompressor.googlecode.com/taglib/compressor"%>
<%@taglib prefix="cache" uri="http://www.opensymphony.com/oscache"%> 
<s:eval var="profile" expression="@envProps['spring.profiles.active']" scope="request"></s:eval>

<cache:cache refresh="${profile=='dev' }">
<compress:html compressJavaScript="true" compressCss="true" preserveLineBreaks="true" removeMultiSpaces="false" yuiCssLineBreak="120" yuiJsLineBreak="120">
<!doctype html>
<html lang="zh">
<head>
<title>属性管理</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
</style>
<script type="text/javascript">
	require([ 'admin/attribute' ]);
</script>
</head>

<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" class="container-fluid">
			<div ng-controller="AttrController as c">
				<div>
					<form name="query-form" class="table-form form-inline">
						<div class="clearfix">
							<div class="form-group"><label>属性代码：</label><input class="form-control" name="attr_code"  ng-model="form.attr_code" ></div>
							
							<div class="form-group"><label>是否系统属性：</label>
								<label class="radio-inline">
								  <input type="radio" name="system" ng-model="form.system" ng-value="null"> 全部
								</label>
								<label class="radio-inline">
								  <input type="radio" name="system" ng-model="form.system" ng-value="true"> 是
								</label>
								<label class="radio-inline">
								  <input type="radio" name="system" ng-model="form.system" ng-value="false"> 否
								</label>
							</div>
							
							<div class="pull-right" style="width: auto;">
								<button type="submit" class="btn btn-default" ng-click="c.load()">
									<i class="al al-sousuo"> </i>查询
								</button>
								<a href="attribute/edit.html" type="button" class="btn btn-default" >
									<i class="al al-xinjian"> </i>新增
								</a>
							</div>
						</div>
					</form>

					<table class="table table-bordered">
						<thead>
							<tr>
								<th>属性代码</th>
								<th>说明</th>
								<th>系统属性</th>
								<th>前端类型</th>
								<th>模型值</th>
								<th>命令</th>
							</tr>
						</thead>
						<tr ng-repeat="item in result.records" ng-cloak>
							<td><a href="attribute/edit.html?attrId={{:: item.attrId}}" >{{:: item.attrCode }}</a></td>
							<td>{{:: item.note }}</td>
							<td>{{:: item.system | boolean }}</td>
							<td>{{:: item.frontendInput }}</td>
							<td>{{:: item.attrModel }}</td>
							<td>
								<a href="attribute/edit.html?attrId={{:: item.attrId}}&copy=true"><i class="al al-fuzhi"></i>复制</a>
								<a href="attribute/edit.html?attrId={{:: item.attrId}}"><i class="al al-edit"></i>编辑</a>
								<a href="" ng-click="c.removeItem($index)"><i class="al al-shanchu"></i>删除</a>
							</td>
						</tr>
					</table>
				</div>
			</div>

		</div>
		<jsp:include page="/WEB-INF/jsp/includes/fragment/footer.jsp" />
	</div>
</body>
</html>
</compress:html>
</cache:cache>
