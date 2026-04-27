<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Đăng nhập - Smart Lib</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <style>
        :root { --primary-color: #173F5F; --accent-color: #ED553B; }
        body { background: #f0f2f5; height: 100vh; display: flex; align-items: center; font-family: 'Inter', sans-serif; }
        .auth-card { border: none; border-radius: 40px; box-shadow: 0 30px 60px rgba(0,0,0,0.12); padding: 60px; max-width: 500px; width: 100%; margin: auto; background: #fff;}
        .form-control { border-radius: 50px; padding: 15px 25px; border: 1px solid #eee; background: #f8f9fa; }
        .btn-auth { background: var(--primary-color); color: white; border-radius: 50px; padding: 15px; font-weight: 800; width: 100%; border: none; margin-top: 10px; transition: 0.3s; text-transform: uppercase; }
        .btn-auth:hover { background: var(--accent-color); transform: translateY(-5px); }
    </style>
</head>
<body>
    <div class="auth-card">
        <div class="text-center mb-5">
            <h2 class="fw-bold" style="color: var(--primary-color);">ĐĂNG NHẬP</h2>
            <p class="text-muted small">Chào mừng sếp quay trở lại Smart Lib</p>
        </div>

        <c:if test="${not empty mess}">
            <div class="alert alert-danger border-0 rounded-pill small py-2 px-4 mb-4 text-center">
                <i class="bi bi-exclamation-circle-fill me-1"></i> ${mess}
            </div>
        </c:if>

        <form action="login" method="POST">
            <div class="mb-4">
                <input type="text" name="user" class="form-control" placeholder="Tên đăng nhập" value="${cookie.cUser.value}" required>
            </div>
            <div class="mb-3">
                <input type="password" name="pass" class="form-control" placeholder="Mật khẩu" value="${cookie.cPass.value}" required>
            </div>
            
            <div class="form-check ms-3 mb-4">
                <input class="form-check-input" type="checkbox" value="1" id="remember" name="remember" ${not empty cookie.cRemember ? 'checked' : ''}>
                <label class="form-check-label text-muted small fw-bold" for="remember">
                    Ghi nhớ đăng nhập
                </label>
            </div>

            <div class="d-flex justify-content-between px-2 mb-4">
                <a href="forgot.jsp" class="text-decoration-none text-muted small">Quên mật khẩu?</a>
                <a href="register.jsp" class="text-decoration-none text-danger fw-bold small">Chưa có tài khoản? Đăng ký</a>
            </div>
            <button type="submit" class="btn btn-auth shadow">Đăng nhập ngay</button>
        </form>
    </div>

    <%-- POPUP THÔNG BÁO TỪ CÁC TRANG KHÁC ĐÁ VỀ --%>
    <c:if test="${param.msg == 'reset_success'}">
        <script>
            Swal.fire({ title: 'THÀNH CÔNG!', text: 'Mật khẩu mới đã được cập nhật. Đăng nhập ngay thôi sếp Hải ơi!', icon: 'success', confirmButtonColor: '#173F5F' });
        </script>
    </c:if>
    
    <%-- 🛑 ĐÃ FIX: CHỚP ĐƯỢC MSG ĐĂNG KÝ VÀ BẬT THÔNG BÁO NÈ --%>
    <c:if test="${param.msg == 'register_success'}">
        <script>
            Swal.fire({ title: 'CHÀO MỪNG SẾP!', text: 'Đăng ký tài khoản thành công! Đăng nhập để lụm sách nào.', icon: 'success', confirmButtonColor: '#173F5F' });
        </script>
    </c:if>
</body>
</html>