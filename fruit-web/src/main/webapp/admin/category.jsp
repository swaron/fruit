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
<title>产品种类及种类属性管理</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
</style>
<script type="text/javascript">
	require([ 'admin/category' ]);
</script>
</head>

<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" class="container-fluid">
			<div ng-controller="CategoryController as c">
				<div>
					<form name="query-form" class="table-form form-inline">
						<div class="clearfix">
							<div class="form-group"><label>类型名称：</label><input type="text" class="form-control" name="name"  ng-model="query.name" ></div>
							<div class="pull-right" style="width: auto;">
								<button type="submit" class="btn btn-default" ng-click="c.load()">
									<i class="al al-xinjian"> </i>查询
								</button>
								<a href="category/edit.html" type="button" class="btn btn-default" >
									<i class="al al-xinjian"> </i>新增
								</a>
							</div>
						</div>
					</form>

					<table class="table table-bordered">
						<thead>
							<tr>
								<th>ID</th>
								<!-- <th>名称</th>
								<th>父ID</th>
								<th>ID路径</th> -->
								<th>名称路径</th>
								<!-- <th>层级</th> -->
								<th>继承</th>
								<th style="width:300px;">直接属性</th>
								<th style="width:400px;">综合属性</th>
								<th>命令</th>
							</tr>
						</thead>
						<tr ng-repeat="item in result.records | orderBy:'namePath'" ng-cloak>
							<td style="min-width: 35px">{{:: item.categoryId }}</td>
							<!-- <td>{{:: item.name }}</td>
							<td>{{:: item.parentId }}</td>
							<td>{{:: item.idPath }}</td -->
							<td>{{:: item.namePath }}</td>
							<!-- <td>{{:: item.level }}</td> -->
							<td>{{:: item.inheritAttr | boolean }}</td>
							<td title="{{:: item.attrs | json }}"><a href="" ng-click="showAttr=!!showAttr">显示属性</a> <pre ng-show="showAttr"> {{:: item.attrs | json }} </pre></td>
							<td><a href="" ng-click="c.toggleTotalAttr(item,$event)"><i class="al al-yulan"></i>点击获取综合属性</a>
							</td>
							<td>
							<a href="category/edit.html?categoryId={{:: item.categoryId}}&copy=true"><i class="al al-fuzhi"></i>复制</a>
							<a href="category/edit.html?categoryId={{:: item.categoryId}}"><i class="al al-edit"></i>编辑</a>
							 <a href="" ng-click="c.removeItem($index)"><i class="al al-shanchu"></i>删除</a></td>
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
