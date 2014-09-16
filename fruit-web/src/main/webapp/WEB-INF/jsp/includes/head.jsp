<%@page session="false" trimDirectiveWhitespaces="true" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="compress" uri="http://htmlcompressor.googlecode.com/taglib/compressor"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="request" />
<c:set var="asset" value="${contextPath}/static" scope="request" />
<c:set var="appVersion" value="${applicationScope.appVersion}" scope="request" />
<c:set var="debug" value="${(param['debug'] == 'true') or ((profile eq 'dev') and (cookie.debug.value eq 'true'))}" scope="request" />

<!-- for 360 browser -->
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black-translucent">
<meta name="renderer" content="webkit">
<meta name="keywords" content="粒粒果鲜">
<meta name="description" content="帮用户团购时令水果。为都市白领提供最新鲜，最实惠的水果。">
<meta name="url" content="liliguoxian.com">
<link rel="shortcut icon" href="${contextPath}/static/css/img/logo/favico-32x32.png?v=1" />
<link rel="apple-touch-icon" href="${contextPath}/static/css/img/logo/logo_80x80.png" />


<script type="text/javascript">
	var AppConfig = {
		contextPath : '<c:out value="${contextPath}" />',
		version : '<c:out value="${appVersion}" default="snapshot"/>'
	};
</script>

<c:if test="${debug eq 'true'}">

<link rel="stylesheet/less" type="text/css" href="${asset}/libs/bootstrap/3.2.0/less/bootstrap.less?v=${appVersion}">
<%-- <link rel="stylesheet/less" type="text/css" href="${contextPath}/static/libs/bootstrap/3.2.0/less/theme.less?v=${appVersion}"> --%>
<link rel="stylesheet/less" type="text/css" href="${asset}/css/app.less?v=${appVersion}" />

<script>
  less = {
    env: "production", 
	/* env: "development",*/
    logLevel: 2,
    dumpLineNumbers: "mediaquery"
  };
</script>
<!-- <script src="${contextPath}/static/libs/less-1.7.4.js" type="text/javascript"></script> -->
<link rel="stylesheet" type="text/css" href="${contextPath}/wro-debug/common.css?v=${appVersion}" >
<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
  <script src="${asset}/libs/ie/ie8-fix.js"></script>
  <script src="${asset}/libs/ie/ie8listener-polyfill.js"></script>
  <script src="${asset}/libs/ie/html5shiv-3.7.2.min.js"></script>
  <script src="${asset}/libs/ie/respond-1.4.2.min.js"></script>
  <script src="${asset}/libs/ie/json3.min.js"></script>
  <script src="${asset}/libs/ie/es5-shim.min.js"></script>
  <script src="${asset}/libs/ie/es5-sham.js"></script>
  <script src="http://libs.baidu.com/jquery/1.11.1/jquery.min.js"></script>
  <script type="text/javascript">
          document.createElement('ng-cloak');
          document.createElement('ng-include');
          document.createElement('ng-pluralize');
          document.createElement('ng-view');
   </script>
<![endif]-->
<script src="${asset}/libs/requirejs/comments/require.js"></script>
<script type="text/javascript">
 require.config({
	baseUrl : AppConfig.contextPath + "/static/libs",
	waitSeconds: 90,
	urlArgs: "bust=${appVersion}",
    paths: {
    	"text":"requirejs/text",
		jquery: [
		    "http://libs.baidu.com/jquery/1.11.1/jquery",
		    "jquery/1.11.1/jquery-1.11.1"
		],
    	"bootstrap":[
 	 	    "bootstrap/3.2.0/dist/js/bootstrap"
 	     ],
 	    "fastclick":"angularjs/fastclick",
        "page": "page",
        "admin": "admin"
    },
    shim: {
    	'jquery':{
    		exports: '$',
    		init:function(){
    			window.jQuery = window.jQuery || this.Zepto;
    		}
    	},
    	'bootstrap': {
            deps: ['jquery']
        }
    }
});
</script>

