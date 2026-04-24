<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý Mượn sách - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <style>
        :root { --primary-color: #173F5F; --bg-light: #F6F6F6; }
        body { font-family: 'Inter', sans-serif; background-color: var(--bg-light); }
        .sidebar { background-color: var(--primary-color); min-height: 100vh; position: fixed; width: 250px; }
        .main-content { margin-left: 250px; padding: 3rem; width: calc(100% - 250px); }
        .sidebar a { color: white; opacity: 0.8; transition: 0.3s; display: block; }
        .sidebar a:hover, .sidebar a.active { opacity: 1; background-color: rgba(255,255,255,0.1); border-radius: 8px; }
    </style>
</head>
<body>
    <div class="d-flex">
        <div class="sidebar p-4 text-white">
            <h4 class="fw-bold mb-4 text-center"><i class="bi bi-book-half"></i> SMART LIB</h4>
            <div class="d-flex flex-column gap-2">
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="text-decoration-none p-2"><i class="bi bi-speedometer2 me-2"></i> Dashboard</a>
                <a href="${pageContext.request.contextPath}/admin/manage-books" class="text-decoration-none p-2"><i class="bi bi-journal-text me-2"></i> Quản lý Sách</a>
                <a href="${pageContext.request.contextPath}/admin/manage-orders" class="text-decoration-none p-2"><i class="bi bi-cart-check me-2"></i> Quản lý Đơn hàng</a>
                
                <a href="${pageContext.request.contextPath}/admin/manage-borrows" class="text-decoration-none p-2 active"><i class="bi bi-journal-check me-2"></i> Quản lý Mượn Sách</a>
                
                <a href="${pageContext.request.contextPath}/admin/manage-users" class="text-decoration-none p-2"><i class="bi bi-people me-2"></i> Quản lý Người dùng</a>
                <hr class="text-white-50">
                <a href="${pageContext.request.contextPath}/home" class="text-decoration-none p-2"><i class="bi bi-house-door me-2"></i> Quay lại Shop</a>
            </div>
        </div>

        <div class="main-content">
            <h2 class="fw-bold mb-4" style="color: var(--primary-color);">Quản lý Phiếu Mượn</h2>
            
            <div class="card shadow-sm border-0">
                <div class="card-body p-0">
                    <table class="table table-hover mb-0 align-middle text-center">
                        <thead class="table-light">
                            <tr>
                                <th>Mã Phiếu</th>
                                <th>Tên Sách</th>
                                <th>Ngày Mượn</th>
                                <th>Hạn Trả</th>
                                <th>Trạng Thái</th>
                                <th>Thao Tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${listBorrows}" var="b">
                                <tr>
                                    <td class="fw-bold">#LIB-${b.id}</td>
                                    <td class="text-start fw-bold text-primary">${b.bookTitle}</td>
                                    <td><fmt:formatDate value="${b.borrowDate}" pattern="dd/MM/yyyy"/></td>
                                    <td><fmt:formatDate value="${b.returnDate}" pattern="dd/MM/yyyy"/></td>
                                    <td>
                                        <%-- 🛑 ĐÃ FIX: Nhận diện cả chữ Pending của DB cũ và số 0 của code mới --%>
                                        <c:choose>
                                            <c:when test="${b.status == '0' || b.status == 'Pending'}"><span class="badge bg-warning text-dark px-3 py-2 rounded-pill">Chờ duyệt</span></c:when>
                                            <c:when test="${b.status == '2'}"><span class="badge bg-info text-dark px-3 py-2 rounded-pill">Sẵn sàng tại quầy</span></c:when>
                                            <c:when test="${b.status == '3'}"><span class="badge bg-primary px-3 py-2 rounded-pill">Đang mượn</span></c:when>
                                            <c:when test="${b.status == '4'}"><span class="badge bg-success px-3 py-2 rounded-pill">Đã trả</span></c:when>
                                            <c:otherwise><span class="badge bg-secondary px-3 py-2 rounded-pill">${b.status}</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <%-- Bước 1: Duyệt đơn --%>
                                        <c:if test="${b.status == '0' || b.status == 'Pending'}">
                                            <a href="${pageContext.request.contextPath}/admin/manage-borrows?action=approve&id=${b.id}" class="btn btn-sm btn-success rounded-pill fw-bold"><i class="bi bi-check-lg"></i> Duyệt phiếu</a>
                                        </c:if>
                                        <%-- Bước 2: Khách ra lấy sách --%>
                                        <c:if test="${b.status == '2'}">
                                            <a href="${pageContext.request.contextPath}/admin/manage-borrows?action=give&id=${b.id}" class="btn btn-sm btn-primary rounded-pill fw-bold"><i class="bi bi-person-check"></i> Đã giao sách</a>
                                        </c:if>
                                        <%-- Bước 3: Khách đem trả --%>
                                        <c:if test="${b.status == '3'}">
                                            <a href="${pageContext.request.contextPath}/admin/manage-borrows?action=return&id=${b.id}" class="btn btn-sm btn-outline-danger rounded-pill fw-bold"><i class="bi bi-arrow-return-left"></i> Nhận trả sách</a>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <c:if test="${not empty sessionScope.sysMsg}">
        <script>
            Swal.fire({
                icon: '${sessionScope.sysMsgType}',
                title: '${sessionScope.sysMsgType == "success" ? "THÀNH CÔNG!" : "CÓ LỖI XẢY RA!"}',
                text: '${sessionScope.sysMsg}',
                confirmButtonColor: '#173F5F'
            });
        </script>
        <c:remove var="sysMsg" scope="session" />
        <c:remove var="sysMsgType" scope="session" />
    </c:if>
</body>
</html>