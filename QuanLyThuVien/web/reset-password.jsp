<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đặt lại mật khẩu - Smart Lib</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <style>
        :root { --primary-color: #173F5F; --accent-color: #ED553B; }
        body { background: #f0f2f5; height: 100vh; display: flex; align-items: center; font-family: 'Inter', sans-serif; }
        .auth-card { border: none; border-radius: 40px; box-shadow: 0 30px 60px rgba(0,0,0,0.12); padding: 50px; max-width: 450px; width: 100%; margin: auto; background: white; }
        .form-control { border-radius: 50px; padding: 12px 25px; border: 1px solid #eee; background: #f8f9fa; }
        .btn-auth { background: var(--primary-color); color: white; border-radius: 50px; padding: 15px; font-weight: 800; width: 100%; border: none; margin-top: 20px; transition: 0.3s; text-transform: uppercase; }
        .btn-auth:hover { background: var(--accent-color); transform: translateY(-5px); }
    </style>
</head>
<body>
    <div class="auth-card text-center">
        <div class="mb-4 text-success">
            <i class="bi bi-shield-check" style="font-size: 4rem;"></i>
        </div>
        <h2 class="fw-bold" style="color: var(--primary-color);">MẬT KHẨU MỚI</h2>
        <p class="text-muted mb-4">Mã OTP chính xác! Bây giờ sếp hãy nhập mật khẩu mới nhé.</p>

        <c:if test="${not empty error}">
            <div class="alert alert-danger border-0 rounded-pill small py-2 mb-3">${error}</div>
        </c:if>

        <form action="reset-password" method="POST">
            <div class="mb-3">
                <input type="password" name="new_pass" class="form-control" placeholder="Mật khẩu mới" required>
            </div>
            <div class="mb-4">
                <input type="password" name="re_pass" class="form-control" placeholder="Xác nhận mật khẩu" required>
            </div>
            <button type="submit" class="btn btn-auth shadow">CẬP NHẬT MẬT KHẨU</button>
        </form>
    </div>
</body>
</html>