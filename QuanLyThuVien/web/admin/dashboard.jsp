<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard - Smart Library</title>
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
        .stat-card { border-left: 5px solid var(--accent-color); border-radius: 12px; transition: transform 0.3s; }
        .stat-card:hover { transform: translateY(-5px); }
    </style>
</head>
<body>
    <div class="d-flex">
        <div class="sidebar p-4 text-white">
            <h4 class="fw-bold mb-4 text-center"><i class="bi bi-book-half"></i> SMART LIB</h4>
            <div class="d-flex flex-column gap-2">
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="text-decoration-none p-2 active">
                    <i class="bi bi-speedometer2 me-2"></i> Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/admin/manage-books" class="text-decoration-none p-2">
                    <i class="bi bi-journal-text me-2"></i> Quản lý Sách
                </a>
                <a href="${pageContext.request.contextPath}/admin/manage-orders" class="text-decoration-none p-2">
                    <i class="bi bi-cart-check me-2"></i> Quản lý Đơn hàng
                </a>
                <a href="${pageContext.request.contextPath}/admin/manage-users" class="text-decoration-none p-2">
                    <i class="bi bi-people me-2"></i> Quản lý Người dùng
                </a>
                <hr class="text-white-50">
                <a href="${pageContext.request.contextPath}/home" class="text-decoration-none p-2">
                    <i class="bi bi-house-door me-2"></i> Quay lại Shop
                </a>
                <a href="${pageContext.request.contextPath}/logout" class="text-decoration-none p-2 text-danger fw-bold">
                    <i class="bi bi-box-arrow-left me-2"></i> Đăng xuất
                </a>
            </div>
        </div>

        <div class="main-content">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="fw-bold" style="color: var(--primary-color);">Tổng quan Hệ thống</h2>
                    <p class="text-muted">Dữ liệu được cập nhật từ Database thời gian thực</p>
                </div>
                <div class="text-end">
                    <span class="badge bg-light text-dark border p-2">
                        <i class="bi bi-calendar3 me-2"></i> Hôm nay: <%= new java.util.Date() %>
                    </span>
                </div>
            </div>

            <div class="row g-4 mb-5">
                <div class="col-md-4">
                    <div class="card shadow-sm border-0 stat-card h-100">
                        <div class="card-body">
                            <h6 class="text-muted text-uppercase fw-bold" style="font-size: 0.8rem;">Tổng doanh thu</h6>
                            <h2 class="fw-bold mb-0 text-primary-custom">
                                <fmt:formatNumber value="${totalRevenue}" type="number" pattern="###,###"/>
                                <small class="fs-6 text-muted">VNĐ</small>
                            </h2>
                        </div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="card shadow-sm border-0 stat-card h-100" style="border-left-color: #28a745;">
                        <div class="card-body">
                            <h6 class="text-muted text-uppercase fw-bold" style="font-size: 0.8rem;">Tổng đơn hàng</h6>
                            <h2 class="fw-bold mb-0">${totalOrders}</h2>
                            <p class="text-muted mb-0 small">Đơn đã hoàn tất & đang chờ</p>
                        </div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="card shadow-sm border-0 stat-card h-100" style="border-left-color: #17a2b8;">
                        <div class="card-body">
                            <h6 class="text-muted text-uppercase fw-bold" style="font-size: 0.8rem;">Sách trong kho</h6>
                            <h2 class="fw-bold mb-0">${totalBooks}</h2>
                            <p class="text-muted mb-0 small">Đầu sách đang kinh doanh</p>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="card border-0 shadow-sm rounded-4 overflow-hidden">
                <div class="card-header bg-white py-3">
                    <h5 class="mb-0 fw-bold"><i class="bi bi-lightning-charge me-2 text-warning"></i>Thao tác nhanh</h5>
                </div>
                <div class="card-body p-4">
                    <div class="row text-center">
                        <div class="col-md-3">
                            <a href="${pageContext.request.contextPath}/admin/manage-books?action=add" class="btn btn-outline-primary w-100 py-3 rounded-3">
                                <i class="bi bi-plus-circle fs-3 d-block mb-2"></i> Thêm sách mới
                            </a>
                        </div>
                        <div class="col-md-3">
                            <a href="${pageContext.request.contextPath}/admin/manage-orders" class="btn btn-outline-success w-100 py-3 rounded-3">
                                <i class="bi bi-check2-all fs-3 d-block mb-2"></i> Duyệt đơn hàng
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>