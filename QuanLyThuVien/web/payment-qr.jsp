<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quét Mã Thanh Toán</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
</head>
<body style="background: #f8f9fa;">
    <jsp:include page="header.jsp" />

    <div class="container mt-5 mb-5">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card border-0 shadow-lg rounded-4 overflow-hidden text-center p-5">
                    <h4 class="fw-bold mb-3 text-primary">THANH TOÁN CHUYỂN KHOẢN</h4>
                    <p class="text-muted">Vui lòng quét mã QR bên dưới để thanh toán cho Đơn hàng Sách Giấy của bạn.</p>
                    
                    <h2 class="text-danger fw-bold my-4"><fmt:formatNumber value="${totalAmount}" pattern="###,###"/> đ</h2>
                    
                    <div class="p-3 bg-white border rounded-4 d-inline-block shadow-sm mx-auto mb-4">
                        <img src="https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=ThanhToanSachGiay-${totalAmount}" alt="QR Code">
                    </div>
                    
                    <div class="alert alert-warning small">
                        <strong>Lưu ý:</strong> Sau khi chuyển khoản thành công, Admin sẽ kiểm tra và xác nhận đơn hàng của bạn trong phần Lịch sử Đơn Hàng.
                    </div>

                    <a href="orders" class="btn btn-success w-100 py-3 rounded-pill fw-bold shadow mt-3">
                        <i class="bi bi-check-circle-fill me-2"></i> TÔI ĐÃ CHUYỂN KHOẢN XONG
                    </a>
                </div>
            </div>
        </div>
    </div>
</body>
</html>