<script type="text/javascript" src="${asset}/libs/angularjs/1.3.0-rc.1/angular.js?v=1"></script>
<script type="text/javascript" src="${asset}/libs/angularjs/1.3.0-rc.1/angular-sanitize.js"></script>
<script type="text/javascript" src="${asset}/libs/angularjs/1.3.0-rc.1/angular-animate.js"></script>
<script type="text/javascript" src="${asset}/libs/angularjs/1.3.0-rc.1/angular-messages.js"></script>
<script type="text/javascript" src="${asset}/libs/angularjs/1.3.0-rc.1/i18n/angular-locale_zh-cn.js"></script>
<script type="text/javascript" src="${asset}/libs/update.js?v=${appVersion}" async="async"></script>
</c:if>

<c:if test="${debug ne 'true'}">
<link rel="stylesheet" type="text/css" href="${asset}/js/bootstrap/3.2.0/less/bootstrap.min.css?v=${appVersion}">
<link rel="stylesheet" type="text/css" href="${asset}/css/app.min.css?v=${appVersion}" />
<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
  <script src="${asset}/libs/ie/ie8-fix.js"></script>
  <script src="${asset}/libs/ie/ie8listener-polyfill.js"></script>
  <script src="${asset}/libs/ie/html5shiv-3.7.2.min.js"></script>
  <script src="${asset}/libs/ie/respond-1.4.2.min.js"></script>
  <script src="${asset}/libs/ie/json3.min.js"></script>
  <script src="${asset}/libs/ie/es5-shim.min.js"></script>
  <script src="${asset}/libs/ie/es5-sham.js"></script>
  <script src="http://libs.baidu.com/jquery/1.11.1/jquery.min.js"></script>
  <script type="text/javascript">
          document.createElement('ng-include');
          document.createElement('ng-pluralize');
          document.createElement('ng-view');
   </script>
<![endif]-->
<script src="${asset}/libs/requirejs/minified/require.js"></script>
<script type="text/javascript">
 require.config({
	baseUrl : AppConfig.contextPath + "/static/js",
	urlArgs: "bust=${appVersion}",
	//网速太慢的时候会导致网站本身的js加载不来
	waitSeconds: 90,
    paths: {
    	"text":"requirejs/text",
    	jquery: [
		    "http://libs.baidu.com/jquery/1.11.1/jquery.min",
		    "jquery/1.11.1/jquery-1.11.1.min"
		],
    	"bootstrap":[
			"bootstrap/3.2.0/dist/js/bootstrap.min"
    	 ],
    	"page": "page",
    	"fastclick":"angularjs/fastclick",
        "admin": "admin"
    },
    shim: {
    	'jquery':{
    		exports: '$',
    		init:function(){
    			window.jQuery = window.jQuery || this.Zepto;
    		}
    	},
    	'bootstrap': {
            deps: ['jquery']
        }
    }
}); 
</script>
<script src="${asset}/js/common.js?v=${appVersion}"></script>
<script type="text/javascript" src="${asset}/js/angularjs/angular-sa-an-ms-cn.js?v=1.3.0-rc.1"></script>
<script type="text/javascript" src="${asset}/js/update.js?v=${appVersion}" async="async"></script>
</c:if>

<c:if test="${profile eq 'prod' }">
<script type="text/javascript">
(function(i, s, o, g, r, a, m) {
	i['GoogleAnalyticsObject'] = r;
	i[r] = i[r] || function() {
		(i[r].q = i[r].q || []).push(arguments)
	}, i[r].l = 1 * new Date();
	a = s.createElement(o), m = s.getElementsByTagName(o)[0];
	a.async = 1;
	a.src = g;
	m.parentNode.insertBefore(a, m)
})(window, document, 'script', '${asset}/libs/google/analytics.js', 'ga');
ga('create', 'UA-49491579-2', {
	'cookieDomain' : 'liliguoxian.com'
});
ga('require', 'displayfeatures');
ga('require', 'linkid', 'linkid.js');
ga('send', 'pageview');
</script>
</c:if>
