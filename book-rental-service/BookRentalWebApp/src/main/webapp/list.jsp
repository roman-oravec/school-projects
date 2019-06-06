<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>
    
<table border="1">
    <thead>
    <tr>
        <th>Book</th>
        <th>Author</th>
        <th>Number of pages</th>
        <th>Copies</th>

    </tr>
    </thead>
    <c:forEach items="${books}" var="book">
        <tr>
            <td><c:out value="${book.name}"/></td>
            <td><c:out value="${book.author}"/></td>
            <td><c:out value="${book.pages}"/></td>
            <td><c:out value="${book.copies}"/></td>
            <td><form method="post" action="${pageContext.request.contextPath}/books/delete?id=${book.id}"
                      style="margin-bottom: 0;"><input type="submit" value="Delete"></form></td>
        </tr>
    </c:forEach>
</table>

<h2>Add a new book</h2>
<c:if test="${not empty error}">
    <div style="border: solid 1px red; background-color: yellow; padding: 10px">
        <c:out value="${error}"/>
    </div>
</c:if>
<form action="${pageContext.request.contextPath}/books/add" method="post">
    <table>
        <tr>
            <th>Book name:</th>
            <td><input type="text" name="name" value="<c:out value='${param.name}'/>"/></td>
        </tr>
        <tr>
            <th>Author name:</th>
            <td><input type="text" name="author" value="<c:out value='${param.author}'/>"/></td>
        </tr>
        <tr>
            <th>Number of pages:</th>
            <td><input type="text" name="pages" value="<c:out value='${param.pages}'/>"/></td>
        </tr>
        <tr>
            <th>Number of copies:</th>
            <td><input type="text" name="copies" value="<c:out value='${param.copies}'/>"/></td>
        </tr>
    </table>
        <input type="Submit" value="OK" />
    <br><br>
    <a href="/BookRentalWebApp/customers"> CUSTOMERS </a>
</form>
</body>
</html>