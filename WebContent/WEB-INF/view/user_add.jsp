<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/include/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户添加添加</title>
<script type="text/javascript">
function turnback(){
	window.location.href="<%=request.getContextPath() %>/user";
}
</script>
</head>
<body>
<form method="post" action="<%=request.getContextPath() %>/user/save">
<div><c:out value="${addstate}"></c:out></div>
<table>
	<tr><td>姓名</td><td><input id="username" name="username" type="text" /></td></tr>
	<tr><td>邮箱登录名</td><td><input id="loginname" name="loginname"  type="text" /></td></tr>
	<tr><td>邮箱登陆密码</td><td><input id="password" name="password"  type="password" /></td></tr>
	<tr><td>手机号</td><td><input id="mobile" name="mobile"  type="text" /></td></tr>
	<tr><td>其他</td><td><input id="other" name="other"  type="text" /></td></tr>
	<tr><td colSpan="2" align="center"><input type="submit" value="提交"/><input type="button" onclick="turnback()" value="返回" /> </td></tr>
</table>

</form>
</body>
</html>