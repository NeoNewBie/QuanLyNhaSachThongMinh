<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- 🛑 BẮT BUỘC PHẢI CÓ DÒNG NÀY ĐỂ KHÔNG BỊ TRẮNG TRANG --%>
<%@ page import="java.util.Date" %>

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
    <%-- 🛑 Chỉ để duy nhất 1 thẻ gọi Bootstrap ở đây --%>

    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
    <jsp:include page="header.jsp" />

    <div class="container py-5" style="max-width: 850px;">
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
                            <c:when test="${borrowDetail.status == '0' || borrowDetail.status == 'Pending'}"><span class="badge bg-warning text-dark badge-status shadow-sm"><i class="bi bi-hourglass-split me-1"></i>Chờ duyệt</span></c:when>
                            <c:when test="${borrowDetail.status == '2'}"><span class="badge bg-info text-dark badge-status shadow-sm"><i class="bi bi-geo-alt-fill me-1"></i>Sẵn sàng tại quầy</span></c:when>
                            <c:when test="${borrowDetail.status == '3' || borrowDetail.status == 'Đang mượn'}"><span class="badge bg-primary badge-status shadow-sm"><i class="bi bi-book-half me-1"></i>Đang mượn</span></c:when>
                            <c:when test="${borrowDetail.status == '4' || not empty borrowDetail.actualReturnDate}"><span class="badge bg-success badge-status shadow-sm"><i class="bi bi-check-circle me-1"></i>Đã trả sách</span></c:when>
                            <c:when test="${borrowDetail.status == '5' || borrowDetail.status == 'Đã hủy'}"><span class="badge bg-danger badge-status shadow-sm"><i class="bi bi-x-circle me-1"></i>Đã hủy</span></c:when>
                            <c:otherwise><span class="badge bg-secondary badge-status shadow-sm">${borrowDetail.status}</span></c:otherwise>
                        </c:choose>
                    </div>
                </div>
                
                <div class="card-body p-4 p-md-5">
                    <h6 class="text-muted fw-bold text-uppercase mb-3"><i class="bi bi-journal-bookmark-fill me-2" style="color: var(--accent-color);"></i>Thông tin Sách</h6>
                    
                    <div class="d-flex flex-column flex-md-row gap-4 align-items-md-center bg-white p-4 border rounded-4 shadow-sm mb-4">
                        <img src="${borrowDetail.bookImage}" class="book-img" onerror="this.src='https://placehold.co/80x110?text=No+Img'">
                        <div class="flex-grow-1">
                            <h4 class="fw-bold mb-2" style="color: var(--primary-color);">${borrowDetail.bookTitle}</h4>
                            <p class="mb-2 text-muted"><i class="bi bi-calendar-event me-2"></i>Ngày hết hạn: <strong class="text-dark"><fmt:formatDate value="${borrowDetail.returnDate}" pattern="dd/MM/yyyy"/></strong></p>
                            
                            <c:if test="${borrowDetail.status != '0' && borrowDetail.status != 'Pending' && borrowDetail.status != '5' && borrowDetail.status != 'Đã hủy'}">
                                <%
                                    model.Borrow b = (model.Borrow) request.getAttribute("borrowDetail");
                                    if(b != null && b.getReturnDate() != null) {
                                        long now = new java.util.Date().getTime();
                                        long due = b.getReturnDate().getTime();
                                        long diff = due - now;
                                        long daysLeft = diff / (1000 * 60 * 60 * 24);
                                        request.setAttribute("daysLeftObj", daysLeft);
                                    }
                                %>
                                <c:choose>
                                    <c:when test="${not empty borrowDetail.actualReturnDate}">
                                        <p class="mb-0 text-success fw-bold"><i class="bi bi-check-all me-2"></i>Trả thực tế: <fmt:formatDate value="${borrowDetail.actualReturnDate}" pattern="dd/MM/yyyy"/></p>
                                    </c:when>
                                    <c:when test="${daysLeftObj < 0}">
                                        <p class="mb-0 text-danger fw-bold"><i class="bi bi-alarm-fill me-2"></i>Đã quá hạn ${-daysLeftObj} ngày 💀</p>
                                    </c:when>
                                    <c:otherwise>
                                        <p class="mb-0 text-primary fw-bold"><i class="bi bi-clock me-2"></i>Còn lại ${daysLeftObj} ngày để đọc</p>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                        </div>
                    </div>

                    <div class="d-flex flex-wrap justify-content-between p-4 bg-light rounded-4 border">
                        <div class="pe-md-4 mb-4 mb-md-0" style="flex: 1; min-width: 280px;">
                            <h6 class="fw-bold text-muted mb-2"><i class="bi bi-shield-check me-2"></i>Quy định thư viện</h6>
                            <p class="small text-secondary mb-1">- Phí trả sách trễ hạn: <strong class="text-danger">5,000đ/ngày</strong>.</p>
                            <p class="small text-secondary mb-1">- Nếu làm mất sách, người mượn phải đền 100% giá bìa.</p>
                            <p class="small text-secondary mb-0">- Mỗi cuốn sách được phép <strong>gia hạn 1 lần (7 ngày)</strong>.</p>
                        </div>

                        <div class="ps-md-4 d-flex flex-column justify-content-center align-items-end" style="width: 280px; border-left: 2px dashed #dee2e6;">
                            <c:if test="${empty borrowDetail.actualReturnDate && (borrowDetail.status == '3' || borrowDetail.status == 'Đang mượn') && daysLeftObj >= 0}">
                                <button onclick="confirmExtend(${borrowDetail.id})" class="btn btn-outline-primary rounded-pill fw-bold px-4 py-2 mb-2 w-100 shadow-sm">
                                    <i class="bi bi-calendar-plus me-2"></i>XIN GIA HẠN THÊM
                                </button>
                            </c:if>
                            
                            <c:if test="${borrowDetail.status == '0' || borrowDetail.status == 'Pending'}">
                                <button onclick="confirmCancelBorrow(${borrowDetail.id})" class="btn btn-outline-danger rounded-pill fw-bold px-4 py-2 w-100 shadow-sm">
                                    <i class="bi bi-trash3-fill me-2"></i>HỦY PHIẾU MƯỢN
                                </button>
                            </c:if>
                        </div>
                    </div>

                </div>
            </div>
        </c:if>
    </div>

    <jsp:include page="footer.jsp" />

    <script>
        function confirmExtend(borrowId) {
            Swal.fire({
                title: 'Xin gia hạn thêm 7 ngày?',
                text: "Hệ thống sẽ tự động cộng 7 ngày vào hạn trả sách của sếp!",
                icon: 'question',
                showCancelButton: true,
                confirmButtonColor: '#173F5F',
                cancelButtonColor: '#6c757d',
                confirmButtonText: 'Đồng ý gia hạn'
            }).then((result) => {
                if (result.isConfirmed) window.location.href = 'extend-borrow?id=' + borrowId;
            })
        }

        function confirmCancelBorrow(borrowId) {
            Swal.fire({
                title: 'Hủy phiếu mượn này?',
                text: "Sếp đổi ý không mượn nữa đúng không?",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#ED553B',
                cancelButtonColor: '#6c757d',
                confirmButtonText: 'Hủy phiếu mượn'
            }).then((result) => {
                if (result.isConfirmed) window.location.href = 'cancel-borrow?id=' + borrowId;
            })
        }
    </script>
</body>
</html>