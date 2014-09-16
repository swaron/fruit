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
<title>新增产品</title>
<jsp:include page="/WEB-INF/jsp/includes/head.jsp" />
<style type="text/css">
.tab-content {
	border-left: solid 1px rgb(231, 230, 230);
	border-right: solid 1px rgb(231, 230, 230);
	border-bottom: solid 1px rgb(231, 230, 230);
}

.tab-content>.tab-pane {
	padding: 8px;
}

.tab-pane hr.sub {
	margin: 8px -8px;
}


.attr span {
	border: 1px solid rgb(34, 51, 18);
}
.tab-pane table td .badge{
	text-overflow: ellipsis;
	overflow: hidden;
	max-width: 380px;
}
.tab-pane table dl{
	margin-bottom: 0px;
}
</style>
<script type="text/javascript">
	require([ 'admin/product/edit' ]);
</script>
</head>

<body>
	<div id="wrapper">
		<jsp:include page="/WEB-INF/jsp/includes/fragment/header.jsp" />
		<jsp:include page="/WEB-INF/jsp/includes/fragment/side.jsp" />
		<div id="main" class="container-fluid">
			<div ng-controller="ProductController as c">
				<div class="clearfix">
					<h4 class="pull-left">设置产品属性(<span ng-bind="model.product.productId == null ?'新增':'编辑' "></span>)</h4>
					<div class="pull-right">
						<a type="button" href="" ng-click="c.prevStep()" ng-disabled="step==0" class="btn btn-default"> <i
							class="al al-xiangzuo1"> </i>上一步
						</a> <a type="button" href="" ng-click="c.nextStep()" ng-disabled="step==3" class="btn btn-default"> <i
							class="al al-xiangyou1"> </i>下一步
						</a> <a type="button" href="${contextPath}/admin/product.html" class="btn btn-default"> <i
							class="al al-chexiao"> </i>返回
						</a>
						<button type="button" class="btn btn-default" ng-click="c.saveAndEdit(form)">
							<i class="al" ng-class='{"al-baocun":!saving,"al-jiazaizhong al-spin":saving}'> </i>保存
						</button>
					</div>
				</div>
				<hr />
				<ul class="nav nav-tabs" role="tablist">
					<li ng-class="{active:step==0}"><a href="" ng-click="step=0" role="tab">选择类型</a></li>
					<li ng-class="{active:step==1}"><a href="" ng-click="step=1" role="tab">基本属性</a></li>
					<li ng-class="{active:step==2}"><a href="" ng-click="step=2" role="tab">产品属性</a></li>
					<li ng-class="{active:step==3}"><a href="" ng-click="step=3" role="tab">剩余属性</a></li>
				</ul>
				<div class="tab-content">
					<div class="tab-pane" ng-class="{active:step==0}">
						<form name="query-form" class="select-type form-inline">
							<table class="table table-borderless" style="width: auto;">
								<tr>
									<td><label for="typeId">商品类型: </label></td>
									<td><select class="form-control" name="typeId" ng-model="model.product.typeId">
											<option value="1">简单产品</option>
											<option value="2">组合产品</option>
											<option value="3">虚拟产品</option>
											<option value="4">可配置产品</option>
									</select></td>
								</tr>
								<tr>
									<td><label>所属类别: </label></td>
									<td><input class="form-control" readonly="readonly" ng-value="view.categoryDesc" placeholder="请从下表搜索并选择" /></td>
								</tr>
							</table>
						</form>
						<hr class="sub" />
						<form class="table-form clearfix form-inline">
							<div class="form-group">
								<label>按名称搜索：</label><input class="form-control" name="categoryName" ng-model="query.categoryName">
							</div>
							<div class="pull-right" style="width: auto;">
								<button type="submit" class="btn btn-default" ng-click="c.loadCategory()">
									<i class="al al-sousuo"> </i>查询
								</button>
							</div>
						</form>
						<table class="table table-bordered">
							<thead>
								<tr>
									<th>ID</th>
									<th>名称</th>
									<th>ID路径</th>
									<th>名称路径</th>
									<!-- <th style="width: 400px;">直接属性</th> -->
									<th style="width: 400px;">综合属性</th>
								</tr>
							</thead>
							<tr ng-repeat="item in result.categorys" ng-cloak>
								<td>{{:: item.categoryId }}</td>
								<td><a href="" ng-click="model.product.categoryId=item.categoryId;">{{:: item.name }}</a></td>
								<td>{{:: item.idPath }}</td>
								<td>{{:: item.namePath }}</td>
								<!-- <td title="{{:: item.attrs | json }}">{{:: item.attrs | json }}</td> -->
								<td><a href="" ng-click="c.toggleTotalAttr(item,$event)"><i class="al al-yulan"></i>点击获取综合属性</a></td>
							</tr>
						</table>
					</div>
					<div class="tab-pane" ng-class="{active:step==1}">
						<table class="table table-borderless">
							<tr>
								<td style="width: 100px;"><label for="name">名称</label></td>
								<td><input type="text" class="form-control" name="name" ng-model="model.product.name" required="required" /></td>
							</tr>
							<!-- <tr>
							tag 是json格式
								<td><label>Tag</label></td>
								<td><textarea rows="3" cols="" class="form-control" name="tag" ng-model="model.product.tag"></textarea></td>
							</tr> -->
							<tr>
								<td><label>是否有效</label></td>
								<td><input type="checkbox" name="enabled" ng-model="model.product.enabled" /></td>
							</tr>
							<tr ng-if="model.product.typeId == 2">
								<td><label for="typeId">包含子产品: </label></td>
								<td><ul class="list-unstyled">
										<li ng-repeat="(key,val) in view.childProducts">{{::val.sku}},{{::val.name}} <a href="" ng-click="c.removeChildProduct(key)"><i class="al al-cuowu"></i></a></li>
									</ul>
									
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
										<tr ng-repeat="item in result.candidateProducts" ng-cloak>
											<td><a href="javascript:void(0);">{{:: item.name }}</a></td>
											<td>{{:: item.sku }}</td>
											<td>{{:: item.enabled }}</td>
											<td>{{:: item.attrs |json }}</td>
											<td>
												<a href="" ng-click="c.addChildProduct(item)"><i class="al al-jia"></i>添加</a></td>
										</tr>
									</table></td>
							</tr>
							<tr ng-init="model.product.attrs._sku_variant_attrs=model.product.attrs._sku_variant_attrs||{}" ng-if="model.product.typeId == 2">
								<td><label>SKU属性(本组合): </label></td>
								<td>
								<!-- //mergedSkuAttrs,merged的属性集合，及相同的属性,{attrCode:{disabled:true,values:{"1":"attrValue1","2":"attrValue2"} }} } -->
									<table class="table table-bordered table-compact">
										<tr>
											<th style="width: 35px;">选择</th>
											<th>属性名</th>
											<th>各产品属性值</th>
										</tr>
										<tr ng-repeat="(attrCode,attr) in view.mergedSkuAttrs">
											<td style="text-align: center;" title="{{attr.disabled?'子产品所共用的属性才可以作为SKU属性':'' }}">
												<input ng-disabled="attr.disabled" type="checkbox" ng-model="model.product.attrs._sku_variant_attrs[attrCode]"/> 
											</td>
											<td><label>{{attrCode}}</label></td>
											<td> <span ng-repeat="(pindex,pvalue) in attr.values" class="badge">{{pindex}}.{{pvalue}}</span> </td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</div>
					<div class="tab-pane" ng-class="{active:step==2}">
						<form name="form">
							<table class="table table-borderless" style="max-width: 700px;">
								<tr ng-repeat="attr in result.attrs | orderBy:attr.frontendOrder ">
									<td><label>{{attr.attrCode}}</label></td>
									<td><div my-attr-node attr-type="attr.frontendInput" attr-code="attr.attrCode" attr-model="attr.attrModel"
											attr-value="model.product.attrs[attr.attrCode]"></div></td>
								</tr>
							</table>
						</form>
					</div>
					<div class="tab-pane" ng-class="{active:step==3}">
						<div class="">
							<div class="attr">
								<label>剩余属性:</label><span ng-repeat="(key,val) in view.extraAttrs">{{key}} = {{val}} <a href=""
									ng-click="c.removeExtraAttr(key)"><i class="al al-cuowu"></i></a>
								</span>
							</div>
							<hr class="sub">
							<label>添加属性:</label>
							<ul class="form-inline list-unstyled">
								<li><input name="attrCode" class="form-control" ng-model="view.newAttr.attrCode" /> = <input
									name="attrValue" class="form-control" ng-model="view.newAttr.attrValue">
									<button type="button" class="btn btn-default" ng-click="c.addExtraAttr(view.newAttr)">添加</button></li>
							</ul>
						</div>
					</div>
				</div>

				<div class="clearfix">
					<hr />
					<div class="pull-right">
						<a type="button" href="" ng-click="c.prevStep()" ng-disabled="step==0" class="btn btn-default"> <i
							class="al al-xiangzuo1"> </i>上一步
						</a> <a type="button" href="" ng-click="c.nextStep()" ng-disabled="step==3" class="btn btn-default"> <i
							class="al al-xiangyou1"> </i>下一步
						</a> <a type="button" href="${contextPath}/admin/product.html" class="btn btn-default"> <i
							class="al al-chexiao"> </i>返回
						</a>
						<button type="button" class="btn btn-default" ng-click="c.saveAndEdit(form)">
							<i class="al" ng-class='{"al-baocun":!saving,"al-jiazaizhong al-spin":saving}'> </i>保存
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
