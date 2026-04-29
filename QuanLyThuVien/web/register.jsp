<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Đăng ký thành viên - Smart Lib</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        :root { --primary-color: #173F5F; --accent-color: #ED553B; }
        body { background: #f0f2f5; height: 100vh; display: flex; align-items: center; font-family: 'Inter', sans-serif; }
        .auth-card { border: none; border-radius: 40px; box-shadow: 0 30px 60px rgba(0,0,0,0.12); padding: 50px; max-width: 500px; width: 100%; margin: auto; background: white; }
        .form-control { border-radius: 50px; padding: 13px 25px; border: 1px solid #eee; background: #f8f9fa; }
        .btn-auth { background: var(--accent-color); color: white; border-radius: 50px; padding: 15px; font-weight: 800; width: 100%; border: none; margin-top: 10px; transition: 0.3s; text-transform: uppercase; }
        .btn-auth:hover { background: var(--primary-color); transform: translateY(-5px); }
    </style>
</head>
<body>
    <div class="auth-card">
        <div class="text-center mb-4">
            <h2 class="fw-bold" style="color: var(--primary-color);">GIA NHẬP SMART LIB</h2>
            <p class="text-muted small">Tạo tài khoản để mượn sách và đọc truyện ngay</p>
        </div>

        <%-- SỬA LỖI VỆT MÀU: Chỉ hiện div alert khi thực sự có lỗi --%>
        <c:if test="${not empty error}">
            <div class="alert alert-danger border-0 rounded-pill small py-2 px-4 mb-3 text-center">
                ${error}
            </div>
        </c:if>

        <form action="register" method="POST">
            <div class="mb-3"><input type="text" name="user" class="form-control" placeholder="Tên đăng nhập" required></div>
            <div class="mb-3"><input type="email" name="email" class="form-control" placeholder="Địa chỉ Email" required></div>
            <div class="mb-3"><input type="password" name="pass" class="form-control" placeholder="Mật khẩu" required></div>
            <div class="mb-4"><input type="password" name="repass" class="form-control" placeholder="Xác nhận mật khẩu" required></div>
            <button type="submit" class="btn btn-auth shadow">Đăng ký tài khoản</button>
        </form>

        <div class="text-center mt-4">
            <p class="small text-muted">Đã có tài khoản? <a href="login" class="text-danger fw-bold text-decoration-none">Đăng nhập</a></p>
        </div>
    </div>
</body>
</html>