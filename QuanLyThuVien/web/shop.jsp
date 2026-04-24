<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${pageTitle} - Smart Lib</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        body { background: #F8F9FA; color: #333; }
        
        /* SIDEBAR & MENU ACTIVE */
        .sidebar-filter { background: #fff; border-radius: 16px; padding: 25px; border: 1px solid #eee; position: sticky; top: 80px; }
        .filter-section-title { font-size: 0.9rem; font-weight: 800; color: #173F5F; margin-bottom: 15px; text-transform: uppercase; letter-spacing: 1px;}
        
        .cate-item { display: block; padding: 10px 15px; color: #555; font-weight: 600; font-size: 0.95rem; border-radius: 8px; text-decoration: none; margin-bottom: 5px; transition: 0.3s; cursor: pointer; }
        .cate-item:hover { color: #ED553B; background: #fffcfb; padding-left: 20px; }
        
        .cate-item.active-cate { color: #ED553B; background: #FFF5F3; font-weight: 700; border-left: 4px solid #ED553B; }

        /* 🛑 SORT BAR SHOPEE STYLE */
        .sort-bar { background: #fff; padding: 12px 20px; border-radius: 12px; border: 1px solid #eee; display: flex; align-items: center; margin-bottom: 25px; }
        .sort-label { font-size: 0.9rem; font-weight: 700; color: #777; margin-right: 15px; }
        .btn-sort { background: #f1f3f4; color: #444; font-weight: 600; font-size: 0.9rem; border: none; padding: 8px 20px; border-radius: 8px; margin-right: 10px; transition: 0.2s; }
        .btn-sort:hover { background: #e2e6ea; }
        .btn-sort.active { background: var(--primary-color); color: #fff; box-shadow: 0 4px 10px rgba(23, 63, 95, 0.2); }
        .sort-select { background: #f1f3f4; border: none; font-weight: 600; font-size: 0.9rem; color: #444; border-radius: 8px; padding: 8px 30px 8px 15px; cursor: pointer; outline: none; }
        .sort-select.active { background: var(--primary-color); color: white; }

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
        .custom-pagination li.disabled .page-link { border-color: #eee; color: #ccc; pointer-events: none; }
    </style>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
    <jsp:include page="header.jsp" />

    <%-- 🛑 BẢO BỐI: FORM ẨN LƯU TRỮ TRÍ NHỚ BỘ LỌC --%>
    <form id="masterFilterForm" action="shop" method="GET" class="d-none">
        <input type="hidden" name="category" value="${activeCate}">
        <input type="hidden" name="cateId" id="formCateId" value="${cateIdS}">
        <input type="hidden" name="txt" id="formTxt" value="${txtS}">
        <input type="hidden" name="min" id="formMin" value="${minS}">
        <input type="hidden" name="max" id="formMax" value="${maxS}">
        <input type="hidden" name="sort" id="formSort" value="${sortS}">
        <input type="hidden" name="page" id="formPage" value="${currentPage}">
    </form>

    <div class="container mt-4 mb-5">
        <%-- SEARCH BAR --%>
        <div class="row mb-4 align-items-center bg-white p-3 rounded-4 shadow-sm border mx-0">
            <div class="col-12 position-relative">
                <div class="input-group">
                    <span class="input-group-text bg-light border-end-0 rounded-start-pill text-muted px-4"><i class="bi bi-search"></i></span>
                    <input type="text" id="liveSearchInput" class="form-control bg-light border-start-0 py-3 fw-bold" placeholder="Tìm tên sách, tác giả..." value="${txtS}" onkeypress="if(event.key === 'Enter') executeSearch(this.value)">
                    <button type="button" class="btn btn-dark rounded-end-pill px-5 fw-bold" onclick="executeSearch(document.getElementById('liveSearchInput').value)">TÌM KIẾM</button>
                </div>
            </div>
        </div>

        <div class="row g-4">
            <%-- SIDEBAR LỌC --%>
            <div class="col-lg-3">
                <div class="sidebar-filter shadow-sm">
                    <div class="mb-5">
                        <h5 class="filter-section-title"><i class="bi bi-grid me-2"></i>Danh Mục</h5>
                        <div style="max-height: 350px; overflow-y: auto;">
                            <a onclick="updateFilter('cateId', '')" class="cate-item ${empty cateIdS ? 'active-cate' : ''}"><i class="bi bi-book me-2"></i> Tất cả sách</a>
                            <c:forEach items="${listCC}" var="c">
                                <a onclick="updateFilter('cateId', '${c.id}')" class="cate-item ${cateIdS == c.id ? 'active-cate' : ''}"><i class="bi bi-hash me-1"></i> ${c.name}</a>
                            </c:forEach>
                        </div>
                    </div>

                    <div class="mb-4">
                        <h5 class="filter-section-title"><i class="bi bi-cash-coin me-2"></i>Mức Giá</h5>
                        <div class="d-flex flex-column gap-2">
                            <a onclick="updatePrice('0', '50000')" class="cate-item border ${maxS == '50000' ? 'active-cate border-0' : ''}">Dưới 50.000 đ</a>
                            <a onclick="updatePrice('50000', '150000')" class="cate-item border ${minS == '50000' ? 'active-cate border-0' : ''}">50.000 đ - 150.000 đ</a>
                            <a onclick="updatePrice('150000', '9999999')" class="cate-item border ${minS == '150000' ? 'active-cate border-0' : ''}">Trên 150.000 đ</a>
                        </div>
                    </div>

                    <c:if test="${not empty cateIdS or not empty minS or not empty txtS}">
                        <a href="shop" class="btn btn-outline-danger w-100 fw-bold rounded-pill mb-4"><i class="bi bi-x-circle me-1"></i> XÓA BỘ LỌC</a>
                    </c:if>
                </div>
            </div>

            <%-- CONTENT SÁCH --%>
            <div class="col-lg-9">
                <h4 class="fw-bold mb-3" style="color: #173F5F; text-transform: uppercase;">
                    ${pageTitle} <span class="text-danger small ms-2">(${listB.size()} kết quả)</span>
                </h4>

                <%-- 🛑 SORT BAR CHUẨN SHOPEE --%>
                <c:if test="${activeCate != 'ebook'}">
                    <div class="sort-bar shadow-sm">
                        <span class="sort-label">Sắp xếp theo</span>
                        <button onclick="updateFilter('sort', 'popular')" class="btn-sort ${sortS == 'popular' ? 'active' : ''}">Phổ biến</button>
                        <button onclick="updateFilter('sort', 'newest')" class="btn-sort ${sortS == 'newest' ? 'active' : ''}">Mới nhất</button>
                        <button onclick="updateFilter('sort', 'bestseller')" class="btn-sort ${sortS == 'bestseller' ? 'active' : ''}">Bán chạy</button>
                        
                        <select class="sort-select ms-auto ${fn:contains(sortS, 'price') ? 'active' : ''}" onchange="updateFilter('sort', this.value)">
                            <option value="" disabled ${!fn:contains(sortS, 'price') ? 'selected' : ''}>Giá</option>
                            <option value="priceAsc" ${sortS == 'priceAsc' ? 'selected' : ''}>Giá: Thấp đến Cao</option>
                            <option value="priceDesc" ${sortS == 'priceDesc' ? 'selected' : ''}>Giá: Cao đến Thấp</option>
                        </select>
                    </div>
                </c:if>

                <div class="row g-4">
                    <c:forEach items="${listB}" var="b">
                        <div class="col-md-4 col-sm-6">
                            <div class="book-card-new shadow-sm">
                                <a href="book-detail?id=${b.id}" class="text-decoration-none">
                                    <div class="image-box"><img src="${b.coverImage}" onerror="this.src='img/default.jpg'"></div>
                                </a>
                                <div class="content-box">
                                    <p class="book-author">${b.author}</p>
                                    <a href="book-detail?id=${b.id}" class="text-decoration-none"><h6 class="book-title">${b.title}</h6></a>
                                    <div class="d-flex justify-content-between align-items-center mb-3">
                                        <span class="book-price"><fmt:formatNumber value="${b.price}" pattern="###,###"/> đ</span>
                                    </div>
                                    <div class="d-flex gap-2 mt-auto">
                                        <a href="book-detail?id=${b.id}" class="btn-detail m-0 flex-grow-1">CHI TIẾT</a>
                                        <button onclick="addToCartAsync(event, ${b.id})" class="btn btn-outline-danger btn-cart-add"><i class="bi bi-cart-plus"></i></button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                    <c:if test="${empty listB}">
                        <div class="col-12 text-center py-5">
                            <i class="bi bi-emoji-frown display-1 text-muted opacity-50 d-block mb-3"></i>
                            <h4 class="text-muted fw-bold">Không tìm thấy sách theo bộ lọc!</h4>
                            <button onclick="window.location.href='shop'" class="btn btn-primary mt-3 rounded-pill px-4">Xóa lọc</button>
                        </div>
                    </c:if>
                </div>

                <%-- PHÂN TRANG --%>
                <c:if test="${totalPages > 1}">
                    <nav class="mt-5">
                        <ul class="custom-pagination">
                            <li class="page-nav ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link" onclick="updateFilter('page', ${currentPage - 1})"><i class="bi bi-arrow-left-short fs-4"></i></a>
                            </li>
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                    <a class="page-link" onclick="updateFilter('page', ${i})">${i}</a>
                                </li>
                            </c:forEach>
                            <li class="page-nav ${currentPage == totalPages ? 'disabled' : ''}">
                                <a class="page-link" onclick="updateFilter('page', ${currentPage + 1})"><i class="bi bi-arrow-right-short fs-4"></i></a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </div>
        </div>
    </div>

    <jsp:include page="footer.jsp" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <%-- SCRIPT BỘ LỌC MASTER --%>
    <script>
        function updateFilter(key, value) {
            document.getElementById('form' + key.charAt(0).toUpperCase() + key.slice(1)).value = value;
            if(key !== 'page') document.getElementById('formPage').value = '1';
            document.getElementById('masterFilterForm').submit();
        }
        function updatePrice(min, max) {
            document.getElementById('formMin').value = min;
            document.getElementById('formMax').value = max;
            document.getElementById('formPage').value = '1';
            document.getElementById('masterFilterForm').submit();
        }
        function executeSearch(txt) {
            document.getElementById('formTxt').value = txt;
            document.getElementById('formPage').value = '1';
            document.getElementById('masterFilterForm').submit();
        }
        function addToCartAsync(event, bookId) {
            event.preventDefault(); 
            fetch('add-to-cart-async?id=' + bookId).then(r => r.text()).then(data => {
                document.getElementById('cart-badge').innerText = data; 
                Swal.fire({ icon: 'success', title: 'Đã thêm vào giỏ!', showConfirmButton: false, timer: 1500, toast: true, position: 'top-end' });
            });
        }
    </script>
</body>
</html>