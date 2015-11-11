<%@ include file="top.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<form action="${contextPath}/auth" method="POST">
	<table>
		<tr>
			<td>Consumer key</td>
			<td><input name="consumerKey" type="text" maxlength="256" size="80" value=""><br/></td>
		</tr>
		<tr>
			<td>Consumer secret</td>
			<td><input name="consumerSecret" type="text" maxlength="256" size="80" value=""><br/></td>
		</tr>
		<tr>
			<td>Redirect URL</td>
			<td><input name="redirectUrl" type="text" maxlength="256" size="100" value=""><br/></td>
		</tr>
		<tr>
			<td><input type="submit" value="Submit" /></td>
			<td></td>
		</tr>
	</table>
</form>

<%@ include file="bottom.jsp" %>