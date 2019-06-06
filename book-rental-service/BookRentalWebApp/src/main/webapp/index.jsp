<%@ page import="com.mycompany.bookrentalwebapp.BookServlet" %>
<%@ page import="com.mycompany.bookrentalwebapp.RentServlet" %>
<%@ page import="com.mycompany.bookrentalwebapp.CustomerServlet" %>
<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:redirect url="<%=BookServlet.URL_MAPPING%>"/>
