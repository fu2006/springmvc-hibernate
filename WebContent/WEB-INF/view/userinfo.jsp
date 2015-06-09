<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/include/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户</title>
<script language="javascript" src="<%=request.getContextPath()%>/script/jquery.min.js"></script>
<style>
table{  border-collapse:collapse;  }
td{  border:1px solid #f00;  }
</style>
<script type="text/javascript">
function add(){
	window.location.href="<%=request.getContextPath() %>/user/add";
}

function del(id){
$.ajax( {
	type : "POST",
	url : "<%=request.getContextPath()%>/user/del/" + id,
	dataType: "json",
	success : function(data) {
		if(data.del == "true"){
			alert("删除成功！");
			$("#" + id).remove();
		}
		else{
			alert("删除失败！");
		}
	},
	error :function(){
		alert("网络连接出错！");
	}
});
}
</script>
</head>
<body>

<input id="add" type="button" onclick="add()" value="添加"/>
<table >
	<tr>
		<td>序号</td>
		<td>姓名</td>
		<td>邮箱登录名</td>
		<td>邮箱登陆密码</td>
		<td>手机号</td>
		<td>其他</td>
	</tr>
	<c:forEach items="${list}" var="userinfo">
		<tr id="<c:out value="${userinfo.userid}"/>">
		<td><c:out value="${userinfo.userid}"/></td>
		<td><c:out value="${userinfo.username}"/></td>
		<td><c:out value="${userinfo.loginname}"/></td>
		<td><c:out value="${userinfo.password}"/></td>
		<td><c:out value="${userinfo.mobile}"/></td>
		<td><c:out value="${userinfo.other}"/></td>
		<td>
			<input type="button" onclick="del('<c:out value="${userinfo.userid}"/>')" value="删除"/>
		</td>
	</tr>
	</c:forEach>
	
</table>
</body>
</html>