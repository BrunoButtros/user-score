<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="pt-BR"><head><meta charset="UTF-8"><title>Erro</title></head><body><h1>Não foi possível carregar os dados</h1><p><c:out value="${error}"/></p><a href="<c:url value='/login'/>">Voltar</a></body></html>
