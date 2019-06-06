<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>

<table border="1">
    <thead>
    <tr>
        <th>Customer</th>
        <th>Book</th>
        <th>Start</th>
        <th>End</th>

    </tr>
    </thead>
    <c:forEach items="${rents}" var="rent">
        <tr>
            <td><c:out value="${rent.customerId}"/></td>
            <td><c:out value="${rent.bookId}"/></td>
            <td><c:out value="${rent.startDate}"/></td>
            <td><c:out value="${rent.expectedEndDate}"/></td>
            <td><form method="post" action="${pageContext.request.contextPath}/rents/delete?id=${rent.id}"
                      style="margin-bottom: 0;"><input type="submit" value="Delete"></form></td>
        </tr>
    </c:forEach>
</table>
</body>
</html>