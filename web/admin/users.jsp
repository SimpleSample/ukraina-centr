<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<html>
<head>
    <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" name="viewport">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/favicon.png">
    <link href='http://fonts.googleapis.com/css?family=Roboto:300,400&subset=latin,cyrillic-ext' rel='stylesheet' type='text/css'>
    <link href="${pageContext.request.contextPath}/admin/css/kube.css" rel="stylesheet" type="text/css" media="screen">
    <link href="${pageContext.request.contextPath}/css/ng/popup.css" rel="stylesheet" type="text/css" media="screen">
    <link href="${pageContext.request.contextPath}/admin/css/admin.css" rel="stylesheet" type="text/css" media="screen">
    <link href="${pageContext.request.contextPath}/admin/css/print-style.css" type="text/css" rel="stylesheet" media="print" />
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/EventBus.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/ng/ng.common.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/admin/js/ng.admin.users.js" type="text/javascript"></script>
</head>
<body>
<%
%>
<div class="units-row">
    <div class="unit-centered passengers-page">
        <div class="forms add-user">
            <h2>Додати користувача</h2>
            <label>
                <input type="text" name="username" placeholder="Ім'я" class="width-50" />
            </label>
            <label>
                <input type="text" name="surname" placeholder="Прізвище" class="width-50" />
            </label>
            <label>
                <input type="text" name="phone" placeholder="Телефон" class="width-50" />
            </label>
            <label>
                <input type="email" name="email" placeholder="Email" class="width-50" />
            </label>
            <label>
                <input type="password" name="password" placeholder="Пароль" class="width-50" />
            </label>
            <label for="user-roles">
                <select id="user-roles" class="width-50">
                    <option data-role="4" selected>Звичайний користувач</option>
                    <option data-role="1">Партнер</option>
                    <option data-role="0">Адміністратор</option>
                </select>

                <div class="forms-desc">Оберіть роль для користувача</div>
            </label>
            <label for="user-langs">
                <select id="user-langs" class="width-50">
                    <option data-role="uk" selected>Українська</option>
                    <option data-role="it">Італійська</option>
                    <option data-role="en">Англійська</option>
                    <option data-role="ru">Російська</option>
                </select>
                <div class="forms-desc">Оберіть мову інтерфейсу користувача</div>
            </label>
            <p>
                <button id="save-user" class="btn-green width-50">Зберегти</button>
            </p>
        </div>
    </div>
</div>
</body>
</html>
