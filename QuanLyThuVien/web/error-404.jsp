<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>404 - Không tìm thấy trang | Smart Lib</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <style>
        :root { --primary-color: #173F5F; --accent-color: #ED553B; }
        body { background: #f0f2f5; height: 100vh; display: flex; align-items: center; font-family: 'Inter', sans-serif; }
        .error-card { border: none; border-radius: 40px; box-shadow: 0 30px 60px rgba(0,0,0,0.12); padding: 60px; max-width: 600px; width: 100%; margin: auto; background: white; text-align: center; }
        .error-code { font-size: 8rem; font-weight: 900; color: var(--primary-color); line-height: 1; margin-bottom: 20px; }
        .btn-home { background: var(--accent-color); color: white; border-radius: 50px; padding: 15px 40px; font-weight: 800; border: none; transition: 0.3s; text-transform: uppercase; text-decoration: none; display: inline-block; }
        .btn-home:hover { background: var(--primary-color); transform: translateY(-5px); color: white; }
    </style>
</head>
<body>
    <div class="error-card">
        <div class="error-code">404</div>
        <h2 class="fw-bold mb-4" style="color: var(--primary-color);">ỐI! ĐI LẠC RỒI SẾP HẢI ƠI</h2>
        <p class="text-muted mb-5">Cái trang này nó bay màu hoặc chưa được xây dựng rồi. Sếp kiểm tra lại đường link hoặc quay về trang chủ để tiếp tục hành trình nhé!</p>
        
        <a href="index.jsp" class="btn-home shadow-lg">
            <i class="bi bi-house-door-fill me-2"></i> VỀ TRANG CHỦ NGAY
        </a>
    </div>
</body>
</html>