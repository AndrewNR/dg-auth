<%@page import="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="top.jsp" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="errorMsg" value="${requestScope['errorMsg']}" />
<c:set var="availableItems" value="${requestScope['availableItems']}" />

<!-- http://codepen.io/ldesanto/pen/pEftw -->
<link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css" rel="stylesheet prefetch">
<style>
table {
  color: #333;
  font-family: sans-serif;
  font-size: 0.9em;
  font-weight: 300;
  text-align: left;
  line-height: 26px;
  border-spacing: 0;
  border: 2px solid #975997;
  min-width: 300px;
  margin-left: 10px;
}

thead tr:first-child {
  background-color: #AAAADD;
  color: #fff;
  border: none;
}

th {font-weight: bold;}
th, th:first-child, td:first-child {padding: 2px 5px;}

thead tr:last-child th {border-bottom: 3px solid #ddd;}

tbody tr:hover {background-color: #ffeeff;}
tbody tr:last-child td {border: none;}
tbody td {border-bottom: 1px solid #ddd;}

td:last-child {
  text-align: right;
  padding-right: 10px;
}

.button {
  color: #696969;
  padding-right: 5px;
  cursor: pointer;
}
</style>

<h3 style="display: ${errorMsg != null ? 'block' : 'none'}">ERROR: ${errorMsg}</h3>

<c:if test="${errorMsg == null}">
	<c:if test="${availableItems != null && !availableItems.isEmpty()}">
		<form action="${contextPath}/authList" method="GET">
			<table>
				<thead><tr>
					<th>Available Tokens</th>
					<th>Actions</th>
				</tr></thead>
				<tbody>
				<c:forEach items="${availableItems}" var="item">
				<tr>
					<td>${item}</td>
					<td>
						<span class="fa fa-trash button" onclick="doDelete(this, '${item}');"></span>
					</td>
				</tr>
				</c:forEach>
				</tbody>
			</table>
		</form>
	</c:if>
	<c:if test="${availableItems == null || availableItems.isEmpty()}">
		<h3>No Tokens available.</h3>
	</c:if>
</c:if>

<script type="text/javascript">
function doDelete(elem, itemId) {
	alert('Not supported yet!');
	return false;
}
</script>

<%@ include file="bottom.jsp" %>