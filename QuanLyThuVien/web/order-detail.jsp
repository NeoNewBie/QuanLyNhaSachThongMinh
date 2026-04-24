<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi Tiết Đơn Hàng - Smart Library</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <style>
        :root { --primary-color: #173F5F; --accent-color: #ED553B; --bg-light: #F8F9FA; }
        body { font-family: 'Inter', sans-serif; background-color: var(--bg-light); }
        .order-header { background: linear-gradient(135deg, var(--primary-color), #2A618A); color: white; border-radius: 15px 15px 0 0; padding: 25px; }
        .info-card { border-radius: 15px; border: none; box-shadow: 0 5px 20px rgba(0,0,0,0.05); }
        .book-img { width: 60px; height: 85px; object-fit: cover; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .badge-status { font-size: 0.9rem; padding: 8px 15px; border-radius: 50px; font-weight: 600; letter-spacing: 0.5px; }
        .table thead th { text-transform: uppercase; font-size: 0.8rem; color: #666; letter-spacing: 0.5px; border-bottom: 2px solid #eee; }
    </style>
</head>
<body>
    <jsp:include page="header.jsp" />

    <div class="container py-5" style="max-width: 950px;">
        <div class="mb-4 d-flex justify-content-between align-items-center">
            <a href="orders" class="text-decoration-none text-muted fw-bold transition-300 hover-primary">
                <i class="bi bi-arrow-left me-2"></i>Quay lại danh sách
            </a>
            <h3 class="fw-bold mb-0" style="color: var(--primary-color);">Chi Tiết Đơn Hàng</h3>
        </div>

        <div class="card info-card mb-4">
            <div class="order-header d-flex justify-content-between align-items-center">
                <div>
                    <h4 class="mb-1 fw-bold">Mã đơn hàng: <span class="text-warning">#ORD-${order.id}</span></h4>
                    <p class="mb-0 opacity-75"><i class="bi bi-calendar-check me-2"></i>Ngày đặt: <fmt:formatDate value="${order.orderDate}" pattern="HH:mm - dd/MM/yyyy"/></p>
                </div>
                <div>
                    <%-- Trạng thái màu mè nổi bật --%>
                    <c:choose>
                        <c:when test="${order.status == '0'}"><span class="badge bg-warning text-dark badge-status shadow-sm"><i class="bi bi-hourglass-split me-1"></i>Đang chờ duyệt</span></c:when>
                        <c:when test="${order.status == '1'}"><span class="badge bg-info text-dark badge-status shadow-sm"><i class="bi bi-wallet2 me-1"></i>Đã thanh toán (QR)</span></c:when>
                        <c:when test="${order.status == '2'}"><span class="badge bg-primary badge-status shadow-sm"><i class="bi bi-box-seam me-1"></i>Đang chuẩn bị sách</span></c:when>
                        <c:when test="${order.status == '3' || order.status == 'Đã giao'}"><span class="badge bg-success badge-status shadow-sm"><i class="bi bi-check-circle me-1"></i>Hoàn thành</span></c:when>
                        <c:otherwise><span class="badge bg-secondary badge-status shadow-sm">${order.status}</span></c:otherwise>
                    </c:choose>
                </div>
            </div>
            
            <div class="card-body p-4">
                <div class="row mb-5 g-4">
                    <div class="col-md-6">
                        <h6 class="text-muted fw-bold text-uppercase mb-3"><i class="bi bi-person-badge me-2" style="color: var(--accent-color);"></i>Thông tin nhận hàng</h6>
                        <div class="p-3 bg-light rounded-3 h-100 border border-light">
                            <p class="mb-2 fw-bold text-dark fs-5">${acc.fullName != null ? acc.fullName : acc.username}</p>
                            <p class="mb-2 text-secondary"><i class="bi bi-telephone-fill text-muted me-2"></i>${acc.phone != null ? acc.phone : 'Chưa cập nhật số ĐT'}</p>
                            <p class="mb-0 text-secondary"><i class="bi bi-geo-alt-fill text-muted me-2"></i>${acc.address != null ? acc.address : 'Nhận sách trực tiếp tại Thư viện UTE'}</p>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <h6 class="text-muted fw-bold text-uppercase mb-3"><i class="bi bi-credit-card-2-front me-2" style="color: var(--accent-color);"></i>Hình thức thanh toán</h6>
                        <div class="p-3 bg-light rounded-3 h-100 border border-light d-flex flex-column justify-content-center">
                            <p class="mb-2 text-secondary fs-6">Phương thức: <strong class="text-dark">Thanh toán ${order.status == '1' ? 'Chuyển khoản (QR Code)' : 'Tiền mặt (COD)'}</strong></p>
                            <p class="mb-0 text-secondary fs-6">Trạng thái: 
                                <strong class="${order.status == '1' || order.status == '3' || order.status == 'Đã giao' ? 'text-success' : 'text-warning'}">
                                    ${order.status == '1' || order.status == '3' || order.status == 'Đã giao' ? 'Đã thanh toán thành công' : 'Chưa thanh toán'}
                                </strong>
                            </p>
                        </div>
                    </div>
                </div>

                <h6 class="text-muted fw-bold text-uppercase mb-3"><i class="bi bi-bag-check-fill me-2" style="color: var(--accent-color);"></i>Sản phẩm đã đặt</h6>
                <div class="table-responsive mb-4">
                    <table class="table align-middle table-borderless">
                        <thead class="table-light">
                            <tr>
                                <th class="ps-3">Tựa sách</th>
                                <th class="text-center">Đơn giá</th>
                                <th class="text-center">Số lượng</th>
                                <th class="text-end pe-3">Thành tiền</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%-- 🛑 ĐÃ FIX: Đổi từ orderDetailList sang details cho khớp với Servlet --%>
                            <c:forEach items="${details}" var="item">
                                <tr class="border-bottom">
                                    <td class="ps-3 py-3">
                                        <div class="d-flex align-items-center gap-3">
                                            <img src="${item.book.coverImage}" class="book-img" onerror="this.src='https://placehold.co/60x85?text=No+Img'">
                                            <div>
                                                <h6 class="fw-bold mb-1" style="color: var(--primary-color);">${item.book.title}</h6>
                                                <small class="text-muted"><i class="bi bi-pen me-1"></i>${item.book.author}</small>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="text-center"><fmt:formatNumber value="${item.price}" pattern="###,###"/> đ</td>
                                    <td class="text-center fw-bold">
                                        <span class="bg-light px-3 py-1 rounded-pill border">x${item.quantity}</span>
                                    </td>
                                    <td class="text-end pe-3 fw-bold text-danger fs-6"><fmt:formatNumber value="${item.price * item.quantity}" pattern="###,###"/> đ</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                
                <div class="d-flex justify-content-end p-4 bg-light rounded-4 border">
                    <div style="width: 300px;">
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted fw-semibold">Tạm tính sản phẩm:</span>
                            <span class="fw-bold"><fmt:formatNumber value="${order.totalPrice}" pattern="###,###"/> đ</span>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted fw-semibold">Phí giao dịch/Giao hàng:</span>
                            <span class="fw-bold">0 đ</span>
                        </div>
                        <div class="d-flex justify-content-between align-items-center mt-3 pt-3 border-top border-2 border-secondary-subtle">
                            <span class="fw-bold text-uppercase text-muted">Tổng cộng:</span>
                            <span class="fs-3 fw-bold" style="color: var(--accent-color);"><fmt:formatNumber value="${order.totalPrice}" pattern="###,###"/> đ</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="footer.jsp" />
</body>
</html>