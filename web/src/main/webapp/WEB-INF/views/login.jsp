<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="pt-BR"><head><meta charset="UTF-8"><title>User Score — Login</title></head>
<body><h1>User Score</h1><form action="<c:url value='/login'/>" method="post">
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
<label>Login <input name="login" required autocomplete="username"></label>
<label>Senha <input type="password" name="password" required autocomplete="current-password"></label><button>Entrar</button>
</form><p role="alert"><c:out value="${error}"/></p></body></html>
