<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Edit book</title>
    </head>
    <body> 
        <h1>Edit book</h1>
    <form action="${pageContext.request.contextPath}/books/save" method="post">
        
        <input type="hidden" name="id" value="${edit.id}" />
    <table>
        
        <tr>
            <th>Book name:</th>
            <td><input type="text" name="name" value="<c:out value='${edit.name}'/>"/></td>
        </tr>
        <tr>
            <th>Author name:</th>
            <td><input type="text" name="author" value="<c:out value='${edit.author}'/>"/></td>
        </tr>
        <tr>
            <th>Number of pages:</th>
            <td><input type="text" name="pages" value="<c:out value='${edit.pages}'/>"/></td>
        </tr>
        <tr>
            <th>Number of copies:</th>
            <td><input type="text" name="copies" value="<c:out value='${edit.copies}'/>"/></td>
        </tr>
    </table>
        <input type="submit" value="SAVE" />
</form>
    </body>
</html>
