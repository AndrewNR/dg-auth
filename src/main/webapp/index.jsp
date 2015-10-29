<%@ include file="top.jsp" %>

<p>This application demonstrates the use of the OAuth protocol with Salesforce.com's Remote Access applications. 
It uses OAuth to access Accounts and Contacts via the Web Services API.</p>

<form action="<%=application.getContextPath()%>/auth" method="POST">
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