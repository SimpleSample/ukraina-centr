<%@ page import="java.util.*" %>
<%@ page import="com.nagornyi.uc.i18n.I18n" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    ResourceBundle bundle = I18n.getBundle("i18n.header", Locale.forLanguageTag("uk")); //TODO localization issue
    List<String> navItemList = Arrays.asList("index", "schedule", "leasing", "about");
    Map<String, String> navItems = new HashMap<String, String>();
    navItems.put("index", "");
    navItems.put("schedule", "schedule");
    navItems.put("leasing", "leasing");
    navItems.put("about", "about");

%>
<header class="uc-page-header">
    <div class="container">
        <div class="clear-both">
            <a class="logo" href="${pageContext.request.contextPath}/">
                <img src="${pageContext.request.contextPath}/img/logo.png" alt="Україна-Центр">
            </a>
            <div class="uc"><img src="${pageContext.request.contextPath}/img/UC.png"></div>
            <div class="header-right">
                <div class="auth-links clear-both">
                    <a href="#" id="login" style="display: none;" class="italic"><%=bundle.getString("header.login")%></a>
                    <a href="#" id="cabinet" style="display: none;" class="italic"><%=bundle.getString("header.cabinet")%></a>
                    <a href="#" id="register" style="display: none;" class="italic"><%=bundle.getString("header.register")%></a>
                    <a href="#" id="logout" style="display: none;" class="italic"><%=bundle.getString("header.logout")%></a>
                </div>
                <div class="bottom-header-line clear-both">
                    <div style="min-width:60px;" id="currency-container"></div>
                    <div class="lang">
                        <a id="flag-it" class="flag" href="#"><img src="${pageContext.request.contextPath}/img/flag-italy.png"></a>
                        <a id="flag-ukr" class="flag active" href="#"><img src="${pageContext.request.contextPath}/img/flag-ukraine.png"></a>
                        <a id="flag-en" class="flag" href="#"><img src="${pageContext.request.contextPath}/img/flag-uk.png"></a>
                        <a id="flag-ru" class="flag" href="#"><img src="${pageContext.request.contextPath}/img/rus.png"></a>
                    </div>
                </div>
            </div>
        </div>
        <nav class="navbar uc-navbar">
            <div class="container-fluid">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>

                </div>
                <div id="navbar" class="navbar-collapse collapse in">
                    <ul class="nav navbar-nav navbar-right">
                        <%
        //                        Logger logger = Logger.getLogger(getClass().getName());
                            for (String navItem : navItemList) {
                                String styleClass = "";
                                String activeTab = request.getParameter("activeTab");
                                if (activeTab == null) activeTab = "index";
                                if (activeTab.equals(navItem)) {
                                    styleClass = "active";
                                }
                                String localizedValue = bundle.getString("header." + navItem);
                        %>
                        <li><a class="<%=styleClass%>" href="${pageContext.request.contextPath}/<%=navItems.get(navItem)%>"><%=localizedValue%></a></li>
                        <% } %>
                    </ul>
                </div>
            </div>
        </nav>
    </div>
</header>