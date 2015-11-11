<%@page import="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="top.jsp" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="errorMsg" value="${requestScope['errorMsg']}" />
<c:set var="availableItems" value="${requestScope['availableItems']}" />
<h3 style="display: ${errorMsg != null ? 'block' : 'none'}">ERROR: ${errorMsg}</h3>

<c:if test="${errorMsg == null}">
<form action="${contextPath}/authList" method="GET">
	<table>
		<thead><tr>
			<th>Select</th>
			<th>Available Tokens</th>
		</tr></thead>
		<tbody>
		<c:forEach items="${availableItems}" var="item">
		<tr>
			<td><input type="checkbox" value="${item}"/></td>
			<td>${item}</td>
		</tr>
		</c:forEach>
		</tbody>
	</table>
</form>
</c:if>
<%@ include file="bottom.jsp" %>