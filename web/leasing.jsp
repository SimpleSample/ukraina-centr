<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Лізинг</title>
    <jsp:include page="/WEB-INF/jsp/imports.jsp" flush="true"/>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/header.jsp" flush="true">
    <jsp:param name="activeTab" value="leasing"/>
</jsp:include>

<div class="container-fluid page-content">
    <div class="container pad-container">
        <div style="height: 400px; padding-top: 100px; background-color: white; opacity: 0.4; border: 1px solid #85D051; border-radius: 10px;">
            <h2 class="italic" style="text-align: center;font-weight: 100;">Додамо найближчим часом :)</h2>
        </div>
    </div>
</div>


<jsp:include page="/WEB-INF/jsp/footer.jsp" flush="true"/>
</body>
</html>
