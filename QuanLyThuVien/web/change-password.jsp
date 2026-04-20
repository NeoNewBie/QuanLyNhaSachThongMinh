<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đổi Mật Khẩu - Smart Library</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <style>
        :root { --primary-color: #173F5F; --accent-color: #ED553B; --bg-light: #F8F9FA; }
        body { background-color: var(--bg-light); color: #333; }
        
        .password-card { 
            max-width: 550px; 
            margin: 60px auto 80px auto; 
            background: #fff; 
            border-radius: 16px; 
            box-shadow: 0 5px 25px rgba(0,0,0,0.05); 
            padding: 40px 50px; 
        }
        .icon-wrapper {
            width: 70px; height: 70px;
            background: #FFF5F3; color: var(--accent-color);
            border-radius: 50%; display: flex; align-items: center; justify-content: center;
            font-size: 2rem; margin: 0 auto 20px auto;
        }
        .form-control { border-radius: 8px; padding: 12px 15px; border: 1px solid #ddd; background-color: #fafafa; font-size: 0.95rem; transition: 0.3s;}
        .form-control:focus { border-color: var(--primary-color); box-shadow: 0 0 0 3px rgba(23, 63, 95, 0.1); background-color: #fff; }
        .form-label { font-weight: 600; color: #555; font-size: 0.9rem; margin-bottom: 8px; }
        
        /* NÚT XÁC NHẬN MÀU CAM */
        .btn-confirm { 
            background: var(--accent-color); color: white; border: none; border-radius: 8px; 
            padding: 12px; font-weight: 600; transition: all 0.3s ease; width: 100%; margin-top: 10px;
        }
        .btn-confirm:hover { background: #d6452d; transform: translateY(-2px); box-shadow: 0 5px 15px rgba(237, 85, 59, 0.2); }
        
        /* NÚT QUAY LẠI MÀU XÁM KHÓI */
        .btn-back {
            display: block; text-align: center; color: #6c757d; font-weight: 500; margin-top: 20px; text-decoration: none; transition: 0.3s;
        }
        .btn-back:hover { color: var(--primary-color); text-decoration: underline; }
    </style>
</head>
<body>
    <jsp:include page="header.jsp" />

    <c:if test="${sessionScope.acc == null}">
        <c:redirect url="login.jsp"/>
    </c:if>

    <div class="container">
        <div class="password-card">
            <div class="icon-wrapper">
                <i class="bi bi-shield-lock"></i>
            </div>
            <h3 class="fw-bold text-center mb-2" style="color: var(--primary-color);">ĐỔI MẬT KHẨU</h3>
            <p class="text-center text-muted mb-4 small">Hãy bảo vệ tài khoản của bạn bằng một mật khẩu an toàn</p>

            <c:if test="${not empty requestScope.msg}">
                <div class="alert alert-success border-0 rounded-2 mb-4" style="background-color: #d1e7dd; color: #0f5132;"><i class="bi bi-check-circle-fill me-2"></i>${requestScope.msg}</div>
            </c:if>
            <c:if test="${not empty requestScope.msgError}">
                <div class="alert alert-danger border-0 rounded-2 mb-4" style="background-color: #f8d7da; color: #842029;"><i class="bi bi-exclamation-triangle-fill me-2"></i>${requestScope.msgError}</div>
            </c:if>

            <form action="change-password" method="POST">
                <div class="mb-3">
                    <label class="form-label">Mật khẩu hiện tại</label>
                    <input type="password" name="oldPass" class="form-control" placeholder="Nhập mật khẩu đang dùng..." required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Mật khẩu mới</label>
                    <input type="password" name="newPass" class="form-control" placeholder="Mật khẩu mới phải dễ nhớ..." required>
                </div>
                <div class="mb-4">
                    <label class="form-label">Xác nhận mật khẩu mới</label>
                    <input type="password" name="confirmPass" class="form-control" placeholder="Nhập lại mật khẩu mới cho chuẩn..." required>
                </div>

                <button type="submit" class="btn-confirm">XÁC NHẬN ĐỔI MẬT KHẨU</button>
                <a href="profile" class="btn-back"><i class="bi bi-arrow-left me-1"></i> Quay lại Hồ sơ cá nhân</a>
            </form>
        </div>
    </div>

    <jsp:include page="footer.jsp" />
</body>
</html>