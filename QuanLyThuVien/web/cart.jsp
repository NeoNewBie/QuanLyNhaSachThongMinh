<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Giỏ Hàng - Smart Library</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <style>
        :root { --primary-color: #173F5F; --accent-color: #ED553B; }
        body { font-family: 'Inter', sans-serif; background-color: #F6F6F6; }
        .text-primary-custom { color: var(--primary-color) !important; }
        .btn-accent { background-color: var(--accent-color); color: white; border: none; }
        .btn-accent:hover { background-color: #d64a31; color: white; }
        .qty-container { display: flex; align-items: center; justify-content: center; background: #fff; border: 1.5px solid #eee; border-radius: 50px; padding: 2px; width: fit-content; margin: 0 auto; }
        .qty-btn { width: 32px; height: 32px; border-radius: 50%; border: none; background: #f8f9fa; color: var(--primary-color); display: flex; align-items: center; justify-content: center; transition: 0.3s; }
        .qty-btn:hover { background: var(--accent-color); color: #fff; }
        .qty-input { width: 45px; border: none; text-align: center; font-weight: 800; background: transparent; outline: none; color: var(--primary-color); }
    </style>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
    <div class="container mt-5">
        <h2 class="fw-bold text-primary-custom mb-4"><i class="bi bi-cart-check"></i> GIỎ HÀNG CỦA BẠN</h2>
        <div class="card shadow-sm border-0">
            <div class="card-body p-0">
                <table class="table table-hover mb-0 align-middle text-center">
                    <thead class="table-light text-primary-custom">
                        <tr>
                            <th>Hình ảnh</th>
                            <th class="text-start">Tên sách</th>
                            <th>Đơn giá</th>
                            <th>Số lượng</th>
                            <th>Thành tiền</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:if test="${sessionScope.cart != null && sessionScope.cart.items.size() > 0}">
                            <c:forEach items="${sessionScope.cart.items}" var="i">
                                <tr>
                                    <td><img src="${i.book.coverImage}" style="height: 80px; object-fit: contain;"></td>
                                    <td class="text-start fw-bold text-primary-custom">${i.book.title}</td>
                                    <%-- 🛑 FIX GIÁ ĐƠN VỊ --%>
                                    <td class="text-danger fw-bold"><fmt:formatNumber value="${i.book.price}" pattern="###,###"/> đ</td>
                                    <td>
                                        <div class="qty-container shadow-sm">
                                            <button type="button" class="qty-btn" onclick="changeQty(${i.book.id}, -1)"><i class="bi bi-dash"></i></button>
                                            <input type="text" id="qty-${i.book.id}" value="${i.quantity}" class="qty-input" readonly>
                                            <button type="button" class="qty-btn" onclick="changeQty(${i.book.id}, 1)"><i class="bi bi-plus"></i></button>
                                        </div>
                                    </td>
                                    <%-- 🛑 FIX THÀNH TIỀN --%>
                                    <td class="text-danger fw-bold"><fmt:formatNumber value="${i.book.price * i.quantity}" pattern="###,###"/> đ</td>
                                    <td><button type="button" onclick="confirmDelete(${i.book.id})" class="btn btn-sm btn-outline-danger"><i class="bi bi-trash"></i> Xóa</button></td>
                                </tr>
                            </c:forEach>
                        </c:if>
                    </tbody>
                </table>
            </div>
            <c:if test="${sessionScope.cart != null && sessionScope.cart.items.size() > 0}">
                <div class="card-footer bg-white p-4 d-flex justify-content-between align-items-center">
                    <a href="home" class="btn btn-outline-secondary px-4"><i class="bi bi-arrow-left"></i> Tiếp tục mua sách</a>
                    <div class="text-end">
                        <span class="fs-5 text-muted me-3">Tổng cộng:</span>
                        <span class="fs-3 fw-bold text-danger"><fmt:formatNumber value="${sessionScope.cart.totalMoney}" pattern="###,###"/> VNĐ</span>
                        <br>
                        <button type="button" onclick="confirmCheckout()" class="btn btn-accent btn-lg mt-2 px-5 fw-bold text-uppercase">Thanh toán ngay</button>
                    </div>
                </div>
            </c:if>
        </div>
    </div>
<script>
    function changeQty(bookId, delta) {
        const input = document.getElementById('qty-' + bookId);
        let newQty = parseInt(input.value) + delta;
        if (newQty < 1) return;
        // 🛑 GỌI UPDATE-CART SERVLET
        window.location.href = 'update-cart?id=' + bookId + '&quantity=' + newQty;
    }
    function confirmDelete(bookId) {
        Swal.fire({ title: 'Bạn có chắc chắn?', text: "Sách sẽ bị xóa khỏi giỏ!", icon: 'warning', showCancelButton: true, confirmButtonColor: '#ED553B', cancelButtonColor: '#173F5F', confirmButtonText: 'Xóa nó!', cancelButtonText: 'Hủy' }).then((result) => {
            if (result.isConfirmed) window.location.href = 'remove-cart?id=' + bookId;
        });
    } 
    function confirmCheckout() { window.location.href = 'checkout'; }
</script>
</body>
</html>