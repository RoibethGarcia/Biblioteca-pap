<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle}" default="Biblioteca PAP" /></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <c:if test="${not empty additionalCSS}">
        <c:forEach var="css" items="${additionalCSS}">
            <link rel="stylesheet" href="${pageContext.request.contextPath}${css}">
        </c:forEach>
    </c:if>
</head>
<body>
    <!-- Header -->
    <header class="header">
        <div class="container">
            <h1>🏛️ Biblioteca PAP</h1>
            <p>Sistema de Gestión de Biblioteca Comunitaria</p>
        </div>
    </header>

    <!-- Navegación -->
    <c:if test="${not empty showNavigation and showNavigation}">
        <nav class="nav">
            <div class="container">
                <ul>
                    <c:if test="${not empty userSession and userSession.userType == 'BIBLIOTECARIO'}">
                        <li><a href="${pageContext.request.contextPath}/dashboard/bibliotecario">Dashboard</a></li>
                        <li><a href="${pageContext.request.contextPath}/management/lectores">Gestión Lectores</a></li>
                        <li><a href="${pageContext.request.contextPath}/management/prestamos">Gestión Préstamos</a></li>
                        <li><a href="${pageContext.request.contextPath}/management/donaciones">Gestión Donaciones</a></li>
                        <li><a href="${pageContext.request.contextPath}/management/reportes">Reportes</a></li>
                    </c:if>
                    <c:if test="${not empty userSession and userSession.userType == 'LECTOR'}">
                        <li><a href="${pageContext.request.contextPath}/dashboard/lector">Mi Dashboard</a></li>
                        <li><a href="${pageContext.request.contextPath}/management/mis-prestamos">Mis Préstamos</a></li>
                        <li><a href="${pageContext.request.contextPath}/management/solicitar-prestamo">Solicitar Préstamo</a></li>
                    </c:if>
                    <c:if test="${not empty userSession}">
                        <li><a href="${pageContext.request.contextPath}/auth/logout">Cerrar Sesión</a></li>
                    </c:if>
                    <c:if test="${empty userSession}">
                        <li><a href="${pageContext.request.contextPath}/auth/login">Iniciar Sesión</a></li>
                        <li><a href="${pageContext.request.contextPath}/auth/register">Registrarse</a></li>
                    </c:if>
                </ul>
            </div>
        </nav>
    </c:if>

    <!-- Contenido principal -->
    <main class="container" style="margin-top: 2rem; margin-bottom: 2rem;">
        <c:if test="${not empty alertMessage}">
            <div class="alert alert-${alertType}">
                <c:out value="${alertMessage}" />
            </div>
        </c:if>

        <jsp:include page="${contentPage}" />
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; 2025 Biblioteca PAP - Sistema de Gestión de Biblioteca Comunitaria</p>
            <p>Desarrollado con Java, JSP y Web Services</p>
        </div>
    </footer>

    <!-- JavaScript -->
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <c:if test="${not empty additionalJS}">
        <c:forEach var="js" items="${additionalJS}">
            <script src="${pageContext.request.contextPath}${js}"></script>
        </c:forEach>
    </c:if>
</body>
</html>
