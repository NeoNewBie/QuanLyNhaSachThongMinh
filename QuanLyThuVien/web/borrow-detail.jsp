<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi Tiết Phiếu Mượn - Smart Library</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <style>
        :root { --primary-color: #173F5F; --accent-color: #ED553B; --bg-light: #F8F9FA; }
        body { font-family: 'Inter', sans-serif; background-color: var(--bg-light); }
        .order-header { background: linear-gradient(135deg, #2A618A, var(--primary-color)); color: white; border-radius: 15px 15px 0 0; padding: 25px; }
        .info-card { border-radius: 15px; border: none; box-shadow: 0 5px 20px rgba(0,0,0,0.05); }
        .book-img { width: 80px; height: 110px; object-fit: cover; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
        .badge-status { font-size: 0.9rem; padding: 8px 15px; border-radius: 50px; font-weight: 600; letter-spacing: 0.5px; }
    </style>
</head>
<body>
    <jsp:include page="header.jsp" />

    <div class="container py-5" style="max-width: 800px;">
        <div class="mb-4 d-flex justify-content-between align-items-center">
            <a href="borrow-history" class="text-decoration-none text-muted fw-bold hover-primary">
                <i class="bi bi-arrow-left me-2"></i>Quay lại lịch sử
            </a>
        </div>

        <c:if test="${not empty borrowDetail}">
            <div class="card info-card mb-4">
                <div class="order-header d-flex justify-content-between align-items-center">
                    <div>
                        <h4 class="mb-1 fw-bold">Mã Phiếu Mượn: <span class="text-warning">#LIB-${borrowDetail.id}</span></h4>
                        <p class="mb-0 opacity-75"><i class="bi bi-calendar-check me-2"></i>Đăng ký lúc: <fmt:formatDate value="${borrowDetail.borrowDate}" pattern="dd/MM/yyyy"/></p>
                    </div>
                    <div>
                        <c:choose>
                            <c:when test="${borrowDetail.status == '0' || borrowDetail.status == 'Pending'}"><span class="badge bg-warning text-dark badge-status shadow-sm"><i class="bi bi-hourglass-split me-1"></i>Chờ thủ thư duyệt</span></c:when>
                            <c:when test="${borrowDetail.status == '2'}"><span class="badge bg-info text-dark badge-status shadow-sm"><i class="bi bi-geo-alt-fill me-1"></i>Sẵn sàng tại quầy</span></c:when>
                            <c:when test="${borrowDetail.status == '3'}"><span class="badge bg-primary badge-status shadow-sm"><i class="bi bi-book me-1"></i>Đang mượn</span></c:when>
                            <c:when test="${borrowDetail.status == '4'}"><span class="badge bg-success badge-status shadow-sm"><i class="bi bi-check-circle me-1"></i>Đã trả sách</span></c:when>
                            <c:otherwise><span class="badge bg-secondary badge-status shadow-sm">${borrowDetail.status}</span></c:otherwise>
                        </c:choose>
                    </div>
                </div>
                
                <div class="card-body p-4 p-md-5">
                    <h6 class="text-muted fw-bold text-uppercase mb-4"><i class="bi bi-journal-bookmark-fill me-2" style="color: var(--accent-color);"></i>Thông tin Sách</h6>
                    <div class="d-flex align-items-center gap-4 bg-light p-3 rounded-4 border">
                        <img src="${borrowDetail.bookImage}" class="book-img" onerror="this.src='https://placehold.co/80x110?text=No+Img'">
                        <div>
                            <h4 class="fw-bold mb-2" style="color: var(--primary-color);">${borrowDetail.bookTitle}</h4>
                            <p class="mb-1 text-muted"><i class="bi bi-calendar-event me-2"></i>Ngày hết hạn: <strong class="text-dark"><fmt:formatDate value="${borrowDetail.returnDate}" pattern="dd/MM/yyyy"/></strong></p>
                            <c:if test="${borrowDetail.status == '4'}">
                                <p class="mb-0 text-success"><i class="bi bi-check-all me-2"></i>Trả thực tế: <strong><fmt:formatDate value="${borrowDetail.actualReturnDate}" pattern="dd/MM/yyyy"/></strong></p>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>
    </div>

    <jsp:include page="footer.jsp" />
</body>
</html>