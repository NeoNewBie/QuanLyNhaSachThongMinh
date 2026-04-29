<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Xác thực OTP - Smart Lib</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <style>
        :root { --primary-color: #173F5F; --accent-color: #ED553B; }
        body { background: #f0f2f5; height: 100vh; display: flex; align-items: center; font-family: 'Inter', sans-serif; }
        .auth-card { border: none; border-radius: 40px; box-shadow: 0 30px 60px rgba(0,0,0,0.12); padding: 50px; max-width: 450px; width: 100%; margin: auto; background: white; }
        .btn-auth { background: var(--primary-color); color: white; border-radius: 50px; padding: 15px; font-weight: 800; width: 100%; border: none; margin-top: 20px; transition: 0.3s; text-transform: uppercase; }
        .btn-auth:hover { background: var(--accent-color); transform: translateY(-5px); }
        
        /* Style cho 6 ô OTP */
        .otp-container { display: flex; gap: 10px; justify-content: center; }
        .otp-input-box {
            width: 50px; height: 60px; border: 2px solid #eee; border-radius: 12px;
            font-size: 1.5rem; font-weight: 800; text-align: center; background: #f8f9fa;
            color: var(--primary-color); outline: none; transition: 0.3s;
        }
        .otp-input-box:focus { border-color: var(--accent-color); background: #fff; box-shadow: 0 0 10px rgba(237, 85, 59, 0.2); }

        /* Style cho bộ đếm ngược */
        #timer { font-weight: bold; color: var(--accent-color); }
        .resend-link { 
            font-size: 0.9rem; margin-top: 15px; display: block; 
            text-decoration: none; color: #666; pointer-events: none; opacity: 0.5; 
            transition: 0.3s;
        }
        .resend-link.active { pointer-events: auto; opacity: 1; color: var(--primary-color); font-weight: bold; cursor: pointer; }
        .resend-link.active:hover { color: var(--accent-color); }
    </style>
</head>
<body>
    <div class="auth-card text-center">
        <div class="mb-4 text-warning">
            <i class="bi bi-patch-check-fill" style="font-size: 4rem;"></i>
        </div>
        <h2 class="fw-bold" style="color: var(--primary-color);">XÁC THỰC OTP</h2>
        <p class="text-muted mb-4">Nhập mã 6 số gửi về Gmail của ông nhé Hải.</p>

        <c:if test="${not empty error}">
            <div class="alert alert-danger border-0 rounded-pill small py-2 mb-3">
                <i class="bi bi-exclamation-triangle-fill me-2"></i> ${error}
            </div>
        </c:if>

        <form action="verify-otp" method="POST">
            <div class="otp-container mb-4">
                <input type="text" class="otp-input-box" maxlength="1" name="o1" id="otp1" oninput="moveNext(this, 'otp2')" required autofocus>
                <input type="text" class="otp-input-box" maxlength="1" name="o2" id="otp2" oninput="moveNext(this, 'otp3')" required>
                <input type="text" class="otp-input-box" maxlength="1" name="o3" id="otp3" oninput="moveNext(this, 'otp4')" required>
                <input type="text" class="otp-input-box" maxlength="1" name="o4" id="otp4" oninput="moveNext(this, 'otp5')" required>
                <input type="text" class="otp-input-box" maxlength="1" name="o5" id="otp5" oninput="moveNext(this, 'otp6')" required>
                <input type="text" class="otp-input-box" maxlength="1" name="o6" id="otp6" required>
            </div>
            <button type="submit" class="btn btn-auth shadow">Xác nhận mã</button>
        </form>

        <%-- Phần bộ đếm ngược & Gửi lại mã --%>
        <p class="text-muted mt-4 small">
            Không nhận được mã? Gửi lại sau: <span id="timer">01:00</span>
        </p>
        <a href="forgot-password" class="resend-link" id="resendBtn">GỬI LẠI MÃ MỚI</a>
        
        <div class="mt-4">
            <a href="login" class="text-decoration-none text-muted small">← Quay lại Đăng nhập</a>
        </div>
    </div>

    <script>
        // 1. Hàm tự động nhảy sang ô tiếp theo khi gõ
        function moveNext(current, nextId) {
            if (current.value.length >= 1) {
                document.getElementById(nextId).focus();
            }
        }

        // 2. Xử lý bộ đếm ngược 60 giây
        let timeLeft = 60; 
        const timerElement = document.getElementById('timer');
        const resendBtn = document.getElementById('resendBtn');

        const countdown = setInterval(() => {
            timeLeft--;
            let minutes = Math.floor(timeLeft / 60);
            let seconds = timeLeft % 60;
            
            // Định dạng hiển thị 00:00
            timerElement.innerText = (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;

            if (timeLeft <= 0) {
                clearInterval(countdown);
                timerElement.innerText = "00:00";
                resendBtn.classList.add('active'); // Kích hoạt nút gửi lại
                resendBtn.innerText = "GỬI LẠI MÃ NGAY";
            }
        }, 1000);
    </script>
</body>
</html>