<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Lịch sử mượn sách - Smart Lib</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        :root { --primary-color: #173F5F; --accent-color: #ED553B; }
        body { background-color: #f8f9fa; font-family: 'Inter', sans-serif; }
        .history-card { border: none; border-radius: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.05); background: white; padding: 30px; }
        .status-badge { border-radius: 50px; padding: 5px 15px; font-size: 0.75rem; font-weight: 700; }
    </style>
</head>
<body>
    <jsp:include page="header.jsp" />

    <div class="container py-5">
        <div class="history-card">
            <h3 class="fw-bold mb-4" style="color: var(--primary-color);">
                <i class="bi bi-journal-bookmark me-2"></i>Sách bạn đang mượn
            </h3>
            
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>Hình ảnh</th>
                            <th>Tên sách</th>
                            <th>Ngày mượn</th>
                            <th>Hạn trả</th>
                            <th>Trạng thái</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%-- Chỗ này Hải dùng c:forEach để duyệt listB truyền từ Servlet sang nhé --%>
                        <c:if test="${empty listB}">
                            <tr>
                                <td colspan="6" class="text-center py-5 text-muted">
                                    Ông chưa mượn cuốn nào cả Hải ơi. Qua Store lựa vài cuốn đi!
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