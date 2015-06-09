<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/include/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>邮件详细</title>
<script language="javascript" src="<%=request.getContextPath()%>/script/jquery.min.js"></script>
<style>
.left{width: 100px;}
</style>
</head>
<body>
<table >
	<tr>
		<td class="left">主题：</td>
		<td><c:out value="${message.subject}"/></td>
	</tr>
	<tr>
		<td>发送人：</td>
		<td><c:out value="${message.fromname}"/></td>
	</tr>
	<tr>
		<td>发送时间：</td>
		<td><c:out value="${message.senddate}"/></td>
	</tr>
	<tr>
		<td>内容</td>
		<td><c:out escapeXml="false" value="${message.content}"/></td>
	</tr>
</table>
</body>
</html>