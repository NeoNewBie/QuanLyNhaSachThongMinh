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
        
        .item-box { background: #f8f9fa; border: 1px solid #eaeaea; border-radius: 16px; padding: 20px; transition: 0.3s; }
        .item-img { width: 70px; height: 100px; object-fit: cover; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }
        
        .method-card { border: 2px solid #e0e0e0; border-radius: 16px; padding: 20px; cursor: pointer; transition: all 0.2s ease; background: #fff; }
        .method-card:hover { border-color: #fcd5ce; background: #fffcfb; }
        .pay-check:checked + .method-card { border-color: var(--accent-color); background: #fff5f3; box-shadow: 0 5px 15px rgba(237, 85, 59, 0.1); }
        
        .btn-confirm { background: var(--accent-color); color: white; border-radius: 50px; padding: 15px; font-weight: 700; border: none; transition: 0.3s; letter-spacing: 1px; }
        
        .summary-box { background: #173F5F; color: white; border-radius: 20px; padding: 40px 30px; height: 100%; display: flex; flex-direction: column; justify-content: center; }
        
        /* ĐỊA CHỈ & VOUCHER STYLE SHOPEE */
        .address-box { border-top: 4px solid var(--accent-color); background: #fff; border-radius: 12px; padding: 20px; margin-bottom: 20px; box-shadow: 0 5px 15px rgba(0,0,0,0.02); }
        .voucher-box { border: 1px dashed #ED553B; background: #FFF5F3; border-radius: 12px; padding: 15px 20px; cursor: pointer; transition: 0.3s; }
        .ticket-item { display: flex; border: 1px solid #eee; border-radius: 12px; margin-bottom: 15px; overflow: hidden; }
        .ticket-left { background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%); width: 100px; display: flex; flex-direction: column; align-items: center; justify-content: center; color: #d63384; border-right: 2px dashed #fff; }
        .ticket-right { padding: 15px; flex-grow: 1; background: #fff; display: flex; justify-content: space-between; align-items: center; }
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
                    
                    <%-- KHỐI ĐỊA CHỈ NHẬN HÀNG (Bản đồ UI Shopee) --%>
                    <div class="address-box position-relative">
                        <h6 class="fw-bold text-danger mb-3"><i class="bi bi-geo-alt-fill me-2"></i>Địa Chỉ Nhận Hàng</h6>
                        <div class="d-flex justify-content-between align-items-end">
                            <div>
                                <strong class="text-dark fs-6" id="ui-name-phone">${acc.fullName} | ${acc.phone != null ? acc.phone : 'Chưa cập nhật SĐT'}</strong>
                                <p class="text-muted mb-0 mt-1" id="ui-address">${acc.address != null ? acc.address : 'Chưa có địa chỉ. Vui lòng cập nhật!'}</p>
                            </div>
                            <button class="btn text-primary fw-bold text-decoration-none" data-bs-toggle="modal" data-bs-target="#addressModal">THAY ĐỔI</button>
                        </div>
                    </div>

                    <div class="card-custom">
                        <div class="card-header-custom d-flex align-items-center">
                            <c:choose>
                                <c:when test="${sach_mua.isEbook == 1}">
                                    <i class="bi bi-gem fs-3 text-warning me-3"></i><h4 class="fw-bold mb-0">MỞ KHÓA TRUYỆN VIP</h4>
                                </c:when>
                                <c:otherwise>
                                    <i class="bi bi-cart-check fs-3 text-white me-3"></i><h4 class="fw-bold mb-0">XÁC NHẬN ĐƠN HÀNG</h4>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        
                        <div class="p-4 p-md-5">
                            <div class="item-box d-flex align-items-center mb-4">
                                <img src="${sach_mua.coverImage}" class="item-img me-4" onerror="this.src='https://cdn-icons-png.flaticon.com/512/2232/2232688.png'">
                                <div class="flex-grow-1">
                                    <h5 class="fw-bold text-dark mb-1">${sach_mua.title}</h5>
                                    <p class="text-muted small mb-3"><i class="bi bi-person-fill me-1"></i> ${sach_mua.author}</p>
                                    <div class="d-flex justify-content-between align-items-center border-top pt-3 mt-2">
                                        <span class="badge bg-secondary rounded-pill px-3 py-2 fw-normal">Số lượng: ${quantity_mua}</span>
                                        <%-- 🛑 ĐÃ FIX: Nhân giá tiền với số lượng để hiển thị đúng thực tế --%>
                                        <span class="fw-bold text-danger fs-5"><fmt:formatNumber value="${sach_mua.price * quantity_mua}" pattern="###,###"/> đ</span>
                                    </div>
                                </div>
                            </div>

                            <%-- VÍ VOUCHER --%>
                            <div class="voucher-box d-flex justify-content-between align-items-center mb-4" data-bs-toggle="modal" data-bs-target="#voucherModal">
                                <div><i class="bi bi-ticket-perforated-fill fs-4 me-2 text-danger"></i><span class="fw-bold text-danger">SmartLib Voucher</span></div>
                                <div class="d-flex align-items-center">
                                    <span id="selectedVoucherText" class="text-muted me-2 small">Chọn hoặc nhập mã</span><i class="bi bi-chevron-right text-muted"></i>
                                </div>
                            </div>

                            <form action="process-checkout" method="POST" id="mainCheckoutForm">
                                <input type="hidden" name="checkoutType" value="${checkoutType}">
                                <input type="hidden" name="txt_id_sach" value="${sach_mua.id}">
                                <input type="hidden" name="quantity_mua" value="${quantity_mua}">
                                <input type="hidden" name="totalAmount" value="${totalAmount}">
                                <input type="hidden" name="voucherCode" id="hiddenVoucherCode" value="">
                                
                                <%-- FORM ẨN ĐỊA CHỈ --%>
                                <input type="hidden" name="shipName" id="hiddenShipName" value="${acc.fullName}">
                                <input type="hidden" name="shipPhone" id="hiddenShipPhone" value="${acc.phone}">
                                <input type="hidden" name="shipAddress" id="hiddenShipAddress" value="${acc.address}">

                                <h6 class="fw-bold text-muted text-uppercase mb-3 small"><i class="bi bi-wallet2 me-2"></i>Chọn phương thức thanh toán</h6>
                                
                                <div class="mb-3">
                                    <input type="radio" class="pay-check d-none" name="txt_phuong_thuc" id="cod" value="COD" checked>
                                    <label class="method-card d-block" for="cod">
                                        <div class="d-flex align-items-center">
                                            <div class="bg-light rounded-circle p-3 me-4"><i class="bi bi-cash-coin fs-4 text-primary"></i></div>
                                            <div><strong class="d-block fs-6">Thanh toán tiền mặt (COD)</strong></div>
                                        </div>
                                    </label>
                                </div>

                                <div class="mb-4">
                                    <input type="radio" class="pay-check d-none" name="txt_phuong_thuc" id="bank" value="TRANSFER">
                                    <label class="method-card d-block" for="bank">
                                        <div class="d-flex align-items-center">
                                            <div class="bg-light rounded-circle p-3 me-4"><i class="bi bi-qr-code-scan fs-4 text-danger"></i></div>
                                            <div><strong class="d-block fs-6">Chuyển khoản Ngân hàng (QR)</strong></div>
                                        </div>
                                    </label>
                                </div>

                                <button type="submit" class="btn-confirm w-100 shadow-sm mt-2">HOÀN TẤT ĐẶT HÀNG</button>
                            </form>
                        </div>
                    </div>
                </div>

                <div class="col-lg-5">
                    <div class="summary-box shadow-lg text-center position-sticky" style="top: 100px;">
                        <p class="text-uppercase mb-2 opacity-75 small fw-bold">Tổng thanh toán</p>
                        <div class="mb-3"><span class="text-white opacity-50 text-decoration-line-through d-none" id="oldPriceUI"><fmt:formatNumber value="${totalAmount}" pattern="###,###"/> đ</span></div>
                        <h1 class="fw-bold text-warning display-5 mb-4" id="finalPriceUI"><fmt:formatNumber value="${totalAmount}" pattern="###,###"/> đ</h1>
                        <hr class="border-light opacity-25 w-75 mx-auto mb-4">
                        <div class="text-start bg-white bg-opacity-10 p-4 rounded-4">
                            <div class="d-flex justify-content-between mb-2"><span class="small opacity-75">Tạm tính:</span><span class="small fw-bold"><fmt:formatNumber value="${totalAmount}" pattern="###,###"/> đ</span></div>
                            <div class="d-flex justify-content-between mb-2 text-warning d-none" id="discountRowUI"><span class="small">Voucher giảm:</span><span class="small fw-bold" id="discountAmountUI">- 0 đ</span></div>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>
    </div>

    <%-- MODAL NHẬP ĐỊA CHỈ --%>
    <div class="modal fade" id="addressModal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content rounded-4 border-0">
                <div class="modal-header bg-light border-0">
                    <h5 class="modal-title fw-bold text-primary">Cập nhật địa chỉ nhận hàng</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold small text-muted">Họ và tên</label>
                        <input type="text" id="tempName" class="form-control" value="${acc.fullName}">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold small text-muted">Số điện thoại</label>
                        <input type="text" id="tempPhone" class="form-control" value="${acc.phone}">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold small text-muted">Địa chỉ cụ thể (Số nhà, đường, phường...)</label>
                        <textarea id="tempAddress" class="form-control" rows="3">${acc.address}</textarea>
                    </div>
                </div>
                <div class="modal-footer border-0">
                    <button type="button" class="btn btn-secondary rounded-pill px-4" data-bs-dismiss="modal">Hủy</button>
                    <button type="button" class="btn btn-primary rounded-pill px-4 fw-bold" onclick="saveAddress()">HOÀN THÀNH</button>
                </div>
            </div>
        </div>
    </div>

    <%-- MODAL VOUCHER --%>
    <div class="modal fade" id="voucherModal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
            <div class="modal-content border-0 rounded-4">
                <div class="modal-header bg-light border-0 py-3"><h5 class="modal-title fw-bold text-primary">Chọn Voucher</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
                <div class="modal-body p-4" style="background-color: #f8f9fa;">
                    <c:forEach items="${voucherList}" var="v">
                        <div class="ticket-item bg-white">
                            <div class="ticket-left"><i class="bi bi-percent fs-1"></i><span class="small fw-bold">GIẢM GIÁ</span></div>
                            <div class="ticket-right">
                                <div><h6 class="fw-bold mb-1">${v.title}</h6><p class="small text-muted mb-1">Đơn tối thiểu <fmt:formatNumber value="${v.minOrderAmount}" pattern="###,###"/>đ</p></div>
                                <div class="form-check">
                                    <input class="form-check-input voucher-radio" type="radio" name="voucherSelect" data-code="${v.code}" data-discount="${v.discountAmount}" data-min="${v.minOrderAmount}" style="transform: scale(1.5);">
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                    <c:if test="${empty voucherList}">
                        <div class="text-center py-4">
                            <i class="bi bi-ticket-detailed fs-1 text-muted opacity-25"></i>
                            <p class="text-muted mt-2">Hiện sếp chưa có voucher nào trong ví.</p>
                        </div>
                    </c:if>
                </div>
                <div class="modal-footer border-0"><button type="button" class="btn btn-danger rounded-pill px-4 fw-bold" onclick="applyVoucher()">ÁP DỤNG MÃ</button></div>
            </div>
        </div>
    </div>

    <%-- MODAL QR --%>
    <div class="modal fade qr-modal" id="payOSModal" tabindex="-1" data-bs-backdrop="static">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-body text-center p-5">
                    <h4 class="fw-bold text-primary mb-4">QUÉT MÃ THANH TOÁN</h4>
                    <div class="qr-frame shadow-sm"><img id="qr-img" src="" style="width: 230px; height: 230px;"></div>
                    <div class="mt-3"><span class="badge bg-danger bg-opacity-10 text-danger border border-danger px-3 py-2 rounded-pill">Đang chờ nhận tiền...</span></div>
                    <p class="mt-4 small text-muted">Mã giao dịch: <b id="qr-order-id" class="text-dark"></b></p>
                    <%-- 🛑 ĐÃ FIX: Hủy bằng JS không làm load lại trang --%>
                    <button type="button" class="btn btn-sm btn-outline-secondary mt-2 rounded-pill px-4" data-bs-dismiss="modal" onclick="cancelQR()">Hủy giao dịch</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script>
        function saveAddress() {
            const name = document.getElementById('tempName').value;
            const phone = document.getElementById('tempPhone').value;
            const addr = document.getElementById('tempAddress').value;
            
            if(!name || !phone || !addr) {
                Swal.fire({ icon: 'warning', text: 'Sếp điền đủ thông tin giúp em nhé!'}); return;
            }

            document.getElementById('hiddenShipName').value = name;
            document.getElementById('hiddenShipPhone').value = phone;
            document.getElementById('hiddenShipAddress').value = addr;
            
            document.getElementById('ui-name-phone').innerText = name + " | " + phone;
            document.getElementById('ui-address').innerText = addr;

            bootstrap.Modal.getInstance(document.getElementById('addressModal')).hide();
            Swal.fire({ icon: 'success', title: 'Đã lưu', toast: true, position: 'top-end', showConfirmButton: false, timer: 1500 });
        }

        const originalTotal = ${totalAmount};
        let finalTotal = originalTotal;

        function applyVoucher() {
            const selected = document.querySelector('input[name="voucherSelect"]:checked');
            if (selected) {
                const code = selected.getAttribute('data-code');
                const discount = parseFloat(selected.getAttribute('data-discount'));
                const minOrder = parseFloat(selected.getAttribute('data-min'));

                if (originalTotal < minOrder) {
                    Swal.fire({ icon: 'error', title: 'Rất tiếc!', text: 'Đơn hàng chưa đủ điều kiện áp dụng mã này sếp ơi!' });
                    return;
                }

                finalTotal = originalTotal - discount;
                if(finalTotal < 0) finalTotal = 0; 

                document.getElementById('hiddenVoucherCode').value = code;
                document.getElementById('selectedVoucherText').innerText = "Đã áp mã: " + code;
                document.getElementById('selectedVoucherText').classList.replace('text-muted', 'text-danger');
                document.getElementById('selectedVoucherText').classList.add('fw-bold');
                
                document.getElementById('oldPriceUI').classList.remove('d-none');
                document.getElementById('discountRowUI').classList.remove('d-none');
                document.getElementById('discountAmountUI').innerText = "- " + new Intl.NumberFormat('vi-VN').format(discount) + " đ";
                document.getElementById('finalPriceUI').innerText = new Intl.NumberFormat('vi-VN').format(finalTotal) + " đ";

                bootstrap.Modal.getInstance(document.getElementById('voucherModal')).hide();
            }
        }

        document.getElementById('mainCheckoutForm').onsubmit = function(e) {
            const method = document.querySelector('input[name="txt_phuong_thuc"]:checked').value;
            if(!document.getElementById('hiddenShipAddress').value.trim()) {
                e.preventDefault();
                Swal.fire({ icon: 'warning', title: 'Khoan đã sếp!', text: 'Sếp chưa nhập địa chỉ nhận hàng kìa!' });
                return;
            }

            if (method === 'TRANSFER') {
                e.preventDefault(); 
                triggerQRModal(); 
            }
        };

        // 🛑 ĐÃ FIX: Khai báo biến đếm giờ ra ngoài để dễ bề Dừng lại khi Hủy
        let paymentTimer;

        function triggerQRModal() {
            const amount = finalTotal; 
            let orderId = "";
            <c:choose>
                <c:when test="${sach_mua.isEbook == 1}">orderId = "MUACHAP U${sessionScope.acc.id} C${sach_mua.id}";</c:when>
                <c:otherwise>orderId = "BILL_" + Math.floor(Math.random() * 1000000);</c:otherwise>
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
            // Gán vòng lặp vào biến để có thể hủy
            paymentTimer = setInterval(() => {
                fetch('check-payment-status?orderId=' + encodeURIComponent(orderId))
                    .then(res => res.text())
                    .then(status => {
                        if (status.trim() === "success") {
                            clearInterval(paymentTimer);
                            Swal.fire({
                                icon: 'success', title: 'Ting Ting!', text: 'Giao dịch thành công!', showConfirmButton: false, timer: 2000
                            }).then(() => {
                                document.getElementById('mainCheckoutForm').submit();
                            });
                        }
                    });
            }, 3000);
        }

        // 🛑 ĐÃ FIX: Hàm hủy giao dịch ngầm, không tải lại trang
        function cancelQR() {
            if (paymentTimer) {
                clearInterval(paymentTimer); // Dừng kiểm tra tiền gửi về
            }
            document.getElementById('qr-img').src = ""; // Xóa ảnh QR
        }
    </script>
</body>
</html>