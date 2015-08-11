<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.io.*,java.util.*,java.sql.*"%>
<%@ page import="javax.servlet.http.*,javax.servlet.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="/js/jquery-1.11.0.min.js"></script>
<script src="/js/jquery-migrate-1.2.1.min.js"></script>
<title>LogAnalytics</title>
</head>
<body>
	<h1>LogAnalytics</h1>
	報單號碼:
	<input type="text" name="declNo" />
	<br /> 海關通關號碼:
	<input type="text" name="vslRegNo" /> 裝貨單號碼:
	<input type="text" name="soNo" />
	<br /> 主託運單號碼:
	<input type="text" name="mawb" /> 分託運單號碼:
	<input type="text" name="hawb" />
	<br />
	<button>查詢</button>
	<sql:setDataSource var="snapshot" driver="org.postgresql.Driver"
		url="jdbc:postgresql://192.168.52.202:5432/plaDB" user="PLAMGR"
		password="acol342" />
	<sql:query dataSource="${snapshot}" var="result">
SELECT * from exlgdmm2;
</sql:query>
	<table border="1" width="100%">
		<tr>
			<th>LogTime</th>
			<th>LogId</th>
			<th>DeclNo</th>
			<th>VslRegNo</th>
			<th>SoNo</th>
			<th>Mawb</th>
			<th>Hawb</th>
			<th>ProcDesc</th>
		</tr>
		<c:forEach var="row" items="${result.rows}">
			<tr>
				<td><c:out value="${row.log_time}" /></td>
				<td><c:out value="${row.log_id}" /></td>
				<td><c:out value="${row.decl_no}" /></td>
				<td><c:out value="${row.vsl_reg_no}" /></td>
				<td><c:out value="${row.so_no}" /></td>
				<td><c:out value="${row.mawb}" /></td>
				<td><c:out value="${row.hawb}" /></td>
				<td><c:out value="${row.proc_desc}" /></td>
			</tr>
		</c:forEach>
	</table>
</body>
</html>