<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Lịch Sử Mượn Sách - Smart Library</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <style>
        body { background-color: #F8F9FA; color: #333; font-family: system-ui, -apple-system, sans-serif; }
        .order-card { 
            background: #fff; border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); 
            padding: 40px; margin: 50px auto; max-width: 1100px;
        }
        .page-title { color: #173F5F; font-weight: bold; margin-bottom: 30px; font-size: 2rem;}
        .table { vertical-align: middle; font-size: 1rem; }
        .table thead th { border-bottom: 2px solid #dee2e6; color: #495057; font-weight: bold; padding: 15px; }
        .table tbody td { border-bottom: 1px solid #f1f3f5; padding: 15px; }
        .order-id { font-weight: bold; color: #212529; }
        .order-price { font-weight: bold; color: #ED553B; }
        .btn-action { border-radius: 50px; padding: 5px 20px; font-weight: 500; font-size: 0.9rem; transition: 0.2s; }
        .badge { padding: 8px 15px; border-radius: 50px; font-weight: 600; font-size: 0.85rem;}
    </style>
</head>
<body>
    <jsp:include page="header.jsp" />

    <c:if test="${sessionScope.acc == null}">
        <c:redirect url="login.jsp"/>
    </c:if>

    <div class="container">
        <div class="order-card">
            <h2 class="page-title">Lịch sử mượn sách của Hải</h2>

            <div style="display: none;">Số đơn hàng lấy được: ${listO.size()}</div>

            <c:if test="${not empty sessionScope.msg}">
                <div class="alert alert-success fw-bold">
                    <i class="bi bi-check-circle-fill me-2"></i>${sessionScope.msg}
                </div>
                <c:remove var="msg" scope="session" />
            </c:if>

            <c:choose>
                <c:when test="${empty listO}">
                    <div class="text-center py-5">
                        <i class="bi bi-book-half text-muted" style="font-size: 4rem;"></i>
                        <h4 class="fw-bold mt-3">Sếp chưa mượn cuốn nào cả!</h4>
                        <p class="text-muted">Hãy lướt qua kho sách và tìm vài cuốn "tu luyện" kiến thức nhé sếp.</p>
                        <a href="shop" class="btn btn-primary mt-2 px-4 rounded-pill">ĐI MƯỢN SÁCH NGAY</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Mã phiếu</th>
                                    <th>Ngày mượn</th>
                                    <th>Tiền cọc</th>
                                    <th>Trạng thái</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${listO}" var="o">
                                    <tr>
                                        <td class="order-id">#LIB-${o.id}</td>
                                        <td><fmt:formatDate value="${o.orderDate}" pattern="dd/MM/yyyy"/></td>
                                        <td class="order-price"><fmt:formatNumber value="${o.totalPrice}" pattern="###,###"/> VNĐ</td>
                                        <td>
                                            <%-- 🛑 FIX AN TOÀN: Dùng nháy đơn để so sánh chuỗi, tránh lỗi vỡ trang --%>
                                            <c:choose>
                                                <c:when test="${o.status == '0'}"><span class="badge bg-warning text-dark">Chờ duyệt</span></c:when>
                                                <c:when test="${o.status == '1'}"><span class="badge bg-info text-dark">Đang chuẩn bị</span></c:when>
                                                <c:when test="${o.status == '2'}"><span class="badge bg-success">Sẵn sàng nhận</span></c:when>
                                                <c:when test="${o.status == '3'}"><span class="badge bg-primary">Đang mượn</span></c:when>
                                                <c:when test="${o.status == '4'}"><span class="badge bg-secondary">Đã trả sách</span></c:when>
                                                <c:when test="${o.status == '5'}"><span class="badge bg-danger">Đã hủy</span></c:when>
                                                <c:otherwise><span class="badge bg-dark">Code lỗi: ${o.status}</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div class="d-flex gap-2">
                                                <a href="order-detail?id=${o.id}" class="btn btn-outline-primary btn-action">Chi tiết</a>
                                                
                                                <c:if test="${o.status == '0'}">
                                                    <button onclick="confirmCancel(${o.id})" class="btn btn-outline-danger btn-action">Hủy đơn</button>
                                                </c:if>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <jsp:include page="footer.jsp" />

    <script>
        function confirmCancel(orderId) {
            Swal.fire({
                title: 'Hủy phiếu mượn #LIB-' + orderId + '?',
                text: "Sếp chắc chắn muốn hủy chứ? Cơ hội đọc sách tốt lắm đó!",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#dc3545',
                cancelButtonColor: '#6c757d',
                confirmButtonText: 'Đồng ý hủy',
                cancelButtonText: 'Để tôi xem lại'
            }).then((result) => {
                if (result.isConfirmed) {
                    window.location.href = 'cancel-order?id=' + orderId;
                }
            })
        }
    </script>
</body>
</html>