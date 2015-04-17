<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" href="css/kube.css" type="text/css" media="screen">
    <link rel="stylesheet" href="css/admin.css" type="text/css" media="screen">
    <script src="../js/jquery-1.7.1.min.js" type="text/javascript"></script>
    <script src="../js/ng.js" type="text/javascript"></script>
    <script src="js/ng.admin.js" type="text/javascript"></script>
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