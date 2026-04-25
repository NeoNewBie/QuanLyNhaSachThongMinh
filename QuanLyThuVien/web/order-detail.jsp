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
        
        /* 🛑 TRỤC THỜI GIAN GIAO HÀNG (TIMELINE) */
        .tracking-timeline { position: relative; padding: 30px 15px 20px; margin: 20px 0; background: #fff; border-radius: 12px; border: 1px dashed #eee;}
        .tracking-line { position: absolute; height: 4px; background: #e9ecef; top: 45px; left: 12%; right: 12%; z-index: 1; border-radius: 4px; }
        .tracking-progress { position: absolute; height: 100%; background: var(--accent-color); border-radius: 4px; transition: width 0.5s ease; }
        .track-step { width: 25%; position: relative; z-index: 2; text-align: center; float: left; }
        .track-icon { width: 40px; height: 40px; background: #e9ecef; color: #a0a0a0; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin: 0 auto 10px; font-size: 1.2rem; border: 4px solid #fff; transition: 0.3s; }
        .track-text { font-size: 0.8rem; font-weight: 600; color: #a0a0a0; }
        
        .track-step.active .track-icon { background: var(--accent-color); color: #fff; box-shadow: 0 0 0 4px rgba(237, 85, 59, 0.2); }
        .track-step.active .track-text { color: var(--primary-color); }
        
        .track-step.canceled .track-icon { background: #dc3545; color: #fff; box-shadow: 0 0 0 4px rgba(220, 53, 69, 0.2); }
        .track-step.canceled .track-text { color: #dc3545; }

        /* Style cho nút Đã nhận hàng */
        .btn-received { background: #198754; color: white; border: none; padding: 6px 20px; border-radius: 50px; font-weight: 700; transition: 0.3s; box-shadow: 0 4px 15px rgba(25, 135, 84, 0.3); }
        .btn-received:hover { background: #157347; transform: translateY(-2px); color: white; }
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
                    <%-- 🛑 CẬP NHẬT LOGIC THEO DATABASE MỚI NHẤT (4: Thành công, 5: Hủy) --%>
                    <c:choose>
                        <c:when test="${order.status == '0'}"><span class="badge bg-warning text-dark badge-status shadow-sm"><i class="bi bi-hourglass-split me-1"></i>Chờ xác nhận</span></c:when>
                        <c:when test="${order.status == '1'}"><span class="badge bg-info text-dark badge-status shadow-sm"><i class="bi bi-wallet2 me-1"></i>Đã thanh toán (QR)</span></c:when>
                        <c:when test="${order.status == '2'}"><span class="badge bg-primary badge-status shadow-sm"><i class="bi bi-truck me-1"></i>Đang giao hàng</span></c:when>
                        <c:when test="${order.status == '4'}"><span class="badge bg-success badge-status shadow-sm"><i class="bi bi-check-circle me-1"></i>Đã giao thành công</span></c:when>
                        <c:when test="${order.status == '5'}"><span class="badge bg-danger badge-status shadow-sm"><i class="bi bi-x-circle me-1"></i>Đã hủy</span></c:when>
                        <c:otherwise><span class="badge bg-secondary badge-status shadow-sm">${order.status}</span></c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="tracking-timeline shadow-sm mx-4 mt-4 mb-2">
                <c:set var="progressWidth" value="0%" />
                <c:choose>
                    <c:when test="${order.status == '0'}"><c:set var="progressWidth" value="12%" /></c:when>
                    <c:when test="${order.status == '1'}"><c:set var="progressWidth" value="40%" /></c:when>
                    <c:when test="${order.status == '2'}"><c:set var="progressWidth" value="65%" /></c:when>
                    <c:when test="${order.status == '4'}"><c:set var="progressWidth" value="100%" /></c:when>
                </c:choose>

                <%-- Nếu khác 5 (Không bị hủy) thì mới hiện thanh chạy --%>
                <c:if test="${order.status != '5'}">
                    <div class="tracking-line"><div class="tracking-progress" style="width: ${progressWidth};"></div></div>
                </c:if>

                <%-- KHI ĐƠN BỊ HỦY --%>
                <c:if test="${order.status == '5'}">
                    <div class="tracking-line"><div class="tracking-progress" style="width: 100%; background: #dc3545;"></div></div>
                    <div class="track-step active"><div class="track-icon"><i class="bi bi-receipt"></i></div><div class="track-text">Đã đặt hàng</div></div>
                    <div class="track-step w-75 float-end canceled"><div class="track-icon mx-auto"><i class="bi bi-x-circle"></i></div><div class="track-text text-center mt-2">Đơn hàng đã bị hủy bỏ</div></div>
                    <div class="clearfix"></div>
                </c:if>

                <%-- KHI ĐƠN BÌNH THƯỜNG --%>
                <c:if test="${order.status != '5'}">
                    <div class="track-step active">
                        <div class="track-icon"><i class="bi bi-receipt"></i></div>
                        <div class="track-text">Chờ xác nhận<br><small class="text-muted fw-normal"><fmt:formatDate value="${order.orderDate}" pattern="dd/MM"/></small></div>
                    </div>
                    <div class="track-step ${order.status >= '1' ? 'active' : ''}">
                        <div class="track-icon"><i class="bi bi-box-seam"></i></div>
                        <div class="track-text">Đã duyệt & Đóng gói</div>
                    </div>
                    <div class="track-step ${order.status >= '2' ? 'active' : ''}">
                        <div class="track-icon"><i class="bi bi-truck"></i></div>
                        <div class="track-text">Đang giao hàng</div>
                    </div>
                    <div class="track-step ${order.status == '4' ? 'active' : ''}">
                        <div class="track-icon"><i class="bi bi-house-check"></i></div>
                        <div class="track-text">Đã nhận hàng</div>
                    </div>
                    <div class="clearfix"></div>
                </c:if>
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
                            <p class="mb-2 text-secondary fs-6">Phương thức: 
                                <strong class="text-dark">
                                    <%-- 🛑 ĐÃ FIX CHUẨN: Chỉ Status 1 mới là QR, còn lại là COD --%>
                                    <c:choose>
                                        <c:when test="${order.status == '1'}">Thanh toán Chuyển khoản (QR)</c:when>
                                        <c:otherwise>Thanh toán tiền mặt khi nhận hàng (COD)</c:otherwise>
                                    </c:choose>
                                </strong>
                            </p>
                            <p class="mb-0 text-secondary fs-6">Thanh toán: 
                                <strong class="${order.status == '1' || order.status == '4' ? 'text-success' : 'text-warning'}">
                                    ${order.status == '1' || order.status == '4' ? 'Đã thu tiền' : 'Chờ thu tiền (Khi nhận hàng)'}
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
                
                <div class="d-flex flex-wrap justify-content-between p-4 bg-light rounded-4 border mt-2">
                    <div class="pe-md-4 mb-4 mb-md-0" style="flex: 1; min-width: 280px;">
                        <h6 class="fw-bold text-muted mb-2"><i class="bi bi-shield-check me-2"></i>Chính sách & Thao tác Đơn hàng</h6>
                        <p class="small text-secondary mb-1">- Đơn hàng <strong>"Chờ duyệt"</strong> có thể được hủy trực tuyến.</p>
                        <p class="small text-secondary mb-1">- Khách hàng mua sách vật lý vui lòng nhận tại quầy thư viện UTE.</p>
                        <p class="small text-secondary mb-4">- Hỗ trợ kỹ thuật / Hoàn tiền: <strong class="text-dark">0909 123 456</strong></p>

                        <%-- 🛑 NÚT HỦY ĐƠN: Cho phép hủy đơn ở trạng thái 0, 1, 2 --%>
                        <c:if test="${order.status == '0' || order.status == '1' || order.status == '2'}">
                            <button type="button" onclick="confirmCancelOrder(${order.id})" class="btn btn-outline-danger btn-sm rounded-pill fw-bold px-4 py-2 shadow-sm me-2 mb-2">
                                <i class="bi bi-trash3-fill me-1"></i> YÊU CẦU HỦY ĐƠN
                            </button>
                        </c:if>

                        <%-- 🛑 THÊM VÀO: NÚT ĐÃ NHẬN HÀNG DÀNH CHO KHÁCH (Khi đang giao) --%>
                        <c:if test="${order.status == '2'}">
                            <button type="button" onclick="confirmReceivedOrder(${order.id})" class="btn-received mb-2">
                                <i class="bi bi-box2-heart-fill me-1"></i> ĐÃ NHẬN ĐƯỢC HÀNG
                            </button>
                        </c:if>
                    </div>

                    <div class="ps-md-4" style="width: 320px; border-left: 2px dashed #dee2e6;">
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
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script>
        function confirmCancelOrder(orderId) {
            Swal.fire({
                title: 'Hủy đơn hàng này?',
                text: "Sếp có chắc chắn muốn hủy không? Thao tác này không thể hoàn tác!",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#ED553B',
                cancelButtonColor: '#6c757d',
                confirmButtonText: 'Đồng ý hủy đơn!',
                cancelButtonText: 'Giữ lại đơn'
            }).then((result) => {
                if (result.isConfirmed) {
                    window.location.href = "cancel-order?id=" + orderId;
                }
            })
        }

        // 🛑 SCRIPT CHO NÚT XÁC NHẬN NHẬN HÀNG MỚI THÊM
        function confirmReceivedOrder(orderId) {
            Swal.fire({
                title: 'Xác nhận đã nhận hàng?',
                text: "Xác nhận sếp đã nhận sách và thanh toán thành công nhé!",
                icon: 'question',
                showCancelButton: true,
                confirmButtonColor: '#198754',
                cancelButtonColor: '#6c757d',
                confirmButtonText: 'Đã nhận hàng!',
                cancelButtonText: 'Chưa nhận'
            }).then((result) => {
                if (result.isConfirmed) {
                    // Gọi sang Servlet CompleteOrder để Update Status thành 4
                    window.location.href = "complete-order?id=" + orderId;
                }
            })
        }
    </script>
</body>
</html>