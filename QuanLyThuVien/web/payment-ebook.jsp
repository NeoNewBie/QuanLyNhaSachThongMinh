<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thanh toán Truyện - Smart Library</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
</head>
<body style="background: #f8f9fa;">
    <jsp:include page="header.jsp" />

    <div class="container mt-5 mb-5">
        <div class="row justify-content-center">
            <div class="col-md-8 col-lg-6">
                <div class="card border-0 shadow-lg rounded-4 overflow-hidden">
                    <div class="bg-primary text-white text-center py-4">
                        <h4 class="fw-bold mb-0">THANH TOÁN MỞ KHÓA TRUYỆN</h4>
                    </div>
                    <div class="card-body p-5">
                        <div class="d-flex align-items-center mb-4 border-bottom pb-4">
                            <img src="${detail.coverImage.startsWith('http') ? detail.coverImage : pageContext.request.contextPath.concat('/').concat(detail.coverImage)}" style="width: 80px; height: 110px; object-fit: cover; border-radius: 8px;" class="shadow-sm me-4">
                            <div>
                                <h5 class="fw-bold text-dark mb-1">${detail.title}</h5>
                                <p class="text-muted mb-2">${detail.author}</p>
                                <h4 class="fw-bold text-danger mb-0"><fmt:formatNumber value="${detail.price}" pattern="###,###"/> đ</h4>
                            </div>
                        </div>

                        <div class="text-center mb-4">
                            <p class="fw-bold text-muted mb-3">QUÉT MÃ QR QUA MOMO HOẶC NGÂN HÀNG</p>
                            <div class="p-3 bg-white border rounded-4 d-inline-block shadow-sm">
                                <img src="https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=ChuyenKhoan-${detail.id}-${detail.price}" alt="QR Code">
                            </div>
                            <p class="mt-3 small text-muted">Hệ thống sẽ tự động duyệt và mở khóa sách ngay sau khi nhận được thanh toán.</p>
                        </div>

                        <form action="payment-ebook" method="POST">
                            <input type="hidden" name="txt_id_sach" value="${detail.id}">
                            <input type="hidden" name="txt_gia_tien" value="${detail.price}">
                            <button type="submit" class="btn btn-success w-100 py-3 rounded-pill fw-bold shadow">
                                <i class="bi bi-check-circle-fill me-2"></i> TÔI ĐÃ CHUYỂN KHOẢN THÀNH CÔNG
                            </button>
                        </form>
                        
                        <div class="text-center mt-3">
                            <a href="book-detail?id=${detail.id}" class="text-decoration-none text-muted fw-bold">HỦY THANH TOÁN</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="footer.jsp" />
</body>
</html>