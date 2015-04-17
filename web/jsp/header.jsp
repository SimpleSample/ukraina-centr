<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<header>
    <div class="border-bot">
        <div class="main">
            <h1 class="h1-header">
                <a class="logo" href="../index.html">Україна-Центр</a>
                <div class="uc"></div>
                <div class="header-right">
                    <div class="register">
                        <a href="#" id="login" style="display: none;" class="italic">Вхід</a>
                        <a href="#" id="cabinet" style="display: none;" class="italic">Вхід в кабінет</a>
                        <a href="#" id="register" style="display: none;" class="italic">Зареєструватись</a>
                        <a href="#" id="logout" style="display: none;" class="italic">Вихід</a>
                    </div>
                    <div class="lang">
                        <a class="lang-italy" href="#"></a>
                        <a class="lang-ukr" href="#"></a>
                        <a class="lang-eng" href="#"></a>
                        <a class="lang-rus" href="#"></a>
                    </div>
                </div>
            </h1>
            <nav>
                <ul class="menu">
                    <li><a class="active" href="../index.html">Головна</a></li>
                    <li><a href="../schedule.html">Розклад</a></li>
                    <li><a href="../tickets.html">Квитки</a></li>
                    <li><a href="../leasing.html">Оренда</a></li>
                    <li><a href="../about.html">Про компанію</a></li>
                </ul>
            </nav>
            <div class="clear"></div>
        </div>
    </div>
    <%=request.getRequestURL().toString() %>
    <% if (request.getRequestURL().toString().contains("index.jsp")) {%>
    <div class="row-bot">
        <div class="slider-wrapper">
            <div class="slider">
                <ul class="items">
                    <li>
                        <img src="../images/slider-img1.jpg" alt="" />
                    </li>
                    <li>
                        <img src="../images/slider-img2.jpg" alt="" />
                    </li>
                    <li>
                        <img src="../images/slider-img3.jpg" alt="" />
                    </li>
                </ul>
            </div>
        </div>
    </div>
    <%} %>
</header>