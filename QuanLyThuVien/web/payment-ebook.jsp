<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thanh toán Truyện VIP - Smart Library</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <style>
        /* Đồng bộ tông màu chủ đạo của Smart Lib */
        :root { --primary-color: #173F5F; --accent-color: #ED553B; }
        body { font-family: 'Inter', sans-serif; background-color: #f4f7f6; color: #333; }
        
        .payment-card { 
            max-width: 600px; 
            margin: 50px auto; 
            background: #fff; 
            border-radius: 24px; 
            overflow: hidden; 
            box-shadow: 0 15px 40px rgba(0,0,0,0.06); 
            border: none; 
        }
        
        /* Banner Gradient sang trọng */
        .payment-header { 
            background: linear-gradient(135deg, var(--primary-color) 0%, #2A608B 100%); 
            color: white; 
            padding: 35px 20px; 
            text-align: center; 
        }
        
        /* Khung chứa thông tin sách */
        .book-info-box { 
            background: #f8f9fa; 
            border-radius: 16px; 
            padding: 20px; 
            border: 1px solid #eee; 
        }
        
        /* Khung chứa mã QR */
        .qr-wrapper { 
            background: #fff; 
            padding: 20px; 
            border-radius: 24px; 
            display: inline-block; 
            border: 2px dashed #ccc; 
            transition: 0.3s;
        }
        .qr-wrapper:hover { border-color: var(--accent-color); }
        
        /* Nút xác nhận thanh toán */
        .btn-confirm { 
            background: var(--accent-color); 
            color: white; 
            border-radius: 50px; 
            padding: 16px; 
            font-weight: 600; 
            border: none; 
            transition: 0.3s; 
            width: 100%; 
            letter-spacing: 0.5px; 
        }
        .btn-confirm:hover { background: #d4442d; transform: translateY(-2px); color: white; box-shadow: 0 10px 20px rgba(237, 85, 59, 0.2); }
        
        .btn-cancel { color: #888; text-decoration: none; font-weight: 500; font-size: 0.9rem; transition: 0.2s; display: inline-block; }
        .btn-cancel:hover { color: var(--accent-color); }
    </style>
</head>
<body>
    <jsp:include page="header.jsp" />

    <div class="container">
        <div class="payment-card">
            <div class="payment-header">
                <i class="bi bi-gem fs-1 mb-2 text-warning d-block"></i>
                <h4 class="fw-bold mb-0">MỞ KHÓA TRUYỆN VIP</h4>
            </div>
            
            <div class="card-body p-4 p-md-5">
                <div class="book-info-box d-flex align-items-center mb-4">
                    <img src="${detail.coverImage}" class="rounded-3 shadow-sm me-4" style="width: 80px; height: 115px; object-fit: cover;">
                    <div class="flex-grow-1">
                        <h6 class="fw-bold mb-1 text-dark" style="font-size: 1.1rem; line-height: 1.4;">${detail.title}</h6>
                        <p class="text-muted small mb-3">Tác giả: ${detail.author}</p>
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="badge bg-white text-secondary border">Số lượng: 1 chương</span>
                            <span class="fw-bold text-danger fs-5"><fmt:formatNumber value="${detail.price}" pattern="###,###"/> đ</span>
                        </div>
                    </div>
                </div>

                <div class="text-center mb-4 mt-2">
                    <p class="fw-bold text-muted mb-3 small text-uppercase">Quét mã QR để thanh toán</p>
                    <div class="qr-wrapper shadow-sm">
                        <img src="https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=ChuyenKhoan-${detail.id}-${detail.price}&margin=10" alt="QR Code" style="width: 220px; height: 220px;">
                    </div>
                    <p class="mt-3 small text-muted px-4">Hệ thống sẽ tự động đối soát và mở khóa truyện ngay sau khi nhận được thanh toán.</p>
                </div>

                <form action="payment-ebook" method="POST">
                    <input type="hidden" name="txt_id_sach" value="${detail.id}">
                    <input type="hidden" name="txt_gia_tien" value="${detail.price}">
                    <button type="submit" class="btn-confirm">
                        <i class="bi bi-check-circle-fill me-2"></i> TÔI ĐÃ CHUYỂN KHOẢN THÀNH CÔNG
                    </button>
                </form>
                
                <div class="text-center mt-4">
                    <a href="book-detail?id=${detail.id}" class="btn-cancel">
                        <i class="bi bi-arrow-left me-1"></i> Quay lại hoặc hủy giao dịch
                    </a>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="footer.jsp" />
</body>
</html>