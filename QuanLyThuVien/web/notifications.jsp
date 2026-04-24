<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Hộp thư Thông báo - Smart Lib</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <style>
        :root { --primary-color: #173F5F; --bg-light: #F8F9FA; }
        body { font-family: 'Inter', sans-serif; background-color: var(--bg-light); }
        .notif-item { 
            border-radius: 12px; 
            transition: 0.2s; 
            border-left: 5px solid transparent;
            margin-bottom: 10px;
        }
        .notif-item:hover { transform: translateX(5px); box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
        .unread { border-left-color: #ED553B; background-color: #fff !important; font-weight: 600; }
        .read { border-left-color: #ddd; opacity: 0.8; }
    </style>
</head>
<body>
    <jsp:include page="header.jsp" />

    <div class="container py-5" style="max-width: 850px;">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="fw-bold m-0" style="color: var(--primary-color);">
                <i class="bi bi-bell-fill text-warning me-2"></i>Thông báo của tôi
            </h2>
            <span class="badge bg-secondary rounded-pill">${allNotifs.size()} tin nhắn</span>
        </div>

        <div class="list-group">
            <c:choose>
                <c:when test="${empty allNotifs}">
                    <div class="text-center py-5 bg-white rounded-4 shadow-sm">
                        <img src="https://cdn-icons-png.flaticon.com/512/10534/10534066.png" width="100" class="mb-3 opacity-25">
                        <p class="text-muted fs-5">Hộp thư trống rỗng sếp ơi!</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${allNotifs}" var="n">
                        <c:set var="finalUrl" value="home" />
                        
                        <c:choose>
                            <c:when test="${fn:contains(n.message, '#ORD-')}">
                                <c:set var="tempId" value="${fn:substringAfter(n.message, '#ORD-')}" />
                                <c:set var="orderId" value="${fn:substringBefore(tempId, ' ')}" />
                                <c:if test="${empty orderId}"><c:set var="orderId" value="${tempId}" /></c:if>
                                <c:set var="finalUrl" value="order-detail?id=${orderId}" />
                            </c:when>

                            <c:when test="${fn:contains(n.message, '#LIB-')}">
                                <c:set var="tempId" value="${fn:substringAfter(n.message, '#LIB-')}" />
                                <c:set var="borrowId" value="${fn:substringBefore(tempId, ' ')}" />
                                <c:if test="${empty borrowId}"><c:set var="borrowId" value="${tempId}" /></c:if>
                                <c:set var="finalUrl" value="borrow-detail?id=${borrowId}" />
                            </c:when>
                        </c:choose>

                        <%-- 🛑 ĐÃ FIX: Chèn link read-notif --%>
                        <a href="read-notif?nid=${n.notifId}&url=${finalUrl}" class="list-group-item list-group-item-action notif-item p-4 ${n.read ? 'read' : 'unread bg-white shadow-sm'}">
                            <div class="d-flex justify-content-between mb-1">
                                <h6 class="mb-1 text-primary fw-bold">
                                    <i class="bi ${fn:contains(n.message, '#ORD-') ? 'bi-cart-check' : 'bi-journal-bookmark'} me-2"></i>
                                    Smart Lib System
                                </h6>
                                <small class="text-muted"><fmt:formatDate value="${n.createdDate}" pattern="HH:mm - dd/MM/yyyy"/></small>
                            </div>
                            <p class="mb-0 text-dark ${!n.read ? 'fw-bold' : ''}">${n.message}</p>
                            <small class="text-muted mt-2 d-block">Nhấn để xem chi tiết</small>
                        </a>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <jsp:include page="footer.jsp" />
</body>
</html>