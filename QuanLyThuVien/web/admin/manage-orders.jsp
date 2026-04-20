<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý Đơn hàng - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <style>
        :root {
            --primary-color: #173F5F;
            --accent-color: #ED553B;
            --bg-light: #F6F6F6;
        }
        body { font-family: 'Inter', sans-serif; background-color: var(--bg-light); }
        .sidebar { background-color: var(--primary-color); min-height: 100vh; position: fixed; width: 250px; }
        .main-content { margin-left: 250px; padding: 3rem; width: calc(100% - 250px); }
        .sidebar a { color: white; opacity: 0.8; transition: 0.3s; display: block; }
        .sidebar a:hover, .sidebar a.active { opacity: 1; background-color: rgba(255,255,255,0.1); border-radius: 8px; }
    </style>
</head>
<body>
    <div class="d-flex">
        <div class="sidebar p-4 text-white">
            <h4 class="fw-bold mb-4 text-center"><i class="bi bi-book-half"></i> SMART LIB</h4>
            <div class="d-flex flex-column gap-2">
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="text-decoration-none p-2">
                    <i class="bi bi-speedometer2 me-2"></i> Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/admin/manage-books" class="text-decoration-none p-2">
                    <i class="bi bi-journal-text me-2"></i> Quản lý Sách
                </a>
                <a href="${pageContext.request.contextPath}/admin/manage-orders" class="text-decoration-none p-2 active">
                    <i class="bi bi-cart-check me-2"></i> Quản lý Đơn hàng
                </a>
                <a href="${pageContext.request.contextPath}/admin/manage-users" class="text-decoration-none p-2">
                    <i class="bi bi-people me-2"></i> Quản lý Người dùng
                </a>
                <hr class="text-white-50">
                <a href="${pageContext.request.contextPath}/home" class="text-decoration-none p-2">
                    <i class="bi bi-house-door me-2"></i> Quay lại Shop
                </a>
            </div>
        </div>

        <div class="main-content">
            <h2 class="fw-bold mb-4" style="color: var(--primary-color);">Quản lý Đơn hàng</h2>
            
            <div class="card shadow-sm border-0">
                <div class="card-body p-0">
                    <table class="table table-hover mb-0 align-middle text-center">
                        <thead class="table-light">
                            <tr>
                                <th>Mã Đơn</th>
                                <th>Mã Khách</th>
                                <th>Ngày Đặt</th>
                                <th>Tổng Tiền</th>
                                <th>Trạng Thái</th>
                                <th>Thao Tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${listOrders}" var="o">
                                <tr>
                                    <td class="fw-bold">#ORD-${o.id}</td>
                                    <td>USER-${o.userId}</td>
                                    <td>${o.orderDate}</td>
                                    <td class="text-danger fw-bold"><fmt:formatNumber value="${o.totalAmount}" type="number" pattern="###,###"/> đ</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${o.status == 'Đã giao'}">
                                                <span class="badge bg-success px-3 py-2 rounded-pill">${o.status}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-warning text-dark px-3 py-2 rounded-pill">Đang xử lý</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:if test="${o.status != 'Đã giao'}">
                                            <a href="${pageContext.request.contextPath}/admin/manage-orders?action=approve&id=${o.id}" class="btn btn-sm btn-outline-success">
                                                <i class="bi bi-check-circle"></i> Duyệt đơn
                                            </a>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</body>
</html>