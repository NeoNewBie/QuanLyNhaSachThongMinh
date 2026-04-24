<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thanh Toán - Smart Library</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <style>
        :root { --primary-color: #173F5F; --accent-color: #ED553B; }
        body { font-family: 'Inter', sans-serif; background-color: #f0f2f5; color: #333; }
        
        .checkout-container { max-width: 1050px; margin: 50px auto; }
        .card-custom { background: #fff; border-radius: 20px; border: none; box-shadow: 0 10px 30px rgba(0,0,0,0.05); overflow: hidden; }
        
        .card-header-custom { background: linear-gradient(135deg, var(--primary-color) 0%, #2A608B 100%); color: white; padding: 20px 30px; }
        
        /* Box hiển thị sản phẩm gọn gàng, bo góc */
        .item-box { background: #f8f9fa; border: 1px solid #eaeaea; border-radius: 16px; padding: 20px; transition: 0.3s; }
        .item-box:hover { box-shadow: 0 5px 15px rgba(0,0,0,0.05); }
        .item-img { width: 70px; height: 100px; object-fit: cover; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }
        
        /* Tùy chỉnh nút Radio chọn phương thức */
        .method-card { border: 2px solid #e0e0e0; border-radius: 16px; padding: 20px; cursor: pointer; transition: all 0.2s ease; background: #fff; }
        .method-card:hover { border-color: #fcd5ce; background: #fffcfb; }
        .pay-check:checked + .method-card { border-color: var(--accent-color); background: #fff5f3; box-shadow: 0 5px 15px rgba(237, 85, 59, 0.1); }
        
        .btn-confirm { background: var(--accent-color); color: white; border-radius: 50px; padding: 15px; font-weight: 700; border: none; transition: 0.3s; letter-spacing: 1px; }
        .btn-confirm:hover { background: #d4442d; transform: translateY(-2px); box-shadow: 0 10px 20px rgba(237, 85, 59, 0.2); }
        
        /* Cột bên phải tính tiền */
        .summary-box { background: #173F5F; color: white; border-radius: 20px; padding: 40px 30px; height: 100%; display: flex; flex-direction: column; justify-content: center; }
        
        /* Modal QR Code */
        .qr-modal .modal-content { border: none; border-radius: 24px; box-shadow: 0 20px 50px rgba(0,0,0,0.2); }
        .qr-frame { background: white; padding: 15px; border-radius: 20px; border: 2px dashed #ccc; display: inline-block; margin-bottom: 15px; }
    </style>
</head>
<body>
    <jsp:include page="header.jsp" />

    <div class="container checkout-container">
        <c:if test="${empty sach_mua}">
            <div class="text-center py-5">
                <img src="https://cdn-icons-png.flaticon.com/512/2748/2748614.png" width="120" class="opacity-50 mb-3">
                <h4 class="text-muted fw-bold">Không tìm thấy thông tin đơn hàng!</h4>
                <a href="shop" class="btn btn-primary rounded-pill mt-3 px-4">Quay lại Cửa hàng</a>
            </div>
        </c:if>

        <c:if test="${not empty sach_mua}">
            <div class="row g-4">
                <div class="col-lg-7">
                    <div class="card-custom">
                        <div class="card-header-custom d-flex align-items-center">
                            <c:choose>
                                <%-- 🛑 ĐÂY LÀ CHỖ CHỐNG LỖI 500: isEbook == 1 --%>
                                <c:when test="${sach_mua.isEbook == 1}">
                                    <i class="bi bi-gem fs-3 text-warning me-3"></i>
                                    <h4 class="fw-bold mb-0">MỞ KHÓA TRUYỆN VIP</h4>
                                </c:when>
                                <c:otherwise>
                                    <i class="bi bi-cart-check fs-3 text-white me-3"></i>
                                    <h4 class="fw-bold mb-0">XÁC NHẬN ĐƠN HÀNG</h4>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        
                        <div class="p-4 p-md-5">
                            <div class="item-box d-flex align-items-center mb-5">
                                <img src="${sach_mua.coverImage}" class="item-img me-4" onerror="this.src='https://cdn-icons-png.flaticon.com/512/2232/2232688.png'">
                                <div class="flex-grow-1">
                                    <h5 class="fw-bold text-dark mb-1">${sach_mua.title}</h5>
                                    <p class="text-muted small mb-3"><i class="bi bi-person-fill me-1"></i> ${sach_mua.author}</p>
                                    <div class="d-flex justify-content-between align-items-center border-top pt-3 mt-2">
                                        <span class="badge bg-secondary rounded-pill px-3 py-2 fw-normal">Số lượng: ${quantity_mua}</span>
                                        <span class="fw-bold text-danger fs-5"><fmt:formatNumber value="${sach_mua.price}" pattern="###,###"/> đ</span>
                                    </div>
                                </div>
                            </div>

                            <form action="process-checkout" method="POST" id="mainCheckoutForm">
                                <input type="hidden" name="checkoutType" value="${checkoutType}">
                                <input type="hidden" name="txt_id_sach" value="${sach_mua.id}">
                                <input type="hidden" name="quantity_mua" value="${quantity_mua}">
                                <input type="hidden" name="totalAmount" value="${totalAmount}">

                                <h6 class="fw-bold text-muted text-uppercase mb-3 small"><i class="bi bi-wallet2 me-2"></i>Chọn phương thức thanh toán</h6>
                                
                                <div class="mb-3">
                                    <input type="radio" class="pay-check d-none" name="txt_phuong_thuc" id="cod" value="COD" checked>
                                    <label class="method-card d-block" for="cod">
                                        <div class="d-flex align-items-center">
                                            <div class="bg-light rounded-circle p-3 me-4"><i class="bi bi-cash-coin fs-4 text-primary"></i></div>
                                            <div>
                                                <strong class="d-block fs-6">Thanh toán tiền mặt (COD)</strong>
                                                <small class="text-muted">Thanh toán trực tiếp khi nhận sách tại quầy.</small>
                                            </div>
                                        </div>
                                    </label>
                                </div>

                                <div class="mb-4">
                                    <input type="radio" class="pay-check d-none" name="txt_phuong_thuc" id="bank" value="TRANSFER">
                                    <label class="method-card d-block" for="bank">
                                        <div class="d-flex align-items-center">
                                            <div class="bg-light rounded-circle p-3 me-4"><i class="bi bi-qr-code-scan fs-4 text-danger"></i></div>
                                            <div>
                                                <strong class="d-block fs-6">Chuyển khoản Ngân hàng (QR)</strong>
                                                <small class="text-muted">Xác nhận tự động trong 3 giây qua PayOS.</small>
                                            </div>
                                        </div>
                                    </label>
                                </div>

                                <button type="submit" class="btn-confirm w-100 shadow-sm mt-2">
                                    <i class="bi bi-check2-circle me-2"></i> HOÀN TẤT ĐẶT HÀNG
                                </button>
                            </form>
                        </div>
                    </div>
                </div>

                <div class="col-lg-5">
                    <div class="summary-box shadow-lg text-center">
                        <i class="bi bi-receipt text-white opacity-50 mb-3" style="font-size: 3rem;"></i>
                        <p class="text-uppercase mb-2 opacity-75 small fw-bold">Tổng thanh toán</p>
                        <h1 class="fw-bold text-warning display-5 mb-4">
                            <fmt:formatNumber value="${totalAmount}" pattern="###,###"/> đ
                        </h1>
                        <hr class="border-light opacity-25 w-75 mx-auto mb-4">
                        <div class="text-start bg-white bg-opacity-10 p-4 rounded-4">
                            <p class="small mb-2 text-warning"><i class="bi bi-shield-check me-2"></i>Giao dịch an toàn 100%</p>
                            <p class="small mb-2 opacity-75"><i class="bi bi-check-circle me-2"></i>Đơn hàng được duyệt tự động.</p>
                            <p class="small mb-0 opacity-75"><i class="bi bi-check-circle me-2"></i>Hỗ trợ hủy đơn nếu chưa nhận sách.</p>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>
    </div>

    <div class="modal fade qr-modal" id="payOSModal" tabindex="-1" data-bs-backdrop="static">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-body text-center p-5">
                    <h4 class="fw-bold text-primary mb-4">QUÉT MÃ THANH TOÁN</h4>
                    <div class="qr-frame shadow-sm">
                        <img id="qr-img" src="" style="width: 230px; height: 230px;">
                    </div>
                    <div class="mt-3">
                        <span class="badge bg-danger bg-opacity-10 text-danger border border-danger px-3 py-2 rounded-pill">
                            <span class="spinner-grow spinner-grow-sm me-2" role="status" aria-hidden="true"></span>
                            Đang chờ nhận tiền...
                        </span>
                    </div>
                    <p class="mt-4 small text-muted">Mã giao dịch: <b id="qr-order-id" class="text-dark"></b></p>
                    <button type="button" class="btn btn-sm btn-outline-secondary mt-2 rounded-pill px-4" data-bs-dismiss="modal" onclick="window.location.reload()">Hủy giao dịch</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script>
        document.getElementById('mainCheckoutForm').onsubmit = function(e) {
            const method = document.querySelector('input[name="txt_phuong_thuc"]:checked').value;
            if (method === 'TRANSFER') {
                e.preventDefault(); 
                triggerQRModal(); 
            }
        };

        function triggerQRModal() {
            const amount = "${totalAmount}";
            
            // 🛑 ĐÃ FIX: Sinh mã giao dịch chuẩn để gửi đi
            let orderId = "";
            <c:choose>
                <c:when test="${sach_mua.isEbook == 1}">
                    orderId = "MUACHAP U${sessionScope.acc.id} C${sach_mua.id}";
                </c:when>
                <c:otherwise>
                    orderId = "BILL_" + Math.floor(Math.random() * 1000000);
                </c:otherwise>
            </c:choose>

            const myModal = new bootstrap.Modal(document.getElementById('payOSModal'));
            
            document.getElementById('qr-order-id').innerText = orderId;
            document.getElementById('qr-img').src = "https://i.gifer.com/ZKZg.gif"; 
            myModal.show();

            fetch('generate-qr?amount=' + amount + '&desc=' + encodeURIComponent(orderId))
                .then(res => res.text())
                .then(qrData => {
                    document.getElementById('qr-img').src = "https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=" + encodeURIComponent(qrData);
                    startPaymentCheck(orderId);
                });
        }

        function startPaymentCheck(orderId) {
            const timer = setInterval(() => {
                fetch('check-payment-status?orderId=' + encodeURIComponent(orderId))
                    .then(res => res.text())
                    .then(status => {
                        if (status.trim() === "success") {
                            clearInterval(timer);
                            Swal.fire({
                                icon: 'success', title: 'Ting Ting!', text: 'Giao dịch thành công!', showConfirmButton: false, timer: 2000
                            }).then(() => {
                                document.getElementById('mainCheckoutForm').submit();
                            });
                        }
                    });
            }, 3000);
        }
    </script>
</body>
</html>