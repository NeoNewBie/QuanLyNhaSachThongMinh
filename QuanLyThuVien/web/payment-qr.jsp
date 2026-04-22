<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thanh Toán Tự Động - Smart Lib</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <style>
        /* Nền Gradient hòa hợp, không gây rối mắt */
        body {
            background: linear-gradient(135deg, #173F5F 0%, #242424 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Inter', sans-serif;
            color: #fff;
            margin: 0;
        }

        /* Hiệu ứng kính mờ cho Card thanh toán */
        .payment-card {
            background: rgba(255, 255, 255, 0.05);
            backdrop-filter: blur(15px);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 30px;
            padding: 40px;
            width: 100%;
            max-width: 450px;
            text-align: center;
            box-shadow: 0 25px 50px rgba(0,0,0,0.3);
        }

        .qr-wrapper {
            background: #fff;
            padding: 15px;
            border-radius: 20px;
            display: inline-block;
            margin-bottom: 25px;
            box-shadow: 0 10px 20px rgba(0,0,0,0.2);
            position: relative;
        }

        .qr-wrapper img {
            width: 250px;
            height: 250px;
            border-radius: 10px;
        }

        .price-tag {
            font-size: 2rem;
            font-weight: 800;
            color: #ED553B;
            margin-bottom: 10px;
        }

        .status-loading {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
            color: #aaa;
            font-weight: 500;
            margin-top: 20px;
        }

        /* Hiệu ứng mạch đập cho QR để báo hiệu hệ thống đang "thức" */
        @keyframes pulse {
            0% { transform: scale(1); opacity: 1; }
            50% { transform: scale(1.02); opacity: 0.8; }
            100% { transform: scale(1); opacity: 1; }
        }
        .qr-active { animation: pulse 2s infinite ease-in-out; }

    </style>
</head>
<body>
    <div class="payment-card shadow-lg">
        <div class="mb-4">
            <h4 class="fw-bold mb-1">MỞ KHÓA TRUYỆN VIP</h4>
            <p class="small text-muted">Hệ thống tự động duyệt sau 3-5 giây</p>
        </div>

        <div class="price-tag">
            <fmt:formatNumber value="${totalAmount}" pattern="###,###"/> đ
        </div>

        <div class="qr-wrapper qr-active">
            <img src="https://img.vietqr.io/image/MB-0869720489-compact2.png?amount=${totalAmount}&addInfo=${orderId}&accountName=LE%20HOANG%20HAI" 
                 alt="Quét mã để đọc truyện">
        </div>

        <div class="status-loading" id="payment-checker">
            <div class="spinner-grow spinner-grow-sm text-warning" role="status"></div>
            <span>Đang chờ sếp chuyển khoản...</span>
        </div>

        <div class="mt-4 pt-3 border-top border-secondary">
            <p class="small text-muted mb-0">Nội dung chuyển khoản:</p>
            <p class="fw-bold text-warning">${orderId}</p>
        </div>
        
        <a href="book-detail?id=${detail.id}" class="btn btn-link btn-sm text-decoration-none text-muted mt-3">Hủy thanh toán</a>
    </div>

    <script>
        // Tuyệt đối không có nút bấm, dùng Script để soi tiền tự động
        const orderId = "${orderId}";
        const bookId = "${detail.id}";
        
        const checkPayment = setInterval(function() {
            fetch('check-payment-status?orderId=' + encodeURIComponent(orderId))
                .then(res => res.text())
                .then(status => {
                    if (status.trim() === "success") {
                        // Dừng kiểm tra ngay khi thấy tiền vào
                        clearInterval(checkPayment);
                        
                        // Cập nhật UI báo thành công trước khi nhảy trang
                        document.getElementById('payment-checker').innerHTML = 
                            '<i class="bi bi-check-circle-fill text-success fs-4"></i> <span class="text-success fw-bold">Giao dịch thành công!</span>';

                        // Nổ Ting Ting đẹp mắt bằng SweetAlert2
                        Swal.fire({
                            title: 'Ting Ting!',
                            text: 'Smart Lib đã nhận được tiền. Đọc truyện thôi sếp Hải ơi!',
                            icon: 'success',
                            background: '#242424',
                            color: '#fff',
                            showConfirmButton: false,
                            timer: 2500,
                            timerProgressBar: true
                        }).then(() => {
                            // Tự động đá sếp về trang đọc truyện ngay lập tức
                            window.location.href = "read?id=" + bookId;
                        });
                    }
                })
                .catch(err => console.log("Hệ thống đang bận, sếp đợi xíu..."));
        }, 3000); // 3 giây soi một lần
    </script>
</body>
</html>