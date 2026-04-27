<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>${detail.title} - Smart Library</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <style>
        :root { --primary-color: #173F5F; --accent-color: #ED553B; --light-gray: #f8f9fa; }
        body { font-family: 'Inter', sans-serif; background-color: #fff; }
        .book-detail-img { border-radius: 20px; box-shadow: 0 15px 35px rgba(0,0,0,0.1); padding: 10px; background: #fff; max-height: 500px; object-fit: contain; }
        .price-tag { font-size: 2.2rem; color: var(--accent-color); font-weight: 800; }
        .btn-custom { border-radius: 50px; padding: 12px 25px; font-weight: bold; transition: 0.3s; }
        .qty-selector { display: flex; align-items: center; background: #f1f3f4; border-radius: 50px; padding: 5px; width: fit-content; border: 1px solid #e0e0e0; }
        .qty-btn-detail { width: 35px; height: 35px; border-radius: 50%; border: none; background: #fff; color: var(--primary-color); display: flex; align-items: center; justify-content: center; transition: 0.3s; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
        .qty-btn-detail:hover { background: var(--accent-color); color: #fff; }
        .qty-input-detail { width: 50px; border: none; text-align: center; font-weight: 800; background: transparent; outline: none; font-size: 1.1rem; }
        .related-card { transition: 0.3s; border: none; overflow: hidden; }
        .related-card:hover { transform: translateY(-10px); }
        .related-img { height: 220px; object-fit: cover; border-radius: 15px; }
    </style>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
    <jsp:include page="header.jsp" />

    <div class="container mt-5 mb-5">
        <div class="row g-5">
            <div class="col-md-5 text-center">
                <c:set var="mainImg" value="${detail.coverImage.startsWith('http') ? detail.coverImage : pageContext.request.contextPath.concat('/').concat(detail.coverImage)}" />
                <img src="${mainImg}" class="img-fluid book-detail-img" alt="${detail.title}">
            </div>

            <div class="col-md-7">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <span class="badge ${detail.isEbook == 1 ? 'bg-info' : 'bg-success'} mb-3 p-2 px-3 rounded-pill text-uppercase">
                            <i class="bi ${detail.isEbook == 1 ? 'bi-tablet' : 'bi-book'} me-1"></i>
                            ${detail.isEbook == 1 ? 'Ebook' : 'Sách giấy'}
                        </span>
                        <%-- 🛑 CHECK STOCK: Hiện nhãn trên tiêu đề --%>
                        <c:choose>
                            <c:when test="${detail.isEbook == 1}">
                                <span class="badge bg-light text-primary border ms-2 rounded-pill px-3">Sẵn sàng đọc</span>
                            </c:when>
                            <c:when test="${detail.stock > 0}">
                                <span class="badge bg-light text-dark border ms-2 rounded-pill px-3">Còn hàng: ${detail.stock}</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-danger ms-2 rounded-pill px-3 text-white">HẾT HÀNG</span>
                            </c:otherwise>
                        </c:choose>
                        <h1 class="fw-bold display-5 book-title mb-0">${detail.title}</h1>
                    </div>
                    <a href="add-wishlist?bid=${detail.id}" class="text-decoration-none">
                        <i class="bi bi-heart-fill fs-3" style="color: var(--accent-color);"></i>
                    </a>
                </div>

                <p class="text-muted fs-5 mt-2 mb-4">Tác giả: <span class="text-dark fw-bold">${detail.author}</span></p>
                <div class="price-tag mb-4"><fmt:formatNumber value="${detail.price}" pattern="###,###"/> đ</div>
                
                <%-- BỘ CHỌN SỐ LƯỢNG --%>
                <div class="d-flex align-items-center gap-3 mb-4">
                    <span class="fw-bold text-muted small text-uppercase">Số lượng:</span>
                    <div class="qty-selector shadow-sm">
                        <button class="qty-btn-detail" onclick="changeDetailQty(-1)"><i class="bi bi-dash-lg"></i></button>
                        <input type="text" id="detail-qty" value="1" class="qty-input-detail" readonly>
                        <button class="qty-btn-detail" onclick="changeDetailQty(1)"><i class="bi bi-plus-lg"></i></button>
                    </div>
                </div>

                <div class="p-4 bg-light rounded-4 mb-5 border-start border-4 border-primary shadow-sm">
                    <h6 class="fw-bold text-uppercase text-muted small mb-2">Tóm tắt nội dung</h6>
                    <p class="mb-0 text-secondary" style="line-height: 1.8;">${detail.description}</p>
                </div>

                <div class="d-flex flex-wrap gap-3 mb-5">
                    <c:choose>
                        <c:when test="${detail.isEbook == 1}">
                            <a href="read?id=${detail.id}" class="btn btn-primary btn-custom btn-lg px-4 shadow flex-grow-1"><i class="bi bi-phone me-2"></i> ĐỌC ONLINE</a>
                            <button onclick="addToCartWithQty(${detail.id})" class="btn btn-outline-danger btn-custom px-3"><i class="bi bi-cart-plus"></i> THÊM GIỎ</button>
                            <button onclick="buyNow(${detail.id})" class="btn btn-danger btn-custom text-white shadow"><i class="bi bi-bag-check me-1"></i> MUA NGAY</button>
                        </c:when>
                        <c:otherwise>
                            <c:choose>
                                <%-- NẾU TRONG KHO HẾT HÀNG (Stock <= 0) --%>
                                <c:when test="${detail.stock <= 0}">
                                    <div class="w-100">
                                        <div class="alert alert-danger text-center fw-bold rounded-pill mb-3 border-0" style="background: #fff2f0; color: #ED553B;">
                                            <i class="bi bi-x-octagon-fill me-2 fs-5 align-middle"></i> SẢN PHẨM TẠM THỜI HẾT HÀNG
                                        </div>
                                        <button class="btn btn-secondary w-100 py-3 rounded-pill fw-bold shadow-sm" disabled style="opacity: 0.6;">
                                            <i class="bi bi-cart-x me-2"></i> KHÔNG THỂ MUA HOẶC MƯỢN
                                        </button>
                                    </div>
                                </c:when>
                                <%-- NẾU VẪN CÒN HÀNG THÌ HIỆN NÚT BÌNH THƯỜNG --%>
                                <c:otherwise>
                                    <button onclick="addToCartWithQty(${detail.id})" class="btn btn-outline-danger btn-custom px-4"><i class="bi bi-cart-plus me-1"></i> THÊM GIỎ HÀNG</button>
                                    <button onclick="buyNow(${detail.id})" class="btn btn-danger btn-custom text-white shadow flex-grow-1" style="background-color: var(--accent-color);"><i class="bi bi-bag-check me-1"></i> MUA NGAY</button>
                                    <button class="btn btn-outline-primary btn-custom px-4" data-bs-toggle="modal" data-bs-target="#borrowModal"><i class="bi bi-calendar-check me-1"></i> MƯỢN SÁCH</button>
                                </c:otherwise>
                            </c:choose>
                        </c:otherwise>
                    </c:choose>
                </div>

                <%-- PHẦN RELATED BOOKS --%>
                <div class="mt-5">
                    <h4 class="fw-bold mb-4" style="color: var(--primary-color);">CÓ THỂ SẾP CŨNG THÍCH</h4>
                    <div class="row row-cols-2 row-cols-lg-4 g-3">
                        <c:forEach items="${listRelated}" var="r">
                            <div class="col text-center">
                                <a href="book-detail?id=${r.id}" class="card h-100 related-card shadow-sm p-2">
                                    <img src="${r.coverImage}" class="related-img mb-2" onerror="this.src='https://via.placeholder.com/150x220?text=Book'">
                                    <div class="card-body p-0">
                                        <h6 class="text-dark fw-bold text-truncate mb-1 small">${r.title}</h6>
                                        <p class="text-danger fw-bold small mb-0"><fmt:formatNumber value="${r.price}" pattern="###,###"/> đ</p>
                                    </div>
                                </a>
                            </div>
                        </c:forEach>
                    </div>
                </div>

                <hr class="my-5">

                <%-- PHẦN ĐÁNH GIÁ TỪ ĐỘC GIẢ --%>
                <div class="container mt-5 mb-5 p-0">
                    <h4 class="fw-bold mb-4 border-bottom pb-2" style="color: #173F5F;">ĐÁNH GIÁ TỪ ĐỘC GIẢ</h4>
                    
                    <%-- 🛑 TÔI CHỈ THÊM ID Ở ĐÂY CHO JS DỄ TÌM ĐỂ NHÉT BÌNH LUẬN VÀO --%>
                    <div class="mb-5" id="review-list-container">
                        <c:choose>
                            <c:when test="${empty listR}">
                                <div class="text-center py-4 bg-light rounded-3 text-muted" id="empty-review-msg"><i class="bi bi-chat-square-dots fs-1 d-block mb-2"></i>Chưa có bình luận nào. Sếp hãy là người đầu tiên đánh giá nhé!</div>
                            </c:when>
                            <c:otherwise>
                                <c:forEach items="${listR}" var="r">
                                    <div class="py-3 border-bottom">
                                        <div class="d-flex align-items-start">
                                            <div class="me-3" style="width: 45px; height: 45px; background-color: #ED553B; color: white; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: bold;">${r.username.substring(0,1).toUpperCase()}</div>
                                            <div class="flex-grow-1">
                                                <div class="fw-bold text-dark">${r.username}</div>
                                                <div class="text-warning small"><c:forEach begin="1" end="${r.rating}">★</c:forEach></div>
                                                <div class="text-muted small mb-2"><fmt:formatDate value="${r.date}" pattern="dd/MM/yyyy HH:mm"/></div>
                                                <p class="mb-0 text-dark">${r.comment}</p>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="p-4 bg-light rounded-3 border">
                        <h5 class="fw-bold mb-3">Viết nhận xét của bạn</h5>
                        <%-- 🛑 ĐÃ SỬA CÁI FORM THÀNH GỌI AJAX TRỰC TIẾP TRÊN TRANG --%>
                        <form id="ajaxReviewForm" onsubmit="submitReviewAsync(event)">
                            <input type="hidden" name="txt_id_sach" value="${detail.id}">
                            <div class="mb-3">
                                <label class="form-label fw-bold small text-muted">CHẤM ĐIỂM SÁCH</label>
                                <select name="txt_so_sao" class="form-select border-0 shadow-sm" id="star-rating">
                                    <option value="5">⭐⭐⭐⭐⭐ (Tuyệt vời)</option>
                                    <option value="4">⭐⭐⭐⭐ (Rất hay)</option>
                                    <option value="3">⭐⭐⭐ (Bình thường)</option>
                                    <option value="2">⭐⭐ (Hơi tệ)</option>
                                    <option value="1">⭐ (Rất tệ)</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <textarea name="txt_binh_luan" class="form-control border-0 shadow-sm" rows="3" placeholder="Chia sẻ cảm nhận..." required></textarea>
                            </div>
                            <button type="submit" class="btn btn-primary px-4 fw-bold shadow-sm rounded-pill">GỬI BÌNH LUẬN</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <%-- MODAL MƯỢN SÁCH --%>
    <div class="modal fade" id="borrowModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content border-0 shadow-lg" style="border-radius: 25px;">
                <div class="modal-header bg-primary text-white py-3 border-0">
                    <h5 class="modal-title fw-bold mx-auto">XÁC NHẬN MƯỢN SÁCH</h5>
                </div>
                <form action="borrow" method="POST">
                    <div class="modal-body p-4 text-center">
                        <input type="hidden" name="bookID" value="${detail.id}">
                        <img src="${mainImg}" style="height: 150px;" class="mb-3 rounded shadow-sm">
                        <h5 class="fw-bold text-primary-custom mb-3">${detail.title}</h5>
                        <div class="alert alert-info small text-start rounded-4">Thời hạn mượn mặc định là 14 ngày. Hãy trả sách đúng hạn nhé!</div>
                        <label class="form-label small fw-bold text-muted">CHỌN NGÀY BẠN SẼ TRẢ</label>
                        <input type="date" name="returnDate" class="form-control rounded-pill text-center" required>
                    </div>
                    <div class="modal-footer border-0 p-4 pt-0">
                        <button type="submit" class="btn btn-primary w-100 rounded-pill fw-bold py-3 shadow">GỬI YÊU CẦU MƯỢN</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <jsp:include page="footer.jsp" />
    
    <script>
        function changeDetailQty(delta) {
            const input = document.getElementById('detail-qty');
            let val = parseInt(input.value) + delta;
            let max = ${detail.stock};
            if (val >= 1 && val <= max) input.value = val;
            else if (val > max) Swal.fire('Hết hàng!', 'Trong kho chỉ còn ' + max + ' cuốn.', 'warning');
        }
        
        function addToCartWithQty(id) {
            const qty = document.getElementById('detail-qty').value;
            fetch('add-to-cart?id=' + id + '&quantity=' + qty + '&ajax=true')
                .then(res => res.json())
                .then(data => {
                    document.getElementById('cart-badge').innerText = data.cartSize;
                    Swal.fire({ 
                        icon: 'success', 
                        title: 'Đã thêm ' + qty + ' cuốn vào giỏ!', 
                        toast: true, 
                        position: 'top-end', 
                        timer: 1500, 
                        showConfirmButton: false 
                    });
                });
        }
        
        function buyNow(id) {
            const qty = document.getElementById('detail-qty').value;
            window.location.href = 'checkout?id=' + id + '&quantity=' + qty + '&checkoutType=single';
        }

        // 🛑 ĐÂY LÀ HÀM BÌNH LUẬN AJAX MỚI, KHÔNG ĐỤNG CHẠM CSS GÌ CỦA SẾP!
        function submitReviewAsync(e) {
            e.preventDefault(); 
            const form = e.target;
            const formData = new URLSearchParams(new FormData(form));

            fetch('add-review', {
                method: 'POST',
                body: formData
            })
            .then(res => res.json())
            .then(data => {
                if(data.status === 'unauthorized') {
                    window.location.href = 'login.jsp';
                    return;
                }
                if(data.status === 'success') {
                    // Lấy số sao khách vừa chọn
                    const starCount = parseInt(document.getElementById('star-rating').value);
                    let starsHtml = '';
                    for(let i=0; i<starCount; i++) starsHtml += '★';

                    // Lấy chữ cái đầu của tên (Giả lập vì JS không đọc được session trực tiếp như JSP)
                    const firstChar = '${sessionScope.acc != null ? sessionScope.acc.username.substring(0,1).toUpperCase() : "U"}';
                    const userName = '${sessionScope.acc != null ? sessionScope.acc.username : "Người dùng"}';

                    // Dựng lại khối HTML y chang CSS gốc của sếp
                    let newReview = `
                        <div class="py-3 border-bottom">
                            <div class="d-flex align-items-start">
                                <div class="me-3" style="width: 45px; height: 45px; background-color: #ED553B; color: white; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: bold;">\${firstChar}</div>
                                <div class="flex-grow-1">
                                    <div class="fw-bold text-dark">\${userName}</div>
                                    <div class="text-warning small">\${starsHtml}</div>
                                    <div class="text-muted small mb-2">Vừa xong</div>
                                    <p class="mb-0 text-dark">` + form.txt_binh_luan.value + `</p>
                                </div>
                            </div>
                        </div>`;
                    
                    const listContainer = document.getElementById('review-list-container');
                    if(listContainer) {
                        // Xóa dòng "Chưa có bình luận nào" nếu nó đang hiện
                        const emptyMsg = document.getElementById('empty-review-msg');
                        if (emptyMsg) emptyMsg.remove();
                        
                        // Nhét bình luận mới lên đầu
                        listContainer.insertAdjacentHTML('afterbegin', newReview);
                    }
                    form.reset(); 
                    Swal.fire({icon: 'success', title: 'Đã gửi bình luận!', toast: true, position: 'top-end', showConfirmButton: false, timer: 2000});
                }
            });
        }
    </script>
</body>
</html>