define([ "./_module", "app/utils","jquery", "require" ], function(module, utils, jquery,require) {
	angular.element(document).find('head').prepend('<link rel="stylesheet" href="'+require.toUrl('./thumbslider.css')+'">');
	/**
	 * 商品图片底部横排缩略图
	 */
	var hyThumbSlider = ['$compile','$filter', function($compile,$filter){
		return {
			restrict : 'EA',
			scope : {
				//middleThumb
				thumbs : '=hyThumbSlider',
				thumbMd:'=',
				thumbLg:'='
			},
			controller : [ '$scope', function($scope) {
				//$scope.thumbs.index = 0;
				this.plusIndex = function(){
					var thumbs = $scope.thumbs;
					if(thumbs && thumbs.index < (thumbs.length-1)){
						thumbs.index = thumbs.index + 1 ;
					}
				};
				this.minusIndex = function(){
					var thumbs = $scope.thumbs;
					if(thumbs && thumbs.index > 0 ){
						thumbs.index = thumbs.index - 1 ;
					}
				};
			}],
			controllerAs : 'c',
			link : function(scope, element, attrs) {
				var tpl = ['',
				           '	<div class="left ts-nav" ng-click="c.minusIndex()" ng-class="{disabled:thumbs.index <=0 }"><i class="al al-xiangzuo1"></i></div>',
				           '	<div style="height:100%;position:relative;">',
				           '	<div class="ts-wrapper">',
				           '		<ul class="ts-main" ng-style="ulStyle" > ',
				           '			<li ng-repeat="item in thumbs" ng-init="$first?(thumbs.index = 0):0" ng-click="thumbs.index = $index" ng-class="{active:$index == thumbs.index }"><img ng-src="{{item.url | thumb:\'XXS\'}}"></img></li>',
				           '		</ul>',
				           '	</div>',
				           '	</div>',
				           '<div></div>',
				           '	<div class="right ts-nav" ng-click="c.plusIndex()" ng-class="{disabled:thumbs.index >= (thumbs.length-1) }"><i class="al al-xiangyou1"></i></div>',
				           ''].join('');
				var el = jquery(element);
				el.addClass('thumb-slider');
				el.html(tpl);
				$compile(el.contents())(scope);
				el.find('.ts-nav').css('line-height',(parseInt(el.height())-2) +"px");
				scope.$watch('thumbs.index',function(index){
					var liWidth =el.find('ul li').css('width');
					liWidth = (liWidth || "").replace('px','');
					if(index == null){
						return;
					}
					index = index || 0;
					var wrapperWidth = el.find('.ts-wrapper').width();
					var offset = wrapperWidth / 3;
					scope.ulStyle = {
						left:  ((-(liWidth * index)) + offset) + 'px'
					};
					scope.thumbMd = $filter('thumb')(scope.thumbs[index].url ,'M');
					scope.thumbLg = scope.thumbs[index].url;
				});
			}
		};
	} ];
	module.directive('hyThumbSlider', hyThumbSlider);
});
