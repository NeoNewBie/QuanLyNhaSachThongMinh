<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Hồ Sơ Cá Nhân - Smart Library</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <style>
        :root { --primary-color: #173F5F; --accent-color: #ED553B; --bg-light: #F8F9FA; }
        /* Đã bỏ font 'Inter', dùng font mặc định cực kỳ dễ nhìn và tự nhiên */
        body { background-color: var(--bg-light); color: #333; }
        
        .profile-card { border: none; border-radius: 16px; box-shadow: 0 5px 25px rgba(0,0,0,0.05); background: white; overflow: hidden; }
        .profile-header { background: linear-gradient(135deg, var(--primary-color) 0%, #2A608B 100%); padding: 35px 20px; color: white; text-align: center; }
        .avatar-container { width: 110px; height: 110px; border-radius: 50%; background: white; padding: 4px; margin: 0 auto 15px auto; box-shadow: 0 5px 15px rgba(0,0,0,0.15); }
        .avatar-img { width: 100%; height: 100%; border-radius: 50%; object-fit: cover; }
        
        .form-control { border-radius: 8px; padding: 10px 15px; border: 1px solid #ddd; background-color: #fafafa; transition: 0.3s; font-size: 0.95rem; }
        .form-control:focus { border-color: var(--primary-color); box-shadow: 0 0 0 3px rgba(23, 63, 95, 0.1); background-color: #fff; }
        .form-label { font-weight: 600; color: #555; font-size: 0.9rem; margin-bottom: 8px; }
        
        /* Nút LƯU THAY ĐỔI (Cam) */
        .btn-update { 
            background: var(--accent-color); 
            color: white; 
            border: 2px solid var(--accent-color); 
            border-radius: 8px; 
            padding: 10px 25px; 
            font-weight: 600; 
            transition: all 0.3s ease; 
            width: 100%; 
        }
        .btn-update:hover { 
            background: transparent; 
            color: var(--accent-color); 
        }

        /* Nút ĐỔI MẬT KHẨU (Xám tinh tế theo ý sếp) */
        .btn-changepass {
            background: transparent;
            color: #6c757d;
            border: 2px solid #6c757d;
            border-radius: 8px;
            padding: 10px 25px;
            font-weight: 600;
            transition: all 0.3s ease;
            width: 100%;
            display: flex;
            align-items: center;
            justify-content: center;
            text-decoration: none;
        }
        .btn-changepass:hover {
            background: #6c757d;
            color: white;
        }
    </style>
</head>
<body>
    <jsp:include page="header.jsp" />

    <c:if test="${sessionScope.acc == null}">
        <c:redirect url="login.jsp"/>
    </c:if>

    <div class="container py-5">
        <div class="row justify-content-center">
            <div class="col-lg-4 mb-4 mb-lg-0">
                <div class="profile-card h-100">
                    <div class="profile-header">
                        <div class="avatar-container">
                            <img src="https://ui-avatars.com/api/?name=${sessionScope.acc.username}&background=ED553B&color=fff&size=150" class="avatar-img" alt="Avatar">
                        </div>
                        <h5 class="fw-bold mb-1">${empty sessionScope.acc.fullName ? sessionScope.acc.username : sessionScope.acc.fullName}</h5>
                        <p class="mb-0 opacity-75" style="font-size: 0.85rem;">
                            <c:choose>
                                <c:when test="${sessionScope.acc.roleId == 1}">Quản Trị Viên Hệ Thống</c:when>
                                <c:otherwise>Thành Viên Smart Lib</c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                    <div class="p-4">
                        <div class="d-flex align-items-center mb-3 text-muted" style="font-size: 0.9rem;">
                            <i class="bi bi-envelope-fill me-3 fs-5" style="color: #999;"></i>
                            <span>${empty sessionScope.acc.email ? 'Chưa cập nhật Email' : sessionScope.acc.email}</span>
                        </div>
                        <hr class="opacity-10">
                        <div class="d-flex align-items-center mb-3 text-muted" style="font-size: 0.9rem;">
                            <i class="bi bi-telephone-fill me-3 fs-5" style="color: #999;"></i>
                            <span>${empty sessionScope.acc.phone ? 'Chưa cập nhật số điện thoại' : sessionScope.acc.phone}</span>
                        </div>
                        <hr class="opacity-10">
                        <div class="d-flex align-items-center text-muted" style="font-size: 0.9rem;">
                            <i class="bi bi-geo-alt-fill me-3 fs-5" style="color: #999;"></i>
                            <span>${empty sessionScope.acc.address ? 'Chưa cập nhật địa chỉ' : sessionScope.acc.address}</span>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-lg-8">
                <div class="profile-card p-4 p-md-5">
                    <h4 class="fw-bold mb-4" style="color: var(--primary-color);">Cập nhật thông tin</h4>
                    
                    <c:if test="${not empty requestScope.msg}">
                        <div class="alert alert-success border-0 rounded-2 mb-4" style="background-color: #d1e7dd; color: #0f5132;"><i class="bi bi-check-circle-fill me-2"></i>${requestScope.msg}</div>
                    </c:if>
                    <c:if test="${not empty requestScope.msgError}">
                        <div class="alert alert-danger border-0 rounded-2 mb-4" style="background-color: #f8d7da; color: #842029;"><i class="bi bi-exclamation-triangle-fill me-2"></i>${requestScope.msgError}</div>
                    </c:if>

                    <form action="update-profile" method="POST">
                        <div class="row g-4">
                            <div class="col-md-6">
                                <label class="form-label">Tên đăng nhập</label>
                                <input type="text" class="form-control" value="${sessionScope.acc.username}" readonly style="background-color: #eee; color: #777;">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Họ và Tên</label>
                                <input type="text" name="fullname" class="form-control" value="${sessionScope.acc.fullName}" placeholder="Nhập họ và tên thật...">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Địa chỉ Email</label>
                                <input type="email" name="email" class="form-control" value="${sessionScope.acc.email}" placeholder="ví dụ: hai@gmail.com...">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Số điện thoại</label>
                                <input type="text" name="phone" class="form-control" value="${sessionScope.acc.phone}" placeholder="Nhập số điện thoại...">
                            </div>
                            <div class="col-12">
                                <label class="form-label">Địa chỉ giao sách</label>
                                <textarea name="address" class="form-control" rows="3" placeholder="Nhập địa chỉ nhận sách giấy...">${sessionScope.acc.address}</textarea>
                            </div>
                            
                            <div class="col-12 mt-4 pt-2">
                                <div class="row">
                                    <div class="col-md-6 mb-3 mb-md-0">
                                        <button type="submit" class="btn-update">LƯU THAY ĐỔI</button>
                                    </div>
                                    <div class="col-md-6">
                                        <a href="change-password.jsp" class="btn-changepass">
                                            <i class="bi bi-key-fill me-2"></i> ĐỔI MẬT KHẨU
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="footer.jsp" />
</body>
</html>