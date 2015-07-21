<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="col-sm-3 col-md-2 sidebar sidebar-swiped">
    <div class="sidebar-inner">
        <ul class="nav nav-sidebar">
            <li class="overview-item"><a href="/admin">Огляд</a></li>
            <li class="passengers-item"><a href="/admin/passengers">Пасажири</a></li>
            <li class="users-item"><a href="/admin/users">Користувачі</a></li>
            <li class="partners-item"><a href="/admin/partners">Партнери</a></li>
            <li class="tickets-item"><a href="/admin/tickets">Місця</a></li>
            <li class="partners-item"><a href="#">Репорти</a></li>
            <li><a id="scan-barcode" href="zxing://scan/?ret=http%3A%2F%2Fwww.ukraina-centr.com%2Fadmin%3Fbarcode%3D%7BRAWCODE%7D">Scan barcode</a></li>
        </ul>
        <div class="swipe-area">
            <div class="toggle-sidebar-inner">
                <a id="toggle-sidebar-link" href="#"><span class="glyphicon"></span></a>
            </div>
        </div>
    </div>
</div>