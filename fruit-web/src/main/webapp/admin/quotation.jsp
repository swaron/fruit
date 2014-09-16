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
<title>市场行情管理</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
img.thumb{
}
</style>
<script type="text/javascript">
	require([ 'admin/quotation' ]);
</script>
</head>

<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" class="container-fluid">
			<div ng-controller="PageController as c">
				<div>
					<ul class="list-unstyled button-bar">
						<li>
							<a class="btn btn-default"  href="./quotation/edit.html">添加报价</a>
						</li>
					</ul>
					<hr>
					<form name="query-form" class="table-form form-inline">
						<div class="clearfix">
							<div class="form-group"><label>产品名称：</label><input type="text" class="form-control" name="categoryName"  ng-model="query.categoryName" ></div>
							
							<div class="form-group"><label>销售方：</label><input type="text" class="form-control" name="title"  ng-model="query.seller" ></div>
							
							<div class="form-group"><label>报价时间从：</label><input type="date" my-date class="form-control" name="happenTimeFrom"  ng-model="query.happenTimeFrom" ></div>
							<div class="form-group"><label>报价时间到：</label><input type="date" my-date class="form-control" name="happenTimeTo"  ng-model="query.happenTimeTo" ></div>
							<div class="pull-right" style="width: auto;">
								<button type="submit" class="btn btn-default" ng-click="c.load()">
									<i class="al al-xinjian"> </i>查询
								</button>
							</div>
						</div>
					</form>
					<table class="table table-bordered">
						<thead>
							<tr>
								<!-- <th>产品ID</th> -->
								<th>产品名称</th>
								<th>销售方</th>
								<!-- <th>图片</th> -->
								<th>价格</th>
								<th>单位</th>
								<th>报价时间</th>
								<th>命令</th>
							</tr>
						</thead>
						<tr ng-repeat="item in result.records" ng-cloak>
							<!-- <td>{{:: item.productId }}</td> -->
							<td>{{:: item.categoryName }}</td>
							<td>{{:: item.sellerName }}</td>
							<!-- <td>
								<ul class="list-inline"><li ng-repeat="p in ::item.productPhotos"><img class="img-responsive thumb" ng-src="{{::p.url | thumb:'XXS'}}">  </li> </ul> 
							</td> -->
							<td>{{:: item.price }}</td>
							<td>{{:: item.quantity }}{{:: item.unit }}</td>
							<td>{{:: item.happenTime |date:'shortDate' }}</td>
							<td>
							<a href="quotation/edit.html?quotationId={{:: item.quotationId}}&copy=true"><i class="al al-fuzhi"></i>复制</a>
							<a href="quotation/edit.html?quotationId={{:: item.quotationId}}"><i class="al al-edit"></i>编辑</a>
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
