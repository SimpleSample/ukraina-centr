<%@ page import="java.util.Locale" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.nagornyi.uc.i18n.I18n" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    ResourceBundle bundle = I18n.getBundle("i18n.tickets", Locale.forLanguageTag("uk")); //TODO localization issue
%>
<!DOCTYPE html>
<html>
<head>
    <title>Україна-Центр</title>
    <meta property="og:image" content="http://www.ukraina-centr.com/images/photos/DSC_2679_ph.jpg"/>
    <script defer src="js/pikaday.js" type="text/javascript"></script>
    <jsp:include page="/WEB-INF/jsp/imports.jsp" flush="true"/>
    <link rel="stylesheet" href="css/ng/tickets.css" type="text/css" media="screen">
    <link rel="stylesheet" href="css/pikaday.css" type="text/css" media="screen">
    <jsp:include page="/WEB-INF/jsp/i18n.jsp" flush="true">
        <jsp:param name="bundleName" value="client_tickets"/>
    </jsp:include>
    <script src="js/ng/ng.tickets.js" type="text/javascript"></script>
</head>
<body class="index-page">
    <jsp:include page="/WEB-INF/jsp/header.jsp" flush="true">
        <jsp:param name="activeTab" value="index"/>
    </jsp:include>

    <style>
        .index-page header {
            box-shadow: 0px 3px 5px #6B6868;
        }

        .index-page .page-content {
            border-top: 1px solid #999898;
            padding: 10px 0;
        }

        .index-page .page-content > .container > .col-md-8 {
            padding: 0;
        }

        .c-hero-message {
            color: #fff;
            max-width: 940px;
            margin: auto;
            position: absolute;
            left: 0;
            right: 0;
            top: 4px;
            padding: 0 20px;
        }

        .uc-banner {
            background-color: #616161;
            height: 450px;
            position: relative;
            box-shadow: inset 0 0 5px 0 rgba(0, 0, 0, 0.2);
        }

        .c-hero-banner {
            background-image:url(/images/photos/DSC_2679_blurred.jpg);
            height: 100%;
            max-height: 450px;
            margin: auto;
            background-repeat: no-repeat;
            background-size: cover;
            background-position: 10% 40%;
            opacity: .8;
        }

        .c-hero-message h1 {
            font-size: 30px;
            line-height: 1.1em;
            position: relative;
            height: 100%;
            text-shadow: 0 0 12px rgba(0, 0, 0, 0.5), 0 0 32px rgba(0, 0, 0, 0.25), 0 1px rgba(0, 0, 0, 0.75);
            margin: .67em 0;
        }

        .c-banner-search {
            position: relative;
            padding: 10px 0;
        }

        .c-banner-search form {
            margin: 0;
        }

        .c-banner-search-input-container {
            width: 100%;
            position: relative;
            display: inline-block;
            margin: 0;
        }

        .c-banner-search-input {
            width: 100%;
            margin-right: 15px;
            border: 0;
            border-radius: 1px;
            padding: 5px 6px 5px 9px;
            box-shadow: inset 0 0 0 1px #555, 0 0 0 6px rgba(255, 255, 255, 0.5), 0 0 4px 6px rgba(0, 0, 0, 0.25);
            color: #555;
            margin-bottom: 10px;
        }

        .c-banner-stats {
            margin: 10px 0 0 0;
        }

        .c-hero-message h3 {
            font-size: 16px;
            line-height: 1.3em;
            position: relative;
            text-shadow: 0 0 12px rgba(0, 0, 0, 0.5), 0 0 32px rgba(0, 0, 0, 0.25), 0 1px rgba(0, 0, 0, 0.75);
            margin: 0;
            display: inline-block;
            color: #fff;
            letter-spacing: 0;
        }

        .c-banner-search-input[disabled] {
            color: #BEBEBE;
        }

        .both-dirs-block {
            padding-top: 10px;
        }

        .both-dirs-block #both-dirs {
            margin-top: 7px;
            margin-right: 5px;
        }

        .both-dirs-block label {
            font-size: 18px;
            font-weight: 300;
        }

        .uc-banner .get-ticket-btn {
            margin-top: 6px;
        }

        @media (min-width: 768px) {
            .uc-banner {
                height: 360px;
            }

            .index-page .page-content > .container > .col-md-8 {
                padding-right: 15px;
                padding-left: 15px;
            }

            .c-banner-search-input {
                width: 47%;
            }

            .c-hero-message h3 {
                font-size: 21px;
            }

            .c-hero-message h1 {
                font-size: 48px;
            }
        }

        @media (min-width: 992px) {
            .uc-banner {
                height: 320px;
            }

            .c-banner-search-input {
                width: 180px;
                margin-bottom: 0;
            }
        }
    </style>

    <div class="uc-banner">
        <div class="c-hero-banner"></div>
        <div class="c-hero-message">
            <h1><span>Скористайся пошуком квитків</span></h1>
            <div class="c-banner-search">
                <div class="c-banner-search-input-container">
                    <select id="forth-city" class="c-banner-search-input">
                        <option value="" style="display: none;" disabled selected>Звідки</option>
                    </select>
                    <select id="back-city" class="c-banner-search-input">
                        <option value="" style="display: none;" disabled selected>Куди</option>
                    </select>
                    <input type="text" id="forth-date" placeholder="Дата виїзду" name="forth-date"
                           class="c-banner-search-input"/>
                    <input type="text" id="back-date" placeholder="Дата повернення" name="back-date"
                           class="c-banner-search-input" disabled/>
                    <div class="both-dirs-block">
                        <input id="both-dirs" type="checkbox" name="checkbox-1">
                        <label for="both-dirs">Бажаю квиток в обидві сторони</label>
                    </div>
                    <a href="#" id="show-tickets" class="get-ticket-btn">Пошук</a>
                </div>
            </div>
            <div class="c-banner-stats">
                <div><h3><span>Пошук здійснюється за сполученням Україна - Італія</span></h3></div>
                <div><a href="/conditions"><h3 class="c-highlight-link">Дізнатись більше »</h3></a></div>
            </div>
        </div>
    </div>
    <div class="order-board-wrapper container-fluid">
        <div class="order-board clear-both container" style="display: none;">
            <div id="message-container"></div>
            <div class="results">
                <div id="forth-trips" class="forth-trips"></div>
                <div id="back-trips" class="back-trips" style="display: none;"></div>
                <div class="btn-wrapper"><a href="#" id="create-ticket" class="get-ticket-btn">+ Квиток</a></div>
            </div>
            <div class="ticket-board" style="display: none;">
                <div class="tickets-header clear-both">
                    <div class="tickets-title">
                        <span><%=bundle.getString("tickets.tickets")%></span>
                    </div>
                    <!--<select class="change-currency">-->
                    <!--<option id="uah" selected="">₴</option>-->
                    <!--<option id="eur">€</option>-->
                    <!--</select>-->
                </div>
                <div class="ticket-area">
                    <div id="tickets" class="tickets">
                    </div>
                    <div class="button-area">
                        <button id="add-ticket" class="btn btn-green light"><%=bundle.getString("tickets.add")%></button>
                        <button id="delete-ticket" class="btn btn-green light"><%=bundle.getString("tickets.remove")%></button>
                        <button id="buy-tickets" class="btn btn-green light"><%=bundle.getString("tickets.buy")%></button>
                    </div>
                    <div class="ticket-price-sum" style="opacity: 0;">
                        <div class="price-disc clear-both">
                            <div class="price-right"><span class="sign">€</span>
                                <span id="sum-disc-price" class="price-self">0</span>
                            </div>
                        </div>
                        <div class="price-full">
                            <span id="sum-full-price" class="price-self" style="display: none;">0</span>
                        </div>
                    </div>
                    <form method="POST" id="liq-form" action="https://www.liqpay.ua/api/3/checkout" accept-charset="utf-8">
                        <input type="hidden" name="data"/>
                        <input type="hidden" name="signature"/>
                    </form>
                </div>
            </div>
        </div>
    </div>
    <div class="container-fluid page-content">
        <div class="container">
            <div class="col-md-8">
                <div class="col-sm-12">
                    <h2>Вітаємо Вас!</h2>
                    <p class="p2">Заснована понад десять років тому, "Україна-Центр" здійснює міжнародні перевезення пасажирів та багажу, а також пропонує в оренду <a class="link italic" href="/leasing">зручні та просторі автобуси</a>. Тому, незалежно від того, яка послуга вам необхідна: організувати трансфер з аеропорту, перевезення для великого корпоративного заходу, шкільну чи сімейну поїздку, парк сучасних автобусів "Україна-Центр" зможе задовольнити будь-які вимоги до кількості груп.</p>
                    <p class="p3">Ми пишаємося тим, що пропонуємо відмінне співвідношення ціни і якості.  Наша репутація завдячує високій якості обслуговування, яку ми втілюємо в кожній поїздці!</p>
                </div>
                <div class="col-sm-6 col-md-12">
                    <h3 class="p1">До вашої уваги:</h3>
                    <div class="wrapper">
                    <div class="col-md-6">
                        <ul class="list-1">
                            <li><a href="#">Бронювання та придбання квитків</a></li>
                            <li><a href="#">Перевезення багажу</a></li>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <ul class="list-1">
                            <li><a href="#">Пасажирські перевезення</a></li>
                            <li><a href="#">Оренда автобусів</a></li>
                        </ul>
                    </div>
                </div>
                </div>
                <div class="col-sm-6 col-md-12">
                    <h3>Діючі знижки:</h3>
                    <ul class="list-1 italic">
                        <li><b class="discount">50%</b> - при поверненні квитків за 6 поїздок</li>
                        <li><b class="discount">50%</b> - діти до 4-х років</li>
                        <li><b class="discount">30%</b> - діти до 12-ти років</li>
                        <li><b class="discount">10%</b> - люди пенсійного віку та молодь до 25-ти років</li>
                        <li><b class="discount">10%</b> - групи більше 6-ти чоловік</li>
                    </ul>
                </div>
            </div>
            <div class="col-md-4 right-block">
                <div class="news-block">
                    <h3>Відгуки:</h3>
                    <div class="rss-news">Завантажується...</div>
                </div>
                <div class="wrapper margin-bot">
                    <div class="extra-wrap">
                        <strong class="title-1" style="max-width: 340px; margin: 0 auto;">Не забудь поділитись
                            <em style="padding-left: 80px; padding-top: 6px;">з друзями!</em></strong>
                    </div>
                    <div class="social-block">
                            <div class="a2a_kit a2a_kit_size_24 a2a_default_style">
                                <a class="a2a_button_facebook a2a_counter"></a>
                                <a class="a2a_button_google_plusone" data-size="standard" style="width:85px"></a>
                                <a class="a2a_dd a2a_counter" href="https://www.addtoany.com/share"></a>
                            </div>

                            <script async src="https://static.addtoany.com/menu/page.js"></script>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <jsp:include page="/WEB-INF/jsp/footer.jsp" flush="true"/>

    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript">
        function feedTemplate(date, url, title, annotation) {
            return '<div>' +
                        '<div>' +
                            '<span class="feed_date">'+date+'</span>&nbsp;' +
                            '<span class="feed_news_title"><a href="'+url+'">'+title+'</a></span>' +
                        '</div>' +
                        '<div class="feed_annotation">'+annotation+'</div>' +
                    '</div>';
        }
        function ya_format_date(date){
            var d = date.toLocaleTimeString().split(':');
            return [d[0], d[1]].join(':').replace(/d{1,2}:d{1,2}(:d{1,2})/, '');
        }
        $(document).ready(function() {
            setTimeout(function() {
                new Request("getFeedbacks", {count: 5}).send(function(data) {
                    var feedbacks = data.feedbacks;
                    var container = $('.rss-news');
                    container.html('');
                    for (var i = 0; i < feedbacks.length; i++) {
                        var feedback = feedbacks[i];
                        var feedDate = new Date(feedback.date).toLocaleString();
                        var feedHtml = feedTemplate(feedDate, "#", feedback.username, feedback.feedbackText);
                        container.append($(feedHtml));
                    }
                });
            }, 0);
        });
    </script>
</body>
</html>
