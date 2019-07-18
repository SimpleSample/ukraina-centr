<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Лізинг</title>
    <jsp:include page="/WEB-INF/jsp/imports.jsp" flush="true"/>
    <style>
        /* Prevents slides from flashing */
        #photo-slides {
            display:none;
        }

        #photo-slides .slidesjs-previous, #photo-slides .slidesjs-next {
            margin-right: 5px;
            float: left;
        }

        .icon-chevron-left:before {
            content: "\f053";
        }
        .icon-large:before {
            vertical-align: -10%;
            font-size: 1.3333333333333333em;
        }

    </style>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/header.jsp" flush="true">
    <jsp:param name="activeTab" value="leasing"/>
</jsp:include>

<div class="container-fluid page-content">
    <div class="container pad-container">
        <article>
            <h2>Прайс-лист</h2>
            <h3> на оренду автобуса Setra s417HDH до 55 місць</h3>

            <table class="table table-bordered table-striped table-hover" style=" margin-bottom: 0px;">
                <tbody>
                <tr>
                    <td>Подача автобусу</td><td><b>500</b> грн</td>
                </tr>
                <tr>
                    <td>1 година роботи по місту</td><td><b>500</b> грн</td>
                </tr>
                <tr>
                    <td>1 км територією України</td><td><b>25</b> грн</td>
                </tr>
                <tr>
                    <td>1 км країнами Європи</td><td><b>1-1,2</b> €</td>
                </tr>
                <tr>
                    <td>Трансфер Кропивницький - Бориспіль</td><td><b>1500</b> грн</td>
                </tr>
                <tr>
                    <td>Минімальне замовлення 3 год</td><td><b>2000</b> грн</td>
                </tr>
                </tbody>
            </table>

            <div id="photo-slides">
                <img src="images/photos/20170521_120937_ph.jpg">
                <img src="images/photos/20170521_120938_ph.jpg">
                <img src="images/photos/20170527_200157_ph.jpg">
                <img src="images/photos/20170527_201412_ph.jpg">

                <%--<a href="#" class="slidesjs-previous slidesjs-navigation"><span class="glyphicon glyphicon-chevron-left" title="prev" aria-hidden="true"></span></a>--%>
                <%--<a href="#" class="slidesjs-next slidesjs-navigation"><span class="glyphicon glyphicon-chevron-right" title="next" aria-hidden="true"></span></a>--%>
            </div>
        <%--<div style="height: 400px; padding-top: 100px; background-color: white; opacity: 0.4; border: 1px solid #85D051; border-radius: 10px;">--%>
            <%--<h2 class="italic" style="text-align: center;font-weight: 100;">Додамо найближчим часом :)</h2>--%>
        <%--</div>--%>
        </article>
    </div>
</div>


<jsp:include page="/WEB-INF/jsp/footer.jsp" flush="true"/>
<script src="https://cdnjs.cloudflare.com/ajax/libs/slidesjs/3.0/jquery.slides.min.js"></script>
<script>

    $(document).ready(function() {
        $("#photo-slides").slidesjs({
            width: 938,
            height: 528,
            play: {
                active: false,
                // [boolean] Generate the play and stop buttons.
                // You cannot use your own buttons. Sorry.
                effect: "slide",
                // [string] Can be either "slide" or "fade".
                interval: 3000,
                // [number] Time spent on each slide in milliseconds.
                auto: true,
                // [boolean] Start playing the slideshow on load.
                swap: false,
                // [boolean] show/hide stop and play buttons
                pauseOnHover: false,
                // [boolean] pause a playing slideshow on hover
                restartDelay: 2500
                // [number] restart delay on inactive slideshow
            },
            pagination: {
                active: false,
                // [boolean] Create pagination items.
                // You cannot use your own pagination. Sorry.
                effect: "slide"
                // [string] Can be either "slide" or "fade".
            },
            navigation: {
                active: false,
                // [boolean] Generates next and previous buttons.
                // You can set to false and use your own buttons.
                // User defined buttons must have the following:
                // previous button: class="slidesjs-previous slidesjs-navigation"
                // next button: class="slidesjs-next slidesjs-navigation"
                effect: "slide"
                // [string] Can be either "slide" or "fade".
            }
        });
    });
</script>
</body>
</html>
