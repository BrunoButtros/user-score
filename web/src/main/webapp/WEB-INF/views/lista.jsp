<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="pt-BR"><head><meta charset="UTF-8"><title>User Score — Lista</title></head>
<body><h1>Scores dos usuários</h1><table><thead><tr><th>Nome</th><th>Score</th></tr></thead><tbody>
<c:forEach var="user" items="${userScores}"><tr><td><c:out value="${user.nome()}"/></td><td><c:out value="${user.score()}"/></td></tr></c:forEach>
</tbody></table><form action="<c:url value='/logout'/>" method="post"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"><button>Sair</button></form></body></html>
