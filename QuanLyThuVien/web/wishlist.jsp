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

        .wishlist-header { padding: 40px 0; text-align: center; }
        .wishlist-header h2 { color: var(--primary-color); font-weight: 800; letter-spacing: -1px; }

        /* Card Design nâng cấp */
        .book-card { 
            border: none; 
            border-radius: 20px; 
            background: #fff; 
            transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
            position: relative;
            overflow: hidden;
            box-shadow: 0 10px 20px rgba(0,0,0,0.03);
            height: 100%;
        }
        .book-card:hover { 
            transform: translateY(-10px); 
            box-shadow: 0 20px 40px rgba(0,0,0,0.08); 
        }

        /* Container chứa ảnh để ảnh không bị to quá */
        .img-container { 
            padding: 20px; 
            background: #fdfdfd; 
            display: flex; 
            justify-content: center; 
            align-items: center;
            height: 240px;
        }
        .img-container img { 
            max-height: 100%; 
            max-width: 100%; 
            object-fit: contain;
            border-radius: 10px;
            filter: drop-shadow(0 5px 15px rgba(0,0,0,0.1));
        }

        .card-body { padding: 20px; text-align: center; }
        .book-title { 
            font-size: 1rem; 
            font-weight: 700; 
            color: #333; 
            margin-bottom: 8px;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            height: 2.4rem;
        }
        .book-price { 
            color: var(--accent-color); 
            font-weight: 800; 
            font-size: 1.1rem; 
            margin-bottom: 15px; 
        }

        /* Nút thao tác */
        .btn-view { 
            background: var(--primary-color); 
            color: white; 
            border-radius: 50px; 
            padding: 8px 20px; 
            font-size: 0.85rem; 
            font-weight: 600;
            transition: 0.3s;
            border: none;
        }
        .btn-view:hover { background: #0d2639; color: white; transform: scale(1.05); }

        /* Nút xóa kiểu Floating */
        .btn-remove {
            position: absolute;
            top: 15px;
            right: 15px;
            width: 35px;
            height: 35px;
            background: rgba(255, 255, 255, 0.9);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #ff4757;
            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
            transition: 0.3s;
            z-index: 10;
        }
        .btn-remove:hover { background: #ff4757; color: white; transform: rotate(90deg); }

        .empty-state { padding: 80px 0; text-align: center; }
    </style>
</head>
<body>
    <jsp:include page="header.jsp" />

    <div class="wishlist-header">
        <div class="container">
            <h2><i class="bi bi-heart-fill text-danger me-2"></i> DANH SÁCH YÊU THÍCH</h2>
            <p class="text-muted">Lưu giữ những cuốn sách Hải tâm đắc nhất</p>
        </div>
    </div>

    <div class="container pb-5">
        <div class="row g-4">
            <c:forEach items="${listW}" var="b">
                <div class="col-6 col-md-4 col-lg-3"> <%-- Để col-6 cho mobile, lg-3 cho PC để nó nhỏ gọn --%>
                    <div class="book-card">
                        <a href="remove-wishlist?bid=${b.id}" class="btn-remove" title="Xóa khỏi danh sách">
                            <i class="bi bi-x-lg"></i>
                        </a>

                        <div class="img-container">
                            <img src="${b.coverImage}" alt="${b.title}">
                        </div>

                        <div class="card-body">
                            <div class="book-title">${b.title}</div>
                            <div class="book-price">
                                <fmt:formatNumber value="${b.price}" pattern="#,###"/> <small>đ</small>
                            </div>
                            <a href="detail?bid=${b.id}" class="btn-view text-decoration-none">
                                <i class="bi bi-eye me-1"></i> Xem ngay
                            </a>
                        </div>
                    </div>
                </div>
            </c:forEach>

            <c:if test="${empty listW}">
                <div class="col-12">
                    <div class="empty-state">
                        <img src="https://cdn-icons-png.flaticon.com/512/743/743231.png" width="100" class="mb-4 opacity-50">
                        <h4 class="text-muted">Chưa có cuốn nào lọt vào mắt xanh của Hải hết! 😅</h4>
                        <a href="shop" class="btn btn-outline-primary rounded-pill mt-3 px-4">Đi tìm sách ngay</a>
                    </div>
                </div>
            </c:if>
        </div>
    </div>

    <jsp:include page="footer.jsp" />
</body>
</html>