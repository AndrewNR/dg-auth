<%@page import="java.util.List"%>
<%@ include file="top.jsp" %>

<%
String errorMsg = (String) request.getAttribute("errorMsg");
Boolean isError = errorMsg != null;

List<String> availableItems = null;
if (!isError) {
	availableItems = (List<String>) request.getAttribute("availableItems");
}
%>

<h3 style="display: <%= isError ? "block" : "none" %>">ERROR: <%= errorMsg %></h3>

<% if (!isError) { %>
<form action="<%=application.getContextPath()%>/authList" method="GET">
	<table>
		<thead><tr>
			<th>Select</th>
			<th>Available Tokens</th>
		</tr></thead>
		<tbody>
	<% for (String item : availableItems) { %>
		<tr>
			<td><input type="checkbox" value="<%=item%>"/></td>
			<td><%=item%></td>
		</tr>
	<% } %>
		</tbody>
	</table>
</form>
<% } %>
<%@ include file="bottom.jsp" %>