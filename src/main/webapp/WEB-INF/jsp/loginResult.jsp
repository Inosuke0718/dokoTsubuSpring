<%@ page language="java" contentType="text/html; charset=UTF-8" 
    pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>どこつぶ</title>
</head>
<body>
<h1>どこつぶログイン</h1>
<c:choose>
  <c:when test="${not empty loginUser}">
    <p>ログインに成功しました</p>
    <p>ようこそ<c:out value="${loginUser.name}" />さん</p>
    <a href="Main">つぶやき投稿・閲覧へ</a>
  </c:when>
  <c:otherwise>">
    <p>ログインに失敗しました</p>
    <a href="index.jsp">トップへ</a>
  </c:otherwise>
</c:choose>
</body>
</html>