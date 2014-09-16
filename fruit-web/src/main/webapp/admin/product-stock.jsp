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
<title>产品库存管理</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
</style>
<script type="text/javascript">
	require([ 'admin/product' ]);
</script>
</head>

<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" class="container-fluid">
			<div ng-controller="ProductController as c">
				<div>
					<ul class="list-unstyled button-bar">
						<li>
						<a class="btn btn-default" href="./product/edit.html">添加产品</a>
						</li>
					</ul>
					<hr>
					<div class="table-form">
						<div my-filter="c.filter" on-query="c.load()" ></div>
					</div>
					
					<table class="table table-bordered">
						<thead>
							<tr>
								<th style="min-width: 120px;">产品ID</th>
								<th style="min-width: 80px;">产品名称</th>
								<th style="min-width: 80px;">产品SKU</th>
								<th style="min-width: 80px;">类型ID</th>
								<th style="min-width: 80px;">产品是否有效</th>
								<th >产品属性</th>
								<th style="min-width: 60px;">命令</th>
							</tr>
						</thead>
						<tr ng-repeat="item in result.records" ng-cloak>
							<td>{{:: item.productId }}</td>
							<td><a href="javascript:void(0);">{{:: item.name }}</a></td>
							<td>{{:: item.sku }}</td>
							<td>{{:: item.categoryId }}</td>
							<td>{{:: item.enabled }}</td>
							<td><pre style="max-width: 800px;">{{:: item.attrs |json }}</pre></td>
							<td>
							<a href="product/edit.html?productId={{:: item.productId}}&copy=true"><i class="al al-fuzhi"></i>复制</a>
							<a href="product/edit.html?productId={{:: item.productId}}"><i class="al al-edit"></i>编辑</a>
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
