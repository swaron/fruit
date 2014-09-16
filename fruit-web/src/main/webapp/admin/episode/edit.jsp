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
<title>编辑批次内容</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
.modal-title .badge{
	font-size: 1em;
}
</style>
<script type="text/javascript">
	require([ 'admin/episode/edit' ]);
</script>
</head>

<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" class="container-fluid">
			<div ng-controller="EpisodeController as c">
				<div class="help-block clearfix">
					<ul class="list-inline pull-left" ng-cloak>
						<li><label>名称：</label> {{ model.result.name }}</li>
						<li><label>预售日期：</label> {{ model.result.deliveryDate | date:'yyyy-MM-dd' }}</li>
						<li><label>预订开始时间：</label> {{ model.result.bookingTimeStart| date:'yyyy-MM-dd HH:mm' }}</li>
						<li><label>预订结束时间：</label> {{ model.result.bookingTimeEnd | date:'yyyy-MM-dd HH:mm' }}</li>
						<li> <a href="${contextPath}/admin/episode.html">返回批次管理</a>
						</li>
					</ul>
				</div>
				<table class="table table-bordered">
					<thead>
						<tr>
							<th>产品名称</th>
							<th>产品标题</th>
							<th>产品子标题</th>
							<th>单价/单位</th>
							<th>产品产地</th>
							<th style="min-width: 60px;">命令</th>
						</tr>
					</thead>
					<tr ng-repeat="item in model.records" ng-cloak>
						<td>{{:: item.name }}</td>
						<td>{{:: item.attrs['产品标题'] }}</td>
						<td>{{:: item.attrs['产品子标题'] }}</td>
						<td>{{:: item.price |currency }}/{{ item.attrs['水果销售单位数量'] }}{{ item.attrs['水果销售单位名称'] }}</td>
						<td>{{:: item.attrs['水果产地'] }}</td>
						<td><a href="" ng-click="c.removeProduct($index)"><i class="al al-shanchu"></i>删除</a></td>
					</tr>
				</table>
				<hr class="sub" />
				<form class="clearfix form-inline table-form">
					<div class="form-group">
						<label>按名称（包含）：</label><input class="form-control" name="productName" ng-model="query.productName">
					</div>
					<div class="form-group">
						<label>按SKU（开头等于）：</label><input class="form-control" name="productSku" ng-model="query.productSku">
					</div>
					<div class="pull-right">
						<button type="submit" class="btn btn-default" ng-click="c.queryProduct()">
							<i class="al al-sousuo"> </i>查询
						</button>
					</div>
				</form>
				<table class="table table-bordered">
					<thead>
						<tr>
							<th style="min-width: 80px;">产品名称</th>
							<th style="min-width: 80px;">产品SKU</th>
							<th style="min-width: 80px;">产品是否有效</th>
							<th>产品属性</th>
							<th style="min-width: 60px;">命令</th>
						</tr>
					</thead>
					<tr ng-repeat="item in result.products" ng-cloak>
						<td><a href="javascript:void(0);">{{:: item.name }}</a></td>
						<td>{{:: item.sku }}</td>
						<td>{{:: item.enabled }}</td>
						<td><a href="javascript:void(0);" ng-click="showAttr = !showAttr">显示属性</a> <pre ng-show="showAttr"> {{:: item.attrs |json }}</pre></td>
						<td><a href="" ng-click="c.addProduct(item)"><i class="al al-jia"></i>添加</a></td>
					</tr>
				</table>
				<div class="modal fade" id="edit-item-win" tabindex="-1" role="dialog" aria-labelledby="edit-item-win-label"
					aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
								<h4 class="modal-title" id="edit-item-win-label">
									设置批次 <span class="badge" >{{model.result.name}}</span> 产品 <span
										class="badge"> {{view.toAddProduct.name}}</span> 的属性
								</h4>
							</div>
							<div class="modal-body ">
								<form name="addForm" class="form-inline">
									<div class="attr">
										<label>批次产品属性:</label>
										<div> <label for="price">单价</label> = <input type="number" class="form-control" name="price" ng-model="view.episodeProduct.price" required="required" min="0" step="0.01"  /> </div>
										<ul class="list-unstyled">
										<li ng-repeat="(key,val) in view.episodeProduct.attrs">
											<span class="badge">{{key}} = {{val}} <a
												href="" ng-click="c.removeEpisodeProductAttr(key)"><i class="al al-cuowu"></i></a>
											</span>
										</li>
										</ul>
									</div>
									<label>添加属性:</label>
									<ul class="list-unstyled">
										<li><input name="attrCode" class="form-control" ng-model="view.newAttr.attrCode"/> = <input
											name="attrValue" class="form-control" ng-model="view.newAttr.attrValue">
											<button type="button" class="btn btn-default" ng-click="c.addEpisodeProductAttr(view.newAttr)">添加</button></li>
									</ul>
								</form>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
								<button type="button" class="btn btn-primary" ng-click="c.saveProduct(addForm)"
									ng-disabled="addForm.$invalid  || addForm.saving">
									<i class="al" ng-class='{"al-baocun":!saving,"al-jiazaizhong al-spin":saving}'></i>保存
								</button>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
			</div>
		</div>
		<jsp:include page="/WEB-INF/jsp/includes/fragment/footer.jsp" />
	</div>
</body>
</html>
</compress:html>
</cache:cache>
