<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- Placed at the end of the document so the pages load faster -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery-ui.min.js"></script>
<script src="${pageContext.request.contextPath}/js/EventBus.js" type="text/javascript"></script>
<script>
    Tablesaw = {
        i18n: {
            modes: [ 'Stack', 'Swipe', 'Toggle' ],
            columns: 'Кол<span class=\"a11y-sm\">онк</span>и',
            columnBtnText: 'Колонки',
            columnsDialogError: 'Колонки недоступні.',
            sort: 'Сортування'
        }
    };
</script>
<script src="${pageContext.request.contextPath}/js/tablesaw.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/js/ng/ng.common.js"></script>
<script src="${pageContext.request.contextPath}/admin/js/adminpage.common.js"></script>

<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<script>
    (function () {
        'use strict';

        if (navigator.userAgent.match(/IEMobile\/10\.0/)) {
            var msViewportStyle = document.createElement('style');
            msViewportStyle.appendChild(
                    document.createTextNode(
                            '@-ms-viewport{width:auto!important}'
                    )
            );
            document.querySelector('head').appendChild(msViewportStyle)
        }

    })();
</script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/hammer.js/2.0.4/hammer.min.js"></script>
