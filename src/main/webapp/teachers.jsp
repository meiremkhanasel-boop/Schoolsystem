<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>School System | Учителя</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="bg-light">

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
                <li class="nav-item">
                    <a class="nav-link px-3" href="students">Студенты</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link px-3 active fw-bold" href="teachers">Учителя</a>
                </li>
            </ul>
            <div class="d-flex align-items-center">
                <span class="text-white me-3 opacity-75">👤 ${sessionScope.user}</span>
                <a href="logout" class="btn btn-danger btn-sm px-3 shadow-sm">Выйти</a>
            </div>
        </div>
    </div>
</nav>

<div class="container">
    <div class="row g-4">
        <%-- Блок 1: Форма добавления --%>
        <div class="col-md-4">
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-success text-white fw-bold py-3">
                    👩‍🏫 Добавить преподавателя
                </div>
                <div class="card-body p-4">
                    <form action="${pageContext.request.contextPath}/teachers" method="post">
                        <div class="mb-3">
                            <label class="form-label small text-muted fw-bold">ID Номер</label>
                            <input type="number" name="id" class="form-control" placeholder="1" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label small text-muted fw-bold">ФИО Преподавателя</label>
                            <input type="text" name="name" class="form-control" placeholder="Александр Пушкин" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label small text-muted fw-bold">Предмет</label>
                            <input type="text" name="subject" class="form-control" placeholder="Литература" required>
                        </div>
                        <button type="submit" class="btn btn-success w-100 shadow-sm mt-3">Добавить в базу</button>
                    </form>
                </div>
            </div>
        </div>

        <%-- Блок 2: Таблица списка --%>
        <div class="col-md-8">
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white fw-bold py-3 d-flex justify-content-between align-items-center">
                    <span>📚 Список преподавателей</span>
                    <span class="badge bg-success rounded-pill">${teachersList.size()} записей</span>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light">
                            <tr>
                                <th class="ps-4">ID</th>
                                <th>Имя</th>
                                <th>Предмет</th>
                                <th class="text-center">Действие</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="t" items="${teachersList}">
                                <tr>
                                    <td class="ps-4 fw-bold text-success">#${t.id}</td>
                                    <td class="fw-semibold">${t.name}</td>
                                    <td><span class="badge bg-light text-dark border">${t.subject}</span></td>
                                    <td class="text-center">
                                        <a href="teachers?action=delete&id=${t.id}"
                                           class="btn btn-sm btn-outline-danger px-3 rounded-pill"
                                           onclick="return confirm('Удалить преподавателя?')">
                                            Удалить
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty teachersList}">
                                <tr>
                                    <td colspan="4" class="text-center py-5 text-muted">
                                        В базе пока нет учителей.
                                    </td>
                                </tr>
                            </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>