<%@page import="java.util.Random"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
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
<title>Test Calculation</title>
</head>
<body>

	<hr color="#000000" />
	<center>
		<h3>
			基础性能测试页面. 2个参数，ex: test-calc.do?count=10&sleep=200
		</h3>
		<table border="1">
			<tr><td>count</td><td>模拟cpu运算，执行多少次计算，每次计算对1000个字符串排序</td> </tr>
			<tr><td>sleep</td><td>模拟io，sleep多少毫秒</td> </tr>
		</table>
		<h3>
			<font color="#222222">
				<%
				long start = System.currentTimeMillis();
				int count = 1;
				int sleep = 100;
				String countStr = request.getParameter("count");
				String sleepStr = request.getParameter("sleep");
				if(countStr != null){
					count = Integer.valueOf(countStr);
				}
				long total = 0;
				for(int n = 0 ; n<count;n++){
					ArrayList<String> list = new ArrayList<String>();
					for (int i = 0; i < 1000 ; i++) {
						Random random = new Random();
						String rand = String.valueOf(random.nextInt());
						list.add(rand);
						Collections.sort(list);
					}
				}
				long end = System.currentTimeMillis();
				long cost = (end - start);
				
				out.println(String.format("<br/> calculation used : %06d ms",cost));
				
				if(sleepStr != null){
					sleep = Integer.valueOf(sleepStr);
				}
				Thread.sleep(sleep);
				end = System.currentTimeMillis();
				cost = (end - start);
				out.println(String.format("<br/> calculation+sleep used : %06d ms",cost));
				
				%>
			 </font>
		</h3>
		<h3>
			<font color="#0000ff"><%=new java.util.Date()%> </font>
		</h3>
		<hr color="#000000" />
	</center>
</body>
</html>
</compress:html>
</cache:cache>