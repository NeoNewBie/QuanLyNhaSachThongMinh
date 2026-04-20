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
        body { background-color: #F8F9FA; color: #333; }
        .detail-card { background: #fff; border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); padding: 40px; margin: 40px auto; max-width: 1000px; }
        .header-title { color: #173F5F; font-weight: bold; font-size: 1.8rem; }
        
        .info-box { background: #f8f9fa; border: 1px solid #eee; border-radius: 8px; padding: 20px; margin-bottom: 30px; }
        .info-label { font-weight: bold; color: #6c757d; margin-bottom: 5px; font-size: 0.9rem; text-transform: uppercase; }
        .info-value { font-weight: bold; color: #212529; font-size: 1.1rem; }

        .table { vertical-align: middle; }
        .table thead th { border-bottom: 2px solid #dee2e6; color: #495057; font-weight: bold; padding: 15px 10px; }
        .table tbody td { border-bottom: 1px solid #f1f3f5; padding: 15px 10px; font-weight: 500; }
        
        .book-img { width: 60px; height: 80px; object-fit: contain; border-radius: 4px; border: 1px solid #eee; background: #fff; padding: 2px;}
        .total-row td { font-size: 1.2rem; font-weight: bold; color: #173F5F; border-bottom: none; padding-top: 20px;}
        .total-price { color: #ED553B; font-size: 1.5rem; }

        .badge { padding: 8px 15px; border-radius: 50px; font-weight: 500; font-size: 0.9rem;}
        .btn-back { display: inline-block; margin-top: 20px; color: #6c757d; text-decoration: none; font-weight: 500; transition: 0.2s;}
        .btn-back:hover { color: #173F5F; text-decoration: underline; }
    </style>
</head>
<body>
    <jsp:include page="header.jsp" />

    <div class="container">
        <div class="detail-card">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="header-title">Chi Tiết Đơn Hàng #ORD-${order.id}</h2>
                <c:choose>
                    <c:when test="${order.status == 'Đang xử lý'}"><span class="badge bg-warning text-dark fs-6">Đang xử lý</span></c:when>
                    <c:when test="${order.status == 'Đang giao'}"><span class="badge bg-info text-dark fs-6">Đang giao</span></c:when>
                    <c:when test="${order.status == 'Đã giao'}"><span class="badge bg-success fs-6">Đã giao</span></c:when>
                    <c:when test="${order.status == 'Đã hủy'}"><span class="badge bg-danger fs-6">Đã hủy</span></c:when>
                    <c:otherwise><span class="badge bg-secondary fs-6">${order.status}</span></c:otherwise>
                </c:choose>
            </div>

            <div class="row info-box">
                <div class="col-md-4 border-end">
                    <div class="info-label">Ngày đặt hàng</div>
                    <div class="info-value"><fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm"/></div>
                </div>
                <div class="col-md-4 border-end ps-md-4">
                    <div class="info-label">Khách hàng</div>
                    <div class="info-value">${sessionScope.acc.fullName}</div>
                </div>
                <div class="col-md-4 ps-md-4">
                    <div class="info-label">Thông tin liên hệ</div>
                    <div class="info-value fs-6 fw-normal">${sessionScope.acc.phone} <br> ${sessionScope.acc.address}</div>
                </div>
            </div>

            <h4 class="fw-bold mb-3" style="color: #495057;">Danh sách sản phẩm</h4>
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th colspan="2">Sản phẩm</th>
                            <th class="text-center">Đơn giá</th>
                            <th class="text-center">Số lượng</th>
                            <th class="text-end">Thành tiền</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${details}" var="i">
                            <tr>
                                <td style="width: 80px;">
                                    <img src="${i.book.coverImage.startsWith('http') ? i.book.coverImage : pageContext.request.contextPath.concat('/').concat(i.book.coverImage)}" class="book-img">
                                </td>
                                <td>
                                    <span class="fw-bold d-block text-dark">${i.book.title}</span>
                                    <small class="text-muted">${i.book.author}</small>
                                </td>
                                <td class="text-center"><fmt:formatNumber value="${i.price}" pattern="###,###"/> đ</td>
                                <td class="text-center">x${i.quantity}</td>
                                <td class="text-end fw-bold" style="color: #212529;">
                                    <fmt:formatNumber value="${i.price * i.quantity}" pattern="###,###"/> đ
                                </td>
                            </tr>
                        </c:forEach>
                        <tr class="total-row">
                            <td colspan="3"></td>
                            <td class="text-end">TỔNG CỘNG:</td>
                            <td class="text-end total-price"><fmt:formatNumber value="${order.totalPrice}" pattern="###,###"/> VNĐ</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <a href="orders" class="btn-back"><i class="bi bi-arrow-left me-1"></i> Quay lại danh sách đơn hàng</a>
        </div>
    </div>

    <jsp:include page="footer.jsp" />
</body>
</html>