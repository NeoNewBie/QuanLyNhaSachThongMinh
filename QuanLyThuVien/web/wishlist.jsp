<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Sách Yêu Thích - Smart Lib</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <style>
        :root { --primary-color: #173F5F; --accent-color: #ED553B; --bg-light: #F8F9FA; }
        body { background-color: var(--bg-light); font-family: 'Inter', sans-serif; }

        .wishlist-header { padding: 50px 0 30px; text-align: center; }
        .wishlist-header h2 { color: var(--primary-color); font-weight: 900; letter-spacing: -0.5px; }

        /* Card Design phong cách hiện đại */
        .book-card { 
            border: 1px solid rgba(0,0,0,0.05); 
            border-radius: 16px; 
            background: #fff; 
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
            height: 100%;
            display: flex;
            flex-direction: column;
        }
        .book-card:hover { 
            transform: translateY(-8px); 
            box-shadow: 0 15px 30px rgba(0,0,0,0.1); 
            border-color: rgba(237, 85, 59, 0.3);
        }

        /* Vùng chứa ảnh xịn xò */
        .img-container { 
            padding: 25px; 
            background: linear-gradient(180deg, #f8f9fa 0%, #ffffff 100%); 
            display: flex; 
            justify-content: center; 
            align-items: center;
            height: 260px;
            border-bottom: 1px solid rgba(0,0,0,0.03);
        }
        .img-container img { 
            max-height: 100%; 
            max-width: 100%; 
            object-fit: cover;
            border-radius: 6px;
            box-shadow: -5px 5px 15px rgba(0,0,0,0.15);
            transition: 0.3s;
        }
        .book-card:hover .img-container img { transform: scale(1.05); }

        .card-body { padding: 20px; text-align: center; display: flex; flex-direction: column; flex: 1; }
        .book-title { 
            font-size: 1.05rem; 
            font-weight: 700; 
            color: #2b2b2b; 
            margin-bottom: 10px;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            height: 2.8rem;
            line-height: 1.4;
        }
        .book-price { 
            color: var(--accent-color); 
            font-weight: 800; 
            font-size: 1.25rem; 
            margin-top: auto;
            margin-bottom: 15px; 
        }

        /* Nút xem chi tiết */
        .btn-view { 
            background: var(--primary-color); 
            color: white; 
            border-radius: 8px; 
            padding: 10px 20px; 
            font-size: 0.9rem; 
            font-weight: 600;
            transition: 0.3s;
            border: none;
            width: 100%;
        }
        .btn-view:hover { background: #0d2639; color: white; }

        /* Nút xóa Yêu thích */
        .btn-remove {
            position: absolute;
            top: 12px;
            right: 12px;
            width: 32px;
            height: 32px;
            background: #fff;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #ccc;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            transition: 0.3s;
            z-index: 10;
        }
        .btn-remove:hover { background: #ff4757; color: white; transform: scale(1.1); box-shadow: 0 4px 12px rgba(255, 71, 87, 0.3); }

        .empty-state { padding: 80px 0; text-align: center; }
    </style>
</head>
<body>
    <jsp:include page="header.jsp" />

    <div class="wishlist-header">
        <div class="container">
            <h2><i class="bi bi-bookmark-heart-fill text-danger me-2"></i> KHO SÁCH TÂM ĐẮC</h2>
            <p class="text-muted fs-6">Nơi lưu giữ những cuốn sách sếp Hải định rinh về nhà</p>
        </div>
    </div>

    <div class="container pb-5">
        <div class="row g-4 justify-content-center">
            <c:forEach items="${listW}" var="b">
                <div class="col-6 col-md-4 col-lg-3"> 
                    <div class="book-card">
                        <a href="remove-wishlist?bid=${b.id}" class="btn-remove" title="Xóa khỏi danh sách">
                            <i class="bi bi-trash3-fill"></i>
                        </a>

                        <div class="img-container">
                            <img src="${b.coverImage}" alt="${b.title}" onerror="this.src='https://placehold.co/150x220?text=No+Cover'">
                        </div>

                        <div class="card-body">
                            <div class="book-title">${b.title}</div>
                            <div class="book-price">
                                <fmt:formatNumber value="${b.price}" pattern="#,###"/> <small class="text-muted fs-6">đ</small>
                            </div>
                            
                            <%-- 🛑 ĐÃ FIX 404: Đổi từ detail?bid= sang book-detail?id= cho khớp với dự án của sếp --%>
                            <a href="book-detail?id=${b.id}" class="btn-view text-decoration-none">
                                Xem chi tiết <i class="bi bi-arrow-right ms-1"></i>
                            </a>
                        </div>
                    </div>
                </div>
            </c:forEach>

            <c:if test="${empty listW}">
                <div class="col-12">
                    <div class="empty-state bg-white rounded-4 shadow-sm border">
                        <img src="https://cdn-icons-png.flaticon.com/512/743/743231.png" width="100" class="mb-4 opacity-50">
                        <h4 class="text-muted fw-bold">Chưa có cuốn nào lọt vào mắt xanh của Hải hết! 😅</h4>
                        <p class="text-secondary">Hãy dạo quanh cửa hàng và thả tim cho cuốn sách bạn thích nhé.</p>
                        <a href="shop" class="btn btn-primary rounded-pill mt-3 px-5 py-2 fw-bold shadow-sm">Khám Phá Ngay</a>
                    </div>
                </div>
            </c:if>
        </div>
    </div>

    <jsp:include page="footer.jsp" />
</body>
</html>