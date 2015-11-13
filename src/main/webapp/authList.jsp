<%@page import="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ include file="top.jspf"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="errorMsg" value="${requestScope['errorMsg']}" />
<c:set var="availableItems" value="${requestScope['availableItems']}" />

<!-- http://codepen.io/ldesanto/pen/pEftw -->
<link
	href="https://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css"
	rel="stylesheet prefetch">
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

th {
	font-weight: bold;
}

th,th:first-child,td:first-child {
	padding: 2px 5px;
}

thead tr:last-child th {
	border-bottom: 3px solid #ddd;
}

tbody tr:hover {
	background-color: #ffeeff;
}

tbody tr:last-child td {
	border: none;
}

tbody td {
	border-bottom: 1px solid #ddd;
}

.button {
	color: #696969;
	padding-right: 5px;
	cursor: pointer;
}

th .button {
    color: white;
    font-size: 1.3em;
}

.actionsCol {
	padding-left: 10px;
}
</style>

<h3 style="color: red; display: ${errorMsg != null ? 'block' : 'none'};">ERROR: ${errorMsg}</h3>

<c:choose>
<c:when test="${availableItems != null && !availableItems.isEmpty()}">
	<form action="${contextPath}/authList" method="POST">
		<input type="hidden" name="actionType" value="" />
		<input type="hidden" name="deleteType" value="" />
		<input type="hidden" name="itemId" value="" />
		<table>
			<thead>
				<tr>
					<th><span title="OrgId or ConsumerKey for the authorized application.">Available Tokens</span></th>
					<th class="actionsCol"><span class="fa fa-trash button"
							onclick="doDeleteAll();" title="Delete All"></span></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${availableItems}" var="item">
					<tr>
						<td>${item}</td>
						<td class="actionsCol"><span class="fa fa-trash button"
							onclick="doDelete('${item}');" title="Delete"></span></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</form>
</c:when>
<c:otherwise>
	<h3>No Tokens available.</h3>
</c:otherwise>
</c:choose>

<script type="text/javascript">
function doDelete(itemId) {
	if (!!itemId) {
		var form = document.forms[0];
		if (form && form.submit) {
			form.actionType.value = 'delete';
			form.deleteType.value = 'single';
			form.itemId.value = itemId;
			form.action;
			form.submit();
		}
	} else {
		var errorMsg = 'itemId is blank';
		console.log(errorMsg);
		alert(errorMsg);
	}

	return false;
}

function doDeleteAll() {
	if (confirm('Are you sure you want to delete all stored tokens?')) {
		var form = document.forms[0];
		if (form && form.submit) {
			form.actionType.value = 'delete';
			form.deleteType.value = 'all';
			form.submit();
		}
	}
	return false;
}
</script>

<%@ include file="bottom.jspf"%>