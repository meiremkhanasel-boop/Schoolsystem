<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <title>School Management System</title>
</head>
<body>

<%-- Навигация --%>
<nav class="navbar navbar-expand-lg navbar-dark sticky-top mb-4">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">
            🏫 MySchool <span class="badge bg-warning text-dark" style="font-size: 0.5em;">PRO</span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <c:if test="${not empty sessionScope.user}">
                    <li class="nav-item"><a class="nav-link px-3" href="students">Студенты</a></li>
                    <li class="nav-item"><a class="nav-link px-3" href="teachers">Учителя</a></li>
                </c:if>
            </ul>
            <div class="d-flex align-items-center">
                <c:choose>
                    <c:when test="${empty sessionScope.user}">
                        <a href="login" class="btn btn-outline-light btn-sm me-2">Войти</a>
                        <a href="register" class="btn btn-light btn-sm text-primary fw-bold">Регистрация</a>
                    </c:when>
                    <c:otherwise>
                        <span class="text-white me-3 opacity-75">👤 ${sessionScope.user}</span>
                        <a href="logout" class="btn btn-danger btn-sm px-3 shadow-sm">Выйти</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</nav>

<div class="container">
    <div class="main-card card p-5 shadow border-0 mx-auto text-center" style="max-width: 900px; border-radius: 20px;">

        <c:choose>
            <%-- ЕСЛИ ПОЛЬЗОВАТЕЛЬ НЕ ВОШЕЛ --%>
            <c:when test="${empty sessionScope.user}">
                <div class="py-5">
                    <h1 class="display-3 fw-bold mb-3 text-primary">🏫 School System</h1>
                    <p class="lead text-muted mb-5">Добро пожаловать в современную систему управления учебным процессом.</p>
                    <div class="d-grid gap-3 d-sm-flex justify-content-sm-center">
                        <a href="login" class="btn btn-primary btn-lg px-5 shadow">Войти в систему</a>
                        <a href="register" class="btn btn-outline-secondary btn-lg px-5">Создать аккаунт</a>
                    </div>
                </div>
            </c:when>

            <%-- ЕСЛИ ПОЛЬЗОВАТЕЛЬ ВОШЕЛ --%>
            <c:otherwise>
                <h1 class="display-5 fw-bold mb-2">Добрый день, ${sessionScope.user}!</h1>
                <p class="text-muted mb-5">Выберите раздел, с которым хотите работать сегодня:</p>

                <div class="row g-4 justify-content-center">
                    <div class="col-md-6">
                        <div class="card h-100 p-4 border-0 shadow-sm bg-light transition-hover">
                            <div class="display-1 mb-3">👨‍🎓</div>
                            <h3 class="fw-bold">Студенты</h3>
                            <p class="text-muted">Управление базой данных учащихся, классами и удаление записей.</p>
                            <a href="students" class="btn btn-primary btn-lg mt-auto shadow-sm">Открыть раздел</a>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="card h-100 p-4 border-0 shadow-sm bg-light transition-hover">
                            <div class="display-1 mb-3">👩‍🏫</div>
                            <h3 class="fw-bold">Учителя</h3>
                            <p class="text-muted">Просмотр состава преподавателей и их учебных дисциплин.</p>
                            <a href="teachers" class="btn btn-success btn-lg mt-auto shadow-sm text-white">Открыть раздел</a>
                        </div>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>