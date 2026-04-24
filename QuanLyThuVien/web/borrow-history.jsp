<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="java.util.Date" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Lịch sử mượn sách - Smart Lib</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <style>
        :root { --primary-color: #173F5F; --accent-color: #ED553B; }
        body { background-color: #f8f9fa; }
        .history-card { border: none; border-radius: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.05); background: white; padding: 30px; }
        .book-img { width: 50px; height: 75px; object-fit: cover; border-radius: 5px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .badge-smart { border-radius: 50px; padding: 6px 12px; font-weight: 600; font-size: 0.8rem; }
        .table thead th { border-top: none; color: #666; font-weight: 600; text-transform: uppercase; font-size: 0.75rem; letter-spacing: 0.5px; }
        
        /* 🛑 Thêm hiệu ứng Hover làm sáng nguyên hàng khi trỏ chuột */
        .clickable-row { transition: background-color 0.2s ease; cursor: pointer; }
        .clickable-row:hover { background-color: #f1f5f9 !important; }
    </style>
</head>
<body>
    <jsp:include page="header.jsp" />

    <div class="container py-5">
        <div class="history-card">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h3 class="fw-bold m-0" style="color: var(--primary-color);">
                    <i class="bi bi-journal-bookmark-fill me-2"></i>Sách bạn đang mượn
                </h3>
                <span class="badge bg-light text-dark border">Tổng cộng: ${listB.size()} cuốn</span>
            </div>
            
            <div class="table-responsive">
                <table class="table align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>Hình ảnh</th>
                            <th>Tên sách</th>
                            <th class="text-center">Ngày mượn</th>
                            <th class="text-center">Hạn trả</th>
                            <th class="text-center">Trạng thái</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${listB}" var="b">
                            <%-- 🛑 ĐÃ FIX: Biến nguyên cái TR thành nút bấm siêu to khổng lồ --%>
                            <tr class="clickable-row border-bottom" onclick="window.location.href='borrow-detail?id=${b.id}'" title="Nhấn để xem chi tiết">
                                <td class="py-3">
                                    <img src="${empty b.bookImage ? 'https://placehold.co/50x75?text=No+Img' : b.bookImage}" 
                                         class="book-img" alt="book">
                                </td>
                                <td>
                                    <span class="fw-bold text-dark d-block">${b.bookTitle}</span>
                                    <small class="text-muted">Mã mượn: #${b.id}</small>
                                </td>
                                <td class="text-center"><fmt:formatDate value="${b.borrowDate}" pattern="dd/MM/yyyy"/></td>
                                <td class="text-center"><fmt:formatDate value="${b.returnDate}" pattern="dd/MM/yyyy"/></td>
                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${b.status == '0' || b.status == 'Pending'}">
                                            <span class="badge bg-warning text-dark badge-smart"><i class="bi bi-hourglass me-1"></i>Chờ duyệt</span>
                                        </c:when>

                                        <c:when test="${b.status == '2'}">
                                            <span class="badge bg-info text-dark badge-smart"><i class="bi bi-geo-alt-fill me-1"></i>Sẵn sàng tại quầy</span>
                                        </c:when>

                                        <c:when test="${not empty b.actualReturnDate}">
                                            <span class="badge bg-success badge-smart">
                                                <i class="bi bi-check-circle-fill me-1"></i>
                                                Đã trả ngày <fmt:formatDate value="${b.actualReturnDate}" pattern="dd/MM"/>
                                            </span>
                                        </c:when>

                                        <c:otherwise>
                                            <%-- 🛑 ĐÃ DỌN DẸP SẠCH SẼ: Di dời nút "Xin Gia Hạn" vào trong trang Detail để tránh xung đột Click --%>
                                            <%
                                                model.Borrow item = (model.Borrow) pageContext.getAttribute("b");
                                                long now = new Date().getTime();
                                                long due = item.getReturnDate().getTime();
                                                long diff = due - now;
                                                long daysLeft = diff / (1000 * 60 * 60 * 24);

                                                if (daysLeft < 0) {
                                                    out.print("<span class='badge bg-danger badge-smart'><i class='bi bi-alarm-fill me-1'></i>Quá hạn " + Math.abs(daysLeft) + " ngày 💀</span>");
                                                } else if (daysLeft <= 3) {
                                                    out.print("<span class='badge bg-warning text-dark badge-smart'><i class='bi bi-exclamation-triangle-fill me-1'></i>Còn " + daysLeft + " ngày ⚠️</span>");
                                                } else {
                                                    out.print("<span class='badge bg-primary badge-smart'><i class='bi bi-clock-history me-1'></i>Còn " + daysLeft + " ngày</span>");
                                                }
                                            %>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        
                        <c:if test="${empty listB}">
                            <tr>
                                <td colspan="5" class="text-center py-5 text-muted">
                                    <img src="https://cdn-icons-png.flaticon.com/512/10534/10534066.png" width="80" class="mb-3 opacity-50"><br>
                                    <p class="fs-5">Ông chưa mượn cuốn nào cả Hải ơi.</p>
                                    <a href="shop" class="btn btn-sm btn-outline-primary rounded-pill px-4">Qua Store lựa vài cuốn đi!</a>
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <jsp:include page="footer.jsp" />
</body>
</html>