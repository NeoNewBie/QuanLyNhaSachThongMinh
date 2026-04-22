<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${pageTitle} - Smart Lib</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        body { background: #F8F9FA; color: #333; }
        
        /* 🛑 1. SIDEBAR & MENU ACTIVE */
        .sidebar-filter { background: #fff; border-radius: 16px; padding: 25px; border: 1px solid #eee; position: sticky; top: 80px; }
        .filter-section-title { font-size: 0.9rem; font-weight: 800; color: #173F5F; margin-bottom: 15px; text-transform: uppercase; letter-spacing: 1px;}
        
        .cate-item { display: block; padding: 10px 15px; color: #555; font-weight: 600; font-size: 0.95rem; border-radius: 8px; text-decoration: none; margin-bottom: 5px; transition: 0.3s; }
        .cate-item:hover { color: #ED553B; background: #fffcfb; padding-left: 20px; }
        
        .cate-item.active-cate { 
            color: #ED553B; 
            background: #FFF5F3; 
            font-weight: 700; 
            border-left: 4px solid #ED553B; 
        }

        /* 🛑 2. CUSTOM SCROLLBAR CHO LIVE SEARCH & BỘ LỌC */
        #searchSuggestions::-webkit-scrollbar { width: 6px; }
        #searchSuggestions::-webkit-scrollbar-track { background: #f1f1f1; border-radius: 8px; }
        #searchSuggestions::-webkit-scrollbar-thumb { background: #c1c1c1; border-radius: 8px; }
        #searchSuggestions::-webkit-scrollbar-thumb:hover { background: #ED553B; }
        
        .custom-scroll::-webkit-scrollbar { width: 4px; }
        .custom-scroll::-webkit-scrollbar-track { background: transparent; }
        .custom-scroll::-webkit-scrollbar-thumb { background: #e0e0e0; border-radius: 10px; }

        /* Card Sách hiện đại */
        .book-card-new { background: #fff; border-radius: 16px; transition: 0.4s; border: 1px solid #eee; height: 100%; overflow: hidden; display: flex; flex-direction: column;}
        .book-card-new:hover { transform: translateY(-8px); box-shadow: 0 15px 35px rgba(0,0,0,0.08); }
        .image-box { height: 240px; background: #fdfdfd; display: flex; align-items: center; justify-content: center; padding: 20px; transition: 0.3s; }
        .book-card-new:hover .image-box img { transform: scale(1.05); transition: 0.3s; }
        .image-box img { max-height: 100%; filter: drop-shadow(0 10px 15px rgba(0,0,0,0.05)); transition: 0.3s; }
        .content-box { padding: 20px; flex-grow: 1; display: flex; flex-direction: column;}
        .book-title { font-size: 1rem; font-weight: 700; color: #222; margin-bottom: 8px; height: 2.8rem; overflow: hidden; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; transition: 0.2s;}
        .book-title:hover { color: #ED553B; }
        .book-author { font-size: 0.8rem; color: #999; margin-bottom: 10px; }
        .book-price { font-size: 1.1rem; font-weight: 800; color: #ED553B; }

        /* 🛑 NHÃN KHỚP CHƯƠNG (MỚI THÊM) */
        .matched-chapter {
            background-color: #FFF5F3;
            color: #e03131;
            padding: 4px 10px;
            border-radius: 6px;
            font-size: 0.8rem;
            display: inline-block;
            margin-bottom: 12px;
            border: 1px dashed #ffa8a8;
            font-weight: 700;
        }

        .btn-detail { display: block; text-align: center; text-decoration: none; background: #F1F3F4; color: #222; font-weight: 700; font-size: 0.85rem; border-radius: 8px; border: none; padding: 12px; width: 100%; transition: 0.3s;}
        .btn-detail:hover { background: #173F5F; color: #fff; }
        .btn-cart-add { border-radius: 8px; padding: 12px; font-size: 1.1rem; transition: 0.3s; }

        /* Phân trang */
        .custom-pagination { display: flex; justify-content: center; gap: 10px; list-style: none; padding: 0; margin-top: 50px; }
        .custom-pagination li a { width: 42px; height: 42px; display: flex; align-items: center; justify-content: center; border-radius: 50%; font-weight: 700; font-size: 1rem; text-decoration: none; transition: all 0.3s ease; }
        .custom-pagination li.page-item .page-link { background: transparent; color: #a0a0a0; border: 1px solid #e5e5e5; }
        .custom-pagination li.page-item:hover .page-link { border-color: #ED553B; color: #ED553B; }
        .custom-pagination li.page-item.active .page-link { background-color: #ED553B; color: #fff; border-color: #ED553B; box-shadow: 0 5px 15px rgba(237, 85, 59, 0.4); }
        .custom-pagination li.page-nav .page-link { color: #ED553B; border: 2px solid rgba(237, 85, 59, 0.3); background: transparent; }
        .custom-pagination li.page-nav:hover .page-link { background: #FFF5F3; border-color: #ED553B; }
        .custom-pagination li.disabled .page-link { border-color: #eee; color: #ccc; cursor: not-allowed; pointer-events: none; background: transparent; }

        .hover-bg-light:hover { background-color: #f8f9fa; }
    </style>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
    <jsp:include page="header.jsp" />

    <div class="container mt-4 mb-5">
        
        <%-- 🛑 TOP BAR --%>
        <div class="row mb-4 align-items-center bg-white p-3 rounded-4 shadow-sm border mx-0">
            <div class="col-md-8 position-relative mb-3 mb-md-0">
                <form action="shop" method="GET" class="input-group" id="searchForm">
                    <span class="input-group-text bg-light border-end-0 rounded-start-pill text-muted px-4">
                        <i class="bi bi-search"></i>
                    </span>
                    <input type="text" name="txt" id="liveSearchInput" class="form-control bg-light border-start-0 py-3 fw-bold text-primary-custom" 
                           placeholder="Sếp muốn tìm sách gì hôm nay?" autocomplete="off" value="${txtS}">
                    <button type="submit" class="btn btn-dark rounded-end-pill px-5 fw-bold">TÌM KIẾM</button>
                </form>
                
                <div id="searchSuggestions" class="position-absolute w-100 bg-white shadow-lg rounded-4 mt-2 border" 
                     style="display: none; z-index: 1050; max-height: 400px; overflow-y: auto; top: 100%;">
                </div>
            </div>

            <div class="col-md-4">
                <div class="d-flex align-items-center justify-content-end gap-2">
                    <span class="fw-bold text-muted small text-nowrap">SẮP XẾP:</span>
                    <select class="form-select rounded-pill fw-bold bg-light border-0 py-2 w-auto" onchange="window.location.href='shop?sort=' + this.value">
                        <option value="newest" ${sortS == 'newest' ? 'selected' : ''}>Mới nhất</option>
                        <option value="priceAsc" ${sortS == 'priceAsc' ? 'selected' : ''}>Giá tăng dần</option>
                        <option value="priceDesc" ${sortS == 'priceDesc' ? 'selected' : ''}>Giá giảm dần</option>
                    </select>
                </div>
            </div>
        </div>

        <div class="row g-4">
            <%-- SIDEBAR --%>
            <div class="col-lg-3">
                <div class="sidebar-filter shadow-sm">
                    <div class="mb-5">
                        <h5 class="filter-section-title"><i class="bi bi-grid me-2"></i>Danh Mục Sách</h5>
                        <div class="custom-scroll" style="max-height: 350px; overflow-y: auto; padding-right: 5px;">
                            <a href="shop" class="cate-item ${empty param.cateId and empty activeCate ? 'active-cate' : ''}">
                                <i class="bi bi-book me-2"></i> Tất cả sách
                            </a>
                            <c:forEach items="${listCC}" var="c">
                                <a href="shop?cateId=${c.id}" class="cate-item ${param.cateId == c.id ? 'active-cate' : ''}">
                                    <i class="bi bi-hash me-1"></i> ${c.name}
                                </a>
                            </c:forEach>
                        </div>
                    </div>

                    <div class="mb-4">
                        <h5 class="filter-section-title"><i class="bi bi-cash-coin me-2"></i>Mức Giá</h5>
                        <div class="d-flex flex-column gap-2">
                            <a href="shop?min=0&max=50000" class="cate-item border ${param.max == '50000' ? 'active-cate border-0' : ''}">
                                Dưới 50.000 đ
                            </a>
                            <a href="shop?min=50000&max=150000" class="cate-item border ${param.min == '50000' ? 'active-cate border-0' : ''}">
                                50.000 đ - 150.000 đ
                            </a>
                            <a href="shop?min=150000&max=9999999" class="cate-item border ${param.min == '150000' ? 'active-cate border-0' : ''}">
                                Trên 150.000 đ
                            </a>
                        </div>
                    </div>

                    <c:if test="${not empty param.cateId or not empty param.min or not empty txtS}">
                        <a href="shop" class="btn btn-outline-danger w-100 fw-bold rounded-pill mb-4">
                            <i class="bi bi-x-circle me-1"></i> XÓA BỘ LỌC
                        </a>
                    </c:if>

                    <div style="background: #FFF5F3; padding: 20px; border-radius: 16px; border: 1px dashed #ED553B;">
                        <p class="small fw-bold mb-2">Hải cần trợ giúp?</p>
                        <button type="button" onclick="toggleAIChat()" class="btn btn-sm w-100 fw-bold rounded-pill" style="background: #ED553B; color: #fff;">CHAT VỚI AI</button>
                    </div>
                </div>
            </div>

            <%-- CONTENT --%>
            <div class="col-lg-9">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h4 class="fw-bold m-0" style="color: #173F5F; text-transform: uppercase;">
                        ${pageTitle} <span class="text-danger small ms-2">(${listB.size()} kết quả)</span>
                    </h4>
                </div>

                <div class="row g-4">
                    <c:forEach items="${listB}" var="b">
                        <div class="col-md-4 col-sm-6">
                            <div class="book-card-new shadow-sm">
                                <a href="book-detail?id=${b.id}" class="text-decoration-none">
                                    <div class="image-box">
                                        <img src="${b.coverImage}" alt="${b.title}" onerror="this.src='img/default.jpg'">
                                    </div>
                                </a>
                                
                                <div class="content-box">
                                    <p class="book-author">${b.author}</p>
                                    <a href="book-detail?id=${b.id}" class="text-decoration-none">
                                        <h6 class="book-title" title="${b.title}">${b.title}</h6>
                                    </a>

                                    <%-- 🛑 ĐÂY LÀ ĐOẠN UPDATE: HIỂN THỊ TÊN CHƯƠNG KHỚP --%>
                                    <c:if test="${not empty b.description}">
                                        <div class="matched-chapter">
                                            <i class="bi bi-magic"></i> Khớp: "${b.description}"
                                        </div>
                                    </c:if>
                                    
                                    <div class="d-flex justify-content-between align-items-center mb-3">
                                        <span class="book-price"><fmt:formatNumber value="${b.price}" pattern="###,###"/> đ</span>
                                        <span class="text-warning small"><i class="bi bi-star-fill"></i> 5.0</span>
                                    </div>
                                    
                                    <div class="d-flex gap-2 mt-auto">
                                        <a href="book-detail?id=${b.id}" class="btn-detail m-0 flex-grow-1">CHI TIẾT</a>
                                        <button type="button" onclick="addToCartAsync(event, ${b.id})" class="btn btn-outline-danger btn-cart-add">
                                            <i class="bi bi-cart-plus"></i>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                    
                    <c:if test="${empty listB}">
                        <div class="col-12 text-center py-5">
                            <i class="bi bi-emoji-frown display-1 text-muted opacity-50 mb-3 d-block"></i>
                            <h4 class="text-muted fw-bold">Không tìm thấy cuốn sách nào sếp ơi!</h4>
                            <a href="shop" class="btn btn-primary mt-3 rounded-pill px-4">Tải lại trang</a>
                        </div>
                    </c:if>
                </div>

                <%-- PHÂN TRANG --%>
                <c:if test="${totalPages > 1}">
                    <nav aria-label="Page navigation">
                        <ul class="custom-pagination">
                            <li class="page-nav ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link" href="shop?category=${activeCate}&cateId=${param.cateId}&min=${param.min}&max=${param.max}&txt=${txtS}&sort=${sortS}&page=${currentPage - 1}"><i class="bi bi-arrow-left-short fs-4"></i></a>
                            </li>

                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                    <a class="page-link" href="shop?category=${activeCate}&cateId=${param.cateId}&min=${param.min}&max=${param.max}&txt=${txtS}&sort=${sortS}&page=${i}">${i}</a>
                                </li>
                            </c:forEach>

                            <li class="page-nav ${currentPage == totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="shop?category=${activeCate}&cateId=${param.cateId}&min=${param.min}&max=${param.max}&txt=${txtS}&sort=${sortS}&page=${currentPage + 1}"><i class="bi bi-arrow-right-short fs-4"></i></a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </div>
        </div>
    </div>

    <jsp:include page="footer.jsp" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
    function addToCartAsync(event, bookId) {
        if(event) event.preventDefault(); 
        fetch('add-to-cart-async?id=' + bookId)
            .then(response => response.text())
            .then(data => {
                const cartBadge = document.getElementById('cart-badge');
                if (cartBadge) cartBadge.innerText = data; 
                Swal.fire({ icon: 'success', title: 'Đã thêm vào giỏ!', showConfirmButton: false, timer: 1500, toast: true, position: 'top-end' });
            }).catch(error => console.error('Lỗi AJAX:', error));
    }

    let searchTimeout = null;
    const searchInput = document.getElementById('liveSearchInput');
    const suggestionsBox = document.getElementById('searchSuggestions');
    const searchForm = document.getElementById('searchForm');

    if(searchInput) {
        function saveSearchHistory(query) {
            if(!query) return;
            let history = JSON.parse(localStorage.getItem('smartLibHistory') || '[]');
            history = history.filter(item => item !== query);
            history.unshift(query);
            if(history.length > 5) history.pop();
            localStorage.setItem('smartLibHistory', JSON.stringify(history));
        }

        window.showDefaultSuggestions = function() {
            let history = JSON.parse(localStorage.getItem('smartLibHistory') || '[]');
            let html = '<div class="p-3">';
            if(history.length > 0) {
                html += '<div class="d-flex justify-content-between align-items-center mb-2"><span class="fw-bold text-muted small">🕒 LỊCH SỬ TÌM KIẾM</span> <span class="text-danger small fw-bold" style="cursor:pointer;" onclick="clearHistory()">Xóa</span></div>';
                html += '<div class="d-flex flex-wrap gap-2 mb-3">';
                history.forEach(h => {
                    html += `<a href="shop?txt=\${encodeURIComponent(h)}" class="badge bg-light text-dark border text-decoration-none px-3 py-2 rounded-pill hover-bg-light">\${h}</a>`;
                });
                html += '</div>';
            }
            html += '<div class="fw-bold text-muted small mb-2 mt-2">🔥 TỪ KHÓA XU HƯỚNG</div>';
            html += '<div class="d-flex flex-wrap gap-2">';
            const hotTags = ['Java', 'Kỹ năng', 'Tâm lý', 'Lập trình web', 'C#', 'SQL'];
            hotTags.forEach(tag => {
                html += `<a href="shop?txt=\${encodeURIComponent(tag)}" class="badge bg-danger-subtle text-danger border border-danger-subtle text-decoration-none px-3 py-2 rounded-pill">\${tag}</a>`;
            });
            html += '</div></div>';
            suggestionsBox.innerHTML = html;
            suggestionsBox.style.display = 'block';
        };

        window.clearHistory = function() {
            localStorage.removeItem('smartLibHistory');
            showDefaultSuggestions();
        };

        searchInput.addEventListener('focus', function() {
            if (this.value.trim().length === 0) {
                showDefaultSuggestions();
            }
        });

        searchInput.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            const query = this.value.trim();
            if (query.length === 0) {
                showDefaultSuggestions();
                return;
            }
            searchTimeout = setTimeout(() => {
                fetch('live-search?q=' + encodeURIComponent(query))
                    .then(response => response.json())
                    .then(data => {
                        suggestionsBox.innerHTML = ''; 
                        if (data.length === 0) {
                            suggestionsBox.innerHTML = '<div class="p-4 text-center text-muted fw-bold">Không tìm thấy sách sếp yêu cầu!</div>';
                        } else {
                            data.forEach(book => {
                                let priceFormat = new Intl.NumberFormat('vi-VN').format(book.price) + ' đ';
                                let item = `
                                    <a href="book-detail?id=\${book.id}" class="text-decoration-none text-dark border-bottom d-flex align-items-center p-3 hover-bg-light transition-300">
                                        <img src="\${book.image}" class="rounded me-3 shadow-sm border" style="width: 45px; height: 65px; object-fit: cover;">
                                        <div>
                                            <div class="fw-bold text-truncate" style="max-width: 300px; color: #173F5F;">\${book.title}</div>
                                            <div class="text-danger fw-bold">\${priceFormat}</div>
                                        </div>
                                    </a>
                                `;
                                suggestionsBox.innerHTML += item;
                            });
                        }
                        suggestionsBox.style.display = 'block';
                    });
            }, 300);
        });

        searchForm.addEventListener('submit', function() {
            const query = searchInput.value.trim();
            saveSearchHistory(query);
        });

        document.addEventListener('click', function(e) {
            if (!searchInput.contains(e.target) && !suggestionsBox.contains(e.target)) {
                suggestionsBox.style.display = 'none';
            }
        });
    }
</script>
</body>
</html>