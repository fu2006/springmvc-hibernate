<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/include/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>邮件列表</title>
<script language="javascript" src="<%=request.getContextPath()%>/script/jquery.min.js"></script>
<style>
table{  border-collapse:collapse;  }
td{  border:1px solid #f00;  }
</style>
</head>
<body>
<table >
	<tr>
		<td>序号</td>
		<td>uid</td>
		<td>主题</td>
		<td>发送时间</td>
		<td>是否推送</td>
		<!-- <td>内容</td -->
	</tr>
	<c:forEach items="${list}" var="message">
	<tr id="<c:out value="${message.id}"/>">
		<td><c:out value="${message.id}"/></td>
		<td><c:out value="${message.uid}"/></td>
		<td><c:out value="${message.subject}"/></td>
		<td><c:out value="${message.senddate}"/></td>
		<td><c:out value="${message.ispush}"/></td>
		<%-- <td><c:out escapeXml="false" value="${message.content}"/></td> --%>
	</tr>
	</c:forEach>
	
</table>
</body>
</html>