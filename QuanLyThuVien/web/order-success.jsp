<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đặt hàng thành công - Smart Library</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <style>
        body { background-color: #f8f9fa; }
        .success-card { border-radius: 20px; border: none; box-shadow: 0 10px 30px rgba(0,0,0,0.1); }
        .icon-box { width: 100px; height: 100px; background: #d4edda; color: #28a745; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 3rem; margin: 0 auto 20px; }
    </style>
</head>
<body>
    <jsp:include page="header.jsp" />
    <div class="container mt-5 mb-5 text-center">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card success-card p-5">
                    <div class="icon-box animate__animated animate__zoomIn">
                        <i class="bi bi-check-lg"></i>
                    </div>
                    <h2 class="fw-bold text-dark">ĐẶT HÀNG THÀNH CÔNG!</h2>
                    <p class="text-muted fs-5 mt-3">Cảm ơn sếp Hải đã ủng hộ Smart Library. Đơn hàng của sếp đã được hệ thống ghi nhận và đang chờ xử lý.</p>
                    
                    <div class="mt-4 d-grid gap-3">
                        <a href="orders" class="btn btn-primary btn-lg rounded-pill fw-bold py-3 shadow">XEM LỊCH SỬ ĐƠN HÀNG</a>
                        <a href="home" class="btn btn-outline-secondary btn-lg rounded-pill fw-bold py-3">TIẾP TỤC MUA SÁCH</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <jsp:include page="footer.jsp" />
</body>
</html>