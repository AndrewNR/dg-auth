<%@ include file="top.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="errorMsg" value="${requestScope['errorMsg']}" />

<h2>Errors, errors. . .</h2>

<h3 style="color:red;">ERROR: ${errorMsg != null ? errorMsg : "An error occurred."}</h3>

<%@ include file="bottom.jsp" %>