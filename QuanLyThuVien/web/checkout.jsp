<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Thanh Toán - Smart Library</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <style>
        :root { --primary-color: #173F5F; --accent-color: #ED553B; }
        .payment-card { border-radius: 20px; transition: 0.3s; border: 2px solid #eee; cursor: pointer; }
        .payment-card:hover { border-color: var(--accent-color); background: #fffcfb; }
        .payment-check:checked + .payment-card { border-color: var(--accent-color); background: #fffcfb; }
        #qr-area { display: none; }
    </style>
</head>
<body class="bg-light">
    <jsp:include page="header.jsp" />
    <div class="container mt-5 mb-5">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card shadow-sm border-0 rounded-4 p-4">
                    <h3 class="fw-bold mb-4 text-primary"><i class="bi bi-shield-check"></i> XÁC NHẬN THANH TOÁN</h3>
                    
                    <div class="alert alert-secondary border-0 mb-4 rounded-4 p-3" style="background: #e9ecef;">
                        <c:choose>
                           <%-- Trong checkout.jsp --%>
                            <c:when test="${checkoutType == 'single'}">
                                <strong>Sản phẩm:</strong> ${sach_mua.title} 
                                <span class="badge bg-dark ms-2">x ${quantity_mua}</span>
                            </c:when>
                            <c:otherwise>
                                <p class="mb-1"><strong>Sản phẩm:</strong> Thanh toán toàn bộ giỏ hàng</p>
                            </c:otherwise>
                        </c:choose>
                        <h4 class="text-danger fw-bold mt-2 mb-0">Tổng tiền: <fmt:formatNumber value="${totalAmount}" pattern="###,###"/> đ</h4>
                    </div>

                    <form action="process-checkout" method="POST">
                        <input type="hidden" name="checkoutType" value="${checkoutType}">
                        <input type="hidden" name="txt_id_sach" value="${sach_mua.id}">
                        <input type="hidden" name="totalAmount" value="${totalAmount}">
                        
                        <h6 class="fw-bold mb-3 text-uppercase small text-muted">Chọn phương thức thanh toán:</h6>
                        
                        <div class="mb-3 position-relative">
                            <input class="form-check-input d-none payment-check" type="radio" name="txt_phuong_thuc" id="cod" value="COD" checked onclick="toggleQR(false)">
                            <label class="payment-card d-block p-3" for="cod">
                                <div class="d-flex align-items-center">
                                    <i class="bi bi-truck fs-2 text-primary me-3"></i>
                                    <div>
                                        <strong class="d-block">Thanh toán khi nhận hàng (COD)</strong>
                                        <small class="text-muted">Nhận sách tại quầy hoặc ship tận nơi rồi mới trả tiền.</small>
                                    </div>
                                </div>
                            </label>
                        </div>

                        <div class="mb-4 position-relative">
                            <input class="form-check-input d-none payment-check" type="radio" name="txt_phuong_thuc" id="transfer" value="TRANSFER" onclick="toggleQR(true)">
                            <label class="payment-card d-block p-3" for="transfer">
                                <div class="d-flex align-items-center">
                                    <i class="bi bi-qr-code-scan fs-2 text-danger me-3"></i>
                                    <div>
                                        <strong class="d-block">Chuyển khoản Ngân hàng (QR Code)</strong>
                                        <small class="text-muted">Quét mã QR để thanh toán nhanh. Admin sẽ duyệt ngay lập tức.</small>
                                    </div>
                                </div>
                            </label>
                        </div>

                        <div id="qr-area" class="text-center p-4 rounded-4 mb-4" style="background: #fff; border: 2px dashed #ED553B;">
                            <h6 class="fw-bold text-danger mb-3">QUÉT MÃ ĐỂ THANH TOÁN</h6>
                            <%-- Sếp thay MB bằng ngân hàng của sếp, thay số tài khoản của sếp vào đây nhé --%>
                            <img src="https://img.vietqr.io/image/MB-012345678910-compact2.png?amount=${totalAmount}&addTag=SmartLib&memo=Hải_ThanhToan_DonHang" 
                                 class="img-fluid shadow-sm rounded-3" style="max-width: 280px;">
                            <p class="mt-3 mb-0 small text-muted">Nội dung chuyển khoản: <strong>Hải Thanh Toán Đơn Hàng</strong></p>
                        </div>

                        <button type="submit" class="btn btn-danger w-100 py-3 fw-bold rounded-pill shadow-lg" style="background: var(--accent-color);">
                            <i class="bi bi-cart-check-fill me-2"></i> XÁC NHẬN ĐẶT HÀNG
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script>
        function toggleQR(show) {
            const qrArea = document.getElementById('qr-area');
            qrArea.style.display = show ? 'block' : 'none';
        }
    </script>
</body>
</html>