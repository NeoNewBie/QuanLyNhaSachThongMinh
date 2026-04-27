<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>500 - Lỗi Máy Chủ | Smart Lib</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <style>
        :root { --primary-color: #173F5F; --accent-color: #ED553B; }
        body { background: #f0f2f5; height: 100vh; display: flex; align-items: center; font-family: 'Inter', sans-serif; }
        .error-card { border: none; border-radius: 40px; box-shadow: 0 30px 60px rgba(0,0,0,0.12); padding: 60px; max-width: 600px; width: 100%; margin: auto; background: white; text-align: center; }
        .error-code { font-size: 8rem; font-weight: 900; color: var(--accent-color); line-height: 1; margin-bottom: 20px; }
        .btn-home { background: var(--primary-color); color: white; border-radius: 50px; padding: 15px 40px; font-weight: 800; border: none; transition: 0.3s; text-transform: uppercase; text-decoration: none; display: inline-block; }
        .btn-home:hover { background: var(--accent-color); transform: translateY(-5px); color: white; }
    </style>
</head>
<body>
    <div class="error-card">
        <div class="error-code">500</div>
        <h2 class="fw-bold mb-3" style="color: var(--primary-color);">Ối! Server Đang Bốc Khói 🔥</h2>
        <p class="text-muted mb-5 fs-5">Hệ thống đang gặp trục trặc kỹ thuật. Sếp Hải pha ly cà phê đợi một lát rồi thử lại nhé!</p>
        <a href="home" class="btn-home shadow-sm"><i class="bi bi-house-door-fill me-2"></i>Quay Về Trang Chủ</a>
    </div>
</body>
</html>