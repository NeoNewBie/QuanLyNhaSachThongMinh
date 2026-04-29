<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quên mật khẩu - Smart Lib</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <style>
        :root { --primary-color: #173F5F; --accent-color: #ED553B; }
        body { background: #f0f2f5; height: 100vh; display: flex; align-items: center; font-family: 'Inter', sans-serif; }
        .auth-card { border: none; border-radius: 40px; box-shadow: 0 30px 60px rgba(0,0,0,0.12); padding: 60px; max-width: 500px; width: 100%; margin: auto; background: white; }
        .form-control { border-radius: 50px; padding: 15px 25px; border: 1px solid #eee; background: #f8f9fa; }
        .btn-auth { background: var(--primary-color); color: white; border-radius: 50px; padding: 15px; font-weight: 800; width: 100%; border: none; margin-top: 20px; transition: 0.3s; text-transform: uppercase; }
        .btn-auth:hover { background: var(--accent-color); transform: translateY(-5px); }
    </style>
</head>
<body>
    <div class="auth-card text-center shadow-lg">
    <div class="mb-4">
        <i class="bi bi-shield-lock-fill" style="font-size: 4rem; color: var(--accent-color);"></i>
    </div>
    <h2 class="fw-bold" style="color: var(--primary-color);">QUÊN MẬT KHẨU?</h2>
    <p class="text-muted mb-4">Nhập Email của ông, tôi sẽ gửi mã OTP xác nhận về máy nhé Hải.</p>
    
    <%-- 🛑 SỬA LẠI: Chỉ hiện nguyên cả cái khung này khi có biến thôi sếp nhé --%>
    <c:if test="${not empty error}">
        <div class="alert alert-danger border-0 rounded-pill py-2 px-4 mb-4 d-flex align-items-center justify-content-center">
            <i class="bi bi-exclamation-circle-fill me-2"></i>
            <span class="small fw-bold">${error}</span>
        </div>
    </c:if>

    <form action="forgot-password" method="POST">
        <div class="mb-4">
            <div class="input-group">
                <span class="input-group-text bg-light border-0 rounded-start-pill ps-4">
                    <i class="bi bi-envelope-at text-muted"></i>
                </span>
                <input type="email" name="email" class="form-control border-0 bg-light rounded-end-pill py-3" 
                       placeholder="Địa chỉ Email của ông..." required>
            </div>
        </div>
        <button type="submit" class="btn btn-auth shadow-sm w-100 py-3">GỬI MÃ XÁC NHẬN</button>
    </form>
    
    <div class="mt-4 border-top pt-3">
        <a href="login" class="text-decoration-none text-muted small fw-bold">
            <i class="bi bi-arrow-left me-1"></i> QUAY LẠI ĐĂNG NHẬP
        </a>
    </div>
</div>
</body>
</html